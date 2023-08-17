package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.TestConfig;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
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
class LabelControllerTest {

    @Autowired
    private LabelRepository labelRepository;

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
    void getAllLabelsTest() throws Exception {
        // get all labels
        utils.createDefaultLabel();
        final MockHttpServletResponse response = utils
                .perform(get(utils.getBaseUrl() + LABEL_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(labels).hasSize(1);

        // forbidden
        utils.perform(get(utils.getBaseUrl() + LABEL_CONTROLLER_PATH)).andExpect(status().isForbidden());
    }

    @Test
    void getLabelByIdTest() throws Exception {
        // get label by id
        utils.createDefaultLabel();
        final Label expectedLabel = labelRepository.findAll().get(0);
        final MockHttpServletResponse response = utils.perform(
                        get(utils.getBaseUrl() + LABEL_CONTROLLER_PATH + ID, expectedLabel.getId()),
                        TEST_USERNAME
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(expectedLabel.getId(), label.getId());
        assertEquals(expectedLabel.getName(), label.getName());

        // not found
        utils.perform(get(utils.getBaseUrl() + LABEL_CONTROLLER_PATH + ID, 100), TEST_USERNAME)
                .andExpect(status().isNotFound());

        // unprocessable entity
        utils.perform(get(utils.getBaseUrl() + LABEL_CONTROLLER_PATH + ID,
                        "error"), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity());

        // forbidden
        utils.perform(get(utils.getBaseUrl() + LABEL_CONTROLLER_PATH + ID, expectedLabel.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void createLabelTest() throws Exception {
        // created
        assertEquals(0, labelRepository.count());
        utils.createDefaultLabel().andExpect(status().isCreated());

        // unprocessable entity
        final LabelDto labelDtoWithUnprocessableEntity = new LabelDto("");
        final MockHttpServletResponse responseWithUnprocessableEntity = utils
                .perform(post(utils.getBaseUrl() + LABEL_CONTROLLER_PATH)
                        .content(asJson(labelDtoWithUnprocessableEntity))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();
        assertThat(responseWithUnprocessableEntity.getContentAsString()).contains("Label name is required");
        assertThat(responseWithUnprocessableEntity.getContentAsString())
                .contains("Label name needs to be between 1 and 30 characters long");
        assertEquals(1, labelRepository.count());

        //forbidden
        utils.perform(post(utils.getBaseUrl() + LABEL_CONTROLLER_PATH)
                        .content(asJson(utils.getTestLabelDto())).contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateLabelTest() throws Exception {
        // updated
        utils.createDefaultLabel();
        final Long labelId = labelRepository.findAll().get(0).getId();
        final LabelDto labelDto = new LabelDto("new label");
        utils.perform(put(utils.getBaseUrl() + LABEL_CONTROLLER_PATH + ID, labelId)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON), TEST_USERNAME).andExpect(status().isOk());
        assertTrue(labelRepository.existsById(labelId));
        assertEquals(labelDto.name(), labelRepository.findById(labelId).get().getName());

        // forbidden
        utils.perform(put(utils.getBaseUrl() + LABEL_CONTROLLER_PATH + ID, labelId)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON)).andExpect(status().isForbidden());

        // unprocessable entity
        final LabelDto labelDtoWithUnprocessableEntity = new LabelDto("");
        final MockHttpServletResponse responseWithUnprocessableEntity =
                utils.perform(put(utils.getBaseUrl() + LABEL_CONTROLLER_PATH + ID, labelId)
                                .content(asJson(labelDtoWithUnprocessableEntity))
                                .contentType(APPLICATION_JSON), TEST_USERNAME)
                        .andExpect(status().isUnprocessableEntity())
                        .andReturn()
                        .getResponse();
        assertThat(responseWithUnprocessableEntity.getContentAsString()).contains("Label name is required");
        assertThat(responseWithUnprocessableEntity.getContentAsString())
                .contains("Label name needs to be between 1 and 30 characters long");
    }

    @Test
    void deleteLabelTest() throws Exception {
        // deleted
        utils.createDefaultLabel();
        assertEquals(1, labelRepository.count());
        final Long labelId = labelRepository.findAll().get(0).getId();
        utils.perform(delete(utils.getBaseUrl() + LABEL_CONTROLLER_PATH + ID, labelId), TEST_USERNAME)
                .andExpect(status().isOk());
        assertEquals(0, labelRepository.count());

        // forbidden
        utils.createDefaultLabel();
        final Long forbiddenLabelId = labelRepository.findAll().get(0).getId();
        utils.perform(delete(utils.getBaseUrl() + LABEL_CONTROLLER_PATH + ID, forbiddenLabelId))
                .andExpect(status().isForbidden());
        assertEquals(1, labelRepository.count());

        // unprocessable entity
        utils.perform(delete(utils.getBaseUrl() + LABEL_CONTROLLER_PATH + ID,
                        "id"), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity());
        assertEquals(1, labelRepository.count());
    }

}
