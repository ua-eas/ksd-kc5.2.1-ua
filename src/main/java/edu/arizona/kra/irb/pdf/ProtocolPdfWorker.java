package edu.arizona.kra.irb.pdf;

import edu.arizona.kra.irb.pdf.efs.EfsAgent;
import edu.arizona.kra.irb.pdf.excel.ExcelCreator;
import edu.arizona.kra.irb.pdf.excel.ExcelDbAgent;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class ProtocolPdfWorker extends Thread {
    private static final Logger LOG = Logger.getLogger(ProtocolPdfWorker.class);
    private static final String PS_NAME_FORMAT = "Protocol Summary Report %s.pdf";

    private final int workerId;
    private final Set<String> protocolNumbers;
    private final UserSession userSession;
    private BusinessObjectService businessObjectService;
    private ProtocolPrintingService protocolPrintingService;
    private final EfsAgent efsAgent;
    private final boolean pushToEfs;
    private final ExcelDbAgent excelDbAgent;
    private final ClassLoader classLoader;


    public ProtocolPdfWorker(int workerId, Set<String> protocolNumbers, UserSession userSession) {
        this.workerId = workerId;
        this.protocolNumbers = protocolNumbers;
        this.userSession = userSession;
        this.efsAgent = new EfsAgent();
        this.excelDbAgent = new ExcelDbAgent();
        this.classLoader = getClass().getClassLoader();
        this.pushToEfs = getKualiConfigurationService().getPropertyValueAsBoolean("create.efs.files");
    }


    @Override
    public void run() {
        logInfo(String.format("Starting async processing with %d protocol numbers.", protocolNumbers.size()));

        // Since this is a new thread, we need to set UserSession into global scope again
        GlobalVariables.setUserSession(userSession);

        int processedCount = 0;
        int total = protocolNumbers.size();

        for (String protocolNumber : protocolNumbers) {
            if (killSwitchClick()) {
                logInfo("Detected file 'kill-switch.txt' on classpath, stopping pdf loop.");
                break;
            }

            if (protocolNumber.toLowerCase().contains("a") || protocolNumber.toLowerCase().contains("r")) {
                // These are amendment/revision protocols acting as an active parent, which is an invalid state,
                // the IRB office should clean these up before the final migration is started, warn for now and skip
                LOG.warn(String.format("Parent protocol is invalid amendment/revision type, skipping protocol '%s'", protocolNumber));
                processedCount++;
                logInfo(String.format("Protocol skipped, %d/%d left to go.", total - processedCount, total));
                continue;
            }

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

        //TODO: This is not threadsafe, need to have a wrapper thread master to be
        // called by ProtocolPdfWriterServiceImpl which would spin off workers,
        // wait to join them, and then call ExcelCreator. We can't do that in
        // ProtocolPdfWriterServiceImpl since the request will time out, and won't
        // be able to tell UI if workers started ok.
        ExcelCreator excelCreator = new ExcelCreator();
        excelCreator.createAttachmentsSpreadsheet();

        logInfo("Completed all work, worker thread exiting.");
    }


    /*
     * Logic yanked from ProtocolProtocolActionsAction#printProtocolSelectedItems()
     */
    private void processProtocol(Protocol protocol) throws PrintingException {
        String protocolNumber = protocol.getProtocolNumber();
        String filename = String.format(PS_NAME_FORMAT, protocolNumber);

        ProtocolPrintType printType = ProtocolPrintType.PROTOCOL_FULL_PROTOCOL_REPORT;
        String reportName = protocol.getProtocolNumber() + "-" + printType.getReportName();
        AttachmentDataSource attachmentDataSource = getProtocolPrintingService().print(reportName, getPrintArtifacts(protocol));

        if (attachmentDataSource.getContent() == null) {
            logWarn("AttachmentDataSource.getContent() is null for protocol: " + protocolNumber);
            return;
        }

        attachmentDataSource.setFileName(filename);
        String fullEfsFilePath = pushToEfs(attachmentDataSource, protocol.getProtocolNumber());
        createExcelRecord(attachmentDataSource, protocolNumber, fullEfsFilePath);
    }

    private void createExcelRecord(AttachmentDataSource attachmentDataSource, String protocolNumber, String fullEfsFilePath) {
        String id = UUID.randomUUID().toString();
        String fileName = attachmentDataSource.getFileName();
        excelDbAgent.writeToDb(id, protocolNumber, fileName, fullEfsFilePath);
    }


    private String pushToEfs(AttachmentDataSource attachmentDataSource, String protocolNumber) {
        logInfo(String.format("Pushing protocol %s to efs", protocolNumber));
        byte[] bytes = attachmentDataSource.getContent();
        String fullEfsFilePath = efsAgent.pushFileToEfs(attachmentDataSource.getFileName(), bytes, pushToEfs);
        logInfo(String.format("Pushed protocol %s to efs complete", protocolNumber));

        return fullEfsFilePath;
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
        keymap.put("active", "Y");

        Collection<Protocol> protocols = getBusinessObjectService().findMatching(Protocol.class, keymap);
        if (protocols.size() != 1) {
            throw new RuntimeException("Unexpected number of protocols returned for protocol #"
                    + protocolNumber + ", size: " + protocols.size());
        }

        return protocols.iterator().next();
    }


    @SuppressWarnings("ConstantConditions")
    private boolean killSwitchClick() {
        File file;
        try {
            file = new File(classLoader.getResource("kill-switch.txt").getFile());
        } catch (NullPointerException e) {
            return false;
        }

        return file.exists();
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
