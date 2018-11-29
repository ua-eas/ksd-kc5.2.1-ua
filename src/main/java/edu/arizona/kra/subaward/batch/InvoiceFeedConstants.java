package edu.arizona.kra.subaward.batch;

/**
 * Created by nataliac on 8/8/18.
 */
public final class InvoiceFeedConstants {

    public static final String PARAM_NAMESPACE_SUBWAWARD="KC-SUBAWARD";
    public static final String PARAM_COMPONENT_BATCH="Batch";

    public static final String PARAM_NAME_SUBAWARD_INVOICE_FEED_ENABLED="subAwardInvoiceFeedJobEnabled";
    public static final String PARAM_NAME_SUBAWARD_INVOICE_FEED_SCHEDULE="subAwardInvoiceFeedJobRunSchedule";
    public static final String PARAM_NAME_SUBAWARD_INVOICE_DATA_INTERVALS="subAwardInvoiceFeedJobDataIntervalsDays";
    public static final String PARAM_NAME_SUBAWARD_INVOICE_FEED_EMAIL="subAwardInvoiceFeedNotificationEmail";

    public static String CLEAR_GL_IMPORT_TABLE_QUERY="truncate table SUBAWARD_GL_IMPORT";

    public static String BI_GL_TABLE_SELECT_QUERY1="select * from KUALI_ADMIN.KF_UA_DPT_UAR_GL_ENTRY " +
            "WHERE ((FIN_OBJECT_CD IN ('3340', '3350')) AND (FIN_BALANCE_TYP_CD = 'AC') "+
            "AND (TRN_POST_DT BETWEEN timestamp '";
    public static String BI_GL_TABLE_SELECT_QUERY2="' AND timestamp '";
    public static String BI_GL_TABLE_SELECT_QUERY3= "') AND (UNIV_FISCAL_PRD_CD NOT IN ('BB', 'AB', 'CB')) AND (UNIV_FISCAL_YR > 2015))";


    public static final String COL_GL_ENTRY_ID  = "ENTRY_ID";
    public static final String COL_GL_UNIV_FISCAL_YR  = "UNIV_FISCAL_YR";
    public static final String COL_GL_FIN_COA_CD = "FIN_COA_CD";
    public static final String COL_GL_ACCOUNT_NBR = "ACCOUNT_NBR";
    public static final String COL_GL_SUB_ACCT_NBR = "SUB_ACCT_NBR";
    public static final String COL_GL_FIN_OBJECT_CD = "FIN_OBJECT_CD";
    public static final String COL_GL_FIN_SUB_OBJ_CD = "FIN_SUB_OBJ_CD";
    public static final String COL_GL_FIN_BALANCE_TYP_CD = "FIN_BALANCE_TYP_CD";
    public static final String COL_GL_FIN_OBJ_TYP_CD = "FIN_OBJ_TYP_CD";
    public static final String COL_GL_UNIV_FISCAL_PRD_CD ="UNIV_FISCAL_PRD_CD";
    public static final String COL_GL_FDOC_TYP_CD = "FDOC_TYP_CD";
    public static final String COL_GL_FS_ORIGIN_CD = "FS_ORIGIN_CD";
    public static final String COL_GL_FDOC_NBR = "FDOC_NBR";
    public static final String COL_GL_TRN_ENTR_SEQ_NBR = "TRN_ENTR_SEQ_NBR";
    public static final String COL_GL_TRN_LDGR_ENTR_DESC = "TRN_LDGR_ENTR_DESC";
    public static final String COL_GL_TRN_LDGR_ENTR_AMT = "TRN_LDGR_ENTR_AMT";
    public static final String COL_GL_TRN_DEBIT_CRDT_CD = "TRN_DEBIT_CRDT_CD";
    public static final String COL_GL_TRANSACTION_DT = "TRANSACTION_DT";
    public static final String COL_GL_ORG_DOC_NBR = "ORG_DOC_NBR";
    public static final String COL_GL_PROJECT_CD = "PROJECT_CD";
    public static final String COL_GL_ORG_REFERENCE_ID = "ORG_REFERENCE_ID";
    public static final String COL_GL_FDOC_REF_TYP_CD ="FDOC_REF_TYP_CD";
    public static final String COL_GL_FS_REF_ORIGIN_CD = "FS_REF_ORIGIN_CD";
    public static final String COL_GL_FDOC_REF_NBR = "FDOC_REF_NBR";
    public static final String COL_GL_FDOC_REVERSAL_DT = "FDOC_REVERSAL_DT";
    public static final String COL_GL_TRN_ENCUM_UPDT_CD = "TRN_ENCUM_UPDT_CD";
    public static final String COL_GL_TRN_POST_DT = "TRN_POST_DT";
    public static final String COL_GL_TIMESTAMP = "TIMESTAMP";

    public static final String INVOICE_FEED_TRIGGER_NAME = "SUBAWARD_INV_FEED_TRIGGER";
    public static final String INVOICE_FEED_JOB_NAME = "subawardInvoiceFeedJob";
    public static final String INVOICE_FEED_JOB_GROUP = "scheduled";

    public static final String DAYS_INTERVAL_KEY = "DAYS_INTERVAL";
    public static final int DEFAULT_DATA_INTERVAL_DAYS = 1;

    public static final String TIMESTAMP_ZERO = " 00:00:00";
    public static final String TIMESTAMP_EOD = " 23:59:59";

    public static final String DUPLICATE_GLENTRY_ERROR_MSG = "ERROR: Trying to import a duplicate GL Entry! Skipping row... ";


}
