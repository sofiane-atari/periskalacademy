package periskal.academy.backend.jpa;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameChapterEn;
    private String descriptionChapterEn;
    private String nameChapterNl;
    private String descriptionChapterNl;
    private String nameChapterFr;
    private String descriptionChapterFr;
    private String nameChapterDe;
    private String descriptionChapterDe;

    @Lob
    private String textChapterEn;

    @Lob
    private String textChapterNl;
    @Lob
    private String textChapterFr;
    @Lob
    private String textChapterDe;

    @ManyToOne
    @JoinColumn(name = "moduleId", nullable = false)
    @JsonBackReference
    private Module module;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Photo> photos = new ArrayList<>();

    private String videoRemoteFilePath;

    public Chapter(String nameChapter, String descriptionChapter, String textChapter) {
        this.nameChapterEn = nameChapter;
        this.descriptionChapterEn = descriptionChapter;
        this.textChapterEn = textChapter;
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
