package ua.eng.lesson.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.eng.lesson.bot.BotState;
import ua.eng.lesson.service.ReplyMessageService;

import java.util.ArrayList;
import java.util.List;

/*
* The class loads start menu by the command "/start"
* */

@Component
@Slf4j
public class StartMenuHandler implements InputMessageHandler {
    private ReplyMessageService messageService;

    public StartMenuHandler(ReplyMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public BotApiMethod<?> handle(Message message) {
        String chatId = message.getChatId().toString();
        SendMessage replyMessage = messageService.getReplyMessage(chatId, "reply.startMenu");
        replyMessage.setReplyMarkup(getStartMenuButtons(message.getFrom().getId()));
        return replyMessage;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.START_APP;
    }

    private InlineKeyboardMarkup getStartMenuButtons(Integer userId){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonJoinLesson = new InlineKeyboardButton("Join the lesson");
        InlineKeyboardButton buttonLesson = new InlineKeyboardButton("Create lesson");
        buttonJoinLesson.setCallbackData("buttonJoinLesson");
        buttonLesson.setCallbackData("buttonCreateLesson");

        List<InlineKeyboardButton> startButtons = new ArrayList<>();
        startButtons.add(buttonJoinLesson);
        startButtons.add(buttonLesson);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(startButtons);

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackQuery){
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }
}
