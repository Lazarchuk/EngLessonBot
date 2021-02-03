package ua.eng.lesson.handlers.lesson_handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.eng.lesson.bot.BotState;
import ua.eng.lesson.cache.DataCache;
import ua.eng.lesson.handlers.InputCallbackQueryHandler;
import ua.eng.lesson.handlers.InputMessageHandler;

import java.util.ArrayList;
import java.util.List;

/*
* The class is only available for admin user.
* The class creates a lesson room with password, topic, Set of the users in this room
*   and Collection of words according to the topic
* */

@Component
public class LessonCreateTaskHandler implements InputCallbackQueryHandler, InputMessageHandler {

    private DataCache dataCache;

    public LessonCreateTaskHandler(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    @Override
    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        SendMessage replyMessage = new SendMessage(chatId, getTopics());
        if (dataCache.getTopics().isEmpty()) {
            replyMessage.setReplyMarkup(getGoToMenuButton());
        }

        return replyMessage;
    }

    @Override
    public BotApiMethod<?> handle(Message message) {
        String inputText = message.getText();
        String chatId = message.getChatId().toString();

        for (String topic : dataCache.getTopics()) {
            if (inputText.toLowerCase().equals(topic.toLowerCase())) {
                String password = generateLessonPassword();
                SendMessage replyMessage = new SendMessage(chatId, String.format("Lesson with topic \"%s\" was created\nPassword: %s", topic, password));
                replyMessage.setReplyMarkup(getIterationButton());

                dataCache.addLessonRoom(password, topic);
                dataCache.setRoomAdmin(password, chatId);
                dataCache.addLessonUser(chatId, password);
                return replyMessage;
            }
        }
        return new SendMessage(chatId, "There is no such topic. Write again");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CREATE_TASK;
    }

    private String getTopics(){
        String answer = "There are no available topics";

        if (!dataCache.getTopics().isEmpty()) {
            StringBuilder sb = new StringBuilder("Chose topic. Type by the keyboard:\n");
            dataCache.getTopics().forEach(topic ->{
                sb.append(topic).append("\n");
            });
            answer = sb.toString();
        }
        return answer;
    }

    private InlineKeyboardMarkup getGoToMenuButton(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        InlineKeyboardButton buttonAdmin = new InlineKeyboardButton("Go to menu");
        buttonAdmin.setCallbackData("buttonGoToMenu");
        buttons.add(buttonAdmin);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(buttons);

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup getIterationButton(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonGet = new InlineKeyboardButton("Get a word");
        buttonGet.setCallbackData("buttonGetLessonWord");

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(buttonGet);
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(buttons);

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    // 6 digits password. Random generation with checking if no such passwors in the lesson cache
    private String generateLessonPassword(){
        int randomNumber = (int) (Math.random()*900000) +100000;
        String password = Integer.toString(randomNumber);
        if (dataCache.getRooms().containsKey(password)){
            password = generateLessonPassword();
        }
        return password;
    }

}
