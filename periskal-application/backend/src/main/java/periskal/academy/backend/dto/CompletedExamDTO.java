package periskal.academy.backend.dto;



import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

    @Getter
    @Setter
    @NoArgsConstructor
    public class CompletedExamDTO {

        private Long id;
        private String userId;
        private Long trainingId;
        private Long examId;
        private int requiredScore;
        private int score;
        private Date completedDate;


    }
