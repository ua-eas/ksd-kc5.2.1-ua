package edu.arizona.kra.irb.pdf.sftp;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.io.IOException;


public class SftpTransferAgent {
    private static final String DEST_DIR_KEY = "protocol.pdf.dest.dir";

    private String destDir;
    private SFTPClient sftpClient;
    private ConfigurationService kualiConfigurationService;


    public SftpTransferAgent() {
        init();
    }


    public void put(ProtocolPdfFile pdfFile) {
        try {
            sftpClient.put(pdfFile, destDir + pdfFile.getName());
        } catch (IOException e) {
            throw new RuntimeException("Could not push file to sftp: " + pdfFile.getName(), e);
        }
    }


    private void init() {
        destDir = getKualiConfigurationService().getPropertyValueAsString(DEST_DIR_KEY);

        AwsParameterRetriever parameterRetriever = new AwsParameterRetriever();
        String serverUrl = parameterRetriever.getSftpServerUrl();
        String serverPort = parameterRetriever.getSftpPort();
        String username = parameterRetriever.getSftpUsername();
        String password = parameterRetriever.getSftpPassword();

        SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());

        try {
            sshClient.authPassword(username, password);
            sshClient.connect(serverUrl, Integer.parseInt(serverPort));
            this.sftpClient = sshClient.newSFTPClient();
        } catch (IOException e) {
            throw new RuntimeException("Could not create sftp client.", e);
        }
    }


    private ConfigurationService getKualiConfigurationService() {
        if (kualiConfigurationService == null) {
            this.kualiConfigurationService = KraServiceLocator.getService(ConfigurationService.class);
        }
        return  kualiConfigurationService;
    }

}
