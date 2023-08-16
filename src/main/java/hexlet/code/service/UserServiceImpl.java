package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.exeptions.UserNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(final long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("Not found user with 'id': %d", id)));
    }

    @Override
    public User createUser(final UserDto userDto) {
        final User user = new User();
        user.setEmail(userDto.email());
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setPassword(passwordEncoder.encode(userDto.password()));

        return userRepository.save(user);
    }

    @Override
    public User updateUserById(final long id, final UserDto userDto) {
        final User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("Not found user with 'id': %d", id)));
        userToUpdate.setEmail(userDto.email());
        userToUpdate.setFirstName(userDto.firstName());
        userToUpdate.setLastName(userDto.lastName());
        userToUpdate.setPassword(passwordEncoder.encode(userDto.password()));

        return userRepository.save(userToUpdate);
    }

    @Override
    public void deleteUserById(final long id) {
        userRepository.deleteById(id);
    }

}
