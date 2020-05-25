public class Main {

    private static Bot bot;

    public static void main(String[] args) {
        bot = Bot.init();
    }

    public static void sendMessage(Long chatId, String text){
        bot.sendMessage(chatId, text);
    }

    public static void sendImage(Long chatId, String url){
        bot.sendImage(chatId, url);
    }
}
