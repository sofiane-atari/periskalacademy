package periskal.academy.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CertificatePdfDTO {

    private String productName;
    private String productDetails;
    private String shipName;
    private String dateInfo;
    private List<String> crewNames;

    private String signerName;
    private String signerTitle;

    private String companyName;
    private String companyAddress;
    private String companyPhone;

}