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

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

import java.util.Properties;

/**
 * This class wraps the spring version to facilitate setting additional Quartz propoerties
 * nataliac on 8/22/18: Batch framework Imported and adapted from KFS
 */
public class SchedulerFactoryBean extends org.springframework.scheduling.quartz.SchedulerFactoryBean {

    private Properties quartzPropertiesReference;


    @Override
    public void afterPropertiesSet() throws Exception {
        quartzPropertiesReference.put("org.quartz.jobStore.useProperties", "false");
        quartzPropertiesReference.put("org.quartz.jobStore.isClustered", "true");

        setQuartzProperties(quartzPropertiesReference);
        super.afterPropertiesSet();

    }


    /**
     * @see org.springframework.scheduling.quartz.SchedulerFactoryBean#createScheduler(SchedulerFactory, String)
     */
    @Override
    protected Scheduler createScheduler(SchedulerFactory schedulerFactory, String schedulerName) throws SchedulerException {
        Scheduler scheduler = super.createScheduler(schedulerFactory, schedulerName);
        return scheduler;
    }


    /**
     * Sets the quartzPropertiesReference attribute value.
     *
     * @param quartzPropertiesReference The quartzPropertiesReference to set.
     */
    public void setQuartzPropertiesReference(Properties quartzPropertiesReference) {
        this.quartzPropertiesReference = quartzPropertiesReference;
    }
}
