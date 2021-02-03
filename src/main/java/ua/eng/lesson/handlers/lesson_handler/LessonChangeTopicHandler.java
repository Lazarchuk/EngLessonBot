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
import ua.eng.lesson.cache.LessonRoom;
import ua.eng.lesson.handlers.InputCallbackQueryHandler;
import ua.eng.lesson.handlers.InputMessageHandler;

import java.util.ArrayList;
import java.util.List;

/*
* The class is available for admin and change the topic in the room, which admin belongs
* */
@Component
public class LessonChangeTopicHandler implements InputCallbackQueryHandler, InputMessageHandler {
    private DataCache dataCache;

    public LessonChangeTopicHandler(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    // Show an available topics for the admin
    @Override
    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String roomPassword = dataCache.getUserPassword(chatId);
        Integer userId = callbackQuery.getFrom().getId();
        SendMessage replyMessage = new SendMessage(chatId, getTopics());
        LessonRoom room = null;

        if (dataCache.getTopics().isEmpty()) {
            replyMessage.setReplyMarkup(getGoToMenuButton());
            return replyMessage;
        }

        if (roomPassword != null){
            room = dataCache.getRooms().get(roomPassword);
        }

        // If room not NULL, change topic inside this handler, else -> create new room
        if (room != null){
            return replyMessage;
        }
        else {
            dataCache.setUserCurrentBotState(userId, BotState.CREATE_TASK);
            return replyMessage;
        }
    }

    // Check if such topic exists and save that topic in the lesson cache
    @Override
    public BotApiMethod<?> handle(Message message) {
        String inputText = message.getText();
        String chatId = message.getChatId().toString();
        String roomPassword = dataCache.getUserPassword(chatId);

        for (String topic : dataCache.getTopics()) {
            if (inputText.toLowerCase().equals(topic.toLowerCase())) {
                SendMessage replyMessage = new SendMessage(chatId, String.format("Topic was changed to \"%s\"", topic));
                replyMessage.setReplyMarkup(getIterationButton());

                dataCache.changeLessonTopic(roomPassword, topic);
                return replyMessage;
            }
        }
        return new SendMessage(chatId, "There is no such topic. Write again");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CHANGE_TOPIC;
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
}
