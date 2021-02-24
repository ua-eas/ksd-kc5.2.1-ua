package edu.arizona.kra.irb.pdf;

import edu.arizona.kra.irb.excel.ExcelDbAgent;
import edu.arizona.kra.irb.sql.SqlExecutor;
import edu.arizona.kra.irb.efs.EfsAgent;
import java.util.Properties;
import edu.arizona.kra.irb.props.PropertyLoader;
import edu.arizona.kra.irb.sql.enums.Category;

import edu.arizona.kra.irb.pdf.sftp.ProtocolPdfFile;
import edu.arizona.kra.irb.pdf.sftp.SftpTransferAgent;
import edu.arizona.kra.irb.sql.enums.Column;
import edu.arizona.kra.irb.sql.enums.HuronDestination;
import org.apache.log4j.Logger;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.irb.Protocol;
import org.kuali.kra.irb.actions.print.ProtocolPrintType;
import org.kuali.kra.irb.actions.print.ProtocolPrintingService;
import org.kuali.kra.printing.Printable;
import org.kuali.kra.printing.PrintingException;
import org.kuali.kra.printing.print.AbstractPrint;
import org.kuali.kra.proposaldevelopment.bo.AttachmentDataSource;
import org.kuali.kra.protocol.actions.print.ProtocolSummaryPrintOptions;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ProtocolPdfWorker extends Thread {
    private static final Logger LOG = Logger.getLogger(ProtocolPdfWorker.class);

    private final int workerId;
    private final Set<String> protocolNumbers;
    private final UserSession userSession;
    private final SftpTransferAgent sftpTransferAgent;
    private BusinessObjectService businessObjectService;
    private ProtocolPrintingService protocolPrintingService;


    private final ExcelDbAgent excelDbAgent;
    private final EfsAgent efsAgent;
    private final int maxFailures;
    private int processedCount;
    private int numFailures;
    private long unprocessedCount;
    protected SqlExecutor sqlExecutor;
    private ResultSet resultSet;
    private String currentEfsFilePath;
    private final String sftpRootDir;
    private final String efsRootDir;
    private boolean efsWriteModeIsOn;

    public String uiFileName;


    public String getCategory() {
        return Category.Other.getDescription();
    }


    public String getColumnValue(Column column) {
        return getColumnValue(column.toString());
    }
//
//    public abstract String getColumnValue(Column protocolNumber);

//    protected abstract String getColumnValue(String toString);

    protected String getColumnValue(String columnName) {
        String value;
        try {
            value = resultSet.getString(columnName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return value;
    }


    public String getSourceUuid() {
        return "PS" + getColumnValue(Column.PROTOCOL_NUMBER);
    }

    public String getFilePrimaryKey() {
        return getColumnValue(Column.PROTOCOL_NUMBER);
    }

    public String getFileUuid() {
        // 'PS' indicates this came from the PROTOCOL SUMMARIES Front end
        String filePk = getFilePrimaryKey();
        return "PS" + filePk;
    }

    String destType = "_IRBProtocolSummaries";

    public String getHuronDestination() {
        return HuronDestination.HistoricalDocuments.getDestination();
    }

    public String getUiFileName() {
        return this.uiFileName;
    }


    public void setUiFileName(String uiFileName) {
        this.uiFileName = uiFileName;
    }

    public String getUniqueFileName() {
        return String.format("%s %s", getFileUuid(), getDbFileName());
    }

    public String getDbFileName() {
        return getColumnValue(Column.FILE_NAME);
    }

    public String getSftpRootDir() {
        return sftpRootDir;
    }

    public String getEfsRootDir() {
        return efsRootDir;
    }

    private void setEfsWriteMode(String modeString) {
        this.efsWriteModeIsOn = modeString.equals("on");
    }

    public String getSftpFilePath() {
        String uniqueFileName = getUniqueFileName();
        String efsRootDir = getEfsRootDir();
        String sftpRootDir = getSftpRootDir();

        return currentEfsFilePath.replace(efsRootDir, sftpRootDir) + File.separator + uniqueFileName;
    }



    public ProtocolPdfWorker(int workerId, Set<String> protocolNumbers, UserSession userSession) {
        this.workerId = workerId;
        this.protocolNumbers = protocolNumbers;
        this.userSession = userSession;
        this.sftpTransferAgent = new SftpTransferAgent();

        Properties properties = PropertyLoader.getProperties();
//        this.url = properties.getProperty("db.url");
//        this.user = properties.getProperty("db.user");
//        this.password = properties.getProperty("db.password");
        this.maxFailures = Integer.parseInt(properties.getProperty("max.skip"));
        this.sftpRootDir = properties.getProperty("sftp.root.dir");
        this.efsRootDir = properties.getProperty("efs.root.dir");
        this. processedCount = 1;
        this.numFailures = 0;
        this.excelDbAgent = new ExcelDbAgent();
        this.efsAgent = new EfsAgent();
        this.setEfsWriteMode(properties.getProperty("efs.write.mode"));
    }


    @Override
    public void run() {
        logInfo(String.format("Starting async processing with %d protocol numbers.", protocolNumbers.size()));

        // Since this is a new thread, we need to set UserSession into global scope again
        GlobalVariables.setUserSession(userSession);

        int processedCount = 0;
        int total = protocolNumbers.size();

        for (String protocolNumber : protocolNumbers) {
            logInfo(String.format("Processing protocol number '%s'", protocolNumber));
            Protocol protocol = getProtocol(protocolNumber);

            try {
                processProtocol(protocol);
            } catch (Throwable t) {
                logError(String.format("Unexpected issue, skipping protocol '%s'", protocolNumber), t);
            } finally {
                processedCount++;
                logInfo(String.format("Protocol processed, %d/%d left to go.", total - processedCount, total));
            }

        }

        logInfo("Completed all work, worker thread exiting.");
    }


    /*
     * Logic yanked from ProtocolProtocolActionsAction#printProtocolSelectedItems()
     */
    private void processProtocol(Protocol protocol) throws PrintingException {
        String protocolNumber = protocol.getProtocolNumber();
        long protocolId = protocol.getProtocolId();
        String dateString = getDateString(protocol);
        String filename = String.format("ProtocolSummary_%s_%s_%d.pdf", protocolNumber, dateString, protocolId);

        ProtocolPrintType printType = ProtocolPrintType.PROTOCOL_FULL_PROTOCOL_REPORT;
        String reportName = protocol.getProtocolNumber() + "-" + printType.getReportName();
        AttachmentDataSource dataStream = getProtocolPrintingService().print(reportName, getPrintArtifacts(protocol));

        if (dataStream.getContent() == null) {
            logWarn("AttachmentDataSource.getContent() is null for protocol: " + protocolNumber);
            return;
        }

        dataStream.setFileName(filename);
        pushToSftp(dataStream, protocol.getProtocolNumber());

        this.setUiFileName(reportName);

        excelDbAgent.writeToDb(this);
//        sqlExecutor.updateIsProcessed(getUpdateIsProcessedSql(), getFilePrimaryKey());
    }


    private String getDateString(Protocol protocol) {
        Date updateTimestamp = protocol.getUpdateTimestamp();
        LocalDate localDate = Instant.ofEpochMilli(updateTimestamp.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }


    private void pushToSftp(AttachmentDataSource attachmentDataSource, String protocolNumber) {
        logInfo(String.format("Pushing protocol %s to sftp server complete", protocolNumber));
        byte[] bytes = attachmentDataSource.getContent();
        ProtocolPdfFile pdfFile = new ProtocolPdfFile(attachmentDataSource.getFileName(), bytes);
        sftpTransferAgent.put(pdfFile);
        logInfo(String.format("Pushed protocol %s to sftp server complete", protocolNumber));
    }



    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<Printable> getPrintArtifacts(Protocol protocol) {
        ProtocolSummaryPrintOptions summaryOptions = new ProtocolSummaryPrintOptions();
        summaryOptions.setReviewComments(false);

        Map reportParameters = new HashMap();
        reportParameters.put(ProtocolSummaryPrintOptions.class, summaryOptions);

        org.kuali.kra.protocol.actions.print.ProtocolPrintType printType
                = org.kuali.kra.protocol.actions.print.ProtocolPrintType.valueOf("PROTOCOL_FULL_PROTOCOL_REPORT");
        AbstractPrint printable = (AbstractPrint)getProtocolPrintingService().getProtocolPrintable(printType);
        printable.setPrintableBusinessObject(protocol);
        printable.setReportParameters(reportParameters);

        List<Printable> printableArtifactList = new ArrayList<>();
        printableArtifactList.add(printable);

        return printableArtifactList;
    }


    private Protocol getProtocol(String protocolNumber) {
        Map<String,Object> keymap = new HashMap<>();
        keymap.put("protocolNumber", protocolNumber);
        return getBusinessObjectService().findByPrimaryKey(Protocol.class, keymap);
    }


    private void logInfo(String message) {
        LOG.info(String.format("workerId-%d: %s", workerId, message));
    }


    private void logWarn(String message) {
        LOG.warn(String.format("workerId-%d: %s", workerId, message));
    }


    private void logError(String message, Throwable t) {
        LOG.error(String.format("workerId-%d: %s", workerId, message), t);
    }


    private BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            this.businessObjectService = KraServiceLocator.getService(BusinessObjectService.class);
        }
        return businessObjectService;
    }


    private ProtocolPrintingService getProtocolPrintingService() {
        if (protocolPrintingService == null) {
            this.protocolPrintingService = KraServiceLocator.getService(ProtocolPrintingService.class);
        }
        return protocolPrintingService;
    }


}
