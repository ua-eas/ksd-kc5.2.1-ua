package edu.arizona.kra.subaward.batch.service;

import edu.arizona.kra.subaward.batch.bo.UAGlEntry;
import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceError;
import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceFeedSummary;

import java.util.List;

/**
 * Created by nataliac on 10/31/18.
 */
public interface SubawardInvoiceErrorReportService {

    /**
     * Method that saves the occured exception with the given message
     */
    public void recordError(Long executionId, String message, Exception e);


    /**
     * Method that reports a duplicate row problem and the associated glEntry data
     */
    public void recordDuplicateRowError(Long executionId, UAGlEntry glEntry);


    /**
     * Method that saves the occured exception with the given message
     */
    public void recordError(Long executionId, String message, Long glEntryId, Long subawardId, Integer subawardAmtRelId, String invoiceDocNumber);


    /**
     * Method that returns the total error count for a particular execution of the Subaward Invoice Import Job
     */
    public int findJobRunErrorCount(Long executionId);

    /**
     * Method that returns all the errors occurred for a particular execution of the Subaward Invoice Import Job
     */
    public List<UASubawardInvoiceError> findJobRunErrors(Long executionId);



}
