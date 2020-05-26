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
    private int imageCount;
    private ArrayList<URL> imageUrls;

    public ImageLoader(String town, int count){
        imageCount = count;
        imageUrls = getImageUrls(town);
    }

    public ImageLoader(ImageLoader loader){
        imageCount = loader.getImageCount();
        imageUrls = (ArrayList<URL>) loader.getImageUrls().clone();
    }

    @Override
    protected ImageLoader clone() {
        return new ImageLoader(this);
    }

    public boolean isEmpty(){
        return imageUrls.isEmpty();
    }

    public String getNextImageURL(){
        return imageUrls.remove(0).toString();
    }

    public Image getNextImage(){
        try {
            return ImageIO.read(imageUrls.remove(0));
        } catch (Exception e) {
            System.out.println("Картинки кончились");
        }
        return null;
    }

    public int getImageCount() {
        return imageCount;
    }

    public ArrayList<URL> getImageUrls() {
        return imageUrls;
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
            urls = new ArrayList<>(urls.subList(0, imageCount));
            return urls;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
