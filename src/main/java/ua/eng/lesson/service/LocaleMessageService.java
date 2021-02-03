package ua.eng.lesson.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/*
* The class takes messages from specific property file depending on locale
* */

@Component
public class LocaleMessageService {

    private Locale locale;
    private MessageSource messageSource;

    public LocaleMessageService(@Value("en") String localeTag, MessageSource messageSource) {
        this.locale = Locale.forLanguageTag(localeTag);
        this.messageSource = messageSource;
    }

    public String getMessage(String message){
        return messageSource.getMessage(message, null, locale);
    }

    public String getMessage(String message, Object... args){
        return messageSource.getMessage(message, args, locale);
    }

}
