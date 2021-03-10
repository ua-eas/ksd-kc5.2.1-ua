package edu.arizona.kra.irb.pdf;

import org.apache.log4j.Logger;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.KRADConstants;

import java.io.File;

public class StartFilePoller implements Runnable {
    private static final Logger LOG = Logger.getLogger(StartFilePoller.class);


    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        while (!startFileExists()) {
            LOG.info("Did not find 'start-protocol-pdf-worker.txt' on the classpath, sleeping.");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOG.warn("Thread unexpectedly woken up!");
            }
        }

        startPdfWorker();
    }


    private void startPdfWorker() {
        LOG.info("Detected 'start-protocol-pdf-worker.txt' on classpath, starting ProtocolPdfWriterService");
        UserSession userSession = new UserSession(KRADConstants.SYSTEM_USER);
        ProtocolPdfWriterService protocolPdfWriterService = KraServiceLocator.getService(ProtocolPdfWriterService.class);
        ProtocolPdfJobInfo protocolPdfJobInfo = protocolPdfWriterService.generateActiveProtocolPdfsToDisk(userSession);
        LOG.info("Service started: " + protocolPdfJobInfo);
    }


    @SuppressWarnings("ConstantConditions")
    private boolean startFileExists() {
        File file;
        try {
            file = new File(getClass().getClassLoader().getResource("start-protocol-pdf-worker.txt").getFile());
        } catch (NullPointerException e) {
            return false;
        }

        return file.exists();
    }

}
