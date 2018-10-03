package edu.arizona.kra.subaward.batch.service.impl;

import edu.arizona.kra.subaward.batch.service.SubawardInvoiceFeedService;
import edu.arizona.kra.sys.batch.CronTriggerDescriptor;
import edu.arizona.kra.sys.batch.TriggerDescriptor;
import edu.arizona.kra.sys.batch.service.BatchModuleService;
import edu.arizona.kra.sys.batch.service.impl.KcModuleServiceImpl;
import org.apache.cxf.common.util.CollectionUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.util.ArrayList;
import java.util.List;

import static edu.arizona.kra.subaward.batch.InvoiceFeedConstants.*;

/**
 * Created by nataliac on 9/18/18.
 */
public class SubawardModuleServiceImpl extends KcModuleServiceImpl implements BatchModuleService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubawardModuleServiceImpl.class);

    SubawardInvoiceFeedService subawardInvoiceFeedService;
    DateTimeService dateTimeService;

    private List<TriggerDescriptor> triggerDescriptors;

    @Override
    public List<TriggerDescriptor> getTriggerDescriptors(){
        if (CollectionUtils.isEmpty(triggerDescriptors)) {
            //only generate triggers for the SubawardInvoiceJob if the param says the job is enabled.
            if (subawardInvoiceFeedService.isSubawardInvoiceFeedEnabled()) {
                triggerDescriptors = new ArrayList<TriggerDescriptor>();
                List<Integer> dataIntervals = subawardInvoiceFeedService.getSubwawardInvoiceFeedDataIntervalsDays();
                int index = 0;
                for (String cronExpression : subawardInvoiceFeedService.getSubawardInvoiceFeedRunSchedule()) {

                    CronTriggerDescriptor triggerDescriptor = buildSubawardInvoiceFeedTriggerDescriptor(INVOICE_FEED_TRIGGER_NAME + "_" + (index + 1), cronExpression);

                    //set additional data on the trigger for the data interval to be passed to the job when fired
                    Integer dataInterval = DEFAULT_DATA_INTERVAL_DAYS;
                    try {
                        dataInterval = dataIntervals.get(index);
                    } catch (Exception e) {
                        //ignore and just use the default data interval.
                        dataInterval = DEFAULT_DATA_INTERVAL_DAYS;
                    }
                    triggerDescriptor.getTrigger().getJobDataMap().put(DAYS_INTERVAL_KEY, dataInterval);

                    triggerDescriptors.add(triggerDescriptor);

                    index++;
                }
            } else {
                LOG.info("Subaward Invoice feed DISABLED. Skipping adding trigers.");
            }
        }
        return triggerDescriptors;
    }

    private CronTriggerDescriptor buildSubawardInvoiceFeedTriggerDescriptor(String name, String cronExpression){
        CronTriggerDescriptor triggerDescriptor = new CronTriggerDescriptor();
        //set the default bean attributes
        triggerDescriptor.setBeanName(name);
        //set the default bean attributes for descriptor
        triggerDescriptor.setJobName(INVOICE_FEED_JOB_NAME);
        triggerDescriptor.setGroup(INVOICE_FEED_JOB_GROUP);
        triggerDescriptor.setDateTimeService(dateTimeService);
        triggerDescriptor.setCronExpression(cronExpression);

        return triggerDescriptor;
    }

    public void setSubawardInvoiceFeedService(SubawardInvoiceFeedService subawardInvoiceFeedService) {
        this.subawardInvoiceFeedService = subawardInvoiceFeedService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
