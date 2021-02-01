package edu.arizona.kra.irb.pdf.sftp;

import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;

public class AwsParameterAgent {
    private static final String sftpServerUrlKey = "/uar/hdm/SFTP_SERVER";
    private static final String sftpPortKey = "/uar/hdm/SFTP_PORT";
    private static final String sftpUsernameKey = "/uar/hdm/SFTP_USERNAME";
    private static final String sftpPasswordKey = "/uar/hdm/SFTP_PASSWORD";
    private static final String awsRegionKey = "protocol.pdf.aws.region";

    private final String sftpServerUrl;
    private final String sftpPort;
    private final String sftpUsername;
    private final String sftpPassword;
    private ConfigurationService kualiConfigurationService;


    public AwsParameterAgent() {
        this.sftpServerUrl = fetchSsmParameter(sftpServerUrlKey);
        this.sftpPort = fetchSsmParameter(sftpPortKey);
        this.sftpUsername = fetchSsmParameter(sftpUsernameKey);
        this.sftpPassword = fetchSsmParameter(sftpPasswordKey);
    }


    public String fetchSsmParameter(String key) {
        String regionValue = getKualiConfigurationService().getPropertyValueAsString(awsRegionKey);
        Region region = Region.of(regionValue);
        SsmClient ssmClient = SsmClient.builder()
                .region(region)
                .build();

        GetParameterResponse parameterResponse;
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

    private ConfigurationService getKualiConfigurationService() {
        if (kualiConfigurationService == null) {
            this.kualiConfigurationService = KraServiceLocator.getService(ConfigurationService.class);
        }
        return  kualiConfigurationService;
    }
}
