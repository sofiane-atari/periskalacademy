package periskal.academy.backend.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "Client")
@XmlAccessorType(XmlAccessType.FIELD)
public class Client {
    
    @XmlElement(name = "ID")
    private String id;

    @XmlElement(name = "Username")
    private String username;

    @XmlElement(name = "Password")
    private String password;

    @XmlElement(name = "Firstname")
    private String firstname;

    @XmlElement(name = "Lastname")
    private String lastname;

    @XmlElement(name = "Shipname")
    private String shipname;

    @XmlElement(name = "Role")
    private String role;

    @XmlElementWrapper(name = "Products") 
    @XmlElement(name = "Product")         
    private List<String> products;

}
