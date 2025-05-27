package periskal.academy.backend.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequest {

    @NotBlank()
    @Email()
    private String email;

    @NotBlank()
    @Size(min = 10)
    private String message;
    
    private String userId;
    private String firstName;
    private String lastName;
    private String username;
}
