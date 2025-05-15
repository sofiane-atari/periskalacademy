package periskal.academy.backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CertificateDTO {
    private Long id;
    private String name;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String training;
    private boolean readyForDownload;
    private String userId;


}
