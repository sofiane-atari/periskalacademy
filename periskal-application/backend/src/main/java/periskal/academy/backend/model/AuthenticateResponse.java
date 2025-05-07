package periskal.academy.backend.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticateResponse {
    @JsonProperty("ID")
    private String id;

    @JsonProperty("Username")
    private String username;

    @JsonProperty("Firstname")
    private String firstname;

    @JsonProperty("Lastname")
    private String lastname;

    @JsonProperty("Shipname")
    private String shipname;

    @JsonProperty("Role")
    private String role;

    @JsonProperty("Products")
    private List<String> products;

    @JsonProperty("LoginResult")
    private String loginResult;

    @JsonProperty("Token")
    private String token;

    @JsonProperty("Clients")
    private List<Client> clients;
    
}