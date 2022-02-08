package organizer.system;

import organizer.diet.system.IngredientSearch;

import javax.annotation.PostConstruct;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import java.io.Serializable;

public class ApplicationInitializationService implements Serializable, SystemEventListener {


    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {

    }

    @PostConstruct
    public void init(){
        IngredientSearch.getInstance();
        ConnectionPool.getInstance();

    }


    @Override
    public boolean isListenerForSource(Object source) {
        return false;
    }
}