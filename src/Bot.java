import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Bot extends TelegramLongPollingBot {

    public HashMap<Long, User> users = new HashMap<>();
    public HashMap<Integer, Room> rooms = new HashMap<>();

    public static Bot init(){
        ApiContextInitializer.init();
        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            Bot b = new Bot();
            botapi.registerBot(b);
            return b;
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void testImages(){
        ImageLoader test = new ImageLoader("Москва", 5);
        //test
        int i = 0;
        while (!test.isEmpty()){
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

    public String getRandomTown(){
        String[] towns = {"Смоленск", "Самара", "Лондон", "Амстердам", "Москва", "Берлин", "Дрезден", "Торонто"};
        int n = towns.length;
        int r = (int)(Math.random() * n);
        return towns[r];
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        long id = message.getChatId();

        if (text.equals("/start")) {
            sendMessage(message, "Здравствуйте, вот, что я могу!");
            sendMessage(message, textHelp());
            return;
        }

        if (!users.containsKey(id)){
            String name = message.getFrom().getFirstName() + " " + message.getFrom().getLastName();
            sendMessage(message, "Привет, "+name+". Чтобы начать игру введи /start.");
            users.put(id, new User(message.getChatId(), name));
            return;
        }

        User user = users.get(id);
        System.out.println(user + ": " + text);
        if (condition_dispatcher(message, user)) return;
        if (command_dispatcher(message, user))   return;
    }

    private void logOut(Message message, User user){
        sendMessage(message, "Ну и пожалуйста, сам с собой играй!");
        user.setCommand("");
    }

    public boolean condition_dispatcher(Message message, User user){
        if (user.getCommand().equals("")) return false;
        String text = message.getText();
        switch (user.getCommand()) {
            case "begin":
                if (text.equals("Да")) {
                    user.setCommand("SendImage");
                    user.startGame(getRandomTown());
                    sendMessage(message, "Отлично, начнём игру! Что это за город? (Ответь на русском языке)");
                    sendImage(message, user.getImageURL());
                    break;
                }
                if (text.equals("Нет")) {
                    logOut(message, user);
                    break;
                }
                sendMessage(message, "Ты мне втираешь какую-то дичь! Будем играть?");
                break;
            case "SendImage":
                if (text.equals(user.getTown())) {
                    //win
                    user.end_round(true);
                    if (user.isIn_room()) {
                        rooms.get(user.getRoom_id()).checkEndRound();
                        sendMessage(message, "Ты угадал! Раунд Окончен! Ты набрал всего : " +
                                user.getPoints() + " очков. Ожидай следующего раунда");
                    }else {
                        sendMessage(message, "Мои поздравления! Ты выиграл! Ты набрал: " +
                                user.getPoints() + " очков. Хочешь ещё?");
                        user.setCommand("begin");
                    }
                    break;
                }
                if (user.isEnd()) {
                    //end game
                    user.end_round(false);
                    if (user.isIn_room()) {
                        rooms.get(user.getRoom_id()).checkEndRound();
                        sendMessage(message, "Ты не угадал( Раунд Окончен! Ты набрал всего : " +
                                user.getPoints() + " очков. Ожидай следующего раунда");
                    }else {
                        sendMessage(message, "К сожалению ты не очень силён в изображениях городов. Это " +
                                user.getTown() + ". Хочешь попробовать ещё?");
                        user.setCommand("begin");
                    }
                    break;
                }
                sendMessage(message, "Ты ошибся( Это не " + text + ". Попробуй ещё раз!");
                sendImage(message, user.getImageURL());
                break;
            case "changelevel":
                if (text.equals("лёгкий") || text.equals("средний") || text.equals("сложный") || text.equals("ХАРД")) {
                    user.setLevel(text);
                    sendMessage(message, "Сложность успешно установлена");
                    user.setCommand("");
                }else{
                    sendMessage(message, "Я тебя не понимаю, какой уровень сложности?");
                }
                break;
            case "createRoom":
                Room room = new Room(text, message.getChatId());
                room.addUser(user);
                rooms.put(room.getId(), room);
                sendMessage(message, "Вы создали комнату: " + text + ". Её id: " + room.getId());
                user.setCommand("");
                break;
            case "removeRoom":
                try{
                    int room_id = Integer.parseInt(text);
                    if (rooms.get(room_id).isRoot(message.getChatId())) {
                        rooms.remove(room_id);
                        sendMessage(message, "Вы успешно удалили комнату");
                    }else{
                        sendMessage(message, "Вы не можете удалить эту комнату");
                    }
                    user.setCommand("");
                }catch(NullPointerException e){
                    e.printStackTrace();
                    sendMessage(message, "Комнаты с таким id не существует");
                }catch(Exception e){
                    e.printStackTrace();
                    sendMessage(message, "Я тебя не понимаю, введи id комнаты?");
                }
                break;
            case "toRoom":
                try{
                    int room_id = Integer.parseInt(text);
                    room = rooms.get(room_id);
                    room.addUser(user);
                    sendMessage(message, "Вы в комнате " + room.getName());
                    user.setCommand("");
                }catch(NullPointerException e){
                    e.printStackTrace();
                    sendMessage(message, "Комнаты с таким id не существует");
                }catch(Exception e){
                    e.printStackTrace();
                    sendMessage(message, "Я тебя не понимаю, введи id комнаты?");
                }
                break;
            case "fromRoom":
                try{
                    int room_id = Integer.parseInt(text);
                    rooms.get(room_id).removeUser(user);
                    sendMessage(message, "Вы не в комнате");
                    user.setCommand("");
                }catch(NullPointerException e){
                    e.printStackTrace();
                    sendMessage(message, "Комнаты с таким id не существует");
                }catch(Exception e){
                    e.printStackTrace();
                    sendMessage(message, "Я тебя не понимаю, введи id комнаты?");
                }
                break;
            case "roomUsers":
                try{
                    int room_id = Integer.parseInt(text);
                    room = rooms.get(room_id);
                    if (room.isRoot(user.getChatID())) {
                        sendMessage(message, room.getUsers());
                    }else{
                        sendMessage(message, "Вы не можете просмотреть пользователей комнаты");
                    }
                    user.setCommand("");
                }catch(NullPointerException e){
                    e.printStackTrace();
                    sendMessage(message, "Комнаты с таким id не существует");
                }catch(Exception e){
                    e.printStackTrace();
                    sendMessage(message, "Я тебя не понимаю, введи id комнаты?");
                }
                break;
            case "startRoom":
                try{
                    int room_id = Integer.parseInt(text);
                    room = rooms.get(room_id);
                    if (room.isRoot(user.getChatID())) {
                        room.startRoom();
                    }else{
                        sendMessage(message, "Вы не можете запустить игру в этой комнате");
                        user.setCommand("");
                    }
                }catch(NullPointerException e){
                    e.printStackTrace();
                    sendMessage(message, "Комнаты с таким id не существует");
                }catch(Exception e){
                    e.printStackTrace();
                    sendMessage(message, "Я тебя не понимаю, введи id комнаты?");
                }
                break;
            case "Waiting":
                sendMessage(message, "Ожидайте продолжения");
                break;
            default:
                user.setCommand("");
                sendMessage(message, "Неизвестная ошибка");
                break;
        }
        return true;
    }

    public boolean command_dispatcher(Message message, User user){
        String text = message.getText();
        if (text.equals("/help")) {
            sendMessage(message, textHelp());
            return true;
        }
        if (text.equals("/start_game")) {
            sendMessage(message, "Здравствуйте, не хотите ли сыграть в одну игру? Вам предстоит увидеть изображение "+
                    "и отгдадать, что за город изображён на нём. (Да/Нет)");
            users.get(message.getChatId()).setCommand("begin");
            return true;
        }
        if (text.equals("/stop")){
            logOut(message, user);
            return true;
        }
        if (text.equals("/level")){
            sendMessage(message, "Ваш уровень сложности: " + user.getLevel());
            return true;
        }
        if (text.equals("/change_level")){
            sendMessage(message, "Выберите уровень сложности: лёгкий, средний, сложный, ХАРД");
            user.setCommand("changelevel");
            return true;
        }
        if (text.equals("/new_room")){
            sendMessage(message, "Введите название вашей комнаты");
            user.setCommand("createRoom");
            return true;
        }
        if (text.equals("/remove_room")){
            sendMessage(message, "Введите id комнаты");
            user.setCommand("removeRoom");
            return true;
        }
        if (text.equals("/show_rooms")){
            StringBuilder builder = new StringBuilder();
            if (rooms.isEmpty()){
                sendMessage(message, "Комант, увы, нет(");
                return true;
            }
            for (Room room: rooms.values()){
                builder.append(room.getName()+" id: "+room.getId() + "\n");
            }
            sendMessage(message, builder.toString());
            return true;
        }
        if (text.equals("/to_room")){
            sendMessage(message, "Введите id комнаты");
            user.setCommand("toRoom");
            return true;
        }
        if (text.equals("/from_room")){
            sendMessage(message, "Введите id комнаты");
            user.setCommand("fromRoom");
            return true;
        }
        if (text.equals("/show_room_users")){
            sendMessage(message, "Введите id комнаты");
            user.setCommand("roomUsers");
            return true;
        }
        if (text.equals("/start_room")){
            sendMessage(message, "Введите id комнаты");
            user.setCommand("startRoom");
            return true;
        }
        return false;
    }

    private String textHelp() {
        return "Привет, я бот для игры в города и умею мого чего:\n" +
                "/help - выведет это сообщение,\n" +
                "/start - начать общение с ботом игру,\n" +
                "/level - посмотреть урвень сложности,\n" +
                "/change_level - изменить уровень сложности,\n" +
                "/start_game - начать игру\n"+
                "/stop  - остановить игру/операцию\n"+
                "/new_room - создать новую комнату,\n" +
                "/remove_room - удалить комнату,\n" +
                "/show_rooms - показать все комнаты\n"+
                "/to_room - войти в комнату\n" +
                "/from_room - выйти из комнаты\n"+
                "/show_room_users - показать пользователей в комнате\n"+
                "/start_room - запуск игры в комнате";
    }

    public void sendMessage(Long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        System.out.println("Message "+text+" Send to " + chatId);
        //message.enableMarkdown(true);
        //setButtons(message);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendImage(Long chatId, String url){
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(url);
        System.out.println("Photo send to " + chatId);
        try {
            execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Message m, String text){
        sendMessage(m.getChatId(), text);
    }

    private void sendImage(Message m, String url){
       sendImage(m.getChatId(), url);
    }

    @Override
    public String getBotUsername() {
        return "java_try_bot";
    }

    @Override
    public String getBotToken() {
        return "1270371974:AAG3thXALtUx_pzISpRnq1Hz2F8Nn3MkeIA";
    }
}
