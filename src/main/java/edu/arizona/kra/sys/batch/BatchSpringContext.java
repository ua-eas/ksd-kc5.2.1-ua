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
import edu.arizona.kra.sys.batch.bo.Step;
import org.kuali.kra.infrastructure.KraServiceLocator;


public class BatchSpringContext {
    private static final Logger LOG = Logger.getLogger(BatchSpringContext.class);

    public static Step getStep(String beanId) {
        return SpringContext.getBean(Step.class, beanId);
    }

    public static JobDescriptor getJobDescriptor(String beanId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getJobDescriptor:::::" + beanId);
        }
        return KraServiceLocator.getAppContext().getBeansOfType(JobDescriptor.class).get(beanId);
    }

    public static TriggerDescriptor getTriggerDescriptor(String beanId) {
        return KraServiceLocator.getAppContext().getBeansOfType(TriggerDescriptor.class).get(beanId);
    }

    public static BatchInputFileType getBatchInputFileType(String beanId) {
        return KraServiceLocator.getAppContext().getBeansOfType(BatchInputFileType.class).get(beanId);
    }

    public static BatchInputFileSetType getBatchInputFileSetType(String beanId) {
        return KraServiceLocator.getAppContext().getBeansOfType(BatchInputFileSetType.class).get(beanId);
    }
}
