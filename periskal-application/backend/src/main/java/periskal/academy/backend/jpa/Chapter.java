package periskal.academy.backend.jpa;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameChapter;

    private String descriptionChapter;

    private String textChapter;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    @JsonBackReference
    private Module module;

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

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
