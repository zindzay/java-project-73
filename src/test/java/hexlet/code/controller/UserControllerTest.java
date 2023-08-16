package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.TestConfig;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.TestConfig.TEST_PROFILE;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.TEST_USERNAME_2;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestConfig.class)
class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    void getAllUsers() throws Exception {
        utils.createDefaultUser();
        final MockHttpServletResponse response = utils.perform(get(USER_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<User> users = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(users).hasSize(1);
    }

    @Test
    void getUserById() throws Exception {
        // get user by id
        utils.createDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);
        final MockHttpServletResponse response = utils.perform(
                        get(USER_CONTROLLER_PATH + ID, expectedUser.getId())
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final User user = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getFirstName(), user.getFirstName());
        assertEquals(expectedUser.getLastName(), user.getLastName());

        // not found
        utils.perform(get(USER_CONTROLLER_PATH + ID, 100))
                .andExpect(status().isNotFound());

        // unprocessable entity
        utils.perform(get(USER_CONTROLLER_PATH + ID, "error"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createUser() throws Exception {
        // created
        assertEquals(0, userRepository.count());
        utils.createDefaultUser().andExpect(status().isCreated());

        // unprocessable entity
        utils.createDefaultUser().andExpect(status().isUnprocessableEntity());

        // bad request
        final UserDto userDtoWithBadRequest = new UserDto("", "", "email", "");
        final MockHttpServletResponse responseWithBadRequest = utils.perform(post(USER_CONTROLLER_PATH)
                        .content(asJson(userDtoWithBadRequest))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();
        assertThat(responseWithBadRequest.getContentAsString()).contains("Last name is required");
        assertThat(responseWithBadRequest.getContentAsString()).contains("Password is required");
        assertThat(responseWithBadRequest.getContentAsString())
                .contains("Your last name needs to be between 1 and 30 characters long");
        assertThat(responseWithBadRequest.getContentAsString())
                .contains("Your first name needs to be between 8 and 30 characters long");
        assertThat(responseWithBadRequest.getContentAsString())
                .contains("Your password needs to be between 8 and 30 characters long");
        assertThat(responseWithBadRequest.getContentAsString()).contains("Please enter a valid email address");
        assertEquals(1, userRepository.count());
    }

    @Test
    void updateUser() throws Exception {
        // updated
        utils.createDefaultUser();

        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();
        final UserDto userDto = new UserDto("new first name", "new last name",
                TEST_USERNAME_2, "new password");
        utils.perform(put(USER_CONTROLLER_PATH + ID, userId)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON)).andExpect(status().isOk());
        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findByEmail(TEST_USERNAME).orElse(null));
        assertNotNull(userRepository.findByEmail(TEST_USERNAME_2).orElse(null));

        // not found
        utils.perform(put(USER_CONTROLLER_PATH + ID, 100)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON)).andExpect(status().isNotFound());

        // bad request
        final UserDto userDtoWithBadRequest = new UserDto("", "", "email", "");
        final MockHttpServletResponse responseWithBadRequest =
                utils.perform(put(USER_CONTROLLER_PATH + ID, userId)
                                .content(asJson(userDtoWithBadRequest))
                                .contentType(APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse();
        assertThat(responseWithBadRequest.getContentAsString()).contains("Last name is required");
        assertThat(responseWithBadRequest.getContentAsString()).contains("Password is required");
        assertThat(responseWithBadRequest.getContentAsString())
                .contains("Your last name needs to be between 1 and 30 characters long");
        assertThat(responseWithBadRequest.getContentAsString())
                .contains("Your first name needs to be between 8 and 30 characters long");
        assertThat(responseWithBadRequest.getContentAsString())
                .contains("Your password needs to be between 8 and 30 characters long");
        assertThat(responseWithBadRequest.getContentAsString()).contains("Please enter a valid email address");

        // unprocessable entity
        utils.createDefaultUser();
        final Long userIdWithUnprocessableEntity = userRepository.findByEmail(TEST_USERNAME).get().getId();
        final UserDto userDtoWithUnprocessableEntity = new UserDto("new first name", "new last name",
                TEST_USERNAME_2, "new password");
        utils.perform(put(USER_CONTROLLER_PATH + ID, userIdWithUnprocessableEntity)
                .content(asJson(userDtoWithUnprocessableEntity))
                .contentType(APPLICATION_JSON)).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deleteUser() throws Exception {
        // deleted
        utils.createDefaultUser();
        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();
        utils.perform(delete(USER_CONTROLLER_PATH + ID, userId))
                .andExpect(status().isOk());
        assertEquals(0, userRepository.count());

        // unprocessable entity
        utils.createDefaultUser();
        utils.perform(delete(USER_CONTROLLER_PATH + ID, "userId"))
                .andExpect(status().isUnprocessableEntity());
        assertEquals(1, userRepository.count());
    }

}
