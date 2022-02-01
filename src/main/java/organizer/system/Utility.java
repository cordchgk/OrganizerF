package organizer.system;


import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class Utility {


    public static String getURL() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = req.getRequestURL().toString();
        return url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath();
    }

    public static float twoDecimals(float number) {
        return (float) ((int) (number * 10f)) / 10f;
    }


    public static Cookie getCookie() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Cookie t_r = null;
        Cookie[] cookies = request.getCookies();
/*        HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        System.out.println(httpServletRequest.getSession().getId());
*/
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("JSESSIONID")) {

                    return cookies[i];
                }
            }
        }

        return t_r;
    }


    public static String urlBuilder() {
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(origRequest.getRequestURL());
        if (!FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().isEmpty()) {
            stringBuilder.append("?");

            for (String s : FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().keySet()) {
                stringBuilder.append(s);
                stringBuilder.append("=");
                stringBuilder.append(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(s));
            }
        }
        return stringBuilder.toString();
    }

}
