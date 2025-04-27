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
import periskal.academy.backend.jpa.Video.Video;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameChapter_en;
    private String descriptionChapter_en;
    private String nameChapter_nl;
    private String descriptionChapter_nl;
    private String nameChapter_fr;
    private String descriptionChapter_fr;
    private String nameChapter_de;
    private String descriptionChapter_de;

    @Lob
    private String textChapter_en;

    @Lob
    private String textChapter_nl;
    @Lob
    private String textChapter_fr;
    @Lob
    private String textChapter_de;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    @JsonBackReference
    private Module module;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "chapter-photos")
    private List<Photo> photos_en = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "chapter-photos-nl")
    private List<Photo> photos_nl = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "chapter-photos-fr")
    private List<Photo> photos_fr = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "chapter-photos-de")
    private List<Photo> photos_de = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Video> videos = new ArrayList<>();

    public Chapter(String nameChapter, String descriptionChapter, String textChapter) {
        this.nameChapter_en = nameChapter;
        this.descriptionChapter_en = descriptionChapter;
        this.textChapter_en = textChapter;
    }

    public void addPhoto(Photo photo) {
        photos_en.add(photo);
        photo.setChapter(this);
    }

    public void removePhoto(Photo photo) {
        photos_en.remove(photo);
        photo.setChapter(null);
    }
}
