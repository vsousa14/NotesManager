package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

public class LanguageManager {
    private static Properties properties = new Properties();
    private static String currentLanguage = "pt_pt";

    public static void loadLanguage(String languageCode) {
        currentLanguage = languageCode;
        try (InputStream input = LanguageManager.class.getResourceAsStream("/lang/" + languageCode + ".properties");
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            if (input == null) {
                System.out.println("Sorry, unable to find language file for " + languageCode);
                return;
            }
            properties.load(reader);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String get(String key) {
        return properties.getProperty(key, "missing_" + key + "_translation");
    }
}
