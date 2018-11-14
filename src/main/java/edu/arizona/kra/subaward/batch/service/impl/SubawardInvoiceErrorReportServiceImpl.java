package edu.arizona.kra.subaward.batch.service.impl;

import edu.arizona.kra.subaward.batch.service.SubawardInvoiceErrorReportService;

/**
 * Error reporting Service Implementation that will write the errors to the error table
 * Created by nataliac on 10/31/18.
 */
public class SubawardInvoiceErrorReportServiceImpl implements SubawardInvoiceErrorReportService {

    /**
     * Method that saves the occured exception with the given message
     */
    public void recordError(String message, Exception e){
        //TODO UAR-2692 will implement this!
    }


    /**
     * Method that saves the occured exception with the given message
     */
    public void recordError(String message, Exception e, String glEntryID){
        //TODO UAR-2692 will implement this!
    }


    /**
     * Method that saves the occured exception with the given message
     */
    public void recordError(String message, String glEntryId, String subawardId, String subawardAmtRelId, String invoiceDocNumber){
        //TODO UAR-2692 will implement this!
    }

}
