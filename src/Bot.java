import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
        return "Смоленск";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        System.out.println(text);
        long id = message.getChatId();

        if (text.equals("/start")) {
            sendMessage(message, "Здравствуйте, не хотите ли сыграть в одну игру? Вам предстоит увидеть изображение "+
                                     "и отгдадать, что за город изображён на нём.");
            User user = new User(getRandomTown());
            user.setCommand("begin");
            users.put(id, user);

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

    public boolean condition_dispatcher(Message message, User user){
        if (user.getCommand().equals("")) return false;
        String text = message.getText();
        switch (user.getCommand()){
            case "name1":
                user.setName(text);
                user.setCommand("");
                sendMessage(message, "Приятно познакомиться, " + text);
                break;
            case "city1":
                user.setCity(text);
                user.setCommand("");
                sendMessage(message, "город успешно сохранён");
                break;
            case "age1":
                try {
                    user.setAge(Integer.parseInt(text));
                    user.setCommand("");
                    sendMessage(message, "Возраст успешно сохранён");
                }catch (Exception e){
                    sendMessage(message, "Вы ввели не целое число. Введите только целое число");
                }
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
        if (text.equals("/set_city")){
            sendMessage(message, "Введите название города.");
            user.setCommand("city1");
            return true;
        }

        if (text.equals("/city")){
            sendMessage(message, "Ваш город: " + user.getCity());
            return true;
        }

        if (text.equals("/set_age")){
            sendMessage(message, "Введите, сколько вам лет.");
            user.setCommand("age1");
            return true;
        }

        if (text.equals("/age")){
            sendMessage(message, "Ваш возраст: " + user.getAge());
            return true;
        }

        if (text.equals("/rename")){
            sendMessage(message, "Введите ваше новое имя: ");
            user.setCommand("name1");
            return true;
        }

        if (text.equals("/info")){
            sendMessage(message, user.getInfo());
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

    @Override
    public String getBotUsername() {
        return "java_try_bot";
    }

    @Override
    public String getBotToken() {
        return "1270371974:AAG3thXALtUx_pzISpRnq1Hz2F8Nn3MkeIA";
    }
}
