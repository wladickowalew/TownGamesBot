import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class ImageLoader {
    private int IMAGE_COUNT = 5;
    private String town;
    private String[] imageUrls;
    private int current_image;

    public ImageLoader(String town){
        this.town = town;
        imageUrls = getImageUrls();
        current_image = 0;
    }

    public Image getNextImage(){
        try {
            URL url = new URL(imageUrls[current_image++]);
            return ImageIO.read(url);
        } catch (Exception e) {
            System.out.println("Картинки кончились");
        }
        return null;
    }

    private String[] getImageUrls(){
        String URL = "https://yandex.ru/images/search?text=" + town;
        try {
            Document html = Jsoup.connect(URL).get();
            System.out.println(html.title());
            //System.out.println(html);
            Elements objects = html.select(".serp-item__link img");
            ArrayList<String> urls = new ArrayList<>();
            for (Element object: objects){
                urls.add(object.absUrl("src"));
            }
            Collections.shuffle(urls);
            String[] ans = new String[IMAGE_COUNT];
            for (int i = 0; i < IMAGE_COUNT; i++){
                ans[i] = urls.get(i);
            }
            return ans;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
