package organizer.system.exceptions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigException extends RuntimeException {

    private static final long serialVersionUID = -2955744450053285096L;

    /**
     * The class logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ConfigException.class.getName());




    /**
     * Constructor for a ConfigException containing an exception message and the
     * throwable cause.
     *
     * @param message the message to describe the exception
     * @param cause   the throwable cause of the exception
     */
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
        LOGGER.log(Level.SEVERE, message, cause);
    }

    /**
     * Used for the deserialization.
     *
     * @param inputStream the input stream
     * @throws IOException            if serialization fails
     * @throws ClassNotFoundException if serialization fails
     */
    private void readObject(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
    }

    /**
     * Used for the serialization.
     *
     * @param outputStream the output stream
     * @throws IOException if the deserialization fails
     */
    private void writeObject(final ObjectOutputStream outputStream)
            throws IOException {
        outputStream.defaultWriteObject();
    }

}
