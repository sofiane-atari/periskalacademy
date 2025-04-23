package periskal.academy.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class SkippersDTO {
    
    private String id;
    private String username;
    private String firstname;
    private String lastname;
    private String shipname;
    private String role;
    private List<String> products;

}
