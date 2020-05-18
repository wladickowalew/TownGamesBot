import java.util.ArrayList;

public class Room {

    private static int id_generator = 0;

    private int id;
    private String name;
    private String town;
    private User root;
    private ArrayList<User> users;
    private ImageLoader loader;

    public Room(String name, User root){
        id = id_generator++;
        this.root = root;
        users = new ArrayList<User>();
        users.add(root);
        this.name = name;
    }

    public void addUser(User user){
        users.add(user);
    }

    public void removeUser(User user){
        users.remove(user);
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
        this.town = getRandomTown();
        loader = new ImageLoader(town, 5);
    }

    public String getImageURL() {
        return loader.getNextImageURL();
    }

    public boolean isEnd(){
        return loader.isEmpty();
    }
}
