package periskal.academy.backend.dto;

import java.util.List;

import lombok.Data;
import periskal.academy.backend.jpa.Photo;

@Data
public class QuestionDTO {
    private Long id;
    private String text;
    private List<Photo> photos;
}
