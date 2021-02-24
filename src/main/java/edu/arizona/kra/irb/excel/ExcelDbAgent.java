package edu.arizona.kra.irb.excel;

import edu.arizona.kra.irb.sql.QueryConstants;
import edu.arizona.kra.irb.sql.SqlExecutor;
import edu.arizona.kra.irb.sql.enums.Column;
//import edu.arizona.kc.thread.abstracts.AbstractViewWorker;
import edu.arizona.kra.irb.pdf.ProtocolPdfWorker;


public class ExcelDbAgent {
    private final SqlExecutor sqlExecutor;


    public ExcelDbAgent() {
        this.sqlExecutor = new SqlExecutor();
    }

    public void writeToDb(ProtocolPdfWorker viewWorker) {
        String id = viewWorker.getSourceUuid();
        String fileUuid = viewWorker.getFileUuid();
        String destType = "_IRBProtocolSummaries";
        String protocolNumber = getProtocolNumber(viewWorker);
        String huronDestination = viewWorker.getHuronDestination();
        int destAttrIsSet = 1;
        String uiFilenName = viewWorker.getUiFileName();
        String sftpFilePath = viewWorker.getSftpFilePath();
        String category = viewWorker.getCategory();

        sqlExecutor.insert(QueryConstants.INSERT_EXCEL_ROW,
                id, fileUuid, destType, protocolNumber, huronDestination, destAttrIsSet, uiFilenName, sftpFilePath, category);
    }


    /*
     * Strip off 'A00X'/'R00X' suffix if present
     */
    private String getProtocolNumber(ProtocolPdfWorker viewWorker) {
        String protocolNumber = viewWorker.getColumnValue(Column.PROTOCOL_NUMBER);

        int letterIndex = -1;
        if (protocolNumber.contains("A")) {
            letterIndex = protocolNumber.indexOf("A");
        } else if (protocolNumber.contains("R")) {
            letterIndex = protocolNumber.indexOf("R");
        }

        if (letterIndex < 0) {
            // Not an ammendment/revision
            return protocolNumber;
        }

        return protocolNumber.substring(0, letterIndex);
    }

//    public void writeToDb(AbstractViewWorker viewWorker) {
//        String id = viewWorker.getSourceUuid();
//        String fileUuid = viewWorker.getFileUuid();
//        String destType = "_IRBSubmission";
//        String protocolNumber = getProtocolNumber(viewWorker);
//        String huronDestination = viewWorker.getHuronDestination();
//        int destAttrIsSet = 1;
//        String uiFilenName = viewWorker.getUiFileName();
//        String sftpFilePath = viewWorker.getSftpFilePath();
//        String category = viewWorker.getCategory();
//
//        sqlExecutor.insert(QueryConstants.INSERT_EXCEL_ROW,
//                id, fileUuid, destType, protocolNumber, huronDestination, destAttrIsSet, uiFilenName, sftpFilePath, category);
//    }


//
//    /*
//     * Strip off 'A00X'/'R00X' suffix if present
//     */
//    private String getProtocolNumber(AbstractViewWorker viewWorker) {
//        String protocolNumber = viewWorker.getColumnValue(Column.PROTOCOL_NUMBER);
//
//        int letterIndex = -1;
//        if (protocolNumber.contains("A")) {
//            letterIndex = protocolNumber.indexOf("A");
//        } else if (protocolNumber.contains("R")) {
//            letterIndex = protocolNumber.indexOf("R");
//        }
//
//        if (letterIndex < 0) {
//            // Not an ammendment/revision
//            return protocolNumber;
//        }
//
//        return protocolNumber.substring(0, letterIndex);
//    }

}
