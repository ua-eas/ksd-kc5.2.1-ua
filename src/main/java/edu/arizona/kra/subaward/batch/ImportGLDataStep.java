package edu.arizona.kra.subaward.batch;

import edu.arizona.kra.subaward.batch.service.GlDataImportService;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceErrorReportService;
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
    private SubawardInvoiceErrorReportService subawardInvoiceErrorReportService;

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

        int importedLinesCount = glDataImportService.importGLData(startDate, today);
        if (importedLinesCount > 0)
            LOG.info("ImportGLDataStep: execute() finished. Imported number of GL Entries : "+importedLinesCount);
        else
            LOG.info("ImportGLDataStep: execute() finished. ZERO GL Entries imported!");

        return true;
    }

    /**
     * Method that extracts the number of days for this run from the JobDataMap stored in the trigger - since each run of the job can bring back a different number of days worth of data.
     * @param jobDataMap
     * @return
     */

    protected int getDataIntervalInDays(JobDataMap jobDataMap ){
        Integer daysInterval = InvoiceFeedConstants.DEFAULT_DATA_INTERVAL_DAYS;
        if ( jobDataMap != null && jobDataMap.get(InvoiceFeedConstants.DAYS_INTERVAL_KEY) != null ){
            try{
                daysInterval = (Integer) jobDataMap.get(InvoiceFeedConstants.DAYS_INTERVAL_KEY);
            } catch (ClassCastException e){
                daysInterval = InvoiceFeedConstants.DEFAULT_DATA_INTERVAL_DAYS;
                LOG.error("ImportGLDataStep: could not parse days interval: "+jobDataMap.getString(InvoiceFeedConstants.DAYS_INTERVAL_KEY)+" using default value:"+daysInterval);
                subawardInvoiceErrorReportService.recordError("ImportGLDataStep: could not parse days interval: "+jobDataMap.getString(InvoiceFeedConstants.DAYS_INTERVAL_KEY)+" using default value:"+daysInterval, e);
            }
        }
        return daysInterval;
    }


    public void setGlDataImportService(GlDataImportService glDataImportService) {
        this.glDataImportService = glDataImportService;
    }

    public void setSubawardInvoiceErrorReportService(SubawardInvoiceErrorReportService subawardInvoiceErrorReportService) {
        this.subawardInvoiceErrorReportService = subawardInvoiceErrorReportService;
    }

}
