package edu.arizona.kra.subaward.batch.service;

import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceFeedSummary;

import java.util.List;

/**
 * Created by nataliac on 9/18/18.
 */
public interface SubawardInvoiceFeedService {

    /**
     * Method that checks if the Subaward Invoice Feed Job is enabled
     * @return true if the parameter SubawardInvoiceFeedEnabled has Y value, otherwise returns false
     */
    public boolean isSubawardInvoiceFeedEnabled();

    /**
     * Method that returns a list of cron run times for the Subaward Invoice Feed Job from the subAwardInvoiceFeedJobRunSchedule parameter
     * @return List<String> with cron expressions representing the schedule
     */
    public List<String> getSubawardInvoiceFeedRunSchedule();

    /**
     * Method that returns a list of data interval in dqys for the job to bring back at each run from the current day. These values should be set in subAwardInvoiceFeedJobDataIntervalsDays parameter
     * Ex: 2 means it will return data from 2 days before till current date.
     * @return List<Integer> with number of days
     */
    public List<Integer> getSubwawardInvoiceFeedDataIntervalsDays();


    /**
     * Method that returns a list of data interval in dqys for the job to bring back at each run from the current day. These values should be set in subAwardInvoiceFeedJobDataIntervalsDays parameter
     * Ex: 2 means it will return data from 2 days before till current date.
     * @return List<Integer> with number of days
     */
    public String getSubwawardInvoiceFeedDestinationEmail();


    /**
     * TODO explain
     */
    public UASubawardInvoiceFeedSummary createSubawardInvoiceFeedSummary();

    /**
     * TODO explain
     */
    public UASubawardInvoiceFeedSummary updateSubawardInvoiceFeedSummary(UASubawardInvoiceFeedSummary subawardInvoiceFeedSummary);
}
