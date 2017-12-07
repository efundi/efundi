package edu.nwu.sakai.studentlink.server;

import java.io.IOException;
import java.util.Properties;

public class SettingsProperties {

    private static Properties prop;

    public static String getProperty(String key) {
        checkPropExist();
        return prop.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        checkPropExist();
        return prop.getProperty(key, defaultValue);
    }

    public static Properties getSettingProperties() {
        checkPropExist();
        return prop;
    }

    private static void checkPropExist() {
        if (prop == null) {
            prop = new Properties();
            try {
                prop.load(SakaiStudentLinkServiceImpl.class
                        .getResourceAsStream("settings.properties"));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}