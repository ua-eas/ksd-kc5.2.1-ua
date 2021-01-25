package edu.arizona.kra.irb.pdf.impl;

import edu.arizona.kra.irb.pdf.ProtocolNumberDao;
import edu.arizona.kra.irb.pdf.ProtocolPdfWorker;
import edu.arizona.kra.irb.pdf.ProtocolPdfWriterService;
import jdk.internal.org.jline.utils.Log;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.Set;


public class ProtocolPdfWriterServiceImpl implements ProtocolPdfWriterService {
    private BusinessObjectService businessObjectService;
    private ProtocolNumberDao protocolNumberDao;


    @Override
    public boolean generateActiveProtocolPdfsToDisk() {
        boolean startedOk = true;
        try {
            Set<String> protocolNumbers = getProtocolNumberDao().getActiveProtocolNumbers();
            ProtocolPdfWorker protocolPdfWorker = new ProtocolPdfWorker(protocolNumbers, getBusinessObjectService());
            protocolPdfWorker.start(); // async, forked thread
        } catch (Throwable t) {
            Log.error("Could not process PDFs!: " + t.getMessage(), t);
            startedOk = false;
        }

        return startedOk;
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
