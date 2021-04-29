package organizer.system.EventListener;

import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


/**
 * Register HTTP session events and tell HttpSessionController.
 *
 * @author dahm
 */
@WebListener
public class GlobalevHttpSessionListener implements HttpSessionListener {


    @Inject
    private GlobalevHttpSessionController _httpSessionController;

    @Override
    public void sessionCreated(final HttpSessionEvent se) {

        _httpSessionController.addSession(se.getSession());
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {

        _httpSessionController.removeSession(se.getSession());
    }
}