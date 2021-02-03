package ua.eng.lesson.appconfiguration;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import ua.eng.lesson.bot.EngLessonBot;
import ua.eng.lesson.bot.UpdatesDispatcher;

@Setter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class ApplicationConfig {

    private String botPath;

    @Bean
    public EngLessonBot engVocabularyBot(UpdatesDispatcher updatesDispatcher){
        String botUserName = System.getenv("BOT_NAME");
        String botToken = System.getenv("BOT_TOKEN");

        DefaultBotOptions options = new DefaultBotOptions();
        EngLessonBot bot = new EngLessonBot(options, updatesDispatcher);
        bot.setBotUserName(botUserName);
        bot.setBotToken(botToken);
        bot.setBotPath(botPath);
        return bot;
    }

    @Bean
    public MessageSource messageSource(){
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasename("classpath:messages");
        return messageSource;
    }

}
