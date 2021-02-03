package ua.eng.lesson.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/*
* The class receive messages from LocaleMessageService and follows it to a handlers
* */

@Component
public class ReplyMessageService {

    private LocaleMessageService localeMessageService;

    public ReplyMessageService(LocaleMessageService localeMessageService) {
        this.localeMessageService = localeMessageService;
    }

    public SendMessage getReplyMessage(String chatId, String message){
        return new SendMessage(chatId, localeMessageService.getMessage(message));
    }

    public String getReplyText(String message){
        return localeMessageService.getMessage(message);
    }

}
