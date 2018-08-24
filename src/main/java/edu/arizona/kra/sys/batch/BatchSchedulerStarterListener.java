package edu.arizona.kra.sys.batch;

import edu.arizona.kra.sys.batch.service.SchedulerService;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Listener that starts the Batch Quartz schedulers when the Application Context has finished initializing
 * Created by nataliac on 8/23/18.
 */
public class BatchSchedulerStarterListener implements ServletContextListener {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchSchedulerStarterListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        try {
            LOG.info("Attempting to initialize the SchedulerService");
            KraServiceLocator.getService(SchedulerService.class).initialize();

            //KraServiceLocator.getService(Scheduler.class).start();
        } catch (NoSuchBeanDefinitionException e) {
            LOG.warn("Not initializing the scheduler because there is no scheduler bean");
        } catch (Exception ex) {
            LOG.error("Caught Exception while starting the scheduler", ex);
        }

        LOG.info("Finished initializing Batch Scheduler.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            Scheduler schedulerBean = KraServiceLocator.getService(BatchConstants.UAR_SCHEDULER_NAME);
            if ( schedulerBean != null ) {
                if (schedulerBean.isStarted()) {
                    LOG.info("Shutting Down scheduler");
                    schedulerBean.shutdown();
                }
            }
        } catch (SchedulerException ex) {
            LOG.error("Exception while shutting down the scheduler", ex);
        }
    }
}


