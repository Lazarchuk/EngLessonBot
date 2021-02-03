package ua.eng.lesson.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "lesson_task")
public class Word {
    @Id
    ObjectId id;
    String wordEng;
    String wordUkr;
    String description;
    String topic;
}
