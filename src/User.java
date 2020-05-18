import java.awt.*;

public class User {

    private String name;
    private Long chatID;
    private String town;
    private ImageLoader loader;
    private String command;
    private int level;     //коэффициент за уровень сложности
    private int attempts;
    private int points;

    public User(Long id, String name) {
        this.name = name;
        chatID = id;
        command = "";
        level = 3;
    }

    @Override
    public String toString() {
        return "name=" + name +", ID=" + chatID;
    }

    public void startGame(String town){
        this.town = town;
        switch (level){
            case 1:  attempts = 8; break;
            case 3:  attempts = 5; break;
            case 7:  attempts = 3; break;
            case 40: attempts = 1; break;
        }
        loader = new ImageLoader(town, attempts);
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

    public String getImageURL() {
        attempts--;
        return loader.getNextImageURL();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getLevel() {
        switch (level) {
            case 1:  return "лёгкий";
            case 3:  return "средний";
            case 7:  return "сложный";
            case 40: return "ХАРД";
        }
        return null;
    }

    public void setLevel(String level) {
        switch (level) {
            case "лёгкий":  this.level = 1; break;
            case "средний": this.level = 3; break;
            case "сложный": this.level = 7; break;
            case "ХАРД":    this.level = 40; break;
        }
    }

    public int getPoints(){
        return (attempts + 1) * level;
    }

    public boolean isEnd(){
        return attempts == 0;
    }

    public Long getChatID() {
        return chatID;
    }

}
