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

import org.quartz.CronTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.ParseException;


/**
 * nataliac on 8/22/18: Batch framework Imported and adapted from KFS
 * Represents a Cron Trigger Descriptor - has a cron expresion to be set on the trigger
 **/
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CronTriggerDescriptor extends TriggerDescriptor {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TriggerDescriptor.class);

    private String cronExpression;


    protected void completeTriggerDescription(Trigger trigger) {
        // prevent setting of the trigger information in test mode
        try {
            ((CronTrigger) trigger).setTimeZone(getDateTimeService().getCurrentCalendar().getTimeZone());
            if (!isTestMode()) {
                ((CronTrigger) trigger).setCronExpression(cronExpression);
            } else {
                ((CronTrigger) trigger).setCronExpression("0 59 23 31 12 ? 2099");
            }
        } catch (ParseException e) {
            LOG.error("Invalid CRON EXPRESSION: "+cronExpression+" while trying to set the cronExpression attribute of a CronTrigger: " + getJobName(), e);
            try {
                ((CronTrigger) trigger).setCronExpression("0 59 23 31 12 ? 2099");
            } catch (ParseException x){}
        }
    }

    /**
     * Sets the cronExpression attribute value.
     *
     * @param cronExpression The cronExpression to set.
     */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
