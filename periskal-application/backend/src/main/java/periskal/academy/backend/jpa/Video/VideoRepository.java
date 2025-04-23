package periskal.academy.backend.jpa.Video;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    
    
    List<Video> findByChapterId(Long chapterId);
    
    
    List<Video> findByOriginalNameContainingIgnoreCase(String searchTerm);
    
    
    List<Video> findByUploadDateAfter(java.time.LocalDateTime date);
    
    
    Long countByChapterId(Long chapterId);
    
    
    void deleteByChapterId(Long chapterId);
}