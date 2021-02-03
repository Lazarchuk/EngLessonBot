package ua.eng.lesson.cache;

import org.springframework.stereotype.Component;
import ua.eng.lesson.bot.BotState;
import ua.eng.lesson.model.Word;
import ua.eng.lesson.repository.WordRepository;

import java.util.*;

@Component
public class LessonDataCache implements DataCache{

    private WordRepository repository;

    // <password, LessonRoom>
    // Contains lesson room with specific password
    private Map<String, LessonRoom> rooms = new HashMap<>();

    // <chatId, UserData>
    // Contains user data (password, selected word) by user chat ID
    private Map<String, LessonUserData> lessonUsersData = new HashMap<>();

    // Contains current bot state related to specific user (userId)
    private Map<Integer, BotState> usersBotStates = new HashMap<>();

    // All topics in the bata base
    private Set<String> topics = new HashSet<>();

    public LessonDataCache(WordRepository repository) {
        this.repository = repository;

        List<Word> allWards = repository.findAll();
        allWards.forEach(word -> topics.add(word.getTopic()));
    }

    @Override
    public void addLessonRoom(String password, String topic){
        LessonRoom lessonRoom = new LessonRoom();
        lessonRoom.setTopic(topic);

        Set<Word> words = repository.findByTopic(topic);
        Deque<Word> lessonWords = new ArrayDeque<>(words);
        lessonRoom.setLessonWords(lessonWords);
        lessonRoom.setPassword(password);
        rooms.put(password, lessonRoom);
    }

    @Override
    public void addLessonUser(String chatId, String password){
        LessonUserData lessonUserData = new LessonUserData();
        lessonUserData.setPassword(password);

        rooms.get(password).getChatIDs().add(chatId);
        lessonUsersData.put(chatId, lessonUserData);
    }

    @Override
    public String getUserPassword(String chatId){
        LessonUserData userData = lessonUsersData.get(chatId);
        if (userData == null){
            return null;
        }
        return userData.getPassword();

    }

    @Override
    public void addUserSelectedWord(String chatId, Word selectedWord){
        lessonUsersData.get(chatId).setSelectedWord(selectedWord);
    }

    @Override
    public Word getUserSelectedWord(String chatId){
        return lessonUsersData.get(chatId).getSelectedWord();
    }

    @Override
    public Deque<Word> getLessonWords(String password){
        if (rooms.containsKey(password)) {
            return rooms.get(password).getLessonWords();
        }
        else {
            return new ArrayDeque<>();
        }
    }

    @Override
    public void changeLessonTopic(String password, String topic){
        Set<Word> words = repository.findByTopic(topic);
        Deque<Word> lessonWords = new ArrayDeque<>(words);
        rooms.get(password).setTopic(topic);
        rooms.get(password).setLessonWords(lessonWords);
    }

    @Override
    public void setUserCurrentBotState(Integer userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUserCurrentBotState(Integer userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null){
            botState = BotState.START_APP;
        }
        return botState;
    }

    @Override
    public void setRoomAdmin(String password, String chatId) {
        LessonRoom room = rooms.get(password);
        room.setAdminChatId(chatId);
    }

    @Override
    public String getRoomAdmin(String password){
        LessonRoom room = rooms.get(password);
        if (room == null){
            return null;
        }
        return room.getAdminChatId();
    }

    @Override
    public Set<String> getTopics(){
        return topics;
    }

    @Override
    public Map<String, LessonRoom> getRooms() {
        return rooms;
    }

    @Override
    public Map<String, LessonUserData> getLessonUsersData() {
        return lessonUsersData;
    }
}
