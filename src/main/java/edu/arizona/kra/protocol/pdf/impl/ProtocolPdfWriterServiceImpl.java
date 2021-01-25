package edu.arizona.kra.protocol.pdf.impl;

import edu.arizona.kra.protocol.pdf.ProtocolNumberDao;
import edu.arizona.kra.protocol.pdf.ProtocolPdfWorker;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.Set;

public class ProtocolPdfWriterServiceImpl {
    private BusinessObjectService businessObjectService;
    private ProtocolNumberDao protocolNumberDao;


    public void generateActiveProtocolPdfsToDisk() {
        Set<String> protocolNumbers = getProtocolNumberDao().getActiveProtocolNumbers();
        ProtocolPdfWorker protocolPdfWorker = new ProtocolPdfWorker(protocolNumbers, getBusinessObjectService());
        protocolPdfWorker.start(); // async, forked thread
    }


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    private ProtocolNumberDao getProtocolNumberDao() {
        return protocolNumberDao;
    }


    public void setProtocolIdDao(ProtocolNumberDao protocolNumberDao) {
        this.protocolNumberDao = protocolNumberDao;
    }
}
