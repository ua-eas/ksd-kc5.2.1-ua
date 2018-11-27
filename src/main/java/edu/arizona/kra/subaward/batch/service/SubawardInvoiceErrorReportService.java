package edu.arizona.kra.subaward.batch.service;

import edu.arizona.kra.subaward.batch.bo.UAGlEntry;

/**
 * Created by nataliac on 10/31/18.
 */
public interface SubawardInvoiceErrorReportService {

    /**
     * Method that saves the occured exception with the given message
     */
    public void recordError(String message, Exception e);


    /**
     * Method that reports a duplicate row problem and the associated glEntry data
     */
    public void recordDuplicateRowError(UAGlEntry glEntry);


    /**
     * Method that saves the occured exception with the given message
     */
    public void recordError(String message, String glEntryId, String subawardId, String subawardAmtRelId, String invoiceDocNumber);

}
