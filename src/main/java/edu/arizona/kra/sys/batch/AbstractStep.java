package edu.arizona.kra.sys.batch;

import edu.arizona.kra.sys.batch.bo.Step;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants.COMPONENT;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants.NAMESPACE;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

import static org.kuali.rice.coreservice.framework.parameter.ParameterConstants.BATCH_COMPONENT;

/**
 * nataliac on 8/22/18: Batch framework Imported and adapted from KFS
 **/
@COMPONENT(component = BATCH_COMPONENT)
public abstract class AbstractStep implements Step, BeanNameAware, InitializingBean {


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