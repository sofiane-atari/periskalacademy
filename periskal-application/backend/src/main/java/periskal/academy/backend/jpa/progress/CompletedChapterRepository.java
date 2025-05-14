package periskal.academy.backend.jpa.progress;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletedChapterRepository extends CrudRepository<CompletedChapter, Long> {
    
    List<CompletedChapter> findByModuleProgressId(Long moduleProgressId);
    
    Optional<CompletedChapter> findByModuleProgressIdAndChapterId(Long moduleProgressId, Long chapterId);
    
    boolean existsByModuleProgressIdAndChapterId(Long moduleProgressId, Long chapterId);

}


