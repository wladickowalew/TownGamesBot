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

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            botapi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    private void testImages(){
        ImageLoader test = new ImageLoader("Москва");
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
        System.out.println(text);
        long id = message.getChatId();

        if (text.equals("/start")) {
            sendMessage(message, "Здравствуйте, не хотите ли сыграть в одну игру? Вам предстоит увидеть изображение "+
                                     "и отгдадать, что за город изображён на нём. (Да/Нет)");
            users.put(id, new User());
            return;
        }

        if (!users.containsKey(id)){
            sendMessage(message, "Ты не хочешь играть со мной!, введи /start.");
            return;
        }

        User user = users.get(id);
        if (condition_dispatcher(message, user)) return;
        if (command_dispatcher(message, user))   return;
    }

    private void logOut(Message message, User user){
        sendMessage(message, "Ну и пожалуйста, сам с собой играй!");
        users.remove(message.getChatId());
    }

    public boolean condition_dispatcher(Message message, User user){
        if (user.getCommand().equals("")) return false;
        String text = message.getText();
        switch (user.getCommand()){
            case "begin":
                if (text.equals("Да")){
                    user.setCommand("SendImage");
                    user.startGame(getRandomTown());
                    sendMessage(message, "Отлично, начнём игру! Что это за город? (Ответь на русском языке)");
                    sendImage(message, user.getImageURL());
                    break;
                }
                if (text.equals("Нет")){
                   logOut(message, user);
                   break;
                }
                sendMessage(message, "Ты мне втираешь какую-то дичь! Будем играть?");
                break;
            case "SendImage":
                if(text.equals(user.getTown())){
                    //win
                    sendMessage(message, "Мои поздравления! Ты выиграл! Хочешь ещё?");
                    user.setCommand("begin");
                    break;
                }
                if(user.isEnd()){
                    //end game
                    sendMessage(message, "К сожалению ты не очень силён в изображениях городов. Это " +
                                              user.getTown()+". Хочешь попробовать ещё?");
                    user.setCommand("begin");
                    break;
                }
                sendMessage(message, "Ты ошибся( Это не " + text + ". Попробуй ещё раз!");
                sendImage(message, user.getImageURL());
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
        if (text.equals("/stop")){
            logOut(message, user);
            return true;
        }
        return false;
    }

    private void sendMessage(Message m, String text){
        SendMessage message = new SendMessage();
        message.setChatId(m.getChatId());
        message.setText(text);
        //message.enableMarkdown(true);
        //setButtons(message);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendImage(Message m, String url){
        SendPhoto photo = new SendPhoto();
        photo.setChatId(m.getChatId());
        photo.setPhoto(url);
        try {
            execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
