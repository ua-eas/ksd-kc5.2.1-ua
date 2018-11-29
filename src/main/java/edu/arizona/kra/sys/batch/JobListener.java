package edu.arizona.kra.sys.batch;

import edu.arizona.kra.sys.batch.service.SchedulerService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.*;
import org.kuali.kra.service.KcEmailService;
import org.kuali.kra.util.EmailAttachment;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.*;


/**
 * nataliac on 8/22/18: Batch framework Imported and adapted from KFS
 **/
public class JobListener implements org.quartz.JobListener {
    private static final Logger LOG = Logger.getLogger(JobListener.class);
    protected static final String NAME = "jobListener";

    protected SchedulerService schedulerService;
    protected ConfigurationService configurationService;
    protected KcEmailService emailService;
    protected DateTimeService dateTimeService;

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException jobExecutionException) {
        if (jobExecutionContext.getJobInstance() instanceof Job) {
            try {
                if (!((Job) jobExecutionContext.getJobInstance()).isNotRunnable()) {
                    notify(jobExecutionContext, schedulerService.getStatus(jobExecutionContext.getJobDetail()));
                }
            } finally {
                completeLogging(jobExecutionContext);
            }
        }
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        if (jobExecutionContext.getJobInstance() instanceof Job) {
            schedulerService.initializeJob(jobExecutionContext.getJobDetail().getName(), (Job) jobExecutionContext.getJobInstance());
            initializeLogging(jobExecutionContext);
            // We only want to auto-cancel executions if they are part of a master scheduling job
            // Otherwise, this is a standalone job and should fire, regardless of prior status
            if (jobExecutionContext.getMergedJobDataMap().containsKey(BatchConstants.MASTER_JOB_NAME)) {
                if (schedulerService.shouldNotRun(jobExecutionContext.getJobDetail())) {
                    ((Job) jobExecutionContext.getJobInstance()).setNotRunnable(true);
                }
            } else {
                ((Job) jobExecutionContext.getJobInstance()).setNotRunnable(false);
            }
        }
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
        if (jobExecutionContext.getJobInstance() instanceof Job) {
            throw new UnsupportedOperationException("JobListener does not implement jobExecutionVetoed(JobExecutionContext jobExecutionContext)");
        }
    }

    protected void initializeLogging(JobExecutionContext jobExecutionContext) {
        try {
        	if(Logger.getRootLogger().getAppender("StdOut") == null) {
        		Appender appender = new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
            	appender.setName("StdOut");
            	Logger.getRootLogger().addAppender(appender);
        	}
            Calendar startTimeCalendar = dateTimeService.getCurrentCalendar();
            StringBuilder nestedDiagnosticContext = new StringBuilder(StringUtils.substringAfter(BatchSpringContext.getJobDescriptor(jobExecutionContext.getJobDetail().getName()).getNamespaceCode(), "-").toLowerCase()).append(File.separator).append(jobExecutionContext.getJobDetail().getName()).append("-").append(dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()));
            ((Job) jobExecutionContext.getJobInstance()).setNdcAppender(new FileAppender(Logger.getRootLogger().getAppender("StdOut").getLayout(), getLogFileName(nestedDiagnosticContext.toString())));
            ((Job) jobExecutionContext.getJobInstance()).getNdcAppender().addFilter(new NDCFilter(nestedDiagnosticContext.toString()));
            Logger.getRootLogger().addAppender(((Job) jobExecutionContext.getJobInstance()).getNdcAppender());
            NDC.push(nestedDiagnosticContext.toString());
        } catch (IOException e) {
            LOG.warn("Could not initialize special custom logging for job: " + jobExecutionContext.getJobDetail().getName(), e);
        }
    }

    private void completeLogging(JobExecutionContext jobExecutionContext) {
        ((Job) jobExecutionContext.getJobInstance()).getNdcAppender().close();
        Logger.getRootLogger().removeAppender(((Job) jobExecutionContext.getJobInstance()).getNdcAppender());
        NDC.pop();
    }

    protected String getLogFileName(String nestedDiagnosticContext) {
        return new StringBuilder(configurationService.getPropertyValueAsString(BatchConstants.REPORTS_DIRECTORY_KEY)).append(File.separator).append(nestedDiagnosticContext.toString()).append(".log").toString();
    }

    protected void notify(JobExecutionContext jobExecutionContext, String jobStatus) {
        try {
            String envName = configurationService.getPropertyValueAsString(BatchConstants.CONFIGURATION_ENVIRONMENT_KEY);
            String jobName = jobExecutionContext.getJobDetail().getName();
            String fromAddress =  emailService.getDefaultFromAddress();
            String toAddress =  jobExecutionContext.getMergedJobDataMap().containsKey(BatchConstants.REQUESTOR_EMAIL_ADDRESS_KEY)?jobExecutionContext.getMergedJobDataMap().getString(BatchConstants.REQUESTOR_EMAIL_ADDRESS_KEY):null;

            //TODO just for TESTING PURPOSES
            String testAddress = "nataliac@email.arizona.edu";

            Set<String> toAddresses =  new HashSet<>(1);
            if ( StringUtils.isNotEmpty(toAddress)){
                //TODO just for TESTING PURPOSES
                toAddresses.add(testAddress);
            }

            String subject = createEmailSubjectForJob(envName, jobName, jobStatus);
            String body = createEmailBodyForJob(jobName);

            EmailAttachment logAttachment = createEmailAttachment(jobName,getLogFileName(NDC.peek()));
            List<EmailAttachment> attachments = new ArrayList<EmailAttachment>(1);
            attachments.add(logAttachment);

            emailService.sendEmailWithAttachments(fromAddress, toAddresses, subject, null, null, body, false,  attachments);


//            String messageText = MessageFormat.format(configurationService.getPropertyValueAsString(BatchConstants.MESSAGE_BATCH_EMAIL_BODY_KEY), getLogFileName(NDC.peek()));
//            emailMessage.setMessage(messageText);
//            if (emailMessage.getToAddresses().size() > 0) {
//                emailMessage.setSubject(mailMessageSubject.toString());
//
//                //emailService.sendEmail(emailMessage,false);
//            }
        } catch (Exception iae) {
            LOG.error("Caught exception while trying to send job completion notification e-mail for " + jobExecutionContext.getJobDetail().getName(), iae);
        }
    }

    protected EmailAttachment createEmailAttachment(String jobName, String jobLogFilePath) throws IOException{
        EmailAttachment logAttachment = new EmailAttachment();
        logAttachment.setFileName(jobName+".log");
        logAttachment.setMimeType("text/plain");

        //TODO byte[] array = Files.readAllBytes(new File("/path/to/file").toPath());
        logAttachment.setContents( Files.readAllBytes(new File(jobLogFilePath).toPath()));
        return logAttachment;
    }

    protected String createEmailSubjectForJob(String environment, String jobName, String jobStatus){
        Object[] args = new String[]{environment.toUpperCase(), jobName.toUpperCase(),jobStatus};
        return MessageFormat.format(configurationService.getPropertyValueAsString(BatchConstants.MESSAGE_BATCH_EMAIL_SUBJECT_KEY), args);
    }

    protected String createEmailBodyForJob(String jobName){
        Object[] args = new String[]{dateTimeService.toDateTimeString(dateTimeService.getCurrentDate()), jobName.toUpperCase()};
        return MessageFormat.format(configurationService.getPropertyValueAsString(BatchConstants.MESSAGE_BATCH_EMAIL_BODY_KEY),  args);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setEmailService(KcEmailService emailService) {
        this.emailService = emailService;
    }
}
