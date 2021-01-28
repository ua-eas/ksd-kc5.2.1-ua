package edu.arizona.kra.irb.pdf.sftp;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.IOException;


public class SftpTransferAgent {
    private static final String DEST_DIR = "test/";//TODO: Make this config property
    private SFTPClient sftpClient;


    public SftpTransferAgent() {
        init();
    }


    public void put(ProtocolPdfFile pdfFile) {
        try {
            sftpClient.put(pdfFile, DEST_DIR + pdfFile.getName());
        } catch (IOException e) {
            throw new RuntimeException("Could no push file to sftp: " + pdfFile.getName(), e);
        }
    }


    private void init() {
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

}
