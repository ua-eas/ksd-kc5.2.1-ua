package edu.arizona.kra.irb.pdf.impl;

import edu.arizona.kra.irb.pdf.ProtocolNumberDao;
import edu.arizona.kra.irb.pdf.ProtocolPdfWorker;
import edu.arizona.kra.irb.pdf.ProtocolPdfWriterService;
import jdk.internal.org.jline.utils.Log;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ProtocolPdfWriterServiceImpl implements ProtocolPdfWriterService {
    private ConfigurationService kualiConfigurationService;
    private ProtocolNumberDao protocolNumberDao;


    @Override
    public boolean generateActiveProtocolPdfsToDisk() {
        int numWorkerThreads = Integer.parseInt(
                getKualiConfigurationService().getPropertyValueAsString("number.pdf.worker.threads"));

        List<String> protocolNumbers = getProtocolNumberDao().getActiveProtocolNumbers();

        // The quantity of protocolNumbers that one worker should process
        int chunkSize = protocolNumbers.size() / numWorkerThreads;

        // Reported back to the action/form for the jsp forward to display
        boolean startedOk = true;

        try {
            for (int workerId = 0; workerId <= numWorkerThreads; workerId++) {
                Set<String> workerProtocolNumbers = new HashSet<>();

                while (workerProtocolNumbers.size() <= chunkSize && !protocolNumbers.isEmpty()) {
                    // Fill up a worker's hopper with chunkSize worth of proto numbers
                    workerProtocolNumbers.add(protocolNumbers.remove(0));
                }

                if (workerId == numWorkerThreads && !protocolNumbers.isEmpty()) {
                    // Need to account for even/odd quantity of numbers/workers; if we're at the
                    // last worker, give it all remaining work to be done (should only ever be +1)
                    workerProtocolNumbers.addAll(protocolNumbers);
                    protocolNumbers.clear();
                }

                Log.info(String.format("Starting ProtocolPdfWorker thread ID %d with %d protocol numbers.",
                        workerId, workerProtocolNumbers.size()));
                ProtocolPdfWorker protocolPdfWorker = new ProtocolPdfWorker(workerId, workerProtocolNumbers);
                protocolPdfWorker.start(); // async, forked thread
            }//for
        } catch (Throwable t) {
            Log.error("Could not process PDFs!: " + t.getMessage(), t);
            startedOk = false;
        }

        return startedOk;
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
