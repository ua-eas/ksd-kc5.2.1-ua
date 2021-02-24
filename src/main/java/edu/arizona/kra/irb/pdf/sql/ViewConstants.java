package edu.arizona.kra.irb.pdf.sql;

public class ViewConstants {
    public static final String CREATE_CURRENT_DOCS_V_SQL =
            "CREATE OR REPLACE VIEW CURRENT_DOCS_V AS \n" +
                    "(SELECT AF.FILE_NAME, AF.FILE_ID, AF.CONTENT_TYPE, AF.IS_PROCESSED, AF.UPDATE_TIMESTAMP, " +
                    "PAP.TYPE_CD, PAP.PROTOCOL_NUMBER, PAP.DESCRIPTION AS PAP_DESC, PAP.PA_PROTOCOL_ID, PAT.DESCRIPTION AS PAT_DESC \n" +
                    "FROM PROTOCOL P \n" +
                    "JOIN PROTOCOL_ATTACHMENT_PROTOCOL PAP ON PAP.PROTOCOL_NUMBER = P.PROTOCOL_NUMBER \n" +
                    "JOIN ATTACHMENT_FILE AF ON AF.FILE_ID = PAP.FILE_ID \n" +
                    "JOIN PROTOCOL_TYPE PT ON P.PROTOCOL_TYPE_CODE = PT.PROTOCOL_TYPE_CODE \n" +
                    "JOIN PROTOCOL_STATUS PS ON P.PROTOCOL_STATUS_CODE = PS.PROTOCOL_STATUS_CODE \n" +
                    "JOIN PROTOCOL_ATTACHMENT_TYPE PAT ON PAT.TYPE_CD = PAP.TYPE_CD \n" +
                    "WHERE P.ACTIVE = 'Y' \n" +
                    "AND PT.DESCRIPTION NOT IN ('BAA','COI Management Plan','Data Use Agreement','Site Authorization') \n" +
                    "AND PS.DESCRIPTION NOT IN ('Abandoned', 'Closed by Investigator', 'Deleted', 'Not Human Subjects Research', " +
                    "'Recalled in Routing', 'Suspended by DSMB', 'Suspended by Investigator', 'Withdrawn'))";
    public static final String CREATE_AMENDMENTS_V_SQL =
            "create or replace view AMENDMENT_SUB_V as\n" +
            "(\n" +
                    "select \n" +
                    "P.protocol_number,\n" +
                    "PAP.description as PAP_DESC,\n" +
                    "to_char(AF.update_timestamp,'YYYY-MM-DD')as UPDATE_TIMESTAMP,AF.file_name,AF.file_id,\n" +
                    "PAP.pa_protocol_id,\n" +
                    "PAT.description as PAT_DESC\n" +
                    "from protocol P\n" +
                    "join protocol_attachment_protocol PAP on P.protocol_id = PAP.protocol_id_fk\n" +
                    "join attachment_file AF on PAP.file_id = AF.file_id\n" +
                    "join protocol_status PS on P.protocol_status_code = PS.protocol_status_code\n" +
                    "join protocol_type PT on P.protocol_type_code = PT.protocol_type_code\n" +
                    "join protocol_attachment_type PAT on PAP.type_cd=PAT.type_cd\n" +
                    "where P.protocol_number like '%A%'\n" +
                    "and PS.description not in ('Abandoned','Closed by Investigator','Deleted','Not Human Subjects Research','Recalled in Routing','Suspended by DSMB','Suspended by Investigator','Withdrawn') \n" +
                    "and PS.description ='Amendment Incorporated into Protocol'\n" +
                    "and PT.description not in ('BAA','COI Management Plan','Data Use Agreement','Site Authorization')\n" +
                    "and regexp_substr(P.protocol_number,'\\d*') in(\n" +
                    "select regexp_substr(P.protocol_number,'\\d*')\n" +
                    "from protocol P2\n" +
                    "where regexp_substr(P.protocol_number,'\\d*') is not null\n" +
                    "and P2.active='Y')\n" +
                    ")";// Name this AMMENDMENT_SUB_V;
    public static final String CREATE_REVISIONS_V_SQL =
            "create or replace view REVISION_SUB_V as\n" +
            "(\n" +
                    "select \n" +
                    "P.protocol_number,\n" +
                    "PAP.description as PAP_DESC,\n" +
                    "to_char(AF.update_timestamp,'YYYY-MM-DD')as UPDATE_TIMESTAMP,AF.file_name,AF.file_id,\n" +
                    "PAP.pa_protocol_id,\n" +
                    "PAT.description as PAT_DESC\n" +
                    "from protocol P\n" +
                    "join protocol_attachment_protocol PAP on P.protocol_id = PAP.protocol_id_fk\n" +
                    "join attachment_file AF on PAP.file_id = AF.file_id\n" +
                    "join protocol_status PS on P.protocol_status_code = PS.protocol_status_code\n" +
                    "join protocol_type PT on P.protocol_type_code = PT.protocol_type_code\n" +
                    "join protocol_attachment_type PAT on PAP.type_cd=PAT.type_cd\n" +
                    "and P.protocol_number like '%R%'\n" +
                    "and PS.description not in ('Abandoned','Closed by Investigator','Deleted','Not Human Subjects Research','Recalled in Routing','Suspended by DSMB','Suspended by Investigator','Withdrawn') \n" +
                    "and PS.description ='Renewal Incorporated into Protocol'\n" +
                    "and PT.description not in ('BAA','COI Management Plan','Data Use Agreement','Site Authorization')\n" +
                    "and regexp_substr(P.protocol_number,'\\d*') in(\n" +
                    "select regexp_substr(P.protocol_number,'\\d*')\n" +
                    "from protocol P2\n" +
                    "where regexp_substr(P.protocol_number,'\\d*') is not null\n" +
                    "and P2.active='Y')\n" +
                    ")";// Name this: REVISION_SUB_V;
    public static final String CREATE_ORIGINAL_SUB_V_SQL =
            "CREATE OR REPLACE VIEW ORIGINAL_SUB_V AS       \n" +
                    "(SELECT AF.*, PAP.TYPE_CD, PAP.PROTOCOL_NUMBER, PAP.DESCRIPTION, PAP.PA_PROTOCOL_ID \n" +
                    "FROM (SELECT DISTINCT PAP1.FILE_ID, PAP1.DESCRIPTION, PAP1.PA_PROTOCOL_ID, PAP1.PROTOCOL_ID_FK, \n" +
                    "       PAP1.PROTOCOL_NUMBER, PAP1.SEQUENCE_NUMBER , PAP1.ATTACHMENT_VERSION, \n" +
                    "       PAP1.TYPE_CD, PAP1.DOCUMENT_ID \n" +
                    "       FROM PROTOCOL_ATTACHMENT_PROTOCOL PAP1 \n" +
                    "       JOIN (SELECT FILE_ID, PROTOCOL_NUMBER, PA_PROTOCOL_ID, PROTOCOL_ID_FK, \n" +
                    "             MAX(SEQUENCE_NUMBER) AS MAXSEQ FROM PROTOCOL_ATTACHMENT_PROTOCOL \n" +
                    "             GROUP BY FILE_ID, PROTOCOL_NUMBER, PA_PROTOCOL_ID, PROTOCOL_ID_FK) PAP2 \n" +
                    "                  ON PAP2.FILE_ID = PAP1.FILE_ID \n" +
                    "                  AND PAP2.PROTOCOL_NUMBER = PAP1.PROTOCOL_NUMBER \n" +
                    "                  AND PAP2.PA_PROTOCOL_ID =  PAP1.PA_PROTOCOL_ID \n" +
                    "                  AND PAP2.PROTOCOL_ID_FK =  PAP1.PROTOCOL_ID_FK \n" +
                    "                  AND PAP2.MAXSEQ = PAP1.SEQUENCE_NUMBER) PAP \n" +
                    "JOIN ATTACHMENT_FILE AF ON AF.FILE_ID = PAP.FILE_ID  \n" +
                    "JOIN PROTOCOL P ON P.PROTOCOL_NUMBER = PAP.PROTOCOL_NUMBER AND PAP.PROTOCOL_ID_FK = P.PROTOCOL_ID  \n" +
                    "JOIN (SELECT MIN(P1.APPROVAL_DATE) AS MIN_APPROVAL_DATE, PA.PROTOCOL_ID \n" +
                    "        FROM PROTOCOL P1 \n" +
                    "        JOIN PROTOCOL_ACTIONS PA ON PA.PROTOCOL_NUMBER = P1.PROTOCOL_NUMBER  \n" +
                    "        WHERE PA.PROTOCOL_ACTION_TYPE_CODE IN ('201','204','205','206','210')   -- Approval Actions \n" +
                    "        AND P1.PROTOCOL_STATUS_CODE IN ('103','200','203','310')    -- Approval Statuses \n" +
                    "        GROUP BY PA.PROTOCOL_ID) MD ON P.APPROVAL_DATE = MD.MIN_APPROVAL_DATE AND P.PROTOCOL_ID = MD.PROTOCOL_ID \n" +
                    "JOIN PROTOCOL_TYPE PT ON P.PROTOCOL_TYPE_CODE = PT.PROTOCOL_TYPE_CODE     \n" +
                    "JOIN PROTOCOL_STATUS PS ON P.PROTOCOL_STATUS_CODE = PS.PROTOCOL_STATUS_CODE        \n" +
                    "WHERE P.ACTIVE = 'N' AND \n" +
                    "      PT.DESCRIPTION NOT IN ('BAA','COI Management Plan','Data Use Agreement','Site Authorization') \n" +
                    "      AND PS.DESCRIPTION NOT IN ('Abandoned', 'Closed by Investigator', 'Deleted', 'Not Human Subjects Research', \n" +
                    "                    'Recalled in Routing', 'Suspended by DSMB', 'Suspended by Investigator', 'Withdrawn')) ";
    public static final String CREATE_NOTIFY_IRB_V_SQL =
            "CREATE OR REPLACE VIEW NOTIFY_IRB_V AS \n" +
                    "(SELECT PSD.FILE_NAME, PSD.SUBMISSION_DOC_ID AS FILE_ID, PSD.CONTENT_TYPE, P.PROTOCOL_NUMBER," +
                    "P.PROTOCOL_ID, PS.SUBMISSION_ID, PS.SUBMISSION_DATE, PSD.IS_PROCESSED \n" +
                    "FROM PROTOCOL_SUBMISSION PS \n" +
                    "JOIN PROTOCOL_SUBMISSION_DOC PSD ON PSD.SUBMISSION_ID_FK = PS.SUBMISSION_ID \n" +
                    "JOIN PROTOCOL P on P.PROTOCOL_NUMBER = PSD.PROTOCOL_NUMBER \n" +
                    "JOIN PROTOCOL_TYPE PT ON P.PROTOCOL_TYPE_CODE = PT.PROTOCOL_TYPE_CODE \n" +
                    "JOIN PROTOCOL_STATUS PS ON P.PROTOCOL_STATUS_CODE = PS.PROTOCOL_STATUS_CODE \n" +
                    "WHERE P.ACTIVE = 'Y' \n" +
                    "AND PT.DESCRIPTION NOT IN ('BAA','COI Management Plan','Data Use Agreement','Site Authorization') \n" +
                    "AND PS.DESCRIPTION NOT IN ('Abandoned', 'Closed by Investigator', 'Deleted', 'Not Human Subjects Research', \n" +
                    "'Recalled in Routing', 'Suspended by DSMB', 'Suspended by Investigator', 'Withdrawn'))";
    public static final String CREATE_CORRESPONDENCE_V_SQL =
            "CREATE OR REPLACE VIEW CORRESPONDENCE_V AS \n" +
                    "(SELECT PC.ID, PC.ID as FILE_ID, PC.PROTOCOL_ID, PC.UPDATE_TIMESTAMP, PCT.DESCRIPTION, P.PROTOCOL_NUMBER, " +
                    "PC.IS_PROCESSED, PT.DESCRIPTION AS PTYPE_DESC \n" +
                    "FROM PROTOCOL_CORRESPONDENCE PC \n" +
                    "JOIN PROTO_CORRESP_TYPE PCT ON PCT.PROTO_CORRESP_TYPE_CODE = PC.PROTO_CORRESP_TYPE_CODE \n" +
                    "JOIN PROTOCOL P ON P.PROTOCOL_NUMBER = PC.PROTOCOL_NUMBER \n" +
                    "JOIN PROTOCOL_TYPE PT ON P.PROTOCOL_TYPE_CODE = PT.PROTOCOL_TYPE_CODE \n" +
                    "JOIN PROTOCOL_STATUS PS ON P.PROTOCOL_STATUS_CODE = PS.PROTOCOL_STATUS_CODE \n" +
                    "WHERE P.ACTIVE = 'Y' \n" +
                    "AND PT.DESCRIPTION NOT IN ('BAA','COI Management Plan','Data Use Agreement','Site Authorization') \n" +
                    "AND PS.DESCRIPTION NOT IN ('Abandoned', 'Closed by Investigator', 'Deleted', 'Not Human Subjects Research', \n" +
                    "'Recalled in Routing', 'Suspended by DSMB', 'Suspended by Investigator', 'Withdrawn'))";
}
