package edu.arizona.kra.subaward.batch.service.impl;

import edu.arizona.kra.subaward.batch.InvoiceFeedConstants;
import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceData;
import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceFeedSummary;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceFeedService;

import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.rules.ErrorReporter;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.subaward.bo.SubAwardAmountReleased;
import org.kuali.kra.subaward.service.SubAwardService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.service.*;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.*;

import static edu.arizona.kra.subaward.batch.InvoiceFeedConstants.*;
import static edu.arizona.kra.sys.batch.BatchConstants.BATCH_USER;

/**
 * Created by nataliac on 9/18/18.
 */
public class SubawardInvoiceFeedServiceImpl implements SubawardInvoiceFeedService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubawardInvoiceFeedServiceImpl.class);


    List <String> runSchedule;
    List <Integer> dataIntervals;
    String jobNotificationEmail;

    ParameterService parameterService;
    BusinessObjectService businessObjectService;
    DocumentService documentService;
    DateTimeService dateTimeService;
    SubAwardService subAwardService;
    DictionaryValidationService dictionaryValidationService;

    private ErrorReporter errorReporter = new ErrorReporter();


    public boolean isSubawardInvoiceFeedEnabled(){
        if (parameterService.parameterExists(InvoiceFeedConstants.PARAM_NAMESPACE_SUBWAWARD, InvoiceFeedConstants.PARAM_COMPONENT_BATCH, InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_FEED_ENABLED )) {
            return parameterService.getParameterValueAsBoolean(InvoiceFeedConstants.PARAM_NAMESPACE_SUBWAWARD, InvoiceFeedConstants.PARAM_COMPONENT_BATCH, InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_FEED_ENABLED);
        }
        return false;
    }

    public List<String> getSubawardInvoiceFeedRunSchedule(){

        if (CollectionUtils.isEmpty(runSchedule)) {
            runSchedule = new ArrayList<>();

            if ( isSubawardInvoiceFeedEnabled() && parameterService.parameterExists(InvoiceFeedConstants.PARAM_NAMESPACE_SUBWAWARD, InvoiceFeedConstants.PARAM_COMPONENT_BATCH, InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_FEED_SCHEDULE ) ) {
                String schedules = parameterService.getParameterValueAsString(InvoiceFeedConstants.PARAM_NAMESPACE_SUBWAWARD, InvoiceFeedConstants.PARAM_COMPONENT_BATCH, InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_FEED_SCHEDULE );
                if (!StringUtils.isEmpty(schedules)){
                    runSchedule = Arrays.asList(schedules.split(";"));
                }
           }
        }
        return runSchedule;
    }



    public List<Integer> getSubwawardInvoiceFeedDataIntervalsDays(){
        if (CollectionUtils.isEmpty( dataIntervals)) {
            dataIntervals = new ArrayList<>();
            if ( isSubawardInvoiceFeedEnabled()  && parameterService.parameterExists(InvoiceFeedConstants.PARAM_NAMESPACE_SUBWAWARD, InvoiceFeedConstants.PARAM_COMPONENT_BATCH, InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_DATA_INTERVALS ) ) {
                String paramValue = parameterService.getParameterValueAsString(InvoiceFeedConstants.PARAM_NAMESPACE_SUBWAWARD, InvoiceFeedConstants.PARAM_COMPONENT_BATCH, InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_DATA_INTERVALS );

                if (!StringUtils.isEmpty(paramValue)){
                    List<String> intervals = Arrays.asList(paramValue.split(";"));
                    for (String interval:intervals){
                        try {
                            dataIntervals.add(Integer.parseInt(interval));
                        } catch (NumberFormatException e){
                            LOG.error("getSubwawardInvoiceFeedDataIntervalsDays: Could not parse integer from SUBAWARD_INVOICE_DATA_INTERVALS:"+paramValue+" SKIPPING...");
                        }
                    }
                }
            }
        }
        return dataIntervals;
    }



    public String getSubwawardInvoiceFeedDestinationEmail(){
        if ( StringUtils.isEmpty(jobNotificationEmail) ) {
            jobNotificationEmail = parameterService.getParameterValueAsString(InvoiceFeedConstants.PARAM_NAMESPACE_SUBWAWARD, InvoiceFeedConstants.PARAM_COMPONENT_BATCH, InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_FEED_EMAIL );
            if (StringUtils.isEmpty(jobNotificationEmail)){
                LOG.error("getSubwawardInvoiceFeedDestinationEmail: Could not determine DESTINATION EMAIL ADDRESS integer from parameter: "+InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_FEED_EMAIL);
            }
        }
        return jobNotificationEmail;
    }



    public UASubawardInvoiceFeedSummary createSubawardInvoiceFeedSummary(){
        UASubawardInvoiceFeedSummary sifExecutionJobSummary = new UASubawardInvoiceFeedSummary();
        sifExecutionJobSummary.setJobStartTime(dateTimeService.getCurrentSqlDate());
        //TODO DO WE NEED THIS? Job creates User session of 'kr' kind... hack to avoid NULL from UserSession... TODO should we start a user session??! ->execute as BATCH USER??? in Scheduler or job??
        sifExecutionJobSummary.setUpdateUser(BATCH_USER);
        sifExecutionJobSummary.setUpdateUserSet(true);
        return businessObjectService.save(sifExecutionJobSummary);
    }


    public UASubawardInvoiceFeedSummary updateSubawardInvoiceFeedSummary(UASubawardInvoiceFeedSummary sifExecutionJobSummary){
        return businessObjectService.save(sifExecutionJobSummary);
    }



    public MaintenanceDocument createSubawardInvoiceDoc(UASubawardInvoiceData invoiceData, SubAward subaward) throws WorkflowException {
        LOG.debug("ENTER createSubawardInvoice invoiceData="+invoiceData.toString() +" subawardCode="+subaward.getSubAwardCode());
        MaintenanceDocument maintenanceDoc = (MaintenanceDocument)documentService.getNewDocument(SUBAWARD_INVOICE_FEED_MAINT_DOC_TYPE_NAME, BATCH_USER);
        maintenanceDoc.getDocumentHeader().setDocumentDescription(INVOICE_DOCUMENT_DESCRIPTION);
        maintenanceDoc.getDocumentHeader().setOrganizationDocumentNumber(invoiceData.getPurchaseOrderNumber());

        SubAwardAmountReleased invoice = (SubAwardAmountReleased) maintenanceDoc.getNoteTarget();
        invoice.setDocumentNumber(maintenanceDoc.getDocumentNumber());

        populateInvoiceWithData(invoice, invoiceData, subaward);

        LOG.debug("createSubawardInvoice DONE.");
        return maintenanceDoc;
    }


    protected SubAwardAmountReleased populateInvoiceWithData(SubAwardAmountReleased invoice, UASubawardInvoiceData invoiceData, SubAward subaward){
        invoice.setUpdateUser(BATCH_USER);
        invoice.setUpdateUserSet(true);
        invoice.setCreatedBy(BATCH_USER);

        invoice.setSubAward(subaward);
        invoice.setSubAwardId(subaward.getSubAwardId());
        invoice.setSubAwardCode(subaward.getSubAwardCode());
        invoice.setSequenceNumber(subaward.getSequenceNumber());
        invoice.setStartDate(subaward.getStartDate());
        invoice.setEndDate(subaward.getEndDate());

        invoice.setInvoiceNumber(invoiceData.getPurchaseOrderNumber()+"-"+invoiceData.getFinancialDocNumber());
        invoice.setAmountReleased(invoiceData.getAmountReleased());
        invoice.setEffectiveDate(invoiceData.getEffectiveDate());
        invoice.setComments(invoiceData.getComments());

        invoice.setCreatedDate(dateTimeService.getCurrentTimestamp());
        return invoice;
    }

    public Map validateSubawardInvoice(MaintenanceDocument subawardInvoiceMaintenanceDoc){
        LOG.debug("ENTER validateSubawardInvoice" );
        Map errors = new HashMap();
        SubAwardAmountReleased invoice = (SubAwardAmountReleased) subawardInvoiceMaintenanceDoc.getNoteTarget();
        errors.putAll(validateNewInvoice(invoice));
        errors.putAll(validateInvoiceMaintenanceDoc(subawardInvoiceMaintenanceDoc));
        LOG.debug("validateSubawardInvoice DONE. Nbr of errors="+errors.size() );
        return errors;
    }


    protected Map validateNewInvoice(SubAwardAmountReleased invoice){
        GlobalVariables.getMessageMap().addToErrorPath("document.newMaintainableObject");
        getDictionaryValidationService().validateBusinessObject(invoice, true);
        GlobalVariables.getMessageMap().removeFromErrorPath("document.newMaintainableObject");
        return GlobalVariables.getMessageMap().getErrorMessages();
    }


    protected Map validateInvoiceMaintenanceDoc(MaintenanceDocument subawardInvoiceMaintenanceDoc) {

        GlobalVariables.getMessageMap().addToErrorPath("document.newMaintainableObject");
        SubAwardAmountReleased invoice = (SubAwardAmountReleased) subawardInvoiceMaintenanceDoc.getNoteTarget();

        if(invoice.getStartDate() != null && invoice.getEndDate() != null && invoice.getEndDate().before(invoice.getStartDate())) {
            this.errorReporter.reportError("startDate", "subaward.error.end.date.greater.than.start", new String[0]);
        }

        if(invoice.getAmountReleased() != null) {
            if(invoice.getAmountReleased().isZero()) {
                this.errorReporter.reportError("amountReleased", "subaward.error.amount.released.zero", new String[0]);
            }

            SubAward subAward = invoice.getSubAward();
            if(subAward.getSubAwardAmountReleasedList().contains(invoice)) {
                subAward.getSubAwardAmountReleasedList().remove(invoice);
            }

            subAward.getSubAwardAmountReleasedList().add(invoice);
            getSubAwardService().getAmountInfo(subAward);
            if(invoice.getSubAward().getTotalAvailableAmount().isNegative()) {
                this.errorReporter.reportError("amountReleased", "subaward.error.amount.released.greater.than.obligated.amount", new String[0]);
            }
        }

        GlobalVariables.getMessageMap().clearErrorPath();
        return GlobalVariables.getMessageMap().getErrorMessages();
    }


    public void saveSubawardInvoice(MaintenanceDocument subawardInvoiceMaintenanceDoc){
        LOG.debug("Start saveSubawardInvoiceDocument "+subawardInvoiceMaintenanceDoc.getDocumentNumber());
        try {
            //TECH NOTE: subaward invoice is going to be saved in SubawardInvoiceMaintainable.prepareForSave() called before MaintDoc is saved..
            documentService.saveDocument(subawardInvoiceMaintenanceDoc);

            //route the doc to FINAL
            if ( subawardInvoiceMaintenanceDoc.getDocumentHeader().getWorkflowDocument().isInitiated()
                    || subawardInvoiceMaintenanceDoc.getDocumentHeader().getWorkflowDocument().isSaved()) {
                subawardInvoiceMaintenanceDoc = (MaintenanceDocument) documentService.routeDocument(subawardInvoiceMaintenanceDoc, "Route To Final", new ArrayList());
                //update status on invoice as well
                SubAwardAmountReleased invoice = (SubAwardAmountReleased) subawardInvoiceMaintenanceDoc.getNoteTarget();
                invoice.setInvoiceStatus(subawardInvoiceMaintenanceDoc.getDocumentHeader().getWorkflowDocument().getStatus().getCode());
                invoice.populateAttachment();
            }
            documentService.saveDocument(subawardInvoiceMaintenanceDoc);

        } catch (Exception e){
            LOG.error("Error when saving subawardInvoiceMaintenanceDoc id "+ subawardInvoiceMaintenanceDoc.getDocumentNumber(), e);
            throw new RuntimeException("Error when saving subawardInvoiceMaintenanceDoc id "+ subawardInvoiceMaintenanceDoc.getDocumentNumber(), e );
        }

        LOG.debug("Finished saveSubawardInvoice +"+subawardInvoiceMaintenanceDoc.getDocumentNumber());

    }

    public UASubawardInvoiceData updateSubawardInvoiceData(UASubawardInvoiceData invoiceData, MaintenanceDocument subawardInvoiceMaintenanceDoc){
        LOG.debug("Start updateSubawardInvoiceData for GLEntryId="+invoiceData.getEntryId());

        SubAwardAmountReleased invoice = (SubAwardAmountReleased)subawardInvoiceMaintenanceDoc.getNoteTarget();
        invoiceData.setSubAwardId(invoice.getSubAwardId());
        invoiceData.setSubAwardAmtReleasedId(invoice.getSubAwardAmtReleasedId());
        invoiceData.setInvoiceDocumentNumber(subawardInvoiceMaintenanceDoc.getDocumentNumber());
        invoiceData.setImportedDate(dateTimeService.getCurrentSqlDate());

        businessObjectService.save(invoiceData);

        LOG.debug("FINISH updateSubawardInvoiceData for GLEntryId="+invoiceData.getEntryId());
        return invoiceData;
    }


    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public SubAwardService getSubAwardService() {
        if (subAwardService == null) {
            subAwardService = (SubAwardService) KraServiceLocator.getService(SubAwardService.class);
        }
        return subAwardService;
    }

    public DictionaryValidationService getDictionaryValidationService() {
        if ( dictionaryValidationService == null ){
            dictionaryValidationService = (DictionaryValidationService)KraServiceLocator.getService(DictionaryValidationService.class);
        }
        return dictionaryValidationService;
    }

}
