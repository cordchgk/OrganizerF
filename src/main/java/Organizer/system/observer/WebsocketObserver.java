package organizer.system.observer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.event.WebsocketEvent;
import javax.websocket.CloseReason;
import javax.websocket.OnMessage;

@ApplicationScoped
public class WebsocketObserver {
/**
    public void onOpen(@Observes @WebsocketEvent.Opened WebsocketEvent event) {
        String channel = event.getChannel(); // Returns <f:websocket channel>.
        Long userId = event.getUser(); // Returns <f:websocket user>, if any.

    }

    public void onClose(@Observes @WebsocketEvent.Closed WebsocketEvent event) {
        String channel = event.getChannel(); // Returns <f:websocket channel>.
        Long userId = event.getUser(); // Returns <f:websocket user>, if any.
        CloseReason.CloseCode code = event.getCloseCode(); // Returns close reason code.
        // ...
    }
    @OnMessage
    public void onMessage(@Observes  WebsocketEvent event){

    }


**/
}