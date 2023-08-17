package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.TestConfig;
import hexlet.code.dto.StatusDto;
import hexlet.code.model.Status;
import hexlet.code.repository.StatusRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static hexlet.code.controller.StatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
class StatusControllerTest {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void init() throws Exception {
        utils.createDefaultUser();
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    void getAllStatusesTest() throws Exception {
        // get all statuses
        utils.createDefaultStatus();
        final MockHttpServletResponse response = utils
                .perform(get(utils.getBaseUrl() + STATUS_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<Status> statuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(statuses).hasSize(1);

        // forbidden
        utils.perform(get(utils.getBaseUrl() + STATUS_CONTROLLER_PATH)).andExpect(status().isForbidden());
    }

    @Test
    void getStatusByIdTest() throws Exception {
        // get status by id
        utils.createDefaultStatus();
        final Status expectedStatus = statusRepository.findAll().get(0);
        final MockHttpServletResponse response = utils.perform(
                        get(utils.getBaseUrl() + STATUS_CONTROLLER_PATH + ID, expectedStatus.getId()),
                        TEST_USERNAME
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final Status status = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(expectedStatus.getId(), status.getId());
        assertEquals(expectedStatus.getName(), status.getName());

        // not found
        utils.perform(get(utils.getBaseUrl() + STATUS_CONTROLLER_PATH + ID, 100), TEST_USERNAME)
                .andExpect(status().isNotFound());

        // unprocessable entity
        utils.perform(get(utils.getBaseUrl() + STATUS_CONTROLLER_PATH + ID,
                        "error"), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity());

        // forbidden
        utils.perform(get(utils.getBaseUrl() + STATUS_CONTROLLER_PATH + ID, expectedStatus.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void createStatusTest() throws Exception {
        // created
        assertEquals(0, statusRepository.count());
        utils.createDefaultStatus().andExpect(status().isCreated());

        // unprocessable entity
        final StatusDto statusDtoWithUnprocessableEntity = new StatusDto("");
        final MockHttpServletResponse responseWithUnprocessableEntity = utils
                .perform(post(utils.getBaseUrl() + STATUS_CONTROLLER_PATH)
                        .content(asJson(statusDtoWithUnprocessableEntity))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();
        assertThat(responseWithUnprocessableEntity.getContentAsString()).contains("Status name is required");
        assertThat(responseWithUnprocessableEntity.getContentAsString())
                .contains("Status name needs to be between 1 and 30 characters long");
        assertEquals(1, statusRepository.count());

        //forbidden
        utils.perform(post(utils.getBaseUrl() + STATUS_CONTROLLER_PATH)
                        .content(asJson(utils.getTestStatusDto())).contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateStatusTest() throws Exception {
        // updated
        utils.createDefaultStatus();
        final Long statusId = statusRepository.findAll().get(0).getId();
        final StatusDto statusDto = new StatusDto("new status");
        utils.perform(put(utils.getBaseUrl() + STATUS_CONTROLLER_PATH + ID, statusId)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON), TEST_USERNAME).andExpect(status().isOk());
        assertTrue(statusRepository.existsById(statusId));
        assertEquals(statusDto.name(), statusRepository.findById(statusId).get().getName());

        // forbidden
        utils.perform(put(utils.getBaseUrl() + STATUS_CONTROLLER_PATH + ID, statusId)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON)).andExpect(status().isForbidden());

        // unprocessable entity
        final StatusDto statusDtoWithUnprocessableEntity = new StatusDto("");
        final MockHttpServletResponse responseWithUnprocessableEntity =
                utils.perform(put(utils.getBaseUrl() + STATUS_CONTROLLER_PATH + ID, statusId)
                                .content(asJson(statusDtoWithUnprocessableEntity))
                                .contentType(APPLICATION_JSON), TEST_USERNAME)
                        .andExpect(status().isUnprocessableEntity())
                        .andReturn()
                        .getResponse();
        assertThat(responseWithUnprocessableEntity.getContentAsString()).contains("Status name is required");
        assertThat(responseWithUnprocessableEntity.getContentAsString())
                .contains("Status name needs to be between 1 and 30 characters long");
    }

    @Test
    void deleteStatusTest() throws Exception {
        // deleted
        utils.createDefaultStatus();
        assertEquals(1, statusRepository.count());
        final Long statusId = statusRepository.findAll().get(0).getId();
        utils.perform(delete(utils.getBaseUrl() + STATUS_CONTROLLER_PATH + ID, statusId), TEST_USERNAME)
                .andExpect(status().isOk());
        assertEquals(0, statusRepository.count());

        // forbidden
        utils.createDefaultStatus();
        final Long forbiddenStatusId = statusRepository.findAll().get(0).getId();
        utils.perform(delete(utils.getBaseUrl() + STATUS_CONTROLLER_PATH + ID, forbiddenStatusId))
                .andExpect(status().isForbidden());
        assertEquals(1, statusRepository.count());

        // unprocessable entity
        utils.perform(delete(utils.getBaseUrl() + STATUS_CONTROLLER_PATH + ID,
                        "id"), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity());
        assertEquals(1, statusRepository.count());
    }

}
