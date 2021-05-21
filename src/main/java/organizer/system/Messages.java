package organizer.system;

import org.jetbrains.annotations.NonNls;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum Messages {
    ;

    /**
     * The resource bundle name for messages.properties.
     */
    @NonNls private static final String BUNDLE_NAME = "config_de";

    /**
     * The resource bundle.
     */
    private static final ResourceBundle RESOURCE_BUNDLE =
            ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * The class logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Messages.class.getName());

    /**
     * The message if the resource bundle does not find the entry.
     */
    @NonNls private static final String ENTRY_NOT_FOUND =
            "Message entry {0} not found";

    /**
     * Formats the message entry from the message file.
     *
     * @param key    the unique key for the message entry
     * @param values value(s) to replace in the message
     * @return the formatted message
     */
    public static String getFormattedString(@NonNls final String key,
            @NonNls final Object... values) {
        final String message = getString(key);
        return MessageFormat.format(message, values);
    }

    /**
     * Get a message entry from the message file.
     *
     * @param key the unique key for the message entry
     * @return the message string
     */
    public static String getString(@NonNls final String key) {
        String message = "";
        try {
            message = RESOURCE_BUNDLE.getString(key);
        } catch (final MissingResourceException e) {
            final String logMsg = MessageFormat.format(ENTRY_NOT_FOUND, key);
            LOGGER.log(Level.WARNING, logMsg, e);
        }
        return message;
    }

}
