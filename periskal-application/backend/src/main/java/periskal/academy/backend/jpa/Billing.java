package periskal.academy.backend.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;
    private String city;
    private String companyName;
    private String countryCode;
    private String email;
    private int examId;
    private String fullName;
    private String state;
    private String tax;
    private String zipCode;
    private String userId;

}
