package bookstore.service.impl;

import bookstore.dto.user.UserRegistrationRequestDto;
import bookstore.dto.user.UserResponseDto;
import bookstore.exceptions.RegistrationException;
import bookstore.mapper.UserMapper;
import bookstore.model.User;
import bookstore.repository.user.UserRepository;
import bookstore.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto) {
        if (userRegistrationRequestDto != null) {
            User user = userMapper.registerModelFromDto(userRegistrationRequestDto);
            user.setEmail(user.getEmail().toLowerCase());
            User savedUser = userRepository.save(user);
            return userMapper.toDto(savedUser);
        }
        throw new RegistrationException("UserRegistrationRequestDto is null");
    }
}
