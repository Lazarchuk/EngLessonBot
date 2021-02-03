package ua.eng.lesson.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ua.eng.lesson.model.Word;

import java.util.List;
import java.util.Set;

@Repository
public interface WordRepository extends MongoRepository<Word, ObjectId> {

    Set<Word> findByTopic(String topic);
}
