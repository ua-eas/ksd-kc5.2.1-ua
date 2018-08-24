package edu.arizona.kra.subaward.batch;

import edu.arizona.kra.subaward.batch.service.GlDataImportService;
import edu.arizona.kra.sys.batch.AbstractStep;

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
     * @see edu.arizona.kra.sys.batch.bo.Step#execute(java.lang.String, java.util.Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        LOG.debug("execute() started");
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        java.sql.Date threeDaysAgo = InvoiceFeedUtils.substractDaysFromDate(today, 3);

        //TODO set appropriate dates from Trigger - figure out how to get that info in the step
        glDataImportService.importGLData(threeDaysAgo, today);

        return true;
    }


    public void setGlDataImportService(GlDataImportService glDataImportService) {
        this.glDataImportService = glDataImportService;
    }
}
