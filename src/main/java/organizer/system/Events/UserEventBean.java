package organizer.system.Events;


import organizer.commons.beans.HeaderBean;
import organizer.user.beans.UserBean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;


@ApplicationScoped
@Named("userEvent")
public class UserEventBean implements Serializable {

    private static UserEventBean instance = new UserEventBean();
    private HashMap<Integer, List<HeaderBean>> map;


    private UserEventBean() {
        map = new HashMap<>();

    }







    public static UserEventBean getInstance() {
        return instance;
    }

    public void add(int gID, HeaderBean headerBean) {
        if (!this.map.containsKey(gID)) {
            this.map.put(gID, new ArrayList<>());
            this.map.get(gID).add(headerBean);
        } else {
            this.map.get(gID).add(headerBean);
        }

    }


    public void remove(int gID, HeaderBean headerBean) {
        this.map.get(gID).remove(headerBean);

    }


    public void alert(int gID, String message) {
        for (HeaderBean bean : this.map.get(gID)
        ) {

            bean.notificate();

        }
    }




}
