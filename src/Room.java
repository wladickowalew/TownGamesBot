import java.beans.Expression;
import java.util.ArrayList;

public class Room {

    private static int id_generator = 0;

    private int id;
    private String name;
    private String town;
    private Long root;
    private ArrayList<User> users;
    private ImageLoader loader;
    private int rounds = 3;
    private int current_round;

    public Room(String name, Long root){
        id = id_generator++;
        this.root = root;
        users = new ArrayList<User>();
        this.name = name;
    }

    public void addUser(User user){
        if (!users.contains(user))
            users.add(user);
    }

    public void removeUser(User user){
        if (users.contains(user))
            users.remove(user);
    }

    public String getUsers(){
        StringBuilder builder = new StringBuilder();
        for (User user: users){
            builder.append(user.toString() + "\n");
        }
        return builder.toString();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getRandomTown(){
        String[] towns = {"Смоленск", "Самара", "Лондон", "Амстердам", "Москва", "Берлин", "Дрезден", "Торонто"};
        int n = towns.length;
        int r = (int)(Math.random() * n);
        return towns[r];
    }

    public void startRoom(){
        current_round = 0;
        for (User user: users){
            user.startGameInRoom(loader);
        }
    }

    public void checkEndRound(){

    }

    public void newRound(){
        this.town = getRandomTown();
        loader = new ImageLoader(town, 5);
        for (User user: users){
            user.startRoundInRoom(loader);
        }
    }

    public String getImageURL() {
        return loader.getNextImageURL();
    }

    public boolean isEnd(){
        return loader.isEmpty();
    }

    public boolean isRoot(Long user){
        System.out.println(user);
        System.out.println(root);
        return root.equals(user);
    }
}
