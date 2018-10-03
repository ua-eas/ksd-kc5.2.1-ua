package edu.arizona.kra.sys.batch;

import edu.arizona.kra.sys.batch.service.SchedulerService;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;

import java.util.Date;


public class ScheduleStep extends AbstractStep {
    private static final Logger LOG = Logger.getLogger(ScheduleStep.class);
    private SchedulerService schedulerService;


    public boolean execute(String jobName, Date jobRunDate,  JobDataMap jobDataMap) {
        boolean isPastScheduleCutoffTime = false;
        schedulerService.reinitializeScheduledJobs();
        while (schedulerService.hasIncompleteJob() && !isPastScheduleCutoffTime) {
            schedulerService.processWaitingJobs();
            try {
                Thread.sleep(Integer.parseInt(getParameterService().getParameterValueAsString(getClass(), BatchConstants.BATCH_SCHEDULE_STATUS_CHECK_INTERVAL)));
            } catch (InterruptedException e) {
                throw new RuntimeException("Schedule step encountered interrupt exception while trying to wait for the specified batch schedule status check interval", e);
            }
        }

        schedulerService.logScheduleResults();
        return !isPastScheduleCutoffTime;
    }

    /**
     * Sets the schedulerService attribute value.
     *
     * @param schedulerService The schedulerService to set.
     */
    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}
