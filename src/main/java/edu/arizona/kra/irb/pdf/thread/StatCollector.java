package edu.arizona.kra.irb.pdf.thread;

import com.google.common.base.Stopwatch;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;


public class StatCollector {
    private static final Logger LOG = Logger.getLogger(StatCollector.class);

    private volatile Stopwatch stopwatch;
    private StatReporter statReporter;
    private long totalNumProcessed;
    private long totalProcessedSuccess;
    private long totalProcessedFailed;
    private long unprocessedTotal;
    private final long numWorkers;


    public StatCollector(long numWorkers) {
        this.stopwatch = new Stopwatch();
        this.totalNumProcessed = 0;
        this.totalProcessedSuccess = 0;
        this.totalProcessedFailed = 0;
        this.numWorkers = numWorkers;
    }


    public void init() {
        // Weird bug where statReport would have an initialized
        // statCollector if start() called in constructor
        this.statReporter = new StatReporter();
        statReporter.setStatCollector(this);
        statReporter.start();
    }


    public void reportStats() {
        long elapsedMillis = getElapsedMillis();
        double throughput = getThroughputPerMinute(elapsedMillis);
        double singleThreadThroughput = throughput / (double) numWorkers;

        LOG.info("#### Protocol Summaries ##############################################################");
        LOG.info(String.format("#     Total processed: %d", totalNumProcessed));
        LOG.info(String.format("#          To process: %d", unprocessedTotal));
        LOG.info(String.format("#       Success count: %d", totalProcessedSuccess));
        LOG.info(String.format("#       Failure count: %d", totalProcessedFailed));
        LOG.info(String.format("#          Throughput: %.2f pdfs/min (%.2f pdfs/min/thread)", throughput, singleThreadThroughput));
        LOG.info(String.format("#        Time Elapsed: %s", formatMillis(elapsedMillis)));
        LOG.info(String.format("#           Time left: %s", getEstimatedTimeLeft(unprocessedTotal, throughput)));
        LOG.info("######################################################################################");
    }


    private String getEstimatedTimeLeft(long unprocessedTotal, double throughput) {
        long millisLeft = (long) (((double) unprocessedTotal / throughput) * (double) 1000 * (double) 60);

        return formatMillis(millisLeft);
    }


    private double getThroughputPerMinute(long runtimeMillis) {
        return ((double) totalNumProcessed / (double) runtimeMillis)
                * (double) 1000//to seconds
                * (double) 60;//to minutes
    }


    public void recordBatchProcessed(BatchResult batchResult, int unprocessedTotal, int currentFailedCount) {
        this.unprocessedTotal = unprocessedTotal;
        totalNumProcessed += batchResult.getTotalProcessed();
        totalProcessedSuccess += batchResult.getSuccessCount();
        totalProcessedFailed = currentFailedCount;
    }


    private long getElapsedMillis() {
        return stopwatch.elapsedMillis();
    }


    public void startStopwatch() {
        stopwatch.start();
        LOG.info("Started stopwatch.");
    }


    public void stopStopwatch() {
        stopwatch.stop();
        LOG.info("Stopped stopwatch.");
    }


    public void setUnprocessedTotal(long unprocessedTotal) {
        this.unprocessedTotal = unprocessedTotal;
    }


    public void processingComplete() {
        statReporter.stop();
        statReporter.interrupt();
    }


    private String formatMillis(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return String.format("%02dd %02dh %02dm %02ds", days, hours, minutes, seconds);
    }

}
