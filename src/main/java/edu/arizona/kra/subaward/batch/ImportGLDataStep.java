package edu.arizona.kra.subaward.batch;

import edu.arizona.kra.subaward.batch.service.GlDataImportService;
import edu.arizona.kra.sys.batch.AbstractStep;
import org.quartz.JobDataMap;

import java.util.Date;

/**
 * First step in SubawardInvoiceFeedJob - Step that imports GL Data of interest for Subaward Invoice Feed from KFS into KC -
 *
 * Created by nataliac on 8/23/18.
 */
public class ImportGLDataStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ImportGLDataStep.class);

    private GlDataImportService glDataImportService;

    public ImportGLDataStep() {
        super();
    }

    /**
     * @see edu.arizona.kra.sys.batch.bo.Step#execute(java.lang.String, java.util.Date, JobDataMap jobDataMap)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate, JobDataMap jobDataMap) throws InterruptedException {
        LOG.debug("ImportGLDataStep: execute() started");
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        int daysInterval = getDataIntervalInDays(jobDataMap);
        java.sql.Date startDate = InvoiceFeedUtils.substractDaysFromDate(today, daysInterval);

        glDataImportService.importGLData(startDate, today);
        LOG.debug("ImportGLDataStep: execute() finished");

        return true;
    }

    protected int getDataIntervalInDays(JobDataMap jobDataMap ){
        Integer daysInterval = InvoiceFeedConstants.DEFAULT_DATA_INTERVAL_DAYS;
        if ( jobDataMap != null && jobDataMap.get(InvoiceFeedConstants.DAYS_INTERVAL_KEY) != null ){
            try{
                daysInterval = (Integer) jobDataMap.get(InvoiceFeedConstants.DAYS_INTERVAL_KEY);
            } catch (ClassCastException e){
                daysInterval = InvoiceFeedConstants.DEFAULT_DATA_INTERVAL_DAYS;
                LOG.error("ImportGLDataStep: could not parse days interval: "+jobDataMap.getString(InvoiceFeedConstants.DAYS_INTERVAL_KEY)+" using default value:"+daysInterval);
            }
        }
        return daysInterval;
    }


    public void setGlDataImportService(GlDataImportService glDataImportService) {
        this.glDataImportService = glDataImportService;
    }
}
