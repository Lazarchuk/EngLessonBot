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
import ua.eng.lesson.model.Word;

import java.util.ArrayList;
import java.util.List;

/*
* The class shows word by word to users in the lesson room.
* The words are loaded from the lesson room.
* */
@Component
public class LessonIterateWordsHandler implements InputMessageHandler, InputCallbackQueryHandler {

    private DataCache dataCache;

    public LessonIterateWordsHandler(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    @Override
    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        Integer userId = callbackQuery.getFrom().getId();
        String userPass = dataCache.getUserPassword(chatId);
        SendMessage replyMessage = new SendMessage(chatId, "Use button \"Get next\" to get a word");

        // If user does not exist in the room, ask a password
        if (!dataCache.getRooms().containsKey(userPass) ||
                !dataCache.getRooms().get(userPass).getChatIDs().contains(chatId)){
            dataCache.setUserCurrentBotState(userId, BotState.HANDLE_TASK);
            return new SendMessage(chatId, "Write a password");
        }

        else if (callbackQuery.getData().equals("buttonGetLessonWord")){
            String text = "List is empty";
            replyMessage.setReplyMarkup(getIterationButtons(userPass, chatId));
            if (!dataCache.getLessonWords(userPass).isEmpty()){
                Word selectedWord = dataCache.getLessonWords(userPass).poll();
                text = selectedWord.getWordEng();
                dataCache.addUserSelectedWord(chatId, selectedWord);
            }
            replyMessage.setChatId(chatId);
            replyMessage.setText(text);
            return replyMessage;
        }

        else if (callbackQuery.getData().equals("buttonSkip")){
            String text = "List is empty. Try to explain this one";
            replyMessage.setReplyMarkup(getIterationButtons(userPass, chatId));
            if (!dataCache.getLessonWords(userPass).isEmpty()){
                dataCache.getLessonWords(userPass).addLast(dataCache.getUserSelectedWord(chatId));
                Word selectedWord = dataCache.getLessonWords(userPass).poll();
                text = selectedWord.getWordEng();
                dataCache.addUserSelectedWord(chatId, selectedWord);
            }
            replyMessage.setChatId(chatId);
            replyMessage.setText(text);
            return replyMessage;
        }

        else if (callbackQuery.getData().equals("buttonTranslation")){
            Word selectedWord = dataCache.getUserSelectedWord(chatId);
            String text = selectedWord.getWordUkr();
            replyMessage.setReplyMarkup(getIterationButtons(userPass, chatId));
            replyMessage.setChatId(chatId);
            replyMessage.setText(text);
            return replyMessage;
        }

        else if (callbackQuery.getData().equals("buttonDescription")){
            Word selectedWord = dataCache.getUserSelectedWord(chatId);
            String text = selectedWord.getDescription();
            replyMessage.setReplyMarkup(getIterationButtons(userPass, chatId));
            replyMessage.setChatId(chatId);
            replyMessage.setText(text);
            return replyMessage;
        }

        return replyMessage;
    }

    @Override
    public BotApiMethod<?> handle(Message message) {
        String chatId = message.getChatId().toString();
        return new SendMessage(chatId, "Use button \"Get next\" to get a word");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.ITERATE_LESSON_WORDS;
    }

    private InlineKeyboardMarkup getIterationButtons(String userPass, String chatId){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonGet = new InlineKeyboardButton("Get next");
        InlineKeyboardButton buttonSkip = new InlineKeyboardButton("I don't know");
        InlineKeyboardButton buttonTranslation = new InlineKeyboardButton("Translation");
        InlineKeyboardButton buttonDescription = new InlineKeyboardButton("Prompt");
        InlineKeyboardButton buttonCloseLesson = new InlineKeyboardButton("Close lesson");
        InlineKeyboardButton buttonChangeTopic = new InlineKeyboardButton("Change topic");
        buttonGet.setCallbackData("buttonGetLessonWord");
        buttonSkip.setCallbackData("buttonSkip");
        buttonTranslation.setCallbackData("buttonTranslation");
        buttonDescription.setCallbackData("buttonDescription");
        buttonCloseLesson.setCallbackData("buttonCloseLesson");
        buttonChangeTopic.setCallbackData("buttonChangeTopic");

        List<InlineKeyboardButton> buttonsRow1 = new ArrayList<>();
        buttonsRow1.add(buttonGet);
        if (!dataCache.getLessonWords(userPass).isEmpty()) {
            buttonsRow1.add(buttonSkip);
        }

        List<InlineKeyboardButton> buttonsRow2 = new ArrayList<>();
        buttonsRow2.add(buttonTranslation);
        buttonsRow2.add(buttonDescription);

        List<InlineKeyboardButton> buttonsRow3 = new ArrayList<>();
        buttonsRow3.add(buttonChangeTopic);
        buttonsRow3.add(buttonCloseLesson);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(buttonsRow1);
        if (!dataCache.getLessonWords(userPass).isEmpty()) {
            rows.add(buttonsRow2);
        }
        // If this user is admin - show closeButton and changeTopicButton
        if (dataCache.getRoomAdmin(userPass).equals(chatId)){
            rows.add(buttonsRow3);
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }
}
