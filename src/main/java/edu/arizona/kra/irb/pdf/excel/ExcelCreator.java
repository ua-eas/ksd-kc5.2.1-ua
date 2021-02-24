package edu.arizona.kra.irb.pdf.excel;

import edu.arizona.kra.irb.pdf.sql.SqlExecutor;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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
        String workbookTemplateFilePath = getKualiConfigurationService().getPropertyValueAsString("workbook.template.file.path");
        this.workbookOutputFilePath = getKualiConfigurationService().getPropertyValueAsString("workbook.output.file.path");

        try {
            InputStream inputStream = getClass().getResourceAsStream(workbookTemplateFilePath);
            this.workbook = new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String tabName  = getKualiConfigurationService().getPropertyValueAsString("workbook.tab.name");
        this.sheet = workbook.getSheet(tabName);
        this.rowIndex = Integer.parseInt(getKualiConfigurationService().getPropertyValueAsString("workbook.sheet.start.row"));
        this.sqlExecutor = new SqlExecutor();
        this.rowsProcessed = 0;
        this.selectCount = sqlExecutor.counExcelRecords();
    }


    public void createAttachmentsSpreadsheet() {
        LOG.info(String.format("Processing %d spreadsheet records", selectCount));

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
