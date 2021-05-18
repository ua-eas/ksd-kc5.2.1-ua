package edu.arizona.kra.irb.pdf.excel;

import edu.arizona.kra.irb.pdf.sql.QueryConstants;
import edu.arizona.kra.irb.pdf.sql.SqlExecutor;
import edu.arizona.kra.irb.pdf.sql.enums.Category;
import edu.arizona.kra.irb.pdf.sql.enums.HuronDestination;

import static edu.arizona.kra.irb.pdf.PdfConstants.EFS_ROOT_DIR;
import static edu.arizona.kra.irb.pdf.PdfConstants.SFTP_ROOT_DIR;
import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class ExcelDbAgent {
    private final SqlExecutor sqlExecutor;
    private final String sftpRootDir;
    private final String efsRootDir;


    public ExcelDbAgent() {
        this.sqlExecutor = new SqlExecutor();
        this.sftpRootDir = getKualiConfigurationService().getPropertyValueAsString(SFTP_ROOT_DIR);
        this.efsRootDir = getKualiConfigurationService().getPropertyValueAsString(EFS_ROOT_DIR);
    }


    public void writeToDb(String id, String protocolNumber, String fileName, String fullEfsFilePath) {
        String destType = "_IRBSubmission";
        String huronDestination = HuronDestination.HistoricalDocuments.getDestination();
        int destAttrIsSet = 1;
        String fullSftpFilePath = fullEfsFilePath.replace(efsRootDir, sftpRootDir);
        String category = Category.Other.getDescription();

        sqlExecutor.insert(QueryConstants.INSERT_EXCEL_ROW,
                id, destType, protocolNumber, huronDestination, destAttrIsSet, fileName, fullSftpFilePath, category);
    }

}
