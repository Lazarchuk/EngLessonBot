package ua.eng.lesson.handlers;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.eng.lesson.bot.BotState;

/*
* Handler only for handle text messages from users
* */
public interface InputMessageHandler {
    BotApiMethod<?> handle(Message message);
    BotState getHandlerName();
}
