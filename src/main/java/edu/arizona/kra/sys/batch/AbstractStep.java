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

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

/*Batch framework Imported from KFS*/
public abstract class AbstractStep extends InitiateDirectoryBase implements Step, BeanNameAware, InitializingBean, InitiateDirectory {

    private static final Logger LOG = Logger.getLogger(AbstractStep.class);

    protected String name;
    protected ParameterService parameterService;
    protected DateTimeService dateTimeService;


    protected boolean interrupted = false;

    /**
     * Initialization  after bean properties are instantiate,
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {

    }


    /**
     * Sets the bean name
     *
     * @param name String that contains the bean name
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    @Override
    public void setBeanName(String name) {
        this.name = name;
    }

    /**
     * Gets the name attribute.
     *
     * @return Returns the name.
     */
    @Override
    public String getName() {
        return name;
    }

    protected ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the dateTimeService attribute.
     *
     * @return Returns the dateTimeService.
     */
    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Returns the boolean value of the interrupted flag
     *
     * @return boolean
     * @see org.kuali.kfs.sys.batch.Step#isInterrupted()
     */
    @Override
    public boolean isInterrupted() {
        return interrupted;
    }


    /**
     * Sets the interruped flag
     *
     * @param interrupted
     * @see org.kuali.kfs.sys.batch.Step#setInterrupted(boolean)
     */
    @Override
    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    /**
     * Initializes the interrupted flag
     *
     * @see org.kuali.kfs.sys.batch.Step#interrupt()
     */
    @Override
    public void interrupt() {
        this.interrupted = true;
    }
}
