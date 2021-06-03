package edu.arizona.kra.irb.pdf;

import edu.arizona.kra.irb.pdf.thread.PdfThreadMaster;
import org.apache.log4j.Logger;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.io.File;


import static edu.arizona.kra.irb.pdf.PdfConstants.START_PROCESSING_FILE_PATH;

public class StartFilePoller implements Runnable {
    private static final Logger LOG = Logger.getLogger(StartFilePoller.class);


    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        String startFilePath = KraServiceLocator
                .getService(ConfigurationService.class).getPropertyValueAsString(START_PROCESSING_FILE_PATH);
        File startFile;

        while (true) {
            startFile = getStartFile(startFilePath);
            if (startFile != null && startFile.exists()) {
                LOG.info("Found start file: " + startFile.getAbsolutePath());
                break;
            }

            try {
                LOG.info("Did not find start file: " + startFilePath);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOG.warn("Thread unexpectedly woken up!");
            }
        }

        // Delete start file so next server restart doesn't auto-kickoff a processing run
        if (!startFile.delete()) {
            throw new RuntimeException("Could not delete start file!: " + startFile.getAbsolutePath());
        }

        // All set to do our work
        startProcessing();
    }


    private void startProcessing() {
        LOG.info("Starting PdfThreadMaster");
        PdfThreadMaster pdfThreadMaster = new PdfThreadMaster();
        Thread thread = new Thread(pdfThreadMaster);
        thread.start();
    }


    private File getStartFile(String startFilePath) {
        File startFile = null;
        try {
            startFile = new File(startFilePath);
        } catch (NullPointerException e) {
            LOG.info("Did not find start file: " + startFilePath);
        }

        return startFile;
    }

}
