package edu.arizona.kra.irb.pdf.impl;

import edu.arizona.kra.irb.pdf.ProtocolNumberDao;
import edu.arizona.kra.irb.pdf.ProtocolPdfWorker;
import edu.arizona.kra.irb.pdf.ProtocolPdfWriterService;
import jdk.internal.org.jline.utils.Log;

import java.util.Set;


public class ProtocolPdfWriterServiceImpl implements ProtocolPdfWriterService {
    private ProtocolNumberDao protocolNumberDao;


    @Override
    public boolean generateActiveProtocolPdfsToDisk() {
        boolean startedOk = true;
        try {
            Set<String> protocolNumbers = getProtocolNumberDao().getActiveProtocolNumbers();
            ProtocolPdfWorker protocolPdfWorker = new ProtocolPdfWorker(protocolNumbers);
            protocolPdfWorker.start(); // async, forked thread
        } catch (Throwable t) {
            Log.error("Could not process PDFs!: " + t.getMessage(), t);
            startedOk = false;
        }

        return startedOk;
    }


    private ProtocolNumberDao getProtocolNumberDao() {
        return protocolNumberDao;
    }


    public void setProtocolIdDao(ProtocolNumberDao protocolNumberDao) {
        this.protocolNumberDao = protocolNumberDao;
    }
}
