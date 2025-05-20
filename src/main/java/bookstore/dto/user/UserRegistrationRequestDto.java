package bookstore.dto.user;

import bookstore.model.User;
import bookstore.validation.fieldmatch.FieldMatch;
import bookstore.validation.uniquefield.UniqueField;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@FieldMatch
public class UserRegistrationRequestDto {
    @NotBlank(message = "must not be blank")
    @Email
    @UniqueField(entity = User.class, fieldName = "email",
            message = "already exist in DB")
    private String email;
    @NotBlank(message = "must not be blank")
    private String firstName;
    @NotBlank(message = "must not be blank")
    private String lastName;
    @NotBlank(message = "must not be blank")
    private String password;
    @NotBlank(message = "must not be blank")
    private String repeatPassword;
    private String shippingAddress;
}
