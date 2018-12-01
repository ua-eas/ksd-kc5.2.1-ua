package edu.arizona.kra.subaward.batch;

import edu.arizona.kra.subaward.batch.bo.UAGlEntry;
import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceData;
import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceFeedSummary;
import edu.arizona.kra.subaward.batch.service.GlDataImportService;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceErrorReportService;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceFeedService;
import edu.arizona.kra.sys.batch.AbstractStep;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.quartz.JobDataMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static edu.arizona.kra.subaward.batch.InvoiceFeedConstants.SIF_JOB_EXECUTION_ID_KEY;
import static edu.arizona.kra.subaward.batch.InvoiceFeedConstants.SIF_JOB_EXECUTION_SUMMARY_KEY;


/**
 * First step in SubawardInvoiceFeedJob - Step that imports GL Data of interest for Subaward Invoice Feed from KFS into KC -
 *
 * Created by nataliac on 8/23/18.
 */
public class ValidateGLDataStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ValidateGLDataStep.class);

    private GlDataImportService glDataImportService;
    private SubawardInvoiceFeedService subawardInvoiceFeedService;
    private SubawardInvoiceErrorReportService subawardInvoiceErrorReportService;
    private BusinessObjectService businessObjectService;

    public ValidateGLDataStep() {
        super();
    }

    /**
     * @see edu.arizona.kra.sys.batch.bo.Step#execute(String, Date, JobDataMap jobDataMap)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate, JobDataMap jobDataMap) throws InterruptedException {
        LOG.info("ValidateGLDataStep: execute() started");
        Long executionId = jobDataMap.getLongValue(SIF_JOB_EXECUTION_ID_KEY);
        Collection<UAGlEntry> importedGlEntries = glDataImportService.allImportedGLEntries();
        List<UASubawardInvoiceData> invoiceDataList = new ArrayList<>(110);
        int importedEntriesCount = 0;
        for ( UAGlEntry glEntry: importedGlEntries){
            if ( glDataImportService.isDuplicateRow(glEntry) ){
                //found a duplicate, report error and skip...
                subawardInvoiceErrorReportService.recordDuplicateRowError(executionId, glEntry);
                LOG.debug("ValidateGLDataStep: execute() Found duplicate Gl Entry:  "+glEntry.toString()+" Skipping!");
            } else {
                UASubawardInvoiceData invoiceData = glDataImportService.createInvoiceData(glEntry);
                invoiceData.setExecutionId( executionId );
                invoiceDataList.add(invoiceData);
                importedEntriesCount++;
                //process in batches of 100 so we don't overbloat the memory
                if ( importedEntriesCount % 100 == 0){
                    businessObjectService.save(invoiceDataList);
                    invoiceDataList.clear();
                }
            }
        }
        if (!invoiceDataList.isEmpty()) {
            businessObjectService.save(invoiceDataList);
        }

        UASubawardInvoiceFeedSummary jobSummary = (UASubawardInvoiceFeedSummary)jobDataMap.get(SIF_JOB_EXECUTION_SUMMARY_KEY);
        jobSummary.setInvoiceDataCount(importedEntriesCount);
        getSubawardInvoiceFeedService().updateSubawardInvoiceFeedSummary(jobSummary);

        LOG.info("ValidateGLDataStep: execute() finished. Imported entries "+importedEntriesCount+ " out of "+importedGlEntries.size());
        return true;
    }


    public void setGlDataImportService(GlDataImportService glDataImportService) {
        this.glDataImportService = glDataImportService;
    }

    public void setSubawardInvoiceErrorReportService(SubawardInvoiceErrorReportService subawardInvoiceErrorReportService) {
        this.subawardInvoiceErrorReportService = subawardInvoiceErrorReportService;
    }


    public SubawardInvoiceFeedService getSubawardInvoiceFeedService() {
        if (subawardInvoiceFeedService == null){
            subawardInvoiceFeedService = KraServiceLocator.getService(SubawardInvoiceFeedService.class);
        }
        return subawardInvoiceFeedService;
    }



    public void setBusinessObjectService( BusinessObjectService bos){
        this.businessObjectService = bos;
    }

}
