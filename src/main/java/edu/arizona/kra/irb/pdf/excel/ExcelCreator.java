package edu.arizona.kra.irb.pdf.excel;

import edu.arizona.kra.irb.pdf.sql.SqlExecutor;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static edu.arizona.kra.irb.pdf.PdfConstants.*;
import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class ExcelCreator {
    private static final Logger LOG = Logger.getLogger(ExcelCreator.class);

    private final String workbookOutputFilePath;
    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;
    private final int selectCount;
    private final SqlExecutor sqlExecutor;
    private int rowIndex;
    private int rowsProcessed;


    public ExcelCreator() {
        String workbookTemplateFilePath = getKualiConfigurationService().getPropertyValueAsString(WORKBOOK_TEMPLATE_PATH);
        this.workbookOutputFilePath = getKualiConfigurationService().getPropertyValueAsString(WORKBOOK_OUTPUT_PATH);

        try {
            InputStream inputStream = getClass().getResourceAsStream(workbookTemplateFilePath);
            this.workbook = new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String tabName  = getKualiConfigurationService().getPropertyValueAsString(WORKBOOK_TAB_NAME);
        this.sheet = workbook.getSheet(tabName);
        this.rowIndex = Integer.parseInt(getKualiConfigurationService().getPropertyValueAsString(WORKBOOK_START_ROW));
        this.sqlExecutor = new SqlExecutor();
        this.rowsProcessed = 0;
        this.selectCount = sqlExecutor.counExcelRecords();
    }


    public void createAttachmentsSpreadsheet() {
        LOG.info(String.format("Processing %d spreadsheet records", selectCount));

        boolean createSpreadsheet = getKualiConfigurationService().getPropertyValueAsBoolean(SHOULD_CREATE_SHEET);
        if (!createSpreadsheet) {
            LOG.info("The 'create.excel.spreadsheet' is set to false, skipping spreadsheet creation.");
            return;
        }

        List<ExcelAttachmentRecord> records = sqlExecutor.findAllExcelRecords();
        for (ExcelAttachmentRecord record : records) {
            addRow(rowIndex, record);
            rowIndex++;
        }

        saveWorkBook();

        LOG.info("Spreadsheet processing complete");
    }


    private void addRow(int rowIndex, ExcelAttachmentRecord record) {
        Row sheetRow = sheet.createRow(rowIndex);
        List<String> columnValues = record.getColumnValues();
        for (int i = 0; i < columnValues.size(); i++) {
            Cell cell = sheetRow.createCell(i);
            cell.setCellValue(columnValues.get(i));
        }
        rowsProcessed++;
        LOG.info(String.format("Saved record %s, %d/%d records written", record.getId(), rowsProcessed, selectCount));
    }


    private void saveWorkBook() {
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(workbookOutputFilePath);
            workbook.write(fos);
            fos.close();
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

}
