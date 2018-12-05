package edu.arizona.kra.subaward.batch;

import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceError;
import edu.arizona.kra.subaward.batch.bo.UASubawardInvoiceFeedSummary;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceErrorReportService;
import edu.arizona.kra.subaward.batch.service.SubawardInvoiceFeedService;
import edu.arizona.kra.sys.batch.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.*;

import org.kuali.kra.infrastructure.KraServiceLocator;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


import java.text.MessageFormat;
import java.util.*;

import static edu.arizona.kra.subaward.batch.InvoiceFeedConstants.SIF_JOB_EXECUTION_ID_KEY;
import static edu.arizona.kra.subaward.batch.InvoiceFeedConstants.SIF_JOB_EXECUTION_SUMMARY_KEY;
import static edu.arizona.kra.subaward.batch.InvoiceFeedConstants.SIF_JOB_NAME;


/**
 * nataliac on 11/29/18: Job Listener that performs Error reporting and Job Status notifications specific to the Subaward Invoice Feed Job.
 **/
public class SubawardInvFeedJobListener extends JobListener {
    private static final Logger LOG = Logger.getLogger(SubawardInvFeedJobListener.class);

    protected SubawardInvoiceErrorReportService subawardInvoiceErrorService;
    protected SubawardInvoiceFeedService subawardInvoiceFeedService;


    @Override
    public String getName() {
        return InvoiceFeedConstants.SIF_JOB_LISTENER_NAME;
    }


    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        if ( SIF_JOB_NAME.equalsIgnoreCase(jobExecutionContext.getJobDetail().getName()) && !((Job) jobExecutionContext.getJobInstance()).isNotRunnable() ) {
            UASubawardInvoiceFeedSummary sifExecutionJobSummary = getSubawardInvoiceFeedService().createSubawardInvoiceFeedSummary();
            LOG.debug("Subaward Invoice Feed Job toBeExecuted: jobExecutionId="+sifExecutionJobSummary.getExecutionId());

            jobExecutionContext.getMergedJobDataMap().put(SIF_JOB_EXECUTION_ID_KEY, sifExecutionJobSummary.getExecutionId());
            jobExecutionContext.getMergedJobDataMap().put(SIF_JOB_EXECUTION_SUMMARY_KEY, sifExecutionJobSummary);
        }
    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException jobExecutionException) {
        if (SIF_JOB_NAME.equalsIgnoreCase(jobExecutionContext.getJobDetail().getName()) && !((Job) jobExecutionContext.getJobInstance()).isNotRunnable()) {
            UASubawardInvoiceFeedSummary sifExecutionJobSummary = null;
            try {
                sifExecutionJobSummary = (UASubawardInvoiceFeedSummary) jobExecutionContext.getMergedJobDataMap().get(SIF_JOB_EXECUTION_SUMMARY_KEY);
                LOG.debug("Subaward Invoice Feed Job jobWasExecuted: jobExecutionId=" + sifExecutionJobSummary.getExecutionId());
                sifExecutionJobSummary.setJobEndTime(dateTimeService.getCurrentSqlDate());
                sifExecutionJobSummary.setJobExecutionTime(jobExecutionContext.getJobRunTime());
                sifExecutionJobSummary.setJobStatus(schedulerService.getStatus(jobExecutionContext.getJobDetail()));

                sifExecutionJobSummary.setErrorCount(subawardInvoiceErrorService.findJobRunErrorCount(sifExecutionJobSummary.getExecutionId()) );

                if (!((Job) jobExecutionContext.getJobInstance()).isNotRunnable()) {
                    notify(jobExecutionContext, schedulerService.getStatus(jobExecutionContext.getJobDetail()), sifExecutionJobSummary);
                }
            } finally {
                if (sifExecutionJobSummary != null)
                    getSubawardInvoiceFeedService().updateSubawardInvoiceFeedSummary(sifExecutionJobSummary);
            }
        }
    }


    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
        //do nothing
    }


    protected void notify(JobExecutionContext jobExecutionContext, String jobStatus,  UASubawardInvoiceFeedSummary invoiceFeedSummary) {
        try {
            String envName = configurationService.getPropertyValueAsString(BatchConstants.CONFIGURATION_ENVIRONMENT_KEY);
            String jobName = jobExecutionContext.getJobDetail().getName();
            String fromAddress =  emailService.getDefaultFromAddress();
            String toAddress =  jobExecutionContext.getMergedJobDataMap().containsKey(BatchConstants.REQUESTOR_EMAIL_ADDRESS_KEY)?jobExecutionContext.getMergedJobDataMap().getString(BatchConstants.REQUESTOR_EMAIL_ADDRESS_KEY):null;

            Set<String> toAddresses =  new HashSet<>(1);
            if ( StringUtils.isNotEmpty(toAddress)){
                toAddresses.add(toAddress);
            }

            String subject = createEmailSubjectForJob(envName, jobName, jobStatus);
            String body = createEmailBodyForJob(jobName, invoiceFeedSummary, subawardInvoiceErrorService.findJobRunErrors(invoiceFeedSummary.getExecutionId()));


            emailService.sendEmail(fromAddress, toAddresses, subject, null, null, body, true);


        } catch (Exception iae) {
            LOG.error("Caught exception while trying to send job completion notification e-mail for " + jobExecutionContext.getJobDetail().getName(), iae);
        }
    }



    protected String createEmailSubjectForJob(String environment, String jobName, String jobStatus){
        Object[] args = new String[]{environment.toUpperCase(), jobName.toUpperCase(),jobStatus};
        return MessageFormat.format(configurationService.getPropertyValueAsString(InvoiceFeedConstants.EMAIL_SUBJECT_KEY), args);
    }

    protected String createEmailBodyForJob(String jobName, UASubawardInvoiceFeedSummary jobSummary, List<UASubawardInvoiceError> errors){
        Object[] args = new String[]{
                dateTimeService.toDateTimeString(dateTimeService.getCurrentDate()),
                jobSummary.getJobStatus().toUpperCase(),
                jobSummary.getJobStartTime()!=null?dateTimeService.toDateTimeString(jobSummary.getJobStartTime()):"",
                jobSummary.getJobEndTime()!=null?dateTimeService.toDateTimeString(jobSummary.getJobEndTime()):"",
                jobSummary.getJobEndTime()!=null?Long.toString(jobSummary.getJobExecutionTime()/60000):"",
                jobSummary.getImportIntervalStartDate()!=null?dateTimeService.toDateTimeString(jobSummary.getImportIntervalStartDate()):"",
                jobSummary.getImportIntervalEndDate()!=null?dateTimeService.toDateTimeString(jobSummary.getImportIntervalEndDate()):"",
                Integer.toString(jobSummary.getImportIntervalNbrOfDays()),
                Integer.toString(jobSummary.getGlEntriesImportCount()),
                Integer.toString(jobSummary.getSubawardInvoicesCount()),
                Integer.toString(jobSummary.getErrorCount())
        };

        StringBuffer sb = new StringBuffer( MessageFormat.format(configurationService.getPropertyValueAsString(InvoiceFeedConstants.EMAIL_BODY_KEY),  args) );
        if ( !errors.isEmpty()) {
            sb.append("<br> Errors: <br>");
            for(UASubawardInvoiceError error:errors){
                sb.append(error.getEmailStringErrorDesc());
                sb.append("<br>");
            }
            sb.append("<br>");
        } else {
            sb.append("Job execution completed without any errors. <br>");
        }
        return sb.toString();
    }


    public void setSubawardInvoiceErrorService(SubawardInvoiceErrorReportService subawardInvoiceErrorService) {
        this.subawardInvoiceErrorService = subawardInvoiceErrorService;
    }

    public SubawardInvoiceFeedService getSubawardInvoiceFeedService() {
        if (subawardInvoiceFeedService == null){
            subawardInvoiceFeedService = KraServiceLocator.getService(SubawardInvoiceFeedService.class);
        }
        return subawardInvoiceFeedService;
    }


}
