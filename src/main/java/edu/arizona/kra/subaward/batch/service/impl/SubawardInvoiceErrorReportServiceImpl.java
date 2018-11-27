package edu.arizona.kra.subaward.batch.service.impl;

import edu.arizona.kra.subaward.batch.InvoiceFeedConstants;
import edu.arizona.kra.subaward.batch.bo.UAGlEntry;
import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceError;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceErrorReportService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.sql.Date;
import java.util.Calendar;

/**
 * Error reporting Service Implementation that will write the errors to the error table
 * Created by nataliac on 10/31/18.
 *  private Long entryId;
 private String organizationDocNumber;
 private String purchaseOrderNumber;
 private KualiDecimal amountReleased;
 private String comments;
 private Date financialDocumentReversalDate;
 private Date effectiveDate;
 private String financialObjectCode;
 private Long subAwardId;
 private Integer subAwardAmtReleasedId;
 private String invoiceDocumentNumber;
 private Date importedDate;
 */
public class SubawardInvoiceErrorReportServiceImpl implements SubawardInvoiceErrorReportService {

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;

    /**
     * Method that saves the occured exception with the given message
     */
    public void recordError(String message, Exception e){
        UASubawardInvoiceError error = new UASubawardInvoiceError();
        error.setErrorMessage(message + e.toString());
        error.setErrorDate(dateTimeService.getCurrentSqlDate());
        businessObjectService.save( error );
    }


    /**
     * Method that saves the a duplicate imported GL entry error including the data in the duplicate row.
     */
    public void recordDuplicateRowError(UAGlEntry uaGlEntry){
        UASubawardInvoiceError error = new UASubawardInvoiceError();
        error.setErrorMessage(InvoiceFeedConstants.DUPLICATE_GLENTRY_ERROR_MSG);
        error.setGlEntryId(uaGlEntry.getEntryId());
        error.setGlEntryData(uaGlEntry.toString());
        error.setErrorDate(dateTimeService.getCurrentSqlDate());
        businessObjectService.save( error );
    }


    /**
     * Method that saves the occured exception with the given message
     */
    public void recordError(String message, String glEntryId, String subawardId, String subawardAmtRelId, String invoiceDocNumber){
        //TODO UAR-2692 will implement this!
    }



    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
