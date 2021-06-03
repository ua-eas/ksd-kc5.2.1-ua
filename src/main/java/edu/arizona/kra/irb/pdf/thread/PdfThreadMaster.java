package edu.arizona.kra.irb.pdf.thread;

import edu.arizona.kra.irb.pdf.ProtocolNumberDao;
import edu.arizona.kra.irb.pdf.excel.ExcelCreator;
import edu.arizona.kra.irb.pdf.utils.BucketHandler;
import edu.arizona.kra.irb.pdf.utils.FileUtils;
import edu.arizona.kra.irb.pdf.utils.SqlUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static edu.arizona.kra.irb.pdf.PdfConstants.*;


public class PdfThreadMaster implements Runnable {
    private static final Logger LOG = Logger.getLogger(PdfThreadMaster.class);

    private final BucketHandler bucketHandler;
    private final StatCollector statCollector;
    private final List<String> failedProtocolNumbers;
    private ConfigurationService kualiConfigurationService;
    private ProtocolNumberDao protocolNumberDao;
    private List<String> protocolNumbers;
    private final int numWorkerThreads;
    private final int totalProtocolCount;
    private final int numRetries;
    private final int batchSize;
    private int numProtocolsLeftToProcess;


    public PdfThreadMaster() {
        this.numWorkerThreads = Integer.parseInt(getKualiConfigurationService().getPropertyValueAsString(NUM_WORKER_THREADS));
        this.batchSize = Integer.parseInt(getKualiConfigurationService().getPropertyValueAsString(PROTOCOL_NUMBER_BATCH_SIZE));
        this.numRetries = Integer.parseInt(getKualiConfigurationService().getPropertyValueAsString(NUMBER_FAILURE_RETRIES));
        this.protocolNumbers = getProtocolNumbers();
        this.totalProtocolCount = protocolNumbers.size();
        this.failedProtocolNumbers = new ArrayList<>();
        this.bucketHandler = new BucketHandler();
        this.numProtocolsLeftToProcess = protocolNumbers.size();
        this.statCollector = new StatCollector(numWorkerThreads);
    }


    @Override
    public void run() {
        SqlUtils.createSpreadsheetTable();
        statCollector.start();
        processMainProtocolNumberList();
        processFailedProtocolNumbers();
        createSpreadsheet();
        FileUtils.createFinishFile(totalProtocolCount, true);
        statCollector.reportStats();

        if (failedProtocolNumbers.size() > 0) {
            LOG.error("Some protocols failed to process: " + failedProtocolNumbers);
        }

        LOG.info("All processing complete, exiting.");
    }


    private void processMainProtocolNumberList() {
        LOG.info("Processing main protocol number list.");
        executeThreading();
        LOG.info("Main list processing complete.");
    }


    private void processFailedProtocolNumbers() {
        LOG.info("Processing failed protocol numbers.");
        if (failedProtocolNumbers.isEmpty()) {
            LOG.info("Found no failed protocols to process.");
            return;
        }

        protocolNumbers = failedProtocolNumbers;
        numProtocolsLeftToProcess = failedProtocolNumbers.size();

        for (int attempts = 1; attempts <= numRetries; attempts++) {
            LOG.info(String.format("Starting attempt %d/%d at re-processing failed protocols", attempts, numRetries));
            executeThreading();
            LOG.info(String.format("Completed attempt %d/%d at re-processing failed protocols", attempts, numRetries));

            if (failedProtocolNumbers.size() == 0) {
                LOG.info("No more failures found to re-process");
                break;
            }

            protocolNumbers = failedProtocolNumbers;
            numProtocolsLeftToProcess = failedProtocolNumbers.size();
        }

        LOG.info("Failed protocol numbers complete.");
    }


    private void createSpreadsheet() {
        ExcelCreator excelCreator = new ExcelCreator();
        excelCreator.createAttachmentsSpreadsheet();
    }


    private void executeThreading() {
        LOG.info(String.format("Kicking off %d PdfWorkerThread(s)", numWorkerThreads));

        statCollector.setUnprocessedTotal(numProtocolsLeftToProcess);
        statCollector.startStopwatch();
        statCollector.reportStats();

        List<Thread> workerThreads = new ArrayList<>();
        for (int workerId = 0; workerId < numWorkerThreads; workerId++) {
            PdfThreadWorker pdfThreadWorker = new PdfThreadWorker(workerId, this);
            Thread thread = new Thread(pdfThreadWorker);
            workerThreads.add(thread);
            thread.start();
        }

        for (Thread workerThread : workerThreads) {
            try {
                workerThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        statCollector.stopStopwatch();
        statCollector.interrupt();
        statCollector.reportStats();
    }


    /*
     * Called by PdfThreadWorkers, so needs to be threadsafe
     */
    public synchronized PdfBatch getNextPdfBatch(BatchResult batchResult) {
        numProtocolsLeftToProcess -= batchResult.getTotalProcessed();
        failedProtocolNumbers.addAll(batchResult.getFailedProtocolNumbers());
        statCollector.recordBatchProcessed(batchResult, numProtocolsLeftToProcess, failedProtocolNumbers.size());

        List<String> numberBatch = new ArrayList<>();
        Iterator<String> iter = protocolNumbers.iterator();

        while (iter.hasNext() && numberBatch.size() < batchSize) {
            numberBatch.add(iter.next());
            iter.remove();
        }

        bucketHandler.incrementProtocolCount(numberBatch.size());
        String bucketPath = bucketHandler.getCurrentBucketPath();

        return new PdfBatch(numberBatch, bucketPath);
    }


    private List<String> getProtocolNumbers() {
        List<String> protocolNumbers;

        boolean getProtocolNumbersFromFile = getKualiConfigurationService().getPropertyValueAsBoolean(GET_PROTOCOL_NUMBERS_FROM_FILE);
        if (getProtocolNumbersFromFile) {
            String numbersFilePath = getKualiConfigurationService().getPropertyValueAsString(PROTOCOL_NUMBERS_FILE_PATH);
            LOG.info("Pulling protocol numbers from file: " + numbersFilePath);
            try {
                protocolNumbers = Files.readAllLines(Paths.get(numbersFilePath));
                protocolNumbers.removeIf(StringUtils::isBlank);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            LOG.info("Pulling protocol numbers from DB");
            protocolNumbers = getProtocolNumberDao().getActiveProtocolNumbers();
        }

        LOG.info("Total protocol numbers pulled: " + protocolNumbers.size());
        return protocolNumbers;
    }


    private ProtocolNumberDao getProtocolNumberDao() {
        if (protocolNumberDao == null) {
            this.protocolNumberDao = KraServiceLocator.getService(ProtocolNumberDao.class);
        }
        return protocolNumberDao;
    }


    public ConfigurationService getKualiConfigurationService() {
        if (kualiConfigurationService == null) {
            this.kualiConfigurationService = KraServiceLocator.getService(ConfigurationService.class);
        }
        return kualiConfigurationService;
    }

}
