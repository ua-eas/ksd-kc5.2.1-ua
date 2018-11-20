package edu.arizona.kra.sys.batch;

/**
 * Created by nataliac on 8/22/18.
 */
public final class BatchConstants {

    public static final String PARAM_NAME_BATCH_NODE_HOSTNAME="batchNodeHostname";
    public static final String PARAM_NAMESPACE_BATCH="KC-SYS";
    public static final String PARAM_BATCH_COMPNENT_CODE="Batch";


    public static final String BATCH_SCHEDULE_STATUS_CHECK_INTERVAL = "STATUS_CHECK_INTERVAL";
    public static final String JOB_RUN_START_STEP = "JOB_RUN_START_STEP";
    public static final String JOB_RUN_END_STEP = "JOB_RUN_END_STEP";
    public static final String MASTER_JOB_NAME = "MASTER_JOB_NAME";
    public static final String STEP_RUN_PARM_NM = "RUN_IND";
    public static final String STEP_RUN_ON_DATE_PARM_NM = "RUN_DATE";
    public static final String STEP_USER_PARM_NM = "USER";
    public static final String JOB_ENABLED_KEY = "JOB_ENABLED";
    public static final String JOB_DISABLED_VALUE = "N";

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

    public static final String NAMESPACE_KC_SYS="KC-SYS";
    public static final String BATCH_COMPONENT_CODE="Batch";
    public static final String PERMISSION_TEMPLATE_MODIFY_BATCH_JOB_NAMESPACE="KR-NS";
    public static final String PERMISSION_MODIFY_BATCH_JOB_NAMESPACE="KR-SYS";
    public static final String PERMISSION_NAME_MODIFY_BATCH_JOB="Modify Batch Job";

    public static final String VIEW_ACTION_NAME ="View";
    public static final String MODIFY_ACTION_NAME ="Modify";

    public static final String JOB_NAME_PARAMETER = "name";
    public static final String JOB_GROUP_PARAMETER = "group";
    public static final String START_STEP_PARAMETER = "startStep";
    public static final String END_STEP_PARAMETER = "endStep";
    public static final String START_TIME_PARAMETER = "startTime";
    public static final String EMAIL_PARAMETER = "emailAddress";

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
