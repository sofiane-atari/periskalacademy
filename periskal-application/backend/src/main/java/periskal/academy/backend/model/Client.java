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
@XmlRootElement(name = "Client", namespace = "http://tempuri.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class Client {
    
    @XmlElement(name = "ID", namespace = "http://tempuri.org/")
    private String id;

    @XmlElement(name = "Username", namespace = "http://tempuri.org/")
    private String username;

    @XmlElement(name = "Password", namespace = "http://tempuri.org/")
    private String password;

    @XmlElement(name = "Firstname", namespace = "http://tempuri.org/")
    private String firstname;

    @XmlElement(name = "Lastname", namespace = "http://tempuri.org/")
    private String lastname;

    @XmlElement(name = "Shipname", namespace = "http://tempuri.org/")
    private String shipname;

    @XmlElement(name = "Role", namespace = "http://tempuri.org/")
    private String role;

    @XmlElementWrapper(name = "Products") 
    @XmlElement(name = "Product", namespace = "http://tempuri.org/")         
    private List<String> products;

}
