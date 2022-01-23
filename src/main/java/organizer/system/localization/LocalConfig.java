package organizer.system.localization;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NonNls;
import organizer.system.Config;
import organizer.system.Messages;
import organizer.system.exceptions.ConfigException;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ViewScoped
@Named("lc")
@Getter
@Setter
public class LocalConfig implements Serializable {


    private List<String> locales = Locales.getAll();

    ResourceBundle bundle;

    @Inject
    UserBean u_bean;

    @PostConstruct
    public void init() {

        if (this.u_bean != null) {
            if (this.u_bean.getDto().getUserSettingsDTO() != null) {
                bundle = ResourceBundle.getBundle(Locales.getPath(this.u_bean.getDto().getUserSettingsDTO().getLocale()));
            } else {
                bundle = ResourceBundle.getBundle(Locales.getPath(this.u_bean.getDto().getLanguage()));
            }

        } else {
            bundle = ResourceBundle.getBundle(Locales.getPath(Locale.ENGLISH.toLanguageTag()));
        }
    }


    private static final Logger LOGGER =
            Logger.getLogger(Config.class.getName());


    public String getEntry(final String key) {

        String t_R = "";
        try {

            final String result = bundle.getString(key);
            t_R = result.trim();
        } catch (final MissingResourceException e) {
            final String infoMsg =
                    Messages.getFormattedString("CONFIG.OPTIONAL_MISSING", key);
            t_R = ResourceBundle.getBundle(Locales.getPath(Locale.ENGLISH.toLanguageTag())).getString(key);
            LOGGER.log(Level.INFO, infoMsg, e);
        }

        return t_R;
    }


}
