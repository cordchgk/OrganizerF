package organizer.system.localization;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum Locales {
    de("/locales/de"),
    en("/locales/en");


    private String path;


    Locales(String path) {
        this.path = path;
    }

    public static String getPath(String path) {

        String t_r = "";
        switch (path) {
            case "de":
                t_r = de.getPath();
                break;
            case "en":
                t_r = en.getPath();
                break;
        }


        return t_r;


    }

    public static List<String> getAll() {
        ArrayList t_R = new ArrayList();
        for (Locales l : Locales.values()) {
            t_R.add(l);
        }
        return t_R;
    }
}
