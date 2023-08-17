package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.StatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static hexlet.code.controller.StatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {

    public static final String TEST_USERNAME = "email@email.com";
    public static final String TEST_USERNAME_2 = "email2@email.com";
    private final UserDto testRegistrationDto = new UserDto(
            "firstName",
            "lastName",
            TEST_USERNAME,
            "password"
    );
    private final StatusDto testStatusDto = new StatusDto("testStatus");
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private final String baseUrl;
    private final MockMvc mockMvc;
    private final UserRepository userRepository;
    private StatusRepository statusRepository;
    private TaskRepository taskRepository;
    private final JWTHelper jwtHelper;

    public TestUtils(@Value("${base-url}") final String baseUrl, final MockMvc mockMvc,
                     final UserRepository userRepository, final StatusRepository statusRepository,
                     final TaskRepository taskRepository, final JWTHelper jwtHelper) {
        this.baseUrl = baseUrl;
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.statusRepository = statusRepository;
        this.taskRepository = taskRepository;
        this.jwtHelper = jwtHelper;
    }

    public UserDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    public StatusDto getTestStatusDto() {
        return testStatusDto;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void tearDown() {
        userRepository.deleteAll();
        statusRepository.deleteAll();
    }

    public ResultActions createDefaultUser() throws Exception {
        return createUser(testRegistrationDto);
    }

    public ResultActions createUser(final UserDto dto) throws Exception {
        final MockHttpServletRequestBuilder request = post(baseUrl + USER_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }

    public ResultActions createDefaultStatus() throws Exception {
        return createStatus(testStatusDto);
    }

    public ResultActions createStatus(final StatusDto dto) throws Exception {
        final MockHttpServletRequestBuilder request = post(baseUrl + STATUS_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request, TEST_USERNAME);
    }
}
