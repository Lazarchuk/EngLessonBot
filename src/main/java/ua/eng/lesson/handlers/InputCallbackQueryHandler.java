package ua.eng.lesson.handlers;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ua.eng.lesson.bot.BotState;

/*
* Handler uses in order to handle button events
* */
public interface InputCallbackQueryHandler {
    BotApiMethod<?> handle(CallbackQuery callbackQuery);
    BotState getHandlerName();
}
