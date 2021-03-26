package edu.arizona.kra.irb.pdf.sql;

import edu.arizona.kra.irb.pdf.excel.ExcelAttachmentRecord;
import edu.arizona.kra.irb.pdf.excel.ExcelRowMapper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class SqlExecutor {
    private final JdbcTemplate jdbcTemplate;


    public SqlExecutor() {
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


    public Integer counExcelRecords() {
        return jdbcTemplate.queryForObject(QueryConstants.EXCEL_RECORD_COUNT, Integer.class);
    }


    private DataSource getDataSource() {
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
