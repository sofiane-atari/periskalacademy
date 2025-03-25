package periskal.academy.backend.jpa;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRepository extends CrudRepository<Training, Long> {
    default List<Training> findAllAsList() {
        return StreamSupport.stream(findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
