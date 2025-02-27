package periskal.academy.backend.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("Password")
    private String password;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getShipname() {
        return shipname;
    }

    public void setShipname(String shipname) {
        this.shipname = shipname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public String getLoginResult() {    
        return loginResult;
    }

    public void setLoginResult(String loginResult) {
        this.loginResult = loginResult;
    }
}