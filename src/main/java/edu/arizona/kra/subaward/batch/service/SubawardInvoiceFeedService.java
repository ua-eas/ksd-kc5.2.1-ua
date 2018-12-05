package edu.arizona.kra.subaward.batch.service;

import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceData;
import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceFeedSummary;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;

import java.util.List;
import java.util.Map;

/**
 * Created by nataliac on 9/18/18.
 */
public interface SubawardInvoiceFeedService {

    /**
     * Method that checks if the Subaward Invoice Feed Job is enabled
     * @return true if the parameter SubawardInvoiceFeedEnabled has Y value, otherwise returns false
     */
    boolean isSubawardInvoiceFeedEnabled();

    /**
     * Method that returns a list of cron run times for the Subaward Invoice Feed Job from the subAwardInvoiceFeedJobRunSchedule parameter
     * @return List<String> with cron expressions representing the schedule
     */
    List<String> getSubawardInvoiceFeedRunSchedule();

    /**
     * Method that returns a list of data interval in dqys for the job to bring back at each run from the current day. These values should be set in subAwardInvoiceFeedJobDataIntervalsDays parameter
     * Ex: 2 means it will return data from 2 days before till current date.
     * @return List<Integer> with number of days
     */
    List<Integer> getSubwawardInvoiceFeedDataIntervalsDays();


    /**
     * Method that returns a list of data interval in dqys for the job to bring back at each run from the current day. These values should be set in subAwardInvoiceFeedJobDataIntervalsDays parameter
     * Ex: 2 means it will return data from 2 days before till current date.
     * @return List<Integer> with number of days
     */
    String getSubwawardInvoiceFeedDestinationEmail();


    /**
     *
     * Method that creates and returns a UASubawardInvoiceSummary holding the statistics about a particular run of the SubawardInvoiceFeed job
     *
     * @return new instance of UASubawardInvoiceFeedSummary
     */
    UASubawardInvoiceFeedSummary createSubawardInvoiceFeedSummary();

    /**
     *
     * Method that saves the given UASubawardInvoiceSummary into the database
     *
     * @return UASubawardInvoiceFeedSummary
     */
    UASubawardInvoiceFeedSummary updateSubawardInvoiceFeedSummary(UASubawardInvoiceFeedSummary subawardInvoiceFeedSummary);


    /**
     * Instantiates a SubawardInvoiceMaintenanceDocument with the containing SubawardReleasedAmount object
     *
     * @return MaintenanceDocument corresponding to a SubawardInvoiceMaintenanceDocumentType
     */
    MaintenanceDocument createSubawardInvoiceDoc(UASubawardInvoiceData invoiceData,  SubAward subaward) throws WorkflowException;


    /**
     * Saves and routes to FINAL the SubawardInvoiceMaintenanceDocument with the containing SubawardReleasedAmount object
     */
    void saveSubawardInvoice(MaintenanceDocument subawardInvoiceMaintenanceDoc);


    /**
     * Validates the SubawardInvoiceMaintenanceDocument and the containing SubawardReleasedAmount object
     *
     *  @return Map - containing validation errors
     */
    Map validateSubawardInvoice(MaintenanceDocument subawardInvoiceMaintenanceDoc);


    /**
     * Updates the UASubawardInvoiceData with the freshly created SubawardAmountReleased info from the maintenance doc
     *
     *  @return UASubawardInvoiceData - updated objects
     */
    UASubawardInvoiceData updateSubawardInvoiceData(UASubawardInvoiceData invoiceData, MaintenanceDocument subawardInvoiceMaintenanceDoc);


}
