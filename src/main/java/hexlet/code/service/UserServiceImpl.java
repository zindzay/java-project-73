package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.exeptions.UserNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll().stream().toList();
    }

    @Override
    public User findUserById(final long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found by id %d", id)));
    }

    @Override
    public User createUser(final UserDto userDto) {
        final User user = new User();
        user.setEmail(userDto.email());
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setPassword(encode(userDto.password()));

        return userRepository.save(user);
    }

    @Override
    public User updateUserById(final long id, final UserDto userDto) {
        final User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found by id %d", id)));
        userToUpdate.setEmail(userDto.email());
        userToUpdate.setFirstName(userDto.firstName());
        userToUpdate.setLastName(userDto.lastName());
        userToUpdate.setPassword(encode(userDto.password()));

        return userRepository.save(userToUpdate);
    }

    @Override
    public void deleteUserById(final long id) {
        userRepository.deleteById(id);
    }

    private String encode(final String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            return Arrays.toString(factory.generateSecret(spec).getEncoded());
        } catch (Exception e) {
            throw new RuntimeException(String.format("Password \"%s\" encode error", password));
        }
    }

}
