package organizer.system.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Is to be thrown by database DAOs if the write process fails because of a
 * duplicate entry. That usually occurs due to race conditions.
 *
 * @author Frederik Fleischmann
 */
public class DuplicateException extends Exception {

    private static final long serialVersionUID = -4937315234618746888L;

    DuplicateException(String errorString) {
        super(errorString);
        Logger.getLogger(DuplicateException.class.getName())
                .severe(errorString);
    }

    DuplicateException(String errorString, Throwable throwable) {
        super(errorString, throwable);
        Logger.getLogger(DuplicateException.class.getName())
                .log(Level.SEVERE, errorString, throwable);
    }

}