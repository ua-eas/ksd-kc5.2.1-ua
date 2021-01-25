package edu.arizona.kra.protocol.pdf;

import org.apache.log4j.Logger;
import org.kuali.kra.irb.Protocol;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ProtocolPdfWorker extends Thread {
    private static final Logger LOG = Logger.getLogger(ProtocolPdfWorker.class);

    private final Set<String> protocolNumbers;
    private final BusinessObjectService businessObjectService;


    public ProtocolPdfWorker(Set<String> protocolNumbers, BusinessObjectService businessObjectService) {
        this.protocolNumbers = protocolNumbers;
        this.businessObjectService = businessObjectService;
    }


    @Override
    public void run() {
        LOG.info(String.format("Starting async processing with %d protocol numbers.", protocolNumbers.size()));

        int processedCount = 0;
        int total = protocolNumbers.size();
        for (String protocolNumber : protocolNumbers) {
            LOG.info(String.format("Processing protocol number '%s'", protocolNumber));
            Protocol protocol = getProtocol(protocolNumber);

            try {
                processProtocol(protocol);
            } catch (Throwable t) {
                LOG.error(String.format("Unexpected isssue, skipping protocol '%s': %s", protocolNumber, t.getMessage()));
            }

            processedCount++;
            LOG.info(String.format("Protocol processed, %d/%d left to go.", total - processedCount, total));
        }
    }


    /*
     * Logic yanked from ProtocolProtocolActionsAction#printProtocolSelectedItems()
     */
    private void processProtocol(Protocol protocol) {
        //TODO: implement from base service class in kc_project
    }


    private Protocol getProtocol(String protocolNumber) {
        Map<String,Object> keymap = new HashMap<>();
        keymap.put("protocolNumber", protocolNumber);
        return getBusinessObjectService().findByPrimaryKey(Protocol.class, keymap);
    }


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }
}
