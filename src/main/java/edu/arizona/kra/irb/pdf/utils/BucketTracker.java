package edu.arizona.kra.irb.pdf.utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.UUID;

import static edu.arizona.kra.irb.pdf.PdfConstants.EFS_BUCKET_SIZE;
import static edu.arizona.kra.irb.pdf.PdfConstants.EFS_ROOT_DIR;
import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class BucketTracker {
    private static final Logger LOG = Logger.getLogger(BucketTracker.class);

    private final String efsRootDir;
    private final int bucketSize;
    private int bucktFileCounter;
    private String currentBucketPath;


    public BucketTracker() {
        this.efsRootDir = getKualiConfigurationService().getPropertyValueAsString(EFS_ROOT_DIR);
        this.bucketSize = Integer.parseInt(getKualiConfigurationService().getPropertyValueAsString(EFS_BUCKET_SIZE));
        this.bucktFileCounter = 0;
    }


    public String getBucketPath() {
        if (bucktFileCounter >= bucketSize) {
            createNewBucket();
            bucktFileCounter = 0;
        }

        return currentBucketPath;
    }


    private void createNewBucket() {
        LOG.info("Creating new bucket");

        UUID uuid = UUID.randomUUID();
        currentBucketPath = efsRootDir + File.separator + uuid;

        File newDirectory = new File(currentBucketPath);
        if (!newDirectory.mkdir()) {
            throw new RuntimeException("Could not create directory!: " + newDirectory.getAbsolutePath());
        }

        LOG.info(String.format("Created new bucket: %s", currentBucketPath));
    }


    public void incrementProtocolCount(int count) {
        bucktFileCounter += count;
    }
}
