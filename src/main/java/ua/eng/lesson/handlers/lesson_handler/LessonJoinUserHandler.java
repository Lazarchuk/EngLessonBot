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
* The class process a password entered by user and add this user to the lesson room
* */

@Component
public class LessonJoinUserHandler implements InputMessageHandler, InputCallbackQueryHandler {
    private DataCache dataCache;

    public LessonJoinUserHandler(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    @Override
    public BotApiMethod<?> handle(Message message) {
        String text = message.getText();
        String chatId = message.getChatId().toString();
        if (dataCache.getRooms().containsKey(text)){
            SendMessage replyMessage = new SendMessage(chatId, "Wellcome!\nTake a word");
            replyMessage.setReplyMarkup(getIterationButton());
            dataCache.addLessonUser(chatId, text);
            return replyMessage;
        }
        else {
            return new SendMessage(chatId, "Wrong password. Try again");
        }
    }

    @Override
    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String userPass = dataCache.getUserPassword(chatId);

        if (dataCache.getRooms().containsKey(userPass) &&
                dataCache.getRooms().get(userPass).getChatIDs().contains(chatId)){
            SendMessage replyMessage = new SendMessage(chatId, "Wellcome!\nTake a word");
            replyMessage.setReplyMarkup(getIterationButton());
            return replyMessage;
        }
        else {
            return new SendMessage(chatId, "Write a password");
        }

    }

    @Override
    public BotState getHandlerName() {
        return BotState.HANDLE_TASK;
    }

    private InlineKeyboardMarkup getIterationButton(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonGet = new InlineKeyboardButton("Get a word");
        buttonGet.setCallbackData("buttonGetLessonWord");

        List<InlineKeyboardButton> buttonsRow1 = new ArrayList<>();
        buttonsRow1.add(buttonGet);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(buttonsRow1);

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }
}
