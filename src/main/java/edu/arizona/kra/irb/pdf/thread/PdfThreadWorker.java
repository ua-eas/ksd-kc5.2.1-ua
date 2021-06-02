package edu.arizona.kra.irb.pdf.thread;

import edu.arizona.kra.irb.pdf.utils.FileUtils;
import edu.arizona.kra.irb.pdf.utils.SqlUtils;
import org.apache.log4j.Logger;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.irb.Protocol;
import org.kuali.kra.irb.actions.print.ProtocolPrintType;
import org.kuali.kra.irb.actions.print.ProtocolPrintingService;
import org.kuali.kra.printing.Printable;
import org.kuali.kra.printing.PrintingException;
import org.kuali.kra.printing.print.AbstractPrint;
import org.kuali.kra.proposaldevelopment.bo.AttachmentDataSource;
import org.kuali.kra.protocol.actions.ProtocolActionBase;
import org.kuali.kra.protocol.actions.print.ProtocolSummaryPrintOptions;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static edu.arizona.kra.irb.pdf.PdfConstants.SHOULD_CREATE_EFS_FILES;
import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class PdfThreadWorker implements Runnable {
    private static final Logger LOG = Logger.getLogger(PdfThreadWorker.class);
    private static final String PS_NAME_FORMAT = "Protocol Summary Report %s.pdf";

    private final PdfThreadMaster pdfThreadMaster;
    private BusinessObjectService businessObjectService;
    private ProtocolPrintingService protocolPrintingService;
    private final boolean pushToEfs;
    private final int workerId;


    public PdfThreadWorker(int workerId, PdfThreadMaster pdfThreadMaster) {
        this.workerId = workerId;
        this.pdfThreadMaster = pdfThreadMaster;
        this.pushToEfs = getKualiConfigurationService().getPropertyValueAsBoolean(SHOULD_CREATE_EFS_FILES);
    }


    @Override
    public void run() {
        // Since this is a new thread, we need to set UserSession into global scope
        GlobalVariables.setUserSession(new UserSession(KRADConstants.SYSTEM_USER));

        BatchResult batchResult = new BatchResult();

        while (true) {
            PdfBatch pdfBatch = pdfThreadMaster.getNextPdfBatch(batchResult);
            if (pdfBatch.getProtocolNumbers().isEmpty()) {
                logInfo("Recieved PdfBatch with no more work, exiting.");
                break;
            }

            List<String> protocolNumbers = pdfBatch.getProtocolNumbers();
            String currentBucketPath = pdfBatch.getBucketPath();

            logInfo(String.format("Starting batch with %d protocol number(s).", protocolNumbers.size()));
            batchResult = new BatchResult();

            for (String protocolNumber : protocolNumbers) {
                logInfo(String.format("Processing protocol: %s", protocolNumber));

                if (!isValidProtocolNumber(protocolNumber)) {
                    continue;
                }

                try {
                    Protocol protocol = getProtocol(protocolNumber);
                    sortProtoclActions(protocol);
                    processProtocol(protocol, currentBucketPath);
                } catch (Throwable t) {
                    batchResult.addFailed(protocolNumber);
                    logError(String.format("Unexpected issue, skipping protocol '%s'", protocolNumber), t);
                    continue;
                }

                batchResult.incrementSuccess();
                logInfo("Processed protocol: " + protocolNumber);
            }//for

        }//while

        logInfo("Completed all work, worker thread exiting.");
    }


    public boolean isValidProtocolNumber(String protocolNumber) {
        if (protocolNumber.toLowerCase().contains("a") || protocolNumber.toLowerCase().contains("r")) {
            // These are amendment/revision protocols acting as an active parent, which is an invalid state,
            // the IRB office should clean these up before the final migration is started, warn for now and skip
            LOG.warn(String.format("Parent protocol is invalid amendment/revision type, skipping protocol '%s'", protocolNumber));
            return false;
        }
        return true;
    }


    /*
     * Descending sort so most recent date is at top
     */
    private void sortProtoclActions(Protocol protocol) {
        List<ProtocolActionBase> protocolActions = protocol.getProtocolActions();
        protocolActions.sort(Comparator.comparing(ProtocolActionBase::getActualActionDate).reversed());
    }


    /*
     * Logic yanked from ProtocolProtocolActionsAction#printProtocolSelectedItems()
     */
    private void processProtocol(Protocol protocol, String currentBucketPath) throws PrintingException {
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
        String efsFilePath = currentBucketPath + File.separator + filename;

        pushToEfs(attachmentDataSource, efsFilePath);
        createExcelRecord(attachmentDataSource, protocolNumber, efsFilePath);
    }


    private void createExcelRecord(AttachmentDataSource attachmentDataSource, String protocolNumber, String fullEfsFilePath) {
        String id = UUID.randomUUID().toString();
        String fileName = attachmentDataSource.getFileName();
        SqlUtils.writeToDb(id, protocolNumber, fileName, fullEfsFilePath);
    }


    private void pushToEfs(AttachmentDataSource attachmentDataSource, String efsFilePath) {
        if (pushToEfs) {
            byte[] bytes = attachmentDataSource.getContent();
            FileUtils.pushFileToEfs(bytes, efsFilePath);
        } else {
            LOG.info("EFS writing turned off, skipped writing file to: " + efsFilePath);
        }
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
