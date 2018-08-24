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
package edu.arizona.kra.sys.batch;

import edu.arizona.kra.sys.batch.bo.Step;
import edu.arizona.kra.sys.batch.service.SchedulerService;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.quartz.*;
import org.springframework.util.StopWatch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * nataliac on 8/22/18: Batch framework Imported and adapted from KFS
 **/
public class Job implements StatefulJob, InterruptableJob {

    private static final Logger LOG = Logger.getLogger(Job.class);
    private SchedulerService schedulerService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    private List<Step> steps;
    private Step currentStep;
    private Appender ndcAppender;
    private boolean notRunnable;
    private transient Thread workerThread;

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        workerThread = Thread.currentThread();
        if (isNotRunnable()) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Skipping job because doNotRun is true: " + jobExecutionContext.getJobDetail().getName());
            }
            return;
        }
        int startStep = 0;
        try {
            startStep = Integer.parseInt(jobExecutionContext.getMergedJobDataMap().getString(BatchConstants.JOB_RUN_START_STEP));
        } catch (NumberFormatException ex) {
            // not present, do nothing
        }
        int endStep = 0;
        try {
            endStep = Integer.parseInt(jobExecutionContext.getMergedJobDataMap().getString(BatchConstants.JOB_RUN_END_STEP));
        } catch (NumberFormatException ex) {
            // not present, do nothing
        }
        Date jobRunDate = dateTimeService.getCurrentDate();
        int currentStepNumber = 0;
        try {
            LOG.info("Executing job: " + jobExecutionContext.getJobDetail() + " on machine " + getMachineName() + " scheduler instance id " + jobExecutionContext.getScheduler().getSchedulerInstanceId() + "\n" + jobDataMapToString(jobExecutionContext.getJobDetail().getJobDataMap()));
            for (Step step : getSteps()) {
                currentStepNumber++;
                // prevent starting of the next step if the thread has an interrupted status
                if (workerThread.isInterrupted()) {
                    LOG.warn("Aborting Job execution due to manual interruption");
                    schedulerService.updateStatus(jobExecutionContext.getJobDetail(), BatchConstants.CANCELLED_JOB_STATUS_CODE);
                    return;
                }
                if (startStep > 0 && currentStepNumber < startStep) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Skipping step " + currentStepNumber + " - startStep=" + startStep);
                    }
                    continue; // skip to next step
                } else if (endStep > 0 && currentStepNumber > endStep) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Ending step loop - currentStepNumber=" + currentStepNumber + " - endStep = " + endStep);
                    }
                    break;
                }
                step.setInterrupted(false);
                try {
                    if (!runStep(parameterService, jobExecutionContext.getJobDetail().getFullName(), currentStepNumber, step, jobRunDate)) {
                        break;
                    }
                } catch (InterruptedException ex) {
                    LOG.warn("Stopping after step interruption");
                    schedulerService.updateStatus(jobExecutionContext.getJobDetail(), BatchConstants.CANCELLED_JOB_STATUS_CODE);
                    return;
                }
                if (step.isInterrupted()) {
                    LOG.warn("attempt to interrupt step failed, step continued to completion");
                    LOG.warn("cancelling remainder of job due to step interruption");
                    schedulerService.updateStatus(jobExecutionContext.getJobDetail(), BatchConstants.CANCELLED_JOB_STATUS_CODE);
                    return;
                }
            }
        } catch (Exception e) {
            schedulerService.updateStatus(jobExecutionContext.getJobDetail(), BatchConstants.FAILED_JOB_STATUS_CODE);
            throw new JobExecutionException("Caught exception in " + jobExecutionContext.getJobDetail().getName(), e, false);
        }
        LOG.info("Finished executing job: " + jobExecutionContext.getJobDetail().getName());
        schedulerService.updateStatus(jobExecutionContext.getJobDetail(), BatchConstants.SUCCEEDED_JOB_STATUS_CODE);
    }

    public static boolean runStep(ParameterService parameterService, String jobName, int currentStepNumber, Step step, Date jobRunDate) throws InterruptedException, WorkflowException {
        boolean continueJob = true;
        if (GlobalVariables.getUserSession() == null) {
            LOG.info(new StringBuffer("Started processing step: ").append(currentStepNumber).append("=").append(step.getName()).append(" for user <unknown>"));
        } else {
            LOG.info(new StringBuffer("Started processing step: ").append(currentStepNumber).append("=").append(step.getName()).append(" for user ").append(GlobalVariables.getUserSession().getPrincipalName()));
        }


        Step unProxiedStep = (Step) BatchUtils.getTargetIfProxied(step);
        Class<?> stepClass = unProxiedStep.getClass();
        GlobalVariables.clear();

        String stepUserName = KRADConstants.SYSTEM_USER;
        if (parameterService.parameterExists(stepClass, BatchConstants.STEP_USER_PARM_NM)) {
            stepUserName = parameterService.getParameterValueAsString(stepClass, BatchConstants.STEP_USER_PARM_NM);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info(new StringBuffer("Creating user session for step: ").append(step.getName()).append("=").append(stepUserName));
        }
        GlobalVariables.setUserSession(new UserSession(stepUserName));
        if (LOG.isInfoEnabled()) {
            LOG.info(new StringBuffer("Executing step: ").append(step.getName()).append("=").append(stepClass));
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);
        try {
            continueJob = step.execute(jobName, jobRunDate);
        } catch (InterruptedException e) {
            LOG.error("Exception occured executing step", e);
            throw e;
        } catch (RuntimeException e) {
            LOG.error("Exception occured executing step", e);
            throw e;
        }
        stopWatch.stop();
        LOG.info(new StringBuffer("Step ").append(step.getName()).append(" of ").append(jobName).append(" took ").append(stopWatch.getTotalTimeSeconds() / 60.0).append(" minutes to complete").toString());
        if (!continueJob) {
            LOG.info("Stopping job after successful step execution");
        }
        LOG.info(new StringBuffer("Finished processing step ").append(currentStepNumber).append(": ").append(step.getName()));
        return continueJob;
    }




    /**
     * @throws UnableToInterruptJobException
     */
    @Override
    public void interrupt() throws UnableToInterruptJobException {
        // ask the step to interrupt
        if (currentStep != null) {
            currentStep.interrupt();
        }
        // also attempt to interrupt the thread, to cause an InterruptedException if the step ever waits or sleeps
        workerThread.interrupt();
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public Appender getNdcAppender() {
        return ndcAppender;
    }

    public void setNdcAppender(Appender ndcAppender) {
        this.ndcAppender = ndcAppender;
    }

    public void setNotRunnable(boolean notRunnable) {
        this.notRunnable = notRunnable;
    }

    protected boolean isNotRunnable() {
        return notRunnable;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    protected String jobDataMapToString(JobDataMap jobDataMap) {
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        Iterator keys = jobDataMap.keySet().iterator();
        boolean hasNext = keys.hasNext();
        while (hasNext) {
            String key = (String) keys.next();
            Object value = jobDataMap.get(key);
            buf.append(key).append("=");
            if (value == jobDataMap) {
                buf.append("(this map)");
            } else {
                buf.append(value);
            }
            hasNext = keys.hasNext();
            if (hasNext) {
                buf.append(", ");
            }
        }
        buf.append("}");
        return buf.toString();
    }

    protected String getMachineName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }
}
