package organizer.system.image;


import lombok.Getter;
import lombok.Setter;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@ApplicationScoped
@Named
@Getter
@Setter
public class ImageService {

    public static void main(String[] args) {


    }


    public static boolean store() {

        return true;
    }

    public String retrieveForIngredient() {

        Path path = Paths.get("/home/cordch/ingredient/");

        BufferedImage img = null;

        try {
            img = ImageIO.read(new File("/home/cordch/ingredient/202103230012.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "https://s7g10.scene7.com/is/image/aldi/202103230012";
    }


}
