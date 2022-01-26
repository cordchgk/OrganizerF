package organizer.system.listener;

import javax.annotation.PreDestroy;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

public class ApplicationShutdownListener implements SystemEventListener {
    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        System.out.println("called");
    }

    @Override
    public boolean isListenerForSource(Object source) {
        return false;
    }


    @PreDestroy
    public void destroy(){
        System.out.println("called");
    }
}
