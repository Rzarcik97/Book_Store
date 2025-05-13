package bookstore.service;

import bookstore.dto.user.UserRegistrationRequestDto;
import bookstore.dto.user.UserResponseDto;

public interface AuthenticationService {

    UserResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto);
}
