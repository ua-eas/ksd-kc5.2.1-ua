package edu.arizona.kra.irb.pdf.thread;

import edu.arizona.kra.irb.pdf.ProtocolNumberDao;
import edu.arizona.kra.irb.pdf.utils.FileUtils;
import edu.arizona.kra.irb.pdf.excel.ExcelCreator;
import edu.arizona.kra.irb.pdf.utils.BucketHandler;
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


public class PdfThreadMaster {
    private static final Logger LOG = Logger.getLogger(PdfThreadMaster.class);

    private ConfigurationService kualiConfigurationService;
    private ProtocolNumberDao protocolNumberDao;
    private final BucketHandler bucketHandler;
    private final StatCollector statCollector;
    private List<String> protocolNumbers;
    private final List<String> failedProtocolNumbers;
    private final int numWorkerThreads;
    private final int totalProtocolCount;
    private int numProtocolsLeftToProcess;
    private final int batchSize;
    private int numRetries;


    public PdfThreadMaster() {
        this.protocolNumbers = getProtocolNumbers();
        this.totalProtocolCount = protocolNumbers.size();
        this.failedProtocolNumbers = new ArrayList<>();
        this.bucketHandler = new BucketHandler();
        this.numWorkerThreads = Integer.parseInt(getKualiConfigurationService().getPropertyValueAsString(NUM_WORKER_THREADS));
        this.numProtocolsLeftToProcess = protocolNumbers.size();
        this .statCollector = new StatCollector(numWorkerThreads, 100);//TODO: Make report count configurable
        this.batchSize = 100; //TODO: Make this configurable
        this.numRetries = 3; //TODO: Make this configurable
    }


    public void process() {
        SqlUtils.createSpreadsheetTable();

        // First time through the loop will be the input list, n-1 iterations
        // will be any leftover failed protococol numbers that the threads report
        while (numRetries + 1 > 0) {
            numRetries--;
            executeThreading();

            if (failedProtocolNumbers.size() == 0) {
                break;
            }

            protocolNumbers = failedProtocolNumbers;
            numProtocolsLeftToProcess = failedProtocolNumbers.size();
        }

        createSpreadsheet();
        FileUtils.createFinishFile(totalProtocolCount, true);
    }


    private void createSpreadsheet() {
        ExcelCreator excelCreator = new ExcelCreator();
        excelCreator.createAttachmentsSpreadsheet();
    }


    private void executeThreading() {
        LOG.info(String.format("Kicking off %d PdfWorkerThread(s)", numWorkerThreads));

        statCollector.startStopwatch();
        statCollector.reportStats(numProtocolsLeftToProcess, true);

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
        statCollector.reportStats(numProtocolsLeftToProcess, true);
    }


    public synchronized PdfBatch getNextPdfBatch(BatchResult batchResult) {
        if (protocolNumbers.isEmpty()) {
            // Signal to PdfThreadWorker that there's no more work so it can exit
            return null;
        }

        List<String> numberBatch = new ArrayList<>();
        Iterator<String> iter = protocolNumbers.iterator();

        while (iter.hasNext() && numberBatch.size() < batchSize) {
            numberBatch.add(iter.next());
            iter.remove();
        }

        bucketHandler.incrementProtocolCount(numberBatch.size());
        String bucketPath = bucketHandler.getCurrentBucketPath();

        if (batchResult != null) {
            numProtocolsLeftToProcess -= batchResult.getTotalProcessed();
            failedProtocolNumbers.addAll(batchResult.getFailedProtocolNumbers());
            statCollector.recordDocProcessed(batchResult, numProtocolsLeftToProcess, failedProtocolNumbers.size());
        }

        return new PdfBatch(numberBatch, bucketPath);
    }


    private List<String> getProtocolNumbers() {
        List<String> protocolNumbers;

        boolean getProtocolNumbersFromFile = getKualiConfigurationService().getPropertyValueAsBoolean(GET_PROTOCOL_NUMBERS_FROM_FILE);
        if (getProtocolNumbersFromFile) {
            String numbersFilePath = getKualiConfigurationService().getPropertyValueAsString(PROTOCOL_NUMBERS_FILE_PATH);
            try {
                protocolNumbers = Files.readAllLines(Paths.get(numbersFilePath));
                protocolNumbers.removeIf(StringUtils::isBlank);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            protocolNumbers = getProtocolNumberDao().getActiveProtocolNumbers();
        }

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
