package ua.eng.lesson.handlers.lesson_handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ua.eng.lesson.bot.BotState;
import ua.eng.lesson.cache.DataCache;
import ua.eng.lesson.handlers.InputCallbackQueryHandler;


import java.util.Set;

/*
* The class is available for admin and remove all information from the lesson cache about the room
* After closing it redirects admin user to the start state
* */
@Component
public class LessonCloseHandler implements InputCallbackQueryHandler {

    private DataCache dataCache;

    public LessonCloseHandler(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    @Override
    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        Integer userId = callbackQuery.getFrom().getId();

        // Get room password
        String roomPassword = dataCache.getUserPassword(chatId);

        // Get all users in the room by password
        Set<String> chatIDs = dataCache.getRooms().get(roomPassword).getChatIDs();

        // Remove all chatIDs from Map of LessonUserData
        chatIDs.forEach(id -> {
            dataCache.getLessonUsersData().remove(chatId);
        });

        // Remove the room from Map of LessonRoom
        dataCache.getRooms().remove(roomPassword);

        dataCache.setUserCurrentBotState(userId, BotState.START_APP);
        return new SendMessage(chatId, "The lesson was closed\n/start");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CLOSE_LESSON;
    }
}
