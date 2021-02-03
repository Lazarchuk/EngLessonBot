package ua.eng.lesson.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.eng.lesson.handlers.InputCallbackQueryHandler;
import ua.eng.lesson.handlers.InputMessageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class HandlersManager {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();
    private Map<BotState, InputCallbackQueryHandler> buttonHandlers = new HashMap<>();

    public HandlersManager(List<InputMessageHandler> messageHandlers, List<InputCallbackQueryHandler> buttonHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
        buttonHandlers.forEach(handler -> this.buttonHandlers.put(handler.getHandlerName(), handler));
    }

    // Search a handler for text message
    public BotApiMethod<?> processInputMessage(BotState currentState, Message message){
        InputMessageHandler currentMessageHandler = messageHandlers.get(currentState);

        // If the appropriate handler was not found by the current BotState
        if (currentMessageHandler == null){
            log.error("A message handler for the BotState \"{}\" was not found. Implement the handler or change BotState.", currentState);
            return messageHandlers.get(BotState.START_APP).handle(message);
        }
        return currentMessageHandler.handle(message);
    }

    // Search a handler for button event
    public BotApiMethod<?> processInputCallbackQuery(BotState currentState, CallbackQuery buttonQuery){
        InputCallbackQueryHandler currentButtonHandler = buttonHandlers.get(currentState);

        // If the appropriate handler was not found by the current BotState
        if (currentButtonHandler == null){
            log.error("A button handler for the BotState \"{}\" was not found. Implement the handler or change BotState.", currentState);
            return sendAnswerCallbackQuery(buttonQuery);
        }
        return currentButtonHandler.handle(buttonQuery);
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(CallbackQuery callbackQuery){
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setShowAlert(true);
        answerCallbackQuery.setText("Button is not supported.");
        return answerCallbackQuery;
    }
}
