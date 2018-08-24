package edu.arizona.kra.subaward.batch;

import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.slf4j.LoggerFactory;
import edu.arizona.kra.sys.batch.SchedulerFactoryBean;

import  org.slf4j.Logger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class managing the Scheduler for the Subaward Invoice Feed Batch Job
 * Created by nataliac on 8/23/18.
 */
public class SubawardBatchSchedulerFactoryBean extends SchedulerFactoryBean {
    private static final Logger LOG = LoggerFactory.getLogger(SubawardBatchSchedulerFactoryBean.class);

    private List<CronTrigger> triggers;


    /**
     * Gets the triggers attribute.
     *
     * @return Returns the triggers.
     */
    public List<CronTrigger> getTriggers() {
        return triggers;
    }

    /**
     * This method checks if cron expressions are valid and will run. Only then, they are passed on to super class for scheduling
     *
     * @param triggers The triggers to set.
     */
    public void setTriggers(List<CronTrigger> triggers) {
        this.triggers = triggers;
        CronExpression cronExpression;
        List<CronTrigger> schedulableTriggers = new ArrayList<CronTrigger>();

        for (CronTrigger cronTrigger : triggers) {
            try {
                cronExpression = new CronExpression(cronTrigger.getCronExpression());
                if (cronExpression.getNextValidTimeAfter(new Date()) == null) {
                    // The cron expression is valid, but will never trigger.
                    LOG.info(cronTrigger.getCronExpression() + " not valid cronexpression. The job will not be scheduled.");
                    continue;
                }
            }
            catch (ParseException e) {
                LOG.info(cronTrigger.getCronExpression() + " not valid cronexpression. The job will not be scheduled.");
                continue;
            }
            schedulableTriggers.add(cronTrigger);
        }
        super.setTriggers(schedulableTriggers.toArray(new CronTrigger[0]));
    }

}
