package edu.arizona.kra.irb.pdf.utils;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

import static edu.arizona.kra.irb.pdf.PdfConstants.FINISH_FILE_PATH;

public class FileUtils {
    private static final Logger LOG = Logger.getLogger(FileUtils.class);


    public static void pushFileToEfs(byte[] bytes, String filePath) {
        LOG.info(String.format("Writing file to EFS: %s", filePath));
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filePath);
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }


    public static void createFinishFile(int processedCount, boolean success) {
        String finishFilePath = KraServiceLocator.getService(ConfigurationService.class).getPropertyValueAsString(FINISH_FILE_PATH);
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(new FileWriter(finishFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String status = success ? "SUCCESS" : "FAILED";
        printWriter.println(new Date());
        printWriter.println("  Processing status: " + status);
        printWriter.println("Protocols processed: " + processedCount);

        printWriter.flush();
        printWriter.close();
    }

}
