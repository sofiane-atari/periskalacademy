package periskal.academy.backend.jpa;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String productCode;

    private String nameEn;
    private String descriptionEn;
    private String nameNl;
    private String descriptionNl;
    private String nameDe;
    private String descriptionDe;
    private String nameFr;
    private String descriptionFr;

    @OneToMany(mappedBy = "training", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Module> modules = new ArrayList<>();

    @OneToOne(mappedBy = "training", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("training-exam")
    private @Nullable Exam exam;

    public Training(String productCode, String nameEn, String descriptionEn, String nameNl, String descriptionNl,
            String nameDe, String descriptionDe, String nameFr, String descriptionFr,
            List<Module> modules) {
        this.productCode = productCode;
        this.nameEn = nameEn;
        this.descriptionEn = descriptionEn;
        this.nameNl = nameNl;
        this.descriptionNl = descriptionNl;
        this.nameDe = nameDe;
        this.descriptionDe = descriptionDe;
        this.nameFr = nameFr;
        this.descriptionFr = descriptionFr;
        this.modules = modules;
    }

    public Training(String name, String description, List<Module> modules) {
        this.nameEn = name;
        this.descriptionEn = description;
        this.modules = modules;
    }
}
