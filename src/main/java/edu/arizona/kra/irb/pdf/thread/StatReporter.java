package edu.arizona.kra.irb.pdf.thread;

import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;


public class StatReporter implements Runnable{
    private static final Logger LOG = Logger.getLogger(StatReporter.class);

    private final StatCollector statCollector;
    private Thread worker;
    private final AtomicBoolean running;
    private final int interval;


    public StatReporter(StatCollector statCollector) {
        this.statCollector = statCollector;
        this.running = new AtomicBoolean(false);
        this.interval = 30*1000;//TODO:Make this configurable
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


    public void run() {
        running.set(true);

        while (running.get()) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e){
                LOG.warn("Recieved interupt!");
            }

            statCollector.reportStats();
        }
    }
}
