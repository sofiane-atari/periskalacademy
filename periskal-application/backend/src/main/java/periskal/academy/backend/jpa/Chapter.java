package periskal.academy.backend.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameChapter;

    private String descriptionChapter;

    @Lob
    private String textChapter;

    public Chapter(String nameChapter, String descriptionChapter, String textChapter) {
        this.nameChapter = nameChapter;
        this.descriptionChapter = descriptionChapter;
        this.textChapter = textChapter;
    }

    public Chapter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameChapter() {
        return nameChapter;
    }

    public void setNameChapter(String nameChapter) {
        this.nameChapter = nameChapter;
    }

    public String getDescriptionChapter() {
        return descriptionChapter;
    }

    public void setDescriptionChapter(String descriptionChapter) {
        this.descriptionChapter = descriptionChapter;
    }

    public String getTextChapter() {
        return textChapter;
    }

    public void setTextChapter(String textChapter) {
        this.textChapter = textChapter;
    }
}
