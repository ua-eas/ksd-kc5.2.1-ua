package edu.arizona.kra.subaward.batch.service.impl;

import edu.arizona.kra.subaward.batch.InvoiceFeedConstants;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceErrorReportService;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceFeedService;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nataliac on 9/18/18.
 */
public class SubawardInvoiceFeedServiceImpl implements SubawardInvoiceFeedService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubawardInvoiceFeedServiceImpl.class);



    List <String> runSchedule;
    List <Integer> dataIntervals;

    ParameterService parameterService;
    SubawardInvoiceErrorReportService subawardInvoiceErrorReportService;

    public boolean isSubawardInvoiceFeedEnabled(){
        if (parameterService.parameterExists(InvoiceFeedConstants.PARAM_NAMESPACE_SUBWAWARD, InvoiceFeedConstants.PARAM_COMPONENT_BATCH, InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_FEED_ENABLED )) {
            return parameterService.getParameterValueAsBoolean(InvoiceFeedConstants.PARAM_NAMESPACE_SUBWAWARD, InvoiceFeedConstants.PARAM_COMPONENT_BATCH, InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_FEED_ENABLED);
        }
        return false;
    }

    public List<String> getSubawardInvoiceFeedRunSchedule(){

        if (CollectionUtils.isEmpty( runSchedule)) {
            runSchedule = new ArrayList<>();

            if ( isSubawardInvoiceFeedEnabled() && parameterService.parameterExists(InvoiceFeedConstants.PARAM_NAMESPACE_SUBWAWARD, InvoiceFeedConstants.PARAM_COMPONENT_BATCH, InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_FEED_SCHEDULE ) ) {
                String schedules = parameterService.getParameterValueAsString(InvoiceFeedConstants.PARAM_NAMESPACE_SUBWAWARD, InvoiceFeedConstants.PARAM_COMPONENT_BATCH, InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_FEED_SCHEDULE );
                if (!StringUtils.isEmpty(schedules)){
                    runSchedule = Arrays.asList(schedules.split(";"));
                }
           }
        }
        return runSchedule;
    }

    public List<Integer> getSubwawardInvoiceFeedDataIntervalsDays(){
        if (CollectionUtils.isEmpty( dataIntervals)) {
            dataIntervals = new ArrayList<>();
            if ( isSubawardInvoiceFeedEnabled()  && parameterService.parameterExists(InvoiceFeedConstants.PARAM_NAMESPACE_SUBWAWARD, InvoiceFeedConstants.PARAM_COMPONENT_BATCH, InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_DATA_INTERVALS ) ) {
                String paramValue = parameterService.getParameterValueAsString(InvoiceFeedConstants.PARAM_NAMESPACE_SUBWAWARD, InvoiceFeedConstants.PARAM_COMPONENT_BATCH, InvoiceFeedConstants.PARAM_NAME_SUBAWARD_INVOICE_DATA_INTERVALS );

                if (!StringUtils.isEmpty(paramValue)){
                    List<String> intervals = Arrays.asList(paramValue.split(";"));
                    for (String interval:intervals){
                        try {
                            dataIntervals.add(Integer.parseInt(interval));
                        } catch (NumberFormatException e){
                            LOG.error("getSubwawardInvoiceFeedDataIntervalsDays: Could not parse integer from SUBAWARD_INVOICE_DATA_INTERVALS:"+paramValue+" SKIPPING...");
                            subawardInvoiceErrorReportService.recordError("getSubwawardInvoiceFeedDataIntervalsDays: Could not parse integer from SUBAWARD_INVOICE_DATA_INTERVALS:"+paramValue+" SKIPPING...", e);
                        }
                    }
                }
            }
        }
        return dataIntervals;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    public SubawardInvoiceErrorReportService getSubawardInvoiceErrorReportService() {
        if ( subawardInvoiceErrorReportService ==null ) {
            subawardInvoiceErrorReportService =   KraServiceLocator.getService(SubawardInvoiceErrorReportService.class);
        }
        return subawardInvoiceErrorReportService;
    }


}
