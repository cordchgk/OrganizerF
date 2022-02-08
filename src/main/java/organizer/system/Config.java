package organizer.system;

import organizer.system.exceptions.ConfigException;
import org.jetbrains.annotations.NonNls;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public enum Config {
    ;

    /**
     * The class logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Config.class.getName());

    /**
     * The resource bundle name for config_de.properties.
     */
    @NonNls
    private static final String BUNDLE_NAME = "config_de" ;

    /**
     * The resource bundle.
     */
    private static ResourceBundle resourceBundle =
            ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * Get a config entry from the config file.
     *
     * @param key          the unique key for the configuration entry
     * @param defaultValue the default value to be used if no config entry is
     *                     not found
     * @return the config entry as Integer
     */
    public static Integer getEntry(final String key,
            final Integer defaultValue) {


        final String defaultString = String.valueOf(defaultValue);
        final String entry = getEntry(key, defaultString);
        return Integer.parseInt(entry);
    }

    /**
     * Get a config entry from the config file.
     *
     * @param key          the unique key for the configuration entry
     * @param defaultValue the default value to be used if no config entry is
     *                     not found
     * @return the config entry as String
     */
    public static String getEntry(final String key, final String defaultValue) {
        String entry = defaultValue;
        try {
            final String result = resourceBundle.getString(key);
            entry = result.trim();
        } catch (final MissingResourceException e) {
            final String infoMsg =
                    Messages.getFormattedString("CONFIG.OPTIONAL_MISSING", key,
                            defaultValue);
            LOGGER.log(Level.INFO, infoMsg, e);
        }
        if (entry.isEmpty()) {
            entry = defaultValue;
        }
        return entry;
    }

    /**
     * Get a mandatory config entry from the config file.
     *
     * @param key the unique key for the configuration entry
     * @return the config entry as String
     * @throws ConfigException if the entry is not found
     */
    public static String getMandatoryEntry(final String key) {
        try {
            return resourceBundle.getString(key);
        } catch (final MissingResourceException e) {
            final String errorMsg =
                    Messages.getFormattedString("CONFIG.MANDATORY_MISSING",
                            key);
            throw new ConfigException(errorMsg, e);
        }
    }

}
