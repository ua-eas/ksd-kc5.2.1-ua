package edu.arizona.kra.sys.batch.bo;

import org.quartz.JobDataMap;

import java.util.Date;

/**
 * nataliac on 8/22/18: Batch framework Imported and adapted from KFS
 **/
public interface Step {
    /**
     * Perform this step of a batch job.
     *
     * @param jobName    the name of the job running the step
     * @param jobRunDate the time/date the job is executed
     * @param jobExecutionContext Job execution context that also contains the JobDataMap
     * @return true if successful and continue the job, false if successful and stop the job
     * @throws Throwable if unsuccessful
     */
    public boolean execute(String jobName, Date jobRunDate, JobDataMap jobDataMap) throws InterruptedException;

    /**
     * Return id of this step spring bean.
     *
     * @return The name of this step.
     */
    public String getName();

    /**
     * Call to attempt to interrupt a step in the middle of processing. Note that this only has an effect if the step in question
     * checks its interrupted status.
     */
    public void interrupt();

    public boolean isInterrupted();

    public void setInterrupted(boolean interrupted);
}
