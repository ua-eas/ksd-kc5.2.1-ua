package edu.arizona.kra.irb.props;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {
    private static final String CLI_PROP_FILE_ARG = "config.file";
    private static Properties properties;


    public static Properties getProperties() {
        if (properties == null) {
            loadProperties();
        }
        return properties;
    }


    private static void loadProperties() {
        properties = new Properties();
        String propertyFile = System.getProperty(CLI_PROP_FILE_ARG);
        try {
            InputStream fileInputStream = new FileInputStream(propertyFile);
            properties.load(fileInputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
