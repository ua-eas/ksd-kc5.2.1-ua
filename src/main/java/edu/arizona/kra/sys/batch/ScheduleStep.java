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

import org.apache.log4j.Logger;
import edu.arizona.kra.sys.batch.service.SchedulerService;

import java.util.Date;



/**
 * nataliac on 8/22/18: Batch framework Imported and adapted from KFS
 **/
public class ScheduleStep extends AbstractStep {
    private static final Logger LOG = Logger.getLogger(ScheduleStep.class);
    private SchedulerService schedulerService;

    @Override
    public boolean execute(String jobName, Date jobRunDate) {

        schedulerService.reinitializeScheduledJobs();
        while (schedulerService.hasIncompleteJob() ) {
            schedulerService.processWaitingJobs();
            try {
                Thread.sleep(Integer.parseInt(getParameterService().getParameterValueAsString(getClass(), BatchConstants.BATCH_SCHEDULE_STATUS_CHECK_INTERVAL)));
            } catch (InterruptedException e) {
                throw new RuntimeException("Schedule step encountered interrupt exception while trying to wait for the specified batch schedule status check interval", e);
            }
        }

        schedulerService.logScheduleResults();
        return true;
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
