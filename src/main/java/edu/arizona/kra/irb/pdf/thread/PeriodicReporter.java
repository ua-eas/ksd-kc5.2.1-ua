package edu.arizona.kra.irb.pdf.thread;

import static edu.arizona.kra.irb.pdf.PdfConstants.REPORTING_INTERVAL_SECONDS;
import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class PeriodicReporter extends Thread {
    private final StatCollector statCollector;
    private final int intervalMillis;


    public PeriodicReporter(StatCollector statCollector) {
        this.statCollector = statCollector;
        int intervalSeconds = Integer.parseInt(getKualiConfigurationService().getPropertyValueAsString(REPORTING_INTERVAL_SECONDS));
        this.intervalMillis = intervalSeconds * 1000;
    }


    @SuppressWarnings("BusyWait")
    @Override
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
