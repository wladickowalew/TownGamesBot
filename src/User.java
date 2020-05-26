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
    private boolean in_room;
    private int room_id;
    private boolean end_round;

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
        in_room = false;
    }

    public void startGameInRoom(){
        in_room = true;
        points = 0;
        switch (level){
            case 1:  attempts = 8; break;
            case 3:  attempts = 5; break;
            case 7:  attempts = 3; break;
            case 40: attempts = 1; break;
        }
        sendMessage("Игра в комнате началась!");
    }

    public void startRoundInRoom(ImageLoader loader, int round){
        setLoader(loader);
        end_round = false;
        sendMessage("Роунд: " + (round + 1));
        command = "SendImage";
        sendImage();
    }

    public void sendImage(){
        Main.sendImage(chatID, getImageURL());
    }

    public void sendMessage(String text){
        Main.sendMessage(chatID, text);
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

    public void end_round(boolean win){
        end_round = true;
        points += (attempts + 1) * level;
    }

    public int getPoints(){
        return points;
    }

    public boolean isEnd(){
        return attempts == 0;
    }

    public Long getChatID() {
        return chatID;
    }

    public void setLoader(ImageLoader loader) {
        this.loader = loader;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isIn_room() {
        return in_room;
    }

    public void setIn_room(boolean in_room) {
        this.in_room = in_room;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public boolean isEnd_round() {
        return end_round;
    }
}
