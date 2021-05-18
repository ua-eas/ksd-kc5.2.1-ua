package edu.arizona.kra.irb.pdf.impl;

import edu.arizona.kra.irb.pdf.PdfProcessingReport;
import edu.arizona.kra.irb.pdf.ProtocolNumberDao;
import edu.arizona.kra.irb.pdf.ProtocolPdfWorker;
import edu.arizona.kra.irb.pdf.ProtocolPdfWriterService;
import edu.arizona.kra.irb.pdf.sql.QueryConstants;
import edu.arizona.kra.irb.pdf.sql.SqlExecutor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.UserSession;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.arizona.kra.irb.pdf.PdfConstants.*;


public class ProtocolPdfWriterServiceImpl implements ProtocolPdfWriterService {
    private static final Logger LOG = Logger.getLogger(ProtocolPdfWriterServiceImpl.class);

    private ConfigurationService kualiConfigurationService;
    private ProtocolNumberDao protocolNumberDao;


    @Override
    public void generateProtocolSummaries(UserSession userSession) {
        createSpreadsheetTable();

        List<String> protocolNumbers = getProtocolNumbers();
        PdfProcessingReport processingReport = new PdfProcessingReport(protocolNumbers.size());

        int numWorkerThreads = Integer.parseInt(
                getKualiConfigurationService().getPropertyValueAsString(NUM_WORKER_THREADS));
        LOG.info(String.format("Creating %d PdfWorkerThread(s)", numWorkerThreads));

        // The quantity of protocolNumbers that one worker should process
        int chunkSize = protocolNumbers.size() / numWorkerThreads;

        List<ProtocolPdfWorker> workers = new ArrayList<>();
        try {
            for (int workerId = 0; workerId < numWorkerThreads; workerId++) {
                Set<String> workerProtocolNumbers = new HashSet<>();

                while (workerProtocolNumbers.size() <= chunkSize && !protocolNumbers.isEmpty()) {
                    // Fill up a worker's hopper with chunkSize worth of proto numbers
                    workerProtocolNumbers.add(protocolNumbers.remove(0));
                }

                if (workerId == numWorkerThreads - 1 && !protocolNumbers.isEmpty()) {
                    // Need to account for even/odd quantity of numbers/workers; if we're at the
                    // last worker, give it all remaining work to be done (should only ever be +1)
                    workerProtocolNumbers.addAll(protocolNumbers);
                    protocolNumbers.clear();
                }

                LOG.info(String.format("Starting ProtocolPdfWorker thread ID %d with %d protocol numbers.",
                        workerId, workerProtocolNumbers.size()));
                ProtocolPdfWorker protocolPdfWorker = new ProtocolPdfWorker(workerId, workerProtocolNumbers, userSession);
                workers.add(protocolPdfWorker);
                protocolPdfWorker.start(); // async, forked thread
            }//for

            processingReport.setWorkersStartedOk(true);

            for (ProtocolPdfWorker worker : workers) {
                worker.join();
            }

            processingReport.setProcessingSuccessful(true);

        } catch (Throwable t) {
            LOG.error("Could not process PDFs!: " + t.getMessage(), t);
        } finally {
            createFinishFile(processingReport);
        }
    }


    private List<String> getProtocolNumbers() {
        List<String> protocolNumbers;

        boolean getProtocolNumbersFromFile = getKualiConfigurationService()
                .getPropertyValueAsBoolean(GET_PROTOCOL_NUMBERS_FROM_FILE);

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


    private void createFinishFile(PdfProcessingReport processingReport) {
        String finishFilePath = getKualiConfigurationService().getPropertyValueAsString(FINISH_FILE_PATH);

        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(new FileWriter(finishFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String status = processingReport.isProcessingSuccessful() ? "SUCCESS" : "FAILED";
        printWriter.println(new Date());
        printWriter.println("  Processing status: " + status);
        printWriter.println(" Workers started ok: " + processingReport.isWorkersStartedOk());
        printWriter.println("Protocols processed: " + processingReport.getNumProtocolsProcessed());

        printWriter.flush();
        printWriter.close();
    }


    private void createSpreadsheetTable() {
        SqlExecutor sqlExecutor = new SqlExecutor();

        try {
            sqlExecutor.execute(QueryConstants.CREATE_SPREADSHEET_TABLE_SQL);
        } catch (Exception e) {
            if (!e.getMessage().contains("ORA-00955")) {
                // This is not the "name is already used by an existing object" error, so fail fast
                throw new RuntimeException(e);
            }
        }

        String truncateSpreadsheetTableOnStart = getKualiConfigurationService().getPropertyValueAsString(SHOULD_TRUNCATE);
        if (truncateSpreadsheetTableOnStart.equals("true")) {
            sqlExecutor.execute(QueryConstants.TRUNCATE_SPREADSHEET_TABLE_SQL);
        }
    }


    private ProtocolNumberDao getProtocolNumberDao() {
        return protocolNumberDao;
    }


    public void setProtocolIdDao(ProtocolNumberDao protocolNumberDao) {
        this.protocolNumberDao = protocolNumberDao;
    }


    public ConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }


    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}
