package ua.eng.lesson.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ua.eng.lesson.cache.DataCache;

/*
* The class distributes input message/callback to a different bot states in order to define necessary handler.
* The class writes each bot state to each user into the data cache.
* */

@Component
@Slf4j
public class UpdatesDispatcher {
    private DataCache dataCache;
    private HandlersManager handlersManager;

    public UpdatesDispatcher(DataCache dataCache, HandlersManager handlersManager) {
        this.dataCache = dataCache;
        this.handlersManager = handlersManager;
    }

    public BotApiMethod<?> handleUpdate(Update update){
        BotApiMethod<?> replyMessage = null;

        if (update.hasCallbackQuery()){
            CallbackQuery buttonQuery = update.getCallbackQuery();
            log.info("New callback query from user: {} by button: {}", getUserName(buttonQuery.getFrom()), buttonQuery.getData());
            replyMessage = handleCallbackQuery(buttonQuery);
        }

        Message message = update.getMessage();
        if (message != null && message.hasText()){
            log.info("New message from user: {} with text: {}", getUserName(message.getFrom()), message.getText());
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    private BotApiMethod<?> handleInputMessage(Message message){
        String inputMessage = message.getText();
        Integer userId = message.getFrom().getId();
        BotState botState;
        BotApiMethod<?> replyMessage;

        switch (inputMessage){
            case "/start":
                botState = BotState.START_APP;
                break;
            default:
                botState = dataCache.getUserCurrentBotState(userId);
                break;
        }

        dataCache.setUserCurrentBotState(userId, botState);
        replyMessage = handlersManager.processInputMessage(botState, message);
        return replyMessage;
    }

    private BotApiMethod<?> handleCallbackQuery(CallbackQuery buttonQuery){
        String queryData = buttonQuery.getData();
        Integer userId = buttonQuery.getFrom().getId();
        BotState botState;
        BotApiMethod<?> replyMessage;

        // "buttonCreateTask - is only for admin. BotState.CREATE_TASK - is only for admin"
        switch (queryData){
            case "buttonCreateLesson":
                botState = BotState.CREATE_TASK;
                break;
            case "buttonMenu":
                botState = BotState.START_APP;
                break;
            case "buttonJoinLesson":
                botState = BotState.HANDLE_TASK;
                break;
            case "buttonGetLessonWord":
                botState = BotState.ITERATE_LESSON_WORDS;
                break;
            case "buttonSkip":
                botState = BotState.ITERATE_LESSON_WORDS;
                break;
            case "buttonCloseLesson":
                botState = BotState.CLOSE_LESSON;
                break;
            case "buttonChangeTopic":
                botState = BotState.CHANGE_TOPIC;
                break;
            default:
                botState = dataCache.getUserCurrentBotState(userId);
                break;
        }

        dataCache.setUserCurrentBotState(userId, botState);
        replyMessage = handlersManager.processInputCallbackQuery(botState, buttonQuery);
        return replyMessage;
    }

    private String getUserName(User user){
        String userName = user.getUserName();
        return userName != null? userName: String.format("%s %s", user.getFirstName(), user.getLastName());
    }

}
