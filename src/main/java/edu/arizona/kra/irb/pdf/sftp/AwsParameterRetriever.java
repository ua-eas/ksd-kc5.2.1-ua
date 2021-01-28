package edu.arizona.kra.irb.pdf.sftp;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;

public class AwsParameterRetriever {
    private static final String sftpServerUrlKey = "/uar/hdm/SFTP_SERVER";
    private static final String sftpPortKey = "/uar/hdm/SFTP_PORT";
    private static final String sftpUsernameKey = "/uar/hdm/SFTP_USERNAME";
    private static final String sftpPasswordKey = "/uar/hdm/SFTP_PASSWORD";

    private final String sftpServerUrl;
    private final String sftpPort;
    private final String sftpUsername;
    private final String sftpPassword;


    public AwsParameterRetriever() {
        this.sftpServerUrl = fetchSsmParameter(sftpServerUrlKey);
        this.sftpPort = fetchSsmParameter(sftpPortKey);
        this.sftpUsername = fetchSsmParameter(sftpUsernameKey);
        this.sftpPassword = fetchSsmParameter(sftpPasswordKey);
    }


    public String fetchSsmParameter(String key) {
        Region region = Region.US_WEST_2;
        SsmClient ssmClient = SsmClient.builder()
                .region(region)
                .build();

        GetParameterResponse parameterResponse = null;
        try {
            GetParameterRequest parameterRequest = GetParameterRequest.builder()
                    .name(key)
                    .withDecryption(true)
                    .build();
            parameterResponse = ssmClient.getParameter(parameterRequest);
        } catch (SsmException e) {
            throw new RuntimeException("Could not fetch parameter: " + key, e);
        }

        return parameterResponse.parameter().value();
    }


    public String getSftpServerUrl() {
        return sftpServerUrl;
    }

    public String getSftpPort() {
        return sftpPort;
    }

    public String getSftpUsername() {
        return sftpUsername;
    }

    public String getSftpPassword() {
        return sftpPassword;
    }
}
