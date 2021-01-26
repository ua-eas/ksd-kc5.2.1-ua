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
import org.kuali.rice.krad.service.BusinessObjectService;

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

    private final Set<String> protocolNumbers;
    private BusinessObjectService businessObjectService;
    private ProtocolPrintingService protocolPrintingService;
    private int workerId;


    public ProtocolPdfWorker(int workerId, Set<String> protocolNumbers) {
        this.workerId = workerId;
        this.protocolNumbers = protocolNumbers;
    }


    @Override
    public void run() {
        LOG.info(String.format("workerId%d: Starting async processing with %d protocol numbers.", workerId, protocolNumbers.size()));

        int processedCount = 0;
        int total = protocolNumbers.size();

        for (String protocolNumber : protocolNumbers) {
            LOG.info(String.format("Processing protocol number '%s'", protocolNumber));
            Protocol protocol = getProtocol(protocolNumber);

            try {
                processProtocol(protocol);
            } catch (Throwable t) {
                LOG.error(String.format("Unexpected issue, skipping protocol '%s'", protocolNumber), t);
            } finally {
                processedCount++;
                LOG.info(String.format("Protocol processed, %d/%d left to go.", total - processedCount, total));
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
        AttachmentDataSource dataStream = getProtocolPrintingService().print(reportName, getPrintArtifacts());

        if (dataStream.getContent() == null) {
            LOG.warn("AttachmentDataSource.getContent() is null for protocol: " + protocolNumber);
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
            LOG.error(String.format("Could not write PDF to disk for protocol '%s': %s", protocolNumber, fullPath), e);
            return;
        }

        LOG.info(String.format("Wrote protocol %s to disk: %s", protocolNumber, attachmentDataSource));
    }



    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<Printable> getPrintArtifacts() {
        ProtocolSummaryPrintOptions summaryOptions = new ProtocolSummaryPrintOptions();
        summaryOptions.setReviewComments(false);

        Map reportParameters = new HashMap();
        reportParameters.put(ProtocolSummaryPrintOptions.class, summaryOptions);

        org.kuali.kra.protocol.actions.print.ProtocolPrintType printType
                = org.kuali.kra.protocol.actions.print.ProtocolPrintType.valueOf("PROTOCOL_FULL_PROTOCOL_REPORT");
        AbstractPrint printable = (AbstractPrint)getProtocolPrintingService().getProtocolPrintable(printType);
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


    public BusinessObjectService getBusinessObjectService() {
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
