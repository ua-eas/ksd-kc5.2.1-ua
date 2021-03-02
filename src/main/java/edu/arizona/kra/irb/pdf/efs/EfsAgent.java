package edu.arizona.kra.irb.pdf.efs;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class EfsAgent {
    private static final Logger LOG = Logger.getLogger(EfsAgent.class);

    private final String efsRootDir;
    private final int bucketSize;
    private int bucktFileCounter;
    private String currentBucketPath;


    public EfsAgent() {
        this.efsRootDir = getKualiConfigurationService().getPropertyValueAsString("efs.root.dir");
        this.bucketSize = Integer.parseInt(getKualiConfigurationService().getPropertyValueAsString("efs.bucket.size"));
        this.bucktFileCounter = 0;
        createNewBucket();
    }


    public String pushFileToEfs(String fileName, byte[] bytes, boolean efsWriteModeIsOn){
        if (bucktFileCounter >= bucketSize) {
            createNewBucket();
            bucktFileCounter = 0;
        }

        String fullFilePath = currentBucketPath + File.separator + fileName;

        if (efsWriteModeIsOn) {
            LOG.info(String.format("Writing file to EFS: %s", fullFilePath));
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(fullFilePath);
                outputStream.write(bytes);
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        }

        bucktFileCounter++;
        return fullFilePath;
    }


    /*
     * Create a new directory bucket and make sure no other thread used the same name
     */
    private void createNewBucket() {
        LOG.info("Creating new bucket");

        UUID uuid = UUID.randomUUID();
        currentBucketPath = efsRootDir + File.separator + uuid;
        File newDirectory = new File(efsRootDir + File.separator + uuid);
        while (newDirectory.exists()) {
            uuid = UUID.randomUUID();
            currentBucketPath = efsRootDir + File.separator + uuid;
            newDirectory = new File(efsRootDir + File.separator + uuid);
        }

        //noinspection ResultOfMethodCallIgnored
        newDirectory.mkdir();

        LOG.info(String.format("Created new bucket: %s", currentBucketPath));
    }

}
