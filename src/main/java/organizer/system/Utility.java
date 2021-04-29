package organizer.system;

import organizer.user.dtos.NotificationDTO;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Utility {





    public static String getURL(){
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = req.getRequestURL().toString();
        return url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath();
    }





    public static List<NotificationDTO> reverseList(List<NotificationDTO> dtos){
        ArrayList<NotificationDTO> toReturn = new ArrayList<>();

        while (dtos.size() > 0 ){
            toReturn.add(dtos.get(dtos.size() - 1));
            dtos.remove(dtos.size() - 1);
        }

return toReturn;
    }
}
