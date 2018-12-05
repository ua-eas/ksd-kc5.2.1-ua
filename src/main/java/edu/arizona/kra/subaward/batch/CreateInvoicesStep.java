package edu.arizona.kra.subaward.batch;


import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceData;
import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceFeedSummary;
import edu.arizona.kra.subaward.batch.service.GlDataImportService;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceErrorReportService;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceFeedService;
import edu.arizona.kra.sys.batch.AbstractStep;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.subaward.bo.SubAward;

import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.service.BusinessObjectService;

import org.quartz.JobDataMap;

import java.util.*;

import static edu.arizona.kra.subaward.batch.InvoiceFeedConstants.SIF_JOB_EXECUTION_ID_KEY;
import static edu.arizona.kra.subaward.batch.InvoiceFeedConstants.SIF_JOB_EXECUTION_SUMMARY_KEY;


/**
 * Third and last step in SubawardInvoiceFeedJob - Step imports all InvoiceData into regular Subaward Invoice Maitnenance Documents
 *
 * Created by nataliac on 12/01/18.
 */
public class CreateInvoicesStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateInvoicesStep.class);

    private GlDataImportService glDataImportService;
    private SubawardInvoiceFeedService subawardInvoiceFeedService;
    private SubawardInvoiceErrorReportService subawardInvoiceErrorReportService;
    private BusinessObjectService businessObjectService;

    public CreateInvoicesStep() {
        super();
    }

    /**
     * @see edu.arizona.kra.sys.batch.bo.Step#execute(String, Date, JobDataMap jobDataMap)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate, JobDataMap jobDataMap) throws InterruptedException {
        Long executionId = jobDataMap.getLongValue(SIF_JOB_EXECUTION_ID_KEY);
        LOG.info("CreateInvoicesStep: execute() STARTED executionId="+executionId);
        Collection<UASubawardInvoiceData> invoiceDataList = glDataImportService.findNewInvoiceDataEntries(executionId);
        int createdInvoicesCount = 0;
        for ( UASubawardInvoiceData invoiceData: invoiceDataList){
            SubAward subaward = null;
            MaintenanceDocument subawardInvoiceMaintenanceDoc = null;
            try {
                subaward = glDataImportService.findMatchingActiveSubaward(invoiceData);

                subawardInvoiceMaintenanceDoc = getSubawardInvoiceFeedService().createSubawardInvoiceDoc(invoiceData, subaward);

                if (getSubawardInvoiceFeedService().validateSubawardInvoice(subawardInvoiceMaintenanceDoc).isEmpty()) {
                    getSubawardInvoiceFeedService().saveSubawardInvoice(subawardInvoiceMaintenanceDoc);
                    createdInvoicesCount++;
                    //Update invoice data to show to what it WAS imported....
                    getSubawardInvoiceFeedService().updateSubawardInvoiceData(invoiceData, subawardInvoiceMaintenanceDoc);
                } else {
                    //TODO create an SUBAWARD ERROR with all messages instead of generic message
                    subawardInvoiceErrorReportService.recordError(executionId, "Problems at validating invoice for GLENTRY_ID="+invoiceData.getEntryId()+" PurchaseOrderNum="+invoiceData.getPurchaseOrderNumber(), invoiceData.getEntryId(),
                            subaward==null?null:subaward.getSubAwardId(), null, subawardInvoiceMaintenanceDoc==null?null:subawardInvoiceMaintenanceDoc.getDocumentNumber());
                    LOG.error("Problems at validating invoice for GLENTRY_ID="+invoiceData.getEntryId()+" PurchaseOrderNum="+invoiceData.getPurchaseOrderNumber());
                }



            } catch (Exception e) {
                StringBuffer sb = new StringBuffer("Error creating Subaward Invoice for GLEntryId:");sb.append(invoiceData.getEntryId());
                sb.append(" PurchaseOrderNumber:"); sb.append(invoiceData.getPurchaseOrderNumber());
                sb.append(" Error:"); sb.append(e.getMessage());
                sb.append(" Cause:"); sb.append(e.getCause());
                subawardInvoiceErrorReportService.recordError(executionId, sb.toString(), invoiceData.getEntryId(),
                        subaward==null?null:subaward.getSubAwardId(), null, subawardInvoiceMaintenanceDoc==null?null:subawardInvoiceMaintenanceDoc.getDocumentNumber());
                LOG.error( sb.toString(), e);
            }
        }


        UASubawardInvoiceFeedSummary jobSummary = (UASubawardInvoiceFeedSummary)jobDataMap.get(SIF_JOB_EXECUTION_SUMMARY_KEY);
        jobSummary.setSubawardInvoicesCount(createdInvoicesCount);
        getSubawardInvoiceFeedService().updateSubawardInvoiceFeedSummary(jobSummary);

        LOG.info("CreateInvoicesStep: execute() finished. Created invoices "+createdInvoicesCount+ " out of "+invoiceDataList.size());
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
