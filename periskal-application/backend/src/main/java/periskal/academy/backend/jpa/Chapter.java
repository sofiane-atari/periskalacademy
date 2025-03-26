package periskal.academy.backend.jpa;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    @JsonBackReference
    private Module module;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

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

    public List<Photo> getPhotos() {
        return photos;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
        photo.setChapter(this);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
        photo.setChapter(null);
    }
}
