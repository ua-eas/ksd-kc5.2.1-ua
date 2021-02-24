package edu.arizona.kra.irb.pdf.excel;

import edu.arizona.kra.irb.pdf.sql.QueryConstants;
import edu.arizona.kra.irb.pdf.sql.SqlExecutor;
import edu.arizona.kra.irb.pdf.sql.enums.Category;
import edu.arizona.kra.irb.pdf.sql.enums.HuronDestination;

import java.io.File;

import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class ExcelDbAgent {
    private final SqlExecutor sqlExecutor;
    private final String sftpRootDir;


    public ExcelDbAgent() {
        this.sqlExecutor = new SqlExecutor();
        this.sftpRootDir = getKualiConfigurationService().getPropertyValueAsString("sftp.root.dir");
    }


    public void writeToDb(String id, String protocolNumber, String fileName) {
        String destType = "_IRBSubmission";
        String huronDestination = HuronDestination.HistoricalDocuments.getDestination();
        int destAttrIsSet = 1;
        String sftpFilePath = sftpRootDir + File.separator + fileName;
        String category = Category.Other.getDescription();

        sqlExecutor.insert(QueryConstants.INSERT_EXCEL_ROW,
                id, destType, protocolNumber, huronDestination, destAttrIsSet, fileName, sftpFilePath, category);
    }

}
