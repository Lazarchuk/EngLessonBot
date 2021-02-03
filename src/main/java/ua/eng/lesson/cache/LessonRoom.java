package ua.eng.lesson.cache;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ua.eng.lesson.model.Word;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonRoom {
    String password;
    String topic;
    String adminChatId;
    Deque<Word> lessonWords;
    Set<String> chatIDs;

    public LessonRoom() {
        lessonWords = new ArrayDeque<>();
        chatIDs = new HashSet<>();
    }
}
