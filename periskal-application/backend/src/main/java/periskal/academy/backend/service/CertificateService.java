package periskal.academy.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import periskal.academy.backend.dto.CertificateDTO;
import periskal.academy.backend.jpa.Certificate;
import periskal.academy.backend.jpa.CertificateRepository;

@Service
public class CertificateService {
    
    private final CertificateRepository certificateRepository;
    

    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }
    

    //Get all certificates for a specific user
    public List<CertificateDTO> getCertificatesByUserId(String userId) {
        List<Certificate> certificates = certificateRepository.findByUserId(userId);
        return certificates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Save a new certificate or update an existing one
    public CertificateDTO saveCertificate(CertificateDTO certificateDTO) {
        Certificate certificate = convertToEntity(certificateDTO);
        Certificate savedCertificate = certificateRepository.save(certificate);
        return convertToDTO(savedCertificate);
    }
    
 
    public CertificateDTO getCertificateById(Long id) {
        return certificateRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }
    

    private CertificateDTO convertToDTO(Certificate certificate) {
        return new CertificateDTO(
                certificate.getId(),
                certificate.getName(),
                certificate.getIssueDate(),
                certificate.getExpiryDate(),
                certificate.getTraining(),
                certificate.isReadyForDownload(),
                certificate.getUserId()
        );
    }
    
    private Certificate convertToEntity(CertificateDTO certificateDTO) {
        Certificate certificate = new Certificate();
        certificate.setId(certificateDTO.getId());
        certificate.setName(certificateDTO.getName());
        certificate.setIssueDate(certificateDTO.getIssueDate());
        certificate.setExpiryDate(certificateDTO.getExpiryDate());
        certificate.setTraining(certificateDTO.getTraining());
        certificate.setReadyForDownload(certificateDTO.isReadyForDownload());
        certificate.setUserId(certificateDTO.getUserId());
        return certificate;
    }
}
