package edu.arizona.kra.irb.pdf.impl;

import edu.arizona.kra.irb.pdf.ProtocolNumberDao;
import edu.arizona.kra.irb.pdf.ProtocolPdfJobInfo;
import edu.arizona.kra.irb.pdf.ProtocolPdfWorker;
import edu.arizona.kra.irb.pdf.ProtocolPdfWriterService;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.UserSession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ProtocolPdfWriterServiceImpl implements ProtocolPdfWriterService {
    private static final Logger LOG = Logger.getLogger(ProtocolPdfWriterServiceImpl.class);

    // These 3 keys are shared with kc-config.xml
    private static final String START_FROM_DATE_KEY = "protocol.pdf.start.from.date";
    private static final String END_TO_DATE_KEY = "protocol.pdf.end.to.date";
    private static final String NUM_WORKER_THREADS_KEY = "protocol.pdf.num.worker.threads";

    private ConfigurationService kualiConfigurationService;
    private ProtocolNumberDao protocolNumberDao;


    @Override
    public ProtocolPdfJobInfo generateActiveProtocolPdfsToDisk(UserSession userSession) {
        String startFromDate = getKualiConfigurationService().getPropertyValueAsString(START_FROM_DATE_KEY);
        String endToDate = getKualiConfigurationService().getPropertyValueAsString(END_TO_DATE_KEY);

        List<String> protocolNumbers = getProtocolNumberDao().getActiveProtocolNumbers(startFromDate, endToDate);
        int totalNumProtocols = protocolNumbers.size();

        int numWorkerThreads = Integer.parseInt(
                getKualiConfigurationService().getPropertyValueAsString(NUM_WORKER_THREADS_KEY));
        LOG.info(String.format("Creating %d PdfWorkerThread(s)", numWorkerThreads));

        // The quantity of protocolNumbers that one worker should process
        int chunkSize = protocolNumbers.size() / numWorkerThreads;

        // Reported back to the action/form for the jsp forward to display
        boolean startedOk = true;

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
                protocolPdfWorker.start(); // async, forked thread
            }//for
        } catch (Throwable t) {
            LOG.error("Could not process PDFs!: " + t.getMessage(), t);
            startedOk = false;
        }

        String outputDir = System.getProperty("java.io.tmpdir");
        return new ProtocolPdfJobInfo(totalNumProtocols, startFromDate, endToDate, startedOk, outputDir);
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
