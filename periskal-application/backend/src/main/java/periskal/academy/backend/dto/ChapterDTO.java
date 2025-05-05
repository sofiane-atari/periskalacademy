package periskal.academy.backend.dto;

import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.jpa.Module;
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
    private String nameChapter_en;
    private String descriptionChapter_en;
    private String textChapter_en;
    private String nameChapter_nl;
    private String descriptionChapter_nl;
    private String textChapter_nl;
    private String nameChapter_fr;
    private String descriptionChapter_fr;
    private String textChapter_fr;
    private String nameChapter_de;
    private String descriptionChapter_de;
    private String textChapter_de;
    private List<Photo> photos;

    public ChapterDTO(Chapter chapter) {
        this.id = chapter.getId();
        this.nameChapter_en = chapter.getNameChapter_en();
        this.descriptionChapter_en = chapter.getDescriptionChapter_en();
        this.textChapter_en = chapter.getTextChapter_en();
        this.nameChapter_nl = chapter.getNameChapter_nl();
        this.descriptionChapter_nl = chapter.getDescriptionChapter_nl();
        this.textChapter_nl = chapter.getTextChapter_nl();
        this.nameChapter_fr = chapter.getNameChapter_fr();
        this.descriptionChapter_fr = chapter.getDescriptionChapter_fr();
        this.textChapter_fr = chapter.getTextChapter_fr();
        this.nameChapter_de = chapter.getNameChapter_de();
        this.descriptionChapter_en = chapter.getDescriptionChapter_de();
        this.textChapter_de = chapter.getTextChapter_de();
        this.photos = chapter.getPhotos_en();
    }
}
