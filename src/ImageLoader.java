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
    private ArrayList<URL> imageUrls;

    public ImageLoader(String town){
        imageUrls = getImageUrls(town);
    }

    public boolean isEmpty(){
        return imageUrls.isEmpty();
    }

    public Image getNextImage(){
        try {
            return ImageIO.read(imageUrls.remove(0));
        } catch (Exception e) {
            System.out.println("Картинки кончились");
        }
        return null;
    }

    private ArrayList<URL> getImageUrls(String town){
        String URL = "https://yandex.ru/images/search?text=" + town;
        try {
            Document html = Jsoup.connect(URL).get();
            //System.out.println(html.title());
            //System.out.println(html);
            Elements objects = html.select(".serp-item__link img");
            ArrayList<URL> urls = new ArrayList<>();
            for (Element object: objects){
                String url = object.absUrl("src");
                urls.add(new URL(url));
            }
            Collections.shuffle(urls);
            urls = new ArrayList<>(urls.subList(0, IMAGE_COUNT));
            return urls;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
