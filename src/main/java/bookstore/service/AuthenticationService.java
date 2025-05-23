package bookstore.service;

import bookstore.dto.user.UserLoginRequestDto;
import bookstore.dto.user.UserLoginResponseDto;
import bookstore.dto.user.UserRegistrationRequestDto;
import bookstore.dto.user.UserResponseDto;

public interface AuthenticationService {

    UserResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto);

    UserLoginResponseDto login(UserLoginRequestDto request);
}
