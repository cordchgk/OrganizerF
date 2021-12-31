package organizer.system.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

@FacesConverter("HashConverter")
public class HashConverter implements Converter {

    private static final Logger LOG =
            Logger.getLogger(HashConverter.class.getName());

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        try {
            return sha384(value);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            return "";
        }
    }

    public static String sha384(final String value)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        final byte[] rawPassword = value.getBytes("UTF8");
        final MessageDigest messageDigest =
                MessageDigest.getInstance("SHA-384");
        messageDigest.update(rawPassword);
        return new BigInteger(1, messageDigest.digest()).toString(16);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        // TODO - implement HashConverter.getAsString
        return value.toString();
    }

}
