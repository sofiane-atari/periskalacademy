package periskal.academy.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FaqDTO {
    private Long id;
    private String question;
    private String answer;
    private String category;
}