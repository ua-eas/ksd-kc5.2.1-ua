package edu.arizona.kra.irb.pdf;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ProtocolPdfWorker extends Thread {
    private static final Logger LOG = Logger.getLogger(ProtocolPdfWorker.class);

    private final int workerId;
    private final Set<String> protocolNumbers;
    private final UserSession userSession;
    private BusinessObjectService businessObjectService;
    private ProtocolPrintingService protocolPrintingService;


    public ProtocolPdfWorker(int workerId, Set<String> protocolNumbers, UserSession userSession) {
        this.workerId = workerId;
        this.protocolNumbers = protocolNumbers;
        this.userSession = userSession;
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
    }


    /*
     * Logic yanked from ProtocolProtocolActionsAction#printProtocolSelectedItems()
     */
    private void processProtocol(Protocol protocol) throws PrintingException {
        String protocolNumber = protocol.getProtocolNumber();
        ProtocolPrintType printType = ProtocolPrintType.PROTOCOL_FULL_PROTOCOL_REPORT;
        String fileName = String.format("Protocol_Summary_Report_%s.pdf", protocolNumber);
        String reportName = protocol.getProtocolNumber() + "-" + printType.getReportName();
        AttachmentDataSource dataStream = getProtocolPrintingService().print(reportName, getPrintArtifacts(protocol));

        if (dataStream.getContent() == null) {
            logWarn("AttachmentDataSource.getContent() is null for protocol: " + protocolNumber);
            return;
        }

        dataStream.setFileName(fileName);
        streamToDisk(dataStream, protocol.getProtocolNumber());
    }


    public void streamToDisk(AttachmentDataSource attachmentDataSource, String protocolNumber) {
        //TODO: Find out where this is on a deployed KC EC2, possibly make a sub folder
        String rootPath = System.getProperty("java.io.tmpdir");
        String fullPath = rootPath + File.separator + attachmentDataSource.getFileName();

        byte[] bytes = attachmentDataSource.getContent();
        try (FileOutputStream fileOutputStream = new FileOutputStream(fullPath)) {
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
        } catch (IOException e) {
            logError(String.format("Could not write PDF to disk for protocol '%s': %s", protocolNumber, fullPath), e);
            return;
        }

        logInfo(String.format("Wrote protocol %s to disk: %s", protocolNumber, fullPath));
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
