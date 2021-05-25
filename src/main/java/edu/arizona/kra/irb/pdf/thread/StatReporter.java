package edu.arizona.kra.irb.pdf.thread;

import static edu.arizona.kra.irb.pdf.PdfConstants.REPORTING_INTERVAL_SECONDS;
import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class StatReporter extends Thread {
    private final StatCollector statCollector;
    private final int intervalMillis;


    public StatReporter(StatCollector statCollector) {
        int intervalSeconds = Integer.parseInt(getKualiConfigurationService().getPropertyValueAsString(REPORTING_INTERVAL_SECONDS));
        this.intervalMillis = intervalSeconds * 1000;
        this.statCollector = statCollector;
    }


    @SuppressWarnings("BusyWait")
    public void run() {
        while (true) {
            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e){
                return;
            }

            statCollector.reportStats();
        }

    }

}
