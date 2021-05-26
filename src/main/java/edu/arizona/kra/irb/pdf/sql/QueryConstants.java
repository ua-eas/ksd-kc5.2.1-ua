package edu.arizona.kra.irb.pdf.sql;

public class QueryConstants {
    public static final String CREATE_SPREADSHEET_TABLE_SQL =
            "create table attachment_spreadsheet_sum ( \n" +
                    "    id                VARCHAR2(60)   PRIMARY KEY, \n" +
                    "    dest_type         VARCHAR2(20)   not null, \n" +
                    "    protocol_number   VARCHAR2(20)   not null, \n" +
                    "    huron_destination VARCHAR2(60)   not null, \n" +
                    "    dest_att_is_set   NUMBER         not null, \n" +
                    "    ui_filename       VARCHAR2(256)  not null, \n" +
                    "    sftp_path         VARCHAR2(512)  not null, \n" +
                    "    category          VARCHAR2(20)   not null, \n" +
                    "    version_id        VARCHAR2(255), \n" +
                    "    is_processed      VARCHAR2(10), \n" +
                    "    error             VARCHAR2(10), \n" +
                    "    document_id       VARCHAR2(10) \n" +
                    "    md5checksum       VARCHAR2(10) \n" +
                    ")";

    public static final String TRUNCATE_SPREADSHEET_TABLE_SQL = "truncate table attachment_spreadsheet_sum";

    public static final String INSERT_EXCEL_ROW =
            "insert into attachment_spreadsheet_sum \n" +
                "(id, dest_type, protocol_number, huron_destination, dest_att_is_set, ui_filename, sftp_path, category, md5checksum) \n" +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String FIND_ALL_EXCEL_ROWS = "select * from attachment_spreadsheet_sum";

    public static final String EXCEL_RECORD_COUNT = "select count(*) from attachment_spreadsheet_sum";

}
