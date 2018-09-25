package edu.arizona.kra.subaward.batch.service.impl;

import edu.arizona.kra.subaward.batch.service.SubawardInvoiceFeedService;
import org.apache.cxf.common.util.CollectionUtils;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nataliac on 9/18/18.
 */
public class SubawardInvoiceFeedServiceImpl implements SubawardInvoiceFeedService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubawardInvoiceFeedServiceImpl.class);

    ParameterService parameterService;

    ArrayList <String> runSchedule;
    ArrayList <Integer> dataIntervals;

    public boolean isSubawardInvoiceFeedEnabled(){
        //TODO implement this!
        //dummy data!!!!!!!!!!!!!!!!!
        return true;
    }

    public List<String> getSubawardInvoiceFeedRunSchedule(){

        if (CollectionUtils.isEmpty( runSchedule)) {
            //TODO implement this! - read from param and parse
            //dummy data!!!!!!!!!!!!!!!!!
            runSchedule = new ArrayList<>();
            runSchedule.add("0 0 3 ? * MON *");
            runSchedule.add("0 0 3 ? * TUE-FRI *");
            runSchedule.add("0 */3 * ? * *");
        }
        return runSchedule;
    }

    public List<Integer> getSubwawardInvoiceFeedDataIntervalsDays(){
        //TODO implement this!
        //dummy data!!!!!!!!!!!!!!!!!
        if (CollectionUtils.isEmpty( dataIntervals)) {
            //TODO implement this! - read from param and parse
            //dummy data!!!!!!!!!!!!!!!!!
            dataIntervals = new ArrayList<>();
            dataIntervals.add(new Integer(3));
            dataIntervals.add(new Integer(1));
            dataIntervals.add(new Integer(1));
        }
        return dataIntervals;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
