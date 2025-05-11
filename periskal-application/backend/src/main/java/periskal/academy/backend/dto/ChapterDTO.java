package periskal.academy.backend.dto;

import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.jpa.Photo;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChapterDTO {
    private Long id;
    private String nameChapterEn;
    private String descriptionChapterEn;
    private String textChapterEn;
    private String nameChapterNl;
    private String descriptionChapterNl;
    private String textChapterNl;
    private String nameChapterFr;
    private String descriptionChapterFr;
    private String textChapterFr;
    private String nameChapterDe;
    private String descriptionChapterDe;
    private String textChapterDe;
    private List<Photo> photos;

    public ChapterDTO(Chapter chapter) {
        this.id = chapter.getId();
        this.nameChapterEn = chapter.getNameChapterEn();
        this.descriptionChapterEn = chapter.getDescriptionChapterEn();
        this.textChapterEn = chapter.getTextChapterEn();
        this.nameChapterNl = chapter.getNameChapterNl();
        this.descriptionChapterNl = chapter.getDescriptionChapterNl();
        this.textChapterNl = chapter.getTextChapterNl();
        this.nameChapterFr = chapter.getNameChapterFr();
        this.descriptionChapterFr = chapter.getDescriptionChapterFr();
        this.textChapterFr = chapter.getTextChapterFr();
        this.nameChapterDe = chapter.getNameChapterDe();
        this.descriptionChapterEn = chapter.getDescriptionChapterDe();
        this.textChapterDe = chapter.getTextChapterDe();
        this.photos = chapter.getPhotos();
    }
}
