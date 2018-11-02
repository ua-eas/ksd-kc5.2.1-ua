package edu.arizona.kra.subaward.batch.service;

/**
 * Created by nataliac on 10/31/18.
 */
public interface SubawardInvoiceErrorReportService {

    /**
     * Method that saves the occured exception with the given message
     */
    public void recordError(String message, Exception e);


    /**
     * Method that saves the occured exception with the given message
     */
    public void recordError(String message, Exception e, String glEntryID);


    /**
     * Method that saves the occured exception with the given message
     */
    public void recordError(String message, String glEntryId, String subawardId, String subawardAmtRelId, String invoiceDocNumber);

}
