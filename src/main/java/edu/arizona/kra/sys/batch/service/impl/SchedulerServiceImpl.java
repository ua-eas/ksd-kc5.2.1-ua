/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.arizona.kra.sys.batch.service.impl;


import edu.arizona.kra.sys.batch.*;
import edu.arizona.kra.sys.batch.Job;
import edu.arizona.kra.sys.batch.JobListener;
import edu.arizona.kra.sys.batch.bo.Step;
import edu.arizona.kra.sys.batch.service.BatchModuleService;
import edu.arizona.kra.sys.batch.service.SchedulerService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.quartz.*;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

import static edu.arizona.kra.sys.batch.BatchConstants.*;


/**
 * nataliac on 8/22/18: Batch framework Imported and adapted from KFS
 **/
@Transactional
public class SchedulerServiceImpl implements SchedulerService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SchedulerServiceImpl.class);

    private Scheduler scheduler;
    private JobListener jobListener;
    private KualiModuleService kualiModuleService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;

    /**
     * Holds a list of job name to job descriptor mappings for those jobs that are externalized (i.e. the module service is responsible for reporting their status)
     */
    protected Map<String, JobDescriptor> externalizedJobDescriptors;

    protected static final List<String> jobStatuses = new ArrayList<String>();

    static {
        jobStatuses.add(SCHEDULED_JOB_STATUS_CODE);
        jobStatuses.add(SUCCEEDED_JOB_STATUS_CODE);
        jobStatuses.add(CANCELLED_JOB_STATUS_CODE);
        jobStatuses.add(RUNNING_JOB_STATUS_CODE);
        jobStatuses.add(FAILED_JOB_STATUS_CODE);
    }

    public SchedulerServiceImpl() {
        externalizedJobDescriptors = new HashMap<>();
    }

    public boolean isBatchNode(){
        LOG.info("isBatchNode: hostname="+BatchUtils.getHostname());
        if ( parameterService.parameterExists(BatchConstants.PARAM_NAME_BATCH_NODE_HOSTNAME, BatchConstants.BATCH_COMPONENT_CODE, BatchConstants.PARAM_NAME_BATCH_NODE_HOSTNAME)) {
            String batchNodename = parameterService.getParameterValueAsString(BatchConstants.NAMESPACE_KC_SYS, BatchConstants.BATCH_COMPONENT_CODE, BatchConstants.PARAM_NAME_BATCH_NODE_HOSTNAME);
            return StringUtils.equals( BatchUtils.getHostname(), batchNodename);
        }
        return false;
    }

    @Override
    public void initialize() {
        LOG.info("Initializing the quartz batch scheduler nodeName="+BatchUtils.getHostname()+" isBatchNode="+isBatchNode());
        //only start scheduler and jobs if hostname is set to be the batch node in the BATCH_HOSTNAME parameter
        if ( isBatchNode() ) {
            jobListener.setSchedulerService(this);
            try {
                scheduler.addGlobalJobListener(jobListener);
            } catch (SchedulerException e) {
                throw new RuntimeException("SchedulerServiceImpl encountered an exception when trying to register the global job listener", e);
            }
            removeOldJobsAndTriggers();
            for (ModuleService moduleService : kualiModuleService.getInstalledModuleServices()) {

                if (CollectionUtils.isNotEmpty(moduleService.getModuleConfiguration().getJobNames())) {
                    initializeJobsForModule(moduleService);
                    initializeTriggersForModule(moduleService);
                }
            }

            dropDependenciesNotScheduled();
            try {
                //start scheduler
                scheduler.startDelayed(10); //TODO <-- just for testing purposes... have to use this instead-> BatchConstants.QUARTZ_SCHEDULER_START_DELAY_SEC);
            } catch (SchedulerException e) {
                e.printStackTrace();
                throw new RuntimeException("SchedulerServiceImpl: Exception when starting the Quartz Scheduler", e);
            }
        } else {
            LOG.info("Skipping the initialization - "+BatchUtils.getHostname()+" is NOT a batch node !");
        }
    }

    /**
     * Method that cleans up the scheduled jobs and triggers from the DB when the scheduler starts. THis prevents old stale jobs from existing in the DB.
     */
    protected void removeOldJobsAndTriggers(){

        try {
            removeTriggers(BatchConstants.SCHEDULED_GROUP);
            removeTriggers(BatchConstants.UNSCHEDULED_GROUP);

            removeJobs(BatchConstants.SCHEDULED_GROUP);
            removeJobs(BatchConstants.UNSCHEDULED_GROUP);

        }catch (Exception e){
            e.printStackTrace();
            LOG.info("Exception at removeOldJobsAndTriggers:",e);
        }
    }

    private void removeJobs(String groupName) throws SchedulerException{
        String[] jobNames = scheduler.getJobNames(groupName);
        if (ArrayUtils.isNotEmpty(jobNames)){
            for (String job:jobNames){
                scheduler.deleteJob(job,groupName);
            }
        }
    }

    private void removeTriggers(String groupName) throws SchedulerException{
        String[] triggerNames = scheduler.getTriggerNames(groupName);
        if (ArrayUtils.isNotEmpty(triggerNames)){
            for (String trigger:triggerNames){
                scheduler.unscheduleJob(trigger,groupName);
            }
        }
    }



    /**
     * Initializes all of the jobs into Quartz for the given ModuleService
     *
     * @param moduleService the ModuleService implementation to initalize jobs for
     */
    protected void initializeJobsForModule(ModuleService moduleService) {
        LOG.info("Loading scheduled jobs for: " + moduleService.getModuleConfiguration().getNamespaceCode());

        JobDescriptor jobDescriptor;
        if (moduleService.getModuleConfiguration().getJobNames() != null) {
            for (String jobName : moduleService.getModuleConfiguration().getJobNames()) {
                try {
                    if (moduleService instanceof BatchModuleService && ((BatchModuleService) moduleService).isExternalJob(jobName)) {
                        jobDescriptor = new JobDescriptor();
                        jobDescriptor.setBeanName(jobName);
                        jobDescriptor.setGroup(SCHEDULED_GROUP);
                        jobDescriptor.setDurable(false);
                        externalizedJobDescriptors.put(jobName, jobDescriptor);
                    } else {
                        jobDescriptor = BatchSpringContext.getJobDescriptor(jobName);
                    }
                    jobDescriptor.setNamespaceCode(moduleService.getModuleConfiguration().getNamespaceCode());
                    loadJob(jobDescriptor);
                } catch (NoSuchBeanDefinitionException ex) {
                    LOG.error("unable to find job bean definition for job: " + ex.getBeanName());
                } catch (Exception ex) {
                    LOG.error("Unable to install " + jobName + " job into scheduler.", ex);
                }
            }
        }
    }

    /**
     * Loops through all the triggers associated with the given module service, adding each trigger to Quartz
     *
     * @param moduleService the ModuleService instance to initialize triggers for
     */
    protected void initializeTriggersForModule(ModuleService moduleService) {
        //add dynamically instantiated triggers for module
        if (moduleService instanceof KcModuleServiceImpl && CollectionUtils.isNotEmpty(((KcModuleServiceImpl)moduleService).getTriggerDescriptors())){
            for (TriggerDescriptor triggerDesc: ((KcModuleServiceImpl) moduleService).getTriggerDescriptors()){
                try {
                    addTrigger(triggerDesc.getTrigger());
                } catch (Exception ex) {
                    LOG.error("Unable to install " + triggerDesc.toString() + " trigger into scheduler.", ex);
                }
            }
        }
        //add regular .xml defined triggers - if any
        if (moduleService.getModuleConfiguration().getTriggerNames() != null) {
            for (String triggerName : moduleService.getModuleConfiguration().getTriggerNames()) {
                try {
                    addTrigger(BatchSpringContext.getTriggerDescriptor(triggerName).getTrigger());
                } catch (NoSuchBeanDefinitionException ex) {
                    LOG.error("unable to find trigger definition: " + ex.getBeanName());
                } catch (Exception ex) {
                    LOG.error("Unable to install " + triggerName + " trigger into scheduler.", ex);
                }
            }
        }
    }

    protected void loadJob(JobDescriptor jobDescriptor) {
        JobDetail jobDetail = jobDescriptor.getJobDetail();
        addJob(jobDetail);
        if (SCHEDULED_GROUP.equals(jobDetail.getGroup())) {
            jobDetail.setGroup(UNSCHEDULED_GROUP);
            addJob(jobDetail);
        }
    }

    /**
     * Remove dependencies that are not scheduled. Important for modularization if some
     * modules arn't loaded or if institutions don't schedule some dependencies
     */
    protected void dropDependenciesNotScheduled() {
        try {
            List<String> scheduledGroupJobNames = Arrays.asList(scheduler.getJobNames(SCHEDULED_GROUP));

            for (String jobName : scheduledGroupJobNames) {
                JobDescriptor jobDescriptor = BatchSpringContext.getJobDescriptor(jobName);

                if (jobDescriptor != null && jobDescriptor.getDependencies() != null) {
                    // dependenciesToBeRemoved so to avoid ConcurrentModificationException
                    ArrayList<Entry<String, String>> dependenciesToBeRemoved = new ArrayList<Entry<String, String>>();
                    Set<Entry<String, String>> dependenciesSet = jobDescriptor.getDependencies().entrySet();
                    for (Entry<String, String> dependency : dependenciesSet) {
                        String dependencyJobName = dependency.getKey();
                        if (!scheduledGroupJobNames.contains(dependencyJobName)) {
                            LOG.warn("Removing dependency " + dependencyJobName + " from " + jobName + " because it is not scheduled.");
                            dependenciesToBeRemoved.add(dependency);
                        }
                    }
                    dependenciesSet.removeAll(dependenciesToBeRemoved);
                }
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while trying to drop dependencies that are not scheduled", e);
        }
    }

    @Override
    public void initializeJob(String jobName, Job job) {
        job.setSchedulerService(this);
        job.setParameterService(parameterService);
        job.setSteps(BatchSpringContext.getJobDescriptor(jobName).getSteps());
        job.setDateTimeService(dateTimeService);
    }

    @Override
    public boolean hasIncompleteJob() {
        StringBuilder log = new StringBuilder("The schedule has incomplete jobs.");
        boolean hasIncompleteJob = false;
        for (String scheduledJobName : getJobNamesForScheduleJob()) {
            JobDetail scheduledJobDetail = getScheduledJobDetail(scheduledJobName);

            boolean jobIsIncomplete = isIncomplete(scheduledJobDetail);
            if (jobIsIncomplete) {
                log.append("\n\t").append(scheduledJobDetail.getFullName());
                hasIncompleteJob = true;
            }
        }
        if (hasIncompleteJob) {
            LOG.info(log);
        }
        return hasIncompleteJob;
    }

    protected boolean isIncomplete(JobDetail scheduledJobDetail) {
        if (scheduledJobDetail == null) {
            return false;
        }

        return !SCHEDULE_JOB_NAME.equals(scheduledJobDetail.getName()) && (isPending(scheduledJobDetail) || isScheduled(scheduledJobDetail));
    }


    @Override
    public void processWaitingJobs() {
        for (String scheduledJobName : getJobNamesForScheduleJob()) {
            JobDetail jobDetail = getScheduledJobDetail(scheduledJobName);
            if (isPending(jobDetail)) {
                if (shouldScheduleJob(jobDetail)) {
                    scheduleJob(SCHEDULED_GROUP, scheduledJobName, 0, 0, new Date(), null, Collections.singletonMap(BatchConstants.MASTER_JOB_NAME, SCHEDULE_JOB_NAME));
                }
                if (shouldCancelJob(jobDetail)) {
                    updateStatus(SCHEDULED_GROUP, scheduledJobName, CANCELLED_JOB_STATUS_CODE);
                }
            }
        }
    }

    @Override
    public void logScheduleResults() {
        StringBuilder scheduleResults = new StringBuilder("The schedule completed.");
        for (String scheduledJobName : getJobNamesForScheduleJob()) {
            JobDetail jobDetail = getScheduledJobDetail(scheduledJobName);
            if (jobDetail != null && !SCHEDULE_JOB_NAME.equals(jobDetail.getName())) {
                scheduleResults.append("\n\t").append(jobDetail.getName()).append("=").append(getStatus(jobDetail));
            }
        }
        LOG.info(scheduleResults);
    }

    @Override
    public boolean shouldNotRun(JobDetail jobDetail) {
        if (isBatchNode()) {
            if (SCHEDULED_GROUP.equals(jobDetail.getGroup())) {
                if ( isDisabled(jobDetail)) {
                    return true;
                }

                if (isCancelled(jobDetail)) {
                    LOG.info("Telling listener not to run job, because it has been cancelled: " + jobDetail.getName());
                    return true;
                } else {
                    for (String dependencyJobName : getJobDependencies(jobDetail.getName()).keySet()) {
                        if (!isDependencySatisfiedPositively(jobDetail, getScheduledJobDetail(dependencyJobName))) {
                            LOG.info("Telling listener not to run job, because a dependency has not been satisfied positively: " + jobDetail.getName() + " (dependency job = " + dependencyJobName + ")");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void updateStatus(JobDetail jobDetail, String jobStatus) {
        LOG.info("Updating status of job: " + jobDetail.getName() + "=" + jobStatus);

        jobDetail.getJobDataMap().put(JOB_STATUS_PARAMETER, jobStatus);
    }

    @Override
    public void runJob(String jobName, String requestorEmailAddress) {
        runJob(jobName, 0, 0, new Date(), requestorEmailAddress);
    }

    @Override
    public void runJob(String jobName, int startStep, int stopStep, Date startTime, String requestorEmailAddress) {
        runJob(UNSCHEDULED_GROUP, jobName, startStep, stopStep, startTime, requestorEmailAddress);
    }

    @Override
    public void runJob(String groupName, String jobName, int startStep, int stopStep, Date jobStartTime, String requestorEmailAddress) {
        if ( isBatchNode() ) {
            LOG.info("Executing user initiated job: " + groupName + "." + jobName + " (startStep=" + startStep + " / stopStep=" + stopStep + " / startTime=" + jobStartTime + " / requestorEmailAddress=" + requestorEmailAddress + ")");

            scheduleJob(groupName, jobName, startStep, stopStep, jobStartTime, requestorEmailAddress, null);
        } else {
            LOG.info("NOT batch node. Cannot execute user initiated job: " + groupName + "." + jobName);
        }
    }

    public void runStep(String groupName, String jobName, String stepName, Date startTime, String requestorEmailAddress) {
        LOG.info("Executing user initiated step: " + stepName + " / requestorEmailAddress=" + requestorEmailAddress);

        // abort if the step is already running
        if (isJobRunning(jobName)) {
            LOG.warn("Attempt to run job already executing, aborting");
            return;
        }
        int stepNum = 1;
        boolean stepFound = false;
        BatchJobStatus job = getJob(groupName, jobName);
        for (Step step : job.getSteps()) {
            if (step.getName().equals(stepName)) {
                stepFound = true;
                break;
            }
            stepNum++;
        }
        if (stepFound) {
            runJob(groupName, jobName, stepNum, stepNum, startTime, requestorEmailAddress);
        } else {
            LOG.warn("Unable to find step " + stepName + " in job " + groupName + "." + jobName);
        }
    }

    @Override
    public boolean isJobRunning(String jobName) {
        List<JobExecutionContext> runningJobs = getRunningJobs();
        for (JobExecutionContext jobCtx : runningJobs) {
            if (jobCtx.getJobDetail().getName().equals(jobName)) {
                return true;
            }
        }
        return false;
    }

    protected void addJob(JobDetail jobDetail) {
        try {
            LOG.info("Adding job: " + jobDetail.getFullName());

            scheduler.addJob(jobDetail, true);
        } catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while adding job: " + jobDetail.getFullName(), e);
        }
    }


    /**
     * Adds a new trigger or replaces an existing trigger in the scheduler
     * @param trigger -> Trigger to be added.
     */
    protected void addTrigger(Trigger trigger) {
        try {
            if (UNSCHEDULED_GROUP.equals(trigger.getGroup())) {
                LOG.error("Triggers should not be specified for jobs in the unscheduled group - not adding trigger: " + trigger.getName());
            } else {
                LOG.info("Adding trigger: " + trigger.getName());
                try {
                   Trigger oldTrigger =  scheduler.getTrigger(trigger.getName(), trigger.getGroup());
                   if ( oldTrigger != null ){
                       scheduler.rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
                   } else {
                       scheduler.scheduleJob(trigger);
                   }
                } catch (ObjectAlreadyExistsException ex) {
                    LOG.debug("Caught ex: ", ex);
                }
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while adding trigger: " + trigger.getFullName(), e);
        }
    }

    protected void scheduleJob(String groupName, String jobName, int startStep, int endStep, Date startTime, String requestorEmailAddress, Map<String, String> additionalJobData) {
        try {
            updateStatus(groupName, jobName, BatchConstants.SCHEDULED_JOB_STATUS_CODE);
            SimpleTriggerDescriptor trigger = new SimpleTriggerDescriptor(jobName, groupName, jobName, dateTimeService);
            trigger.setStartTime(startTime);
            Trigger qTrigger = trigger.getTrigger();
            qTrigger.getJobDataMap().put(BatchConstants.REQUESTOR_EMAIL_ADDRESS_KEY, requestorEmailAddress);
            qTrigger.getJobDataMap().put(BatchConstants.JOB_RUN_START_STEP, String.valueOf(startStep));
            qTrigger.getJobDataMap().put(BatchConstants.JOB_RUN_END_STEP, String.valueOf(endStep));
            if (additionalJobData != null) {
                qTrigger.getJobDataMap().putAll(additionalJobData);
            }
            for (Trigger oldTrigger : scheduler.getTriggersOfJob(jobName, groupName)) {
                scheduler.unscheduleJob(oldTrigger.getName(), groupName);
            }
            scheduler.scheduleJob(qTrigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while scheduling job: " + jobName, e);
        }
    }

    protected boolean shouldScheduleJob(JobDetail jobDetail) {
        try {
            if (scheduler.getTriggersOfJob(jobDetail.getName(), SCHEDULED_GROUP).length > 0) {
                return false;
            }
            for (String dependencyJobName : getJobDependencies(jobDetail.getName()).keySet()) {
                JobDetail dependencyJobDetail = getScheduledJobDetail(dependencyJobName);
                if (dependencyJobDetail == null) {
                    LOG.error("Unable to get JobDetail for dependency of " + jobDetail.getName() + " : " + dependencyJobName);
                    return false;
                }
                if (!isDependencySatisfiedPositively(jobDetail, dependencyJobDetail)) {
                    return false;
                }
            }
        } catch (SchedulerException se) {
            throw new RuntimeException("Caught scheduler exception while determining whether to schedule job: " + jobDetail.getName(), se);
        }
        return true;
    }

    protected boolean shouldCancelJob(JobDetail jobDetail) {
        LOG.info("shouldCancelJob:::::: " + jobDetail.getFullName());
        if (jobDetail == null) {
            return true;
        }
        for (String dependencyJobName : getJobDependencies(jobDetail.getName()).keySet()) {
            LOG.info("dependencyJobName:::::" + dependencyJobName);
            JobDetail dependencyJobDetail = getScheduledJobDetail(dependencyJobName);
            if (isDependencySatisfiedNegatively(jobDetail, dependencyJobDetail)) {
                LOG.info("cancelling " + jobDetail.getFullName() + " because dependency " + dependencyJobName + " was \"satisfied negatively\"");
                return true;
            }
        }
        return false;
    }

    protected boolean isDependencySatisfiedPositively(JobDetail dependentJobDetail, JobDetail dependencyJobDetail) {
        if (dependentJobDetail == null || dependencyJobDetail == null) {
            return false;
        }
        return isSucceeded(dependencyJobDetail) || ((isFailed(dependencyJobDetail) || isCancelled(dependencyJobDetail)) && isSoftDependency(dependentJobDetail.getName(), dependencyJobDetail.getName()));
    }

    protected boolean isDependencySatisfiedNegatively(JobDetail dependentJobDetail, JobDetail dependencyJobDetail) {
        LOG.info("isDependencySatisfiedNegatively::::  dependentJobDetail::: " + dependencyJobDetail.getFullName() + " dependencyJobDetail    " + dependencyJobDetail.getFullName());
        if (dependentJobDetail == null || dependencyJobDetail == null) {
            return true;
        }
        return (isFailed(dependencyJobDetail) || isCancelled(dependencyJobDetail)) && !isSoftDependency(dependentJobDetail.getName(), dependencyJobDetail.getName());
    }

    protected boolean isSoftDependency(String dependentJobName, String dependencyJobName) {
        return BatchConstants.SOFT_DEPENDENCY_CODE.equals(getJobDependencies(dependentJobName).get(dependencyJobName));
    }

    protected Map<String, String> getJobDependencies(String jobName) {
        LOG.info("getJobDependencies:::: for job " + jobName);
        return BatchSpringContext.getJobDescriptor(jobName).getDependencies();
    }

    protected boolean isPending(JobDetail jobDetail) {
        return getStatus(jobDetail) == null;
    }

    protected boolean isScheduled(JobDetail jobDetail) {
        return SCHEDULED_JOB_STATUS_CODE.equals(getStatus(jobDetail));
    }

    protected boolean isSucceeded(JobDetail jobDetail) {
        return SUCCEEDED_JOB_STATUS_CODE.equals(getStatus(jobDetail));
    }

    protected boolean isFailed(JobDetail jobDetail) {
        return FAILED_JOB_STATUS_CODE.equals(getStatus(jobDetail));
    }

    protected boolean isCancelled(JobDetail jobDetail) {
        return CANCELLED_JOB_STATUS_CODE.equals(getStatus(jobDetail));
    }

    protected boolean isDisabled(JobDetail jobDetail){
        if ( jobDetail.getJobDataMap().get(BatchConstants.JOB_ENABLED_KEY) != null ){
            String jobEnabled = jobDetail.getJobDataMap().getString(BatchConstants.JOB_ENABLED_KEY);
            if (StringUtils.equalsIgnoreCase(Boolean.FALSE.toString(), jobEnabled) || StringUtils.equalsIgnoreCase(BatchConstants.JOB_DISABLED_VALUE, jobEnabled)){
                LOG.debug("Job "+jobDetail.getFullName()+" is DISABLED!!! jobDataMapValue="+jobEnabled);
                return true;
            }
        }
        return false;
    }

    @Override
    public String getStatus(JobDetail jobDetail) {
        if (jobDetail == null) {
            return FAILED_JOB_STATUS_CODE;
        }
        KcModuleServiceImpl moduleService = (KcModuleServiceImpl)
                KraServiceLocator.getService(KualiModuleService.class).getResponsibleModuleServiceForJob(jobDetail.getName());
        //If the module service has status information for a job, get the status from it
        //else get status from job detail data map
        return (moduleService != null && moduleService.isExternalJob(jobDetail.getName()))
                ? moduleService.getExternalJobStatus(jobDetail.getName())
                : jobDetail.getJobDataMap().getString(BatchConstants.JOB_STATUS_PARAMETER);
    }

    protected JobDetail getScheduledJobDetail(String jobName) {
        LOG.info("getScheduledJobDetail ::::::: " + jobName);
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobName, SCHEDULED_GROUP);
            if (jobDetail == null) {
                LOG.error("Unable to obtain the job details for the scheduled version of: " + jobName);
            }
            return jobDetail;
        } catch (SchedulerException e) {
            throw new RuntimeException("Caught scheduler exception while getting job detail: " + jobName, e);
        }
    }

    @Override
    public List<BatchJobStatus> getJobs() {
        ArrayList<BatchJobStatus> jobs = new ArrayList<BatchJobStatus>();
        try {
            for (String jobGroup : scheduler.getJobGroupNames()) {
                for (String jobName : scheduler.getJobNames(jobGroup)) {
                    try {
                        JobDescriptor jobDescriptor = retrieveJobDescriptor(jobName);
                        JobDetail jobDetail = scheduler.getJobDetail(jobName, jobGroup);
                        jobs.add(new BatchJobStatus(jobDescriptor, jobDetail));
                    } catch (NoSuchBeanDefinitionException ex) {
                        // do nothing, ignore jobs not defined in spring
                        LOG.warn("Attempt to find bean " + jobGroup + "." + jobName + " failed - not in Spring context");
                    }
                }
            }
        } catch (SchedulerException ex) {
            throw new RuntimeException("Exception while obtaining job list", ex);
        }
        return jobs;
    }

    @Override
    public BatchJobStatus getJob(String groupName, String jobName) {
        for (BatchJobStatus job : getJobs()) {
            if (job.getName().equals(jobName) && job.getGroup().equals(groupName)) {
                return job;
            }
        }
        return null;
    }

    @Override
    public List<BatchJobStatus> getJobs(String groupName) {
        ArrayList<BatchJobStatus> jobs = new ArrayList<BatchJobStatus>();
        try {
            for (String jobName : scheduler.getJobNames(groupName)) {
                try {
                    JobDescriptor jobDescriptor = retrieveJobDescriptor(jobName);
                    JobDetail jobDetail = scheduler.getJobDetail(jobName, groupName);
                    jobs.add(new BatchJobStatus(jobDescriptor, jobDetail));
                } catch (NoSuchBeanDefinitionException ex) {
                    // do nothing, ignore jobs not defined in spring
                    LOG.warn("Attempt to find bean " + groupName + "." + jobName + " failed - not in Spring context");
                }
            }
        } catch (SchedulerException ex) {
            throw new RuntimeException("Exception while obtaining job list", ex);
        }
        return jobs;
    }

    @Override
    public List<JobExecutionContext> getRunningJobs() {
        try {
            List<JobExecutionContext> jobContexts = scheduler.getCurrentlyExecutingJobs();
            return jobContexts;
        } catch (SchedulerException ex) {
            throw new RuntimeException("Unable to get list of running jobs.", ex);
        }
    }

    protected void updateStatus(String groupName, String jobName, String jobStatus) {
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobName, groupName);
            updateStatus(jobDetail, jobStatus);
            scheduler.addJob(jobDetail, true);
        } catch (SchedulerException e) {
            throw new RuntimeException(new StringBuilder("Caught scheduler exception while updating job status: ").append(jobName).append(", ").append(jobStatus).toString(), e);
        }
    }

    @Override
    public void removeScheduled(String jobName) {
        try {
            scheduler.deleteJob(jobName, SCHEDULED_GROUP);
        } catch (SchedulerException ex) {
            throw new RuntimeException("Unable to remove scheduled job: " + jobName, ex);
        }
    }

    @Override
    public void addScheduled(JobDetail job) {
        try {
            job.setGroup(SCHEDULED_GROUP);
            scheduler.addJob(job, true);
        } catch (SchedulerException ex) {
            throw new RuntimeException("Unable to add job to scheduled group: " + job.getName(), ex);
        }
    }

    @Override
    public void addUnscheduled(JobDetail job) {
        try {
            job.setGroup(UNSCHEDULED_GROUP);
            scheduler.addJob(job, true);
        } catch (SchedulerException ex) {
            throw new RuntimeException("Unable to add job to unscheduled group: " + job.getName(), ex);
        }
    }

    @Override
    public List<String> getSchedulerGroups() {
        try {
            return Arrays.asList(scheduler.getJobGroupNames());
        } catch (SchedulerException ex) {
            throw new RuntimeException("Exception while obtaining job list", ex);
        }
    }

    @Override
    public List<String> getJobStatuses() {
        return jobStatuses;
    }

    @Override
    public void interruptJob(String jobName) {
        List<JobExecutionContext> runningJobs = getRunningJobs();
        for (JobExecutionContext jobCtx : runningJobs) {
            if (jobName.equals(jobCtx.getJobDetail().getName())) {
                try {
                    ((Job) jobCtx.getJobInstance()).interrupt();
                } catch (UnableToInterruptJobException ex) {
                    LOG.warn("Unable to perform job interrupt", ex);
                }
                break;
            }
        }
    }

    @Override
    public Date getNextStartTime(BatchJobStatus job) {
        try {
            Trigger[] triggers = scheduler.getTriggersOfJob(job.getName(), job.getGroup());
            Date nextDate = new Date(Long.MAX_VALUE);
            for (Trigger trigger : triggers) {
                if (trigger.getNextFireTime() != null) {
                    if (trigger.getNextFireTime().getTime() < nextDate.getTime()) {
                        nextDate = trigger.getNextFireTime();
                    }
                }
            }
            if (nextDate.getTime() == Long.MAX_VALUE) {
                nextDate = null;
            }
            return nextDate;
        } catch (SchedulerException ex) {

        }
        return null;
    }

    @Override
    public Date getNextStartTime(String groupName, String jobName) {
        BatchJobStatus job = getJob(groupName, jobName);

        return getNextStartTime(job);
    }

    protected JobDescriptor retrieveJobDescriptor(String jobName) {
        if (externalizedJobDescriptors.containsKey(jobName)) {
            return externalizedJobDescriptors.get(jobName);
        }
        return BatchSpringContext.getJobDescriptor(jobName);
    }

    protected List<String> getJobNamesForScheduleJob() {
        List<String> jobNames = new ArrayList<>();
        try {
            for (String scheduledJobName : scheduler.getJobNames(SCHEDULED_GROUP)) {
                if (scheduler.getTriggersOfJob(scheduledJobName, SCHEDULED_GROUP).length == 0) {
                    // jobs that have their own triggers will not be included in the master scheduleJob
                    jobNames.add(scheduledJobName);
                }
            }
        } catch (Exception ex) {
            LOG.error("Error occurred while initializing job name list", ex);
        }
        return jobNames;
    }

    @Override
    public void reinitializeScheduledJobs() {
        try {
            for (String scheduledJobName : getJobNamesForScheduleJob()) {
                updateStatus(SCHEDULED_GROUP, scheduledJobName, null);
            }
        } catch (Exception e) {
            LOG.error("Error occurred while trying to reinitialize jobs", e);
        }
    }

    @Override
    public boolean cronConditionMet(String cronExpressionString) {
        boolean cronConditionMet = false;

        CronExpression cronExpression;
        try {
            cronExpression = new CronExpression(cronExpressionString);
            Date currentDate = dateTimeService.getCurrentDate();
            Date validTimeAfter = cronExpression.getNextValidTimeAfter(dateTimeService.getCurrentDate());
            if (validTimeAfter != null) {
                String cronDate = dateTimeService.toString(validTimeAfter, BatchConstants.MONTH_DAY_YEAR_DATE_FORMAT);
                if (cronDate.equals(dateTimeService.toString(currentDate, BatchConstants.MONTH_DAY_YEAR_DATE_FORMAT))) {
                    cronConditionMet = true;
                }
            } else {
                LOG.error("Null date returned when calling CronExpression.nextValidTimeAfter() for cronExpression: " + cronExpressionString);
            }
        } catch (ParseException ex) {
            LOG.error("Error parsing cronExpression: " + cronExpressionString, ex);
        }

        return cronConditionMet;
    }

    @Override
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setKualiModuleService(KualiModuleService moduleService) {
        this.kualiModuleService = moduleService;
    }

    public void setJobListener(JobListener jobListener) {
        this.jobListener = jobListener;
    }
}
