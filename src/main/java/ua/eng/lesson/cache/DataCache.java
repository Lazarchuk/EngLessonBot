package ua.eng.lesson.cache;

import ua.eng.lesson.bot.BotState;
import ua.eng.lesson.model.Word;

import java.util.Deque;
import java.util.Map;
import java.util.Set;

/*
* The interface defines a cache to store necessary user data to save current chat state
* */

public interface DataCache {
    void addLessonRoom(String password, String topic);
    void addLessonUser(String chatId, String password);
    String getUserPassword(String chatId);
    void addUserSelectedWord(String chatId, Word selectedWord);
    Word getUserSelectedWord(String chatId);
    Deque<Word> getLessonWords(String password);
    void changeLessonTopic(String password, String topic);
    void setUserCurrentBotState(Integer userId, BotState botState);
    BotState getUserCurrentBotState(Integer userId);
    void setRoomAdmin(String password, String chatId);
    String getRoomAdmin(String password);
    Set<String> getTopics();
    Map<String, LessonRoom> getRooms();
    Map<String, LessonUserData> getLessonUsersData();

}
