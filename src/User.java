import java.awt.*;

public class User {

    private String town;
    private ImageLoader loader;
    private String command;

    public User(String town) {
        this.town = town;
        loader = new ImageLoader(town);
        command = "";
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public Image getImage() {
        return loader.getNextImage();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isEnd(){
        return loader.isEmpty();
    }
}
