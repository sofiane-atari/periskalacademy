package periskal.academy.backend.jpa.video;

import periskal.academy.backend.jpa.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalName; // Originele bestandsnaam
    private String storedName; // Unieke naam op FTP server
    private String filePath; // Pad op FTP server
    private Long fileSize; // Grootte in bytes
    private String mimeType; // Bijv. "video/mp4"
    private LocalDateTime uploadDate;

    // @ManyToOne
    // @JoinColumn(name = "chapter_id", nullable = true)
    // @JsonBackReference
    @OneToOne(mappedBy = "videoEn")
    private Chapter chapter; // Relatie naar training

    public Video(String originalName, String storedName, String filePath,
            Long fileSize, String mimeType, Chapter chapter) {
        this.originalName = originalName;
        this.storedName = storedName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.chapter = chapter;
        this.uploadDate = LocalDateTime.now();
    }

}