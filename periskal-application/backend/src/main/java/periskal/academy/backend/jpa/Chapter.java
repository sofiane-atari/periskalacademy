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

    private String nameChapter_en;
    private String descriptionChapter_en;
    private String nameChapter_nl;
    private String descriptionChapter_nl;
    private String nameChapter_fr;
    private String descriptionChapter_fr;
    private String nameChapter_de;
    private String descriptionChapter_de;

    @Lob
    private String textChapter;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    @JsonBackReference
    private Module module;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Photo> photos = new ArrayList<>();

    public Chapter(String nameChapter, String descriptionChapter, String textChapter) {
        this.nameChapter_en = nameChapter;
        this.descriptionChapter_en = descriptionChapter;
        this.textChapter = textChapter;
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
