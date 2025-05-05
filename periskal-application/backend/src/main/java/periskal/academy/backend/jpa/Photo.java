package periskal.academy.backend.jpa;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String imageData;

    // Optional link to a Chapter
    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = true)
    @JsonBackReference
    private Chapter chapter;

    // Optional link to a Question
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = true)
    @JsonBackReference(value = "question-photos")
    private Question question;

    public Photo(String imageData) {
        this.imageData = imageData;
    }
}
