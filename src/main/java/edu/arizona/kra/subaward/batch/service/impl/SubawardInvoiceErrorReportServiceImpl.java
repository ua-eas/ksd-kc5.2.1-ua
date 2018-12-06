package edu.arizona.kra.subaward.batch.service.impl;


import edu.arizona.kra.subaward.batch.InvoiceFeedConstants;
import edu.arizona.kra.subaward.batch.bo.UAGlEntry;
import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceError;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceErrorReportService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.arizona.kra.subaward.batch.InvoiceFeedConstants.GL_ENTRY_ID_KEY;
import static edu.arizona.kra.subaward.batch.InvoiceFeedConstants.SIF_JOB_EXECUTION_ID_KEY;


/**
 * Error reporting Service Implementation that will write the errors to the error table
 * Created by nataliac on 10/31/18.
 */
public class SubawardInvoiceErrorReportServiceImpl implements SubawardInvoiceErrorReportService {

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;

    /**
     * Method that saves the occured exception with the given message
     */
    public void recordError(Long executionId, String message, Exception e){
        UASubawardInvoiceError error = new UASubawardInvoiceError();
        error.setExecutionId(executionId);
        error.setErrorMessage(message + e.toString());
        error.setErrorDate(dateTimeService.getCurrentSqlDate());
        businessObjectService.save( error );
    }


    /**
     * Method that saves the a duplicate imported GL entry error including the data in the duplicate row.
     */
    public void recordDuplicateRowError(Long executionId, UAGlEntry uaGlEntry){
        UASubawardInvoiceError error = new UASubawardInvoiceError();
        error.setExecutionId(executionId);
        error.setErrorMessage(InvoiceFeedConstants.DUPLICATE_GLENTRY_ERROR_MSG);
        error.setGlEntryId(uaGlEntry.getEntryId());
        error.setGlEntryData(uaGlEntry.toString());
        error.setErrorDate(dateTimeService.getCurrentSqlDate());
        businessObjectService.save( error );
    }

    /**
     * Method that counts all errors linked to an execution instance of the Subaward Invoice Feed job.
     */
    public int findJobRunErrorCount(Long executionId){
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put( SIF_JOB_EXECUTION_ID_KEY, executionId );

        return businessObjectService.countMatching(UASubawardInvoiceError.class, primaryKeys );
    }


    /**
     * Method that retrieves all errors linked to a instance of the Subaward Invoice Feed job.
     */
    public List<UASubawardInvoiceError> findJobRunErrors(Long executionId){
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put( SIF_JOB_EXECUTION_ID_KEY, executionId );

        return (List<UASubawardInvoiceError>) businessObjectService.findMatching( UASubawardInvoiceError.class, primaryKeys );
    }


    /**
     * Method that saves the occurred exception with the given message
     */
    public void recordError(Long executionId, String message, Long glEntryId, Long subawardId, Integer subawardAmtRelId, String invoiceDocNumber){
        UASubawardInvoiceError error = new UASubawardInvoiceError();
        error.setExecutionId(executionId);
        error.setErrorMessage(message);
        error.setGlEntryId(glEntryId);
        if ( glEntryId!=null ){
            Map<String, Object> primaryKeys = new HashMap<String, Object>();
            primaryKeys.put( GL_ENTRY_ID_KEY, glEntryId );

            UAGlEntry glEntry =  (UAGlEntry) businessObjectService.findByPrimaryKey( UAGlEntry.class, primaryKeys );
            if ( glEntry !=null ){
                error.setGlEntryData( glEntry.toString());
            }
        }
        error.setSubawardId(subawardId);
        error.setSubAwardAmtReleasedId(subawardAmtRelId);
        error.setInvoiceDocumentNumber(invoiceDocNumber);
        error.setErrorDate(dateTimeService.getCurrentSqlDate());
        businessObjectService.save( error );
    }



    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
