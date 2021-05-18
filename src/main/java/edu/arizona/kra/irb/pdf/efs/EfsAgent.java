package edu.arizona.kra.irb.pdf.efs;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static edu.arizona.kra.irb.pdf.PdfConstants.EFS_BUCKET_SIZE;
import static edu.arizona.kra.irb.pdf.PdfConstants.EFS_ROOT_DIR;
import static org.kuali.rice.core.api.CoreApiServiceLocator.getKualiConfigurationService;


public class EfsAgent {
    private static final Logger LOG = Logger.getLogger(EfsAgent.class);

    private final String efsRootDir;
    private final int bucketSize;
    private int bucktFileCounter;
    private String currentBucketPath;


    public EfsAgent() {
        this.efsRootDir = getKualiConfigurationService().getPropertyValueAsString(EFS_ROOT_DIR);
        this.bucketSize = Integer.parseInt(getKualiConfigurationService().getPropertyValueAsString(EFS_BUCKET_SIZE));
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
        } else {
            LOG.info(String.format("EFS writing turned off, would have written file to: %s", fullFilePath));
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

        File newDirectory = new File(currentBucketPath);
        if (!newDirectory.mkdir()) {
            throw new RuntimeException("Could not create directory!: " + newDirectory.getAbsolutePath());
        }

        LOG.info(String.format("Created new bucket: %s", currentBucketPath));
    }

}
