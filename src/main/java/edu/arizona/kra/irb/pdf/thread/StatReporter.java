package edu.arizona.kra.irb.pdf.thread;

import static edu.arizona.kra.irb.pdf.PdfConstants.REPORTING_INTERVAL_SECONDS;
import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class StatReporter extends Thread {
    private final PdfThreadMaster pdfThreadMaster;
    private final int intervalMillis;


    public StatReporter(PdfThreadMaster pdfThreadMaster) {
        int intervalSeconds = Integer.parseInt(getKualiConfigurationService().getPropertyValueAsString(REPORTING_INTERVAL_SECONDS));
        this.intervalMillis = intervalSeconds * 1000;
        this.pdfThreadMaster = pdfThreadMaster;
    }


    @SuppressWarnings("BusyWait")
    public void run() {
        while (true) {
            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e){
                return;
            }

            pdfThreadMaster.reportStats();
        }

    }

}
