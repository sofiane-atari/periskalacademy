package periskal.academy.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;
    private String text;
    private List<String> photos;
}
