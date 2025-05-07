package periskal.academy.backend.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    
    private String ID;
    private String Username;
    private String Password;
    private String Firstname;
    private String Lastname;
    private String Shipname;
    private String Role;
    private List<String> Products;

}
