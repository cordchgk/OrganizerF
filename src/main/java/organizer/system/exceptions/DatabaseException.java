package organizer.system.exceptions;

import java.util.logging.Logger;


public class DatabaseException extends RuntimeException {

    private static final long serialVersionUID = 1376048188566128000L;

    private static final Logger LOG =
            Logger.getLogger(DatabaseException.class.getName());

    public DatabaseException(String message) {
        super(message);
        LOG.severe(message);
    }
    public DatabaseException(String message, Exception ex) {
        super(message.concat(ex.toString()));
        LOG.severe(message);
    }


}