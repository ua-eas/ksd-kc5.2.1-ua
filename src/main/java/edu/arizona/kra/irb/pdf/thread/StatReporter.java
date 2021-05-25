package edu.arizona.kra.irb.pdf.thread;

import java.util.concurrent.atomic.AtomicBoolean;

import static edu.arizona.kra.irb.pdf.PdfConstants.REPORTING_INTERVAL_SECONDS;
import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class StatReporter implements Runnable{
    private final StatCollector statCollector;
    private Thread worker;
    private final AtomicBoolean running;
    private final int intervalMillis;


    public StatReporter(StatCollector statCollector) {
        this.statCollector = statCollector;
        this.running = new AtomicBoolean(false);

        int intervalSeconds = Integer.parseInt(getKualiConfigurationService().getPropertyValueAsString(REPORTING_INTERVAL_SECONDS));
        this.intervalMillis = intervalSeconds *1000;
    }


    public void start() {
        worker = new Thread(this);
        worker.start();
    }


    public void stop() {
        running.set(false);
    }


    public void interrupt() {
        running.set(false);
        worker.interrupt();
    }


    @SuppressWarnings("BusyWait")
    public void run() {
        running.set(true);

        while (running.get()) {
            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e){
                return;
            }

            statCollector.reportStats();
        }

    }

}
