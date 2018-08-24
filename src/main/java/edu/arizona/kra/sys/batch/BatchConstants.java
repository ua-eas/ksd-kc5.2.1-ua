package edu.arizona.kra.sys.batch;

/**
 * Created by nataliac on 8/22/18.
 */
public final class BatchConstants {

    public static final String BATCH_SCHEDULE_STATUS_CHECK_INTERVAL = "STATUS_CHECK_INTERVAL";
    public static final String JOB_RUN_START_STEP = "JOB_RUN_START_STEP";
    public static final String JOB_RUN_END_STEP = "JOB_RUN_END_STEP";
    public static final String MASTER_JOB_NAME = "MASTER_JOB_NAME";
    public static final String STEP_RUN_PARM_NM = "RUN_IND";
    public static final String STEP_RUN_ON_DATE_PARM_NM = "RUN_DATE";
    public static final String STEP_USER_PARM_NM = "USER";

    public static final String SOFT_DEPENDENCY_CODE = "softDependency";
    public static final String HARD_DEPENDENCY_CODE = "hardDependency";

    public static final String SCHEDULE_JOB_NAME = "scheduleJob";

    public static final String PENDING_JOB_STATUS_CODE = "Pending";
    public static final String SCHEDULED_JOB_STATUS_CODE = "Scheduled";
    public static final String RUNNING_JOB_STATUS_CODE = "Running";
    public static final String SUCCEEDED_JOB_STATUS_CODE = "Succeeded";
    public static final String FAILED_JOB_STATUS_CODE = "Failed";
    public static final String CANCELLED_JOB_STATUS_CODE = "Cancelled";

    public static final String JOB_STATUS_PARAMETER = "status";

    public static final String SCHEDULED_GROUP = "scheduled";
    public static final String UNSCHEDULED_GROUP = "unscheduled";


    public static final String MONTH_DAY_YEAR_DATE_FORMAT = "MM/dd/yyyy";

    public static final int QUARTZ_SCHEDULER_START_DELAY_SEC = 120;
    public static final String UAR_SCHEDULER_NAME = "UARScheduler";

    //Keys
    public static final String REQUESTOR_EMAIL_ADDRESS_KEY = "requestorEmailAdress";
    public static final String MESSAGE_BATCH_FILE_LOG_EMAIL_BODY = "message.batch.log.email.body";
    public static final String REPORTS_DIRECTORY_KEY= "reports.directory";

    //Parameters
}
