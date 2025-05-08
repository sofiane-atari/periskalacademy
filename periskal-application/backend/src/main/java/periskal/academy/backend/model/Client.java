package periskal.academy.backend.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    
    private String id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String shipname;
    private String role;
    private List<String> products;

}
