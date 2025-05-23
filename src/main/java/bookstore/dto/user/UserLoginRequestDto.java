package bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto(
        @NotBlank(message = "must not be blank")
        @Email
        String email,
        @NotBlank(message = "must not be blank")
        String password
){
}

