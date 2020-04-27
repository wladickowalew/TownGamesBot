import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Bot {
    public static void main(String[] args) {
        ImageLoader test = new ImageLoader("Саратов");
        //test
        int i = 0;
        while (true){
            Image img = test.getNextImage();
            if (img == null) break;
            BufferedImage bufferedImage = (BufferedImage) img;
            File out = new File("img" + i++ + ".png");
            try {
                ImageIO.write(bufferedImage, "png", out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //test
    }
}
