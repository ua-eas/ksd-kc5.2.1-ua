package edu.arizona.kra.irb.pdf.sql;

import edu.arizona.kra.irb.pdf.excel.ExcelAttachmentRecord;
import edu.arizona.kra.irb.pdf.excel.ExcelPSRowMapper;
import edu.arizona.kra.irb.pdf.excel.ExcelRowMapper;
import edu.arizona.kra.irb.pdf.excel.ExcelSummaryRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class SqlExecutor {
    private final JdbcTemplate jdbcTemplate;
    private int maxBatchInsert;


    public SqlExecutor() {
        String maxString = getKualiConfigurationService().getPropertyValueAsString("max.batch.insert");
        this.maxBatchInsert = Integer.parseInt(maxString);
        this.jdbcTemplate = new JdbcTemplate(getDataSource());
    }


    // For DDL
    public void execute(String sql) {
        jdbcTemplate.execute(sql);
    }


    public void insert(String sql, Object... args) {
        jdbcTemplate.update(sql, args);

    }


    public List<ExcelAttachmentRecord> findAllExcelRecords() {
        return jdbcTemplate.query(QueryConstants.FIND_ALL_EXCEL_ROWS, new ExcelRowMapper());
    }

    public List<ExcelSummaryRecord> findAllExcelPSRecords() {
        return jdbcTemplate.query(QueryConstants.FIND_ALL_EXCEL_ROWS, new ExcelPSRowMapper());
    }


    public Integer counExcelRecords() {
        return jdbcTemplate.queryForObject(QueryConstants.EXCEL_RECORD_COUNT, Integer.class);
    }


    /*
     * Return all unique protocol numbers from a given table/view
     */
    public List<String> queryForProtocolNumbers(String viewName) {
        // Had to do String.format() since JdbcTemplate.queryForList() was not hydrating '?' params
        String sql = String.format(QueryConstants.SELECT_PROTOCOL_NUMBERS_SQL, viewName);
        return jdbcTemplate.queryForList(sql, String.class);
    }


    public Blob getFileBlob(String selectFileBlobSql, String fileId) {
        return jdbcTemplate.queryForObject(selectFileBlobSql, Blob.class, fileId);
    }


    @SuppressWarnings("Convert2Lambda")
    public void batchInsertProtocolNumbers(Set<String> protocolNumbers) {
        jdbcTemplate.batchUpdate(
                QueryConstants.INSERT_SUBSET_PROTOCOL_NUMBER_SQL,
                protocolNumbers,
                2000,
                new ParameterizedPreparedStatementSetter<String>() {
                    @Override
                    public void setValues(PreparedStatement ps, String protcolNumber) throws SQLException {
                        ps.setString(1, protcolNumber);
                    }
                });
    }


    /*
     * The sql for this comes from the concrete ViewWorker classes, e.g.:
     *     "update tableName set isProcessed = 'Y' where primaryKey = pk"
     * Where tableName is the file's source table, and primaryKey is the
     * pk column name.
     */
    public void updateIsProcessed(String sql, String pk) {
        jdbcTemplate.update(sql, "Y", pk);
    }


    /*
     * Every saved file also generates a new row in the attachments_spreadsheet table.
     * Since EFS files are uniquely named, if the spreadsheet table has at least one row
     * for the file pk, then the file has already been copied to the EFS, and we don't
     * need to copy it in again. (Note: The ViewWorkers can generate many records that
     * reference the same file, we still only want to copy this out once).
     */
    @SuppressWarnings("ConstantConditions")
    public boolean isFileAlreadyOnEfs(String primaryPk) {
        int rowCount = jdbcTemplate.queryForObject(QueryConstants.COUNT_EXCEL_ROWS_FOR_FILE_PK_SQL, Integer.class, primaryPk);
        return rowCount > 0;
    }


    private DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
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
