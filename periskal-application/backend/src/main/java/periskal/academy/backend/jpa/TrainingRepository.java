package periskal.academy.backend.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRepository extends CrudRepository<Training, Long> {
    default List<Training> findAllAsList() {
        return StreamSupport.stream(findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Query("SELECT t FROM Training t LEFT JOIN FETCH t.modules m WHERE t.id = :id")
    Optional<Training> findByIdWithDetails(@Param("id") Long id);
}
