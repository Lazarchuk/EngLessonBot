package ua.eng.lesson.cache;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ua.eng.lesson.model.Word;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonUserData {
    String password;
    Word selectedWord;
}
