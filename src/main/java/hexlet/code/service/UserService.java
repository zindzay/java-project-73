package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

import java.util.List;

public interface UserService {

    List<User> findAllUsers();

    User findUserById(long id);

    User createUser(UserDto userDto);

    User updateUserById(long id, UserDto userDto);

    void deleteUserById(long id);

}
