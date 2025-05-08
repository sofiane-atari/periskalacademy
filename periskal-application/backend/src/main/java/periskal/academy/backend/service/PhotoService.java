package periskal.academy.backend.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.jpa.PhotoRepository;
import periskal.academy.backend.security.Exceptions.General.ResourceNotFoundException;
import periskal.academy.backend.security.Exceptions.General.ValidationException;

@Slf4j
@Service
public class PhotoService {
    private PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public void deletePhoto(Long id) {

        if (!photoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Photo not found with id: " + id);
        }
        try {
            photoRepository.deleteById(id);
            log.info("Successfully deleted Photo with ID: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to delete photo {} due to data integrity violation (e.g., related data exists): {}",
                    id, e.getMessage());
            throw new ValidationException("Cannot delete photo " + id + ". It might be referenced by other data.",
                    List.of(e.getMessage()));
        } catch (DataAccessException e) {
            log.error("Failed to delete photo {} due to database error: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete photo " + id + " from the database.", e);
        }
    }
}
