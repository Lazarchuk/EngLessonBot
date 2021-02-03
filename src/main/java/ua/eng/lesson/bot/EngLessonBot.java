package ua.eng.lesson.bot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class EngLessonBot extends TelegramWebhookBot {

    private String botUserName;
    private String botToken;
    private String botPath;
    private UpdatesDispatcher updatesDispatcher;


    public EngLessonBot(DefaultBotOptions options, UpdatesDispatcher updatesDispatcher) {
        super(options);
        this.updatesDispatcher = updatesDispatcher;
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        return updatesDispatcher.handleUpdate(update);
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public void setBotPath(String botPath) {
        this.botPath = botPath;
    }
}
