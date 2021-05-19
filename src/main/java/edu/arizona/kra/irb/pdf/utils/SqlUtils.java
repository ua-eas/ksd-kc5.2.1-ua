package edu.arizona.kra.irb.pdf.utils;

import edu.arizona.kra.irb.pdf.excel.ExcelAttachmentRecord;
import edu.arizona.kra.irb.pdf.excel.ExcelRowMapper;
import edu.arizona.kra.irb.pdf.sql.QueryConstants;
import edu.arizona.kra.irb.pdf.sql.enums.Category;
import edu.arizona.kra.irb.pdf.sql.enums.HuronDestination;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

import static edu.arizona.kra.irb.pdf.PdfConstants.*;
import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class SqlUtils {
    private static final JdbcTemplate jdbcTemplate;
    private static final String sftpRootDir;
    private static final String efsRootDir;
    static {
        jdbcTemplate = new JdbcTemplate(getDataSource());
        sftpRootDir = getKualiConfigurationService().getPropertyValueAsString(SFTP_ROOT_DIR);
        efsRootDir = getKualiConfigurationService().getPropertyValueAsString(EFS_ROOT_DIR);
    }


    public  static void execute(String sql) {
        jdbcTemplate.execute(sql);
    }


    public  static void insert(String sql, Object... args) {
        jdbcTemplate.update(sql, args);

    }


    public  static List<ExcelAttachmentRecord> findAllExcelRecords() {
        return jdbcTemplate.query(QueryConstants.FIND_ALL_EXCEL_ROWS, new ExcelRowMapper());
    }


    public  static Integer counExcelRecords() {
        return jdbcTemplate.queryForObject(QueryConstants.EXCEL_RECORD_COUNT, Integer.class);
    }


    public static void createSpreadsheetTable() {
        try {
            jdbcTemplate.execute(QueryConstants.CREATE_SPREADSHEET_TABLE_SQL);
        } catch (Exception e) {
            if (!e.getMessage().contains("ORA-00955")) {
                // This is not the "name is already used by an existing object" error, so fail fast
                throw new RuntimeException(e);
            }
        }

        boolean truncateSpreadsheetTableOnStart = getKualiConfigurationService().getPropertyValueAsBoolean(SHOULD_TRUNCATE);
        if (truncateSpreadsheetTableOnStart) {
            jdbcTemplate.execute(QueryConstants.TRUNCATE_SPREADSHEET_TABLE_SQL);
        }
    }


    public static void writeToDb(String id, String protocolNumber, String fileName, String fullEfsFilePath) {
        String destType = "_IRBSubmission";
        String huronDestination = HuronDestination.HistoricalDocuments.getDestination();
        int destAttrIsSet = 1;
        String fullSftpFilePath = fullEfsFilePath.replace(efsRootDir, sftpRootDir);
        String category = Category.Other.getDescription();

        insert(QueryConstants.INSERT_EXCEL_ROW,
                id, destType, protocolNumber, huronDestination, destAttrIsSet, fileName, fullSftpFilePath, category);
    }



    private static DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");

        String dbUrl = getKualiConfigurationService().getPropertyValueAsString("datasource.url");
        dataSource.setUrl(dbUrl);

        String dbUser = getKualiConfigurationService().getPropertyValueAsString("datasource.username");
        dataSource.setUsername(dbUser);

        String dbPass = getKualiConfigurationService().getPropertyValueAsString("datasource.password");
        dataSource.setPassword(dbPass);

        return dataSource;
    }

}
