package organizer.REST.authentication;

import organizer.system.converter.HashConverter;
import organizer.user.daos.UserDAO;
import organizer.user.dtos.UserDTO;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthenticationService {


    private static Map<String, String> getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();

        Enumeration headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }


    public static UserDTO assignUser(HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        HashMap m = (HashMap) getHeadersInfo(request);

        String authorization = (String) m.get("authorization_property");
        String[] auth = authorization.split(" ");
        String username = auth[0];
        String password = auth[1];
        System.out.println(username);
        System.out.println(password);

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(username);
        userDTO.setPasswordHash(HashConverter.sha384(password));
        System.out.println(new UserDAO().selectByDto(userDTO).size());
        List<UserDTO> users = new UserDAO().selectByDto(userDTO);
        if (users.size() == 0){
            return null;
        }
        return users.get(0);


    }
}
