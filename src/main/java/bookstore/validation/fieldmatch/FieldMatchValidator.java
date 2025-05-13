package bookstore.validation.fieldmatch;

import bookstore.dto.user.UserRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FieldMatchValidator implements
        ConstraintValidator<FieldMatch, UserRegistrationRequestDto> {

    @Override
    public boolean isValid(UserRegistrationRequestDto userRegistrationRequestDto,
                           ConstraintValidatorContext context) {
        if (userRegistrationRequestDto.getPassword() == null
                || userRegistrationRequestDto.getRepeatPassword() == null) {
            return false;
        }
        return userRegistrationRequestDto.getPassword()
                .equals(userRegistrationRequestDto.getRepeatPassword());
    }
}
