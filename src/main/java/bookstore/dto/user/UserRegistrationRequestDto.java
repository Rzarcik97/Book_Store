package bookstore.dto.user;

import bookstore.model.User;
import bookstore.validation.fieldmatch.FieldMatch;
import bookstore.validation.uniquefield.UniqueField;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@FieldMatch
public class UserRegistrationRequestDto {
    @NotBlank(message = "email must not be blank")
    @Email
    @UniqueField(entity = User.class, fieldName = "email",
            message = "email already exist in DB")
    private String email;
    @NotBlank(message = "firstName must not be blank")
    private String firstName;
    @NotBlank(message = "lastName must not be blank")
    private String lastName;
    @NotBlank(message = "password must not be blank")
    private String password;
    @NotBlank(message = "repeatPassword must not be blank")
    private String repeatPassword;
    @NotBlank(message = "shippingAddress must not be blank")
    private String shippingAddress;
}
