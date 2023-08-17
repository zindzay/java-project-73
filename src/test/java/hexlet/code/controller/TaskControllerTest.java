package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.TestConfig;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static hexlet.code.config.TestConfig.TEST_PROFILE;
import static hexlet.code.controller.StatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
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
class TaskControllerTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void init() throws Exception {
        utils.createDefaultUser();
        utils.createDefaultStatus();
        utils.createDefaultLabel();
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
        taskRepository.deleteAll();
    }

    @Test
    void getAllTasksTest() throws Exception {
        // get all tasks
        createDefaultTask();
        final MockHttpServletResponse response = utils
                .perform(get(utils.getBaseUrl() + TASK_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(tasks).hasSize(1);

        // forbidden
        utils.perform(get(utils.getBaseUrl() + TASK_CONTROLLER_PATH)).andExpect(status().isForbidden());
    }

    @Test
    void getTaskByIdTest() throws Exception {
        // get task by id
        createDefaultTask();
        final Long authorId = userRepository.findByEmail(TEST_USERNAME).get().getId();
        final Task expectedTask = taskRepository.findAll().get(0);
        final MockHttpServletResponse response = utils.perform(
                        get(utils.getBaseUrl() + TASK_CONTROLLER_PATH + ID, expectedTask.getId()),
                        TEST_USERNAME
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final Task status = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(expectedTask.getId(), status.getId());
        assertEquals(expectedTask.getName(), status.getName());
        assertEquals(expectedTask.getLabels().stream().toList().get(0).getId(),
                status.getLabels().stream().toList().get(0).getId());
        assertEquals(expectedTask.getDescription(), status.getDescription());
        assertEquals(expectedTask.getExecutor().getId(), status.getExecutor().getId());
        assertEquals(expectedTask.getAuthor().getId(), authorId);

        // not found
        utils.perform(get(utils.getBaseUrl() + TASK_CONTROLLER_PATH + ID, 100), TEST_USERNAME)
                .andExpect(status().isNotFound());

        // unprocessable entity
        utils.perform(get(utils.getBaseUrl() + TASK_CONTROLLER_PATH + ID,
                        "error"), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity());

        // forbidden
        utils.perform(get(utils.getBaseUrl() + TASK_CONTROLLER_PATH + ID, expectedTask.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void createTaskTest() throws Exception {
        // created
        assertEquals(0, taskRepository.count());
        createDefaultTask().andExpect(status().isCreated());

        // unprocessable entity
        final TaskDto taskDtoWithBadRequest = new TaskDto("", "", null, null, null);
        final MockHttpServletResponse responseWithBadRequest = utils
                .perform(post(utils.getBaseUrl() + TASK_CONTROLLER_PATH)
                        .content(asJson(taskDtoWithBadRequest))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();
        assertThat(responseWithBadRequest.getContentAsString()).contains("Task name is required");
        assertThat(responseWithBadRequest.getContentAsString())
                .contains("Task name needs to be between 1 and 200 characters long");
        assertThat(responseWithBadRequest.getContentAsString())
                .contains("Task status is required");
        assertEquals(1, userRepository.count());

        //forbidden
        utils.perform(post(utils.getBaseUrl() + STATUS_CONTROLLER_PATH)
                        .content(asJson(taskDtoWithBadRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateTaskTest() throws Exception {
        // updated
        createDefaultTask();
        final Long taskId = taskRepository.findAll().get(0).getId();
        final Long executorId = userRepository.findAll().get(0).getId();
        utils.createDefaultStatus();
        final Long newTaskStatusId = statusRepository.findAll().get(1).getId();
        final TaskDto taskDto = new TaskDto("new name", "new description", newTaskStatusId,
                null, executorId);
        utils.perform(put(utils.getBaseUrl() + TASK_CONTROLLER_PATH + ID, taskId)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON), TEST_USERNAME).andExpect(status().isOk());
        assertTrue(taskRepository.existsById(taskId));
        assertEquals(taskDto.name(), taskRepository.findById(taskId).get().getName());
        assertEquals(taskDto.description(), taskRepository.findById(taskId).get().getDescription());
        assertEquals(taskDto.taskStatusId(), taskRepository.findById(taskId).get().getTaskStatus().getId());
        assertTrue(taskDto.labelIds().isEmpty());
        assertEquals(taskDto.executorId(), taskRepository.findById(taskId).get().getExecutor().getId());

        // forbidden
        utils.perform(put(utils.getBaseUrl() + TASK_CONTROLLER_PATH + ID, taskId)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON)).andExpect(status().isForbidden());

        // unprocessable entity
        final TaskDto taskDtoWithBadRequest = new TaskDto("", "", null, null, null);
        final MockHttpServletResponse responseWithBadRequest =
                utils.perform(put(utils.getBaseUrl() + TASK_CONTROLLER_PATH + ID, taskId)
                                .content(asJson(taskDtoWithBadRequest))
                                .contentType(APPLICATION_JSON), TEST_USERNAME)
                        .andExpect(status().isUnprocessableEntity())
                        .andReturn()
                        .getResponse();
        assertThat(responseWithBadRequest.getContentAsString()).contains("Task name is required");
        assertThat(responseWithBadRequest.getContentAsString())
                .contains("Task name needs to be between 1 and 200 characters long");
        assertThat(responseWithBadRequest.getContentAsString())
                .contains("Task status is required");
    }

    @Test
    void deleteTaskTest() throws Exception {
        // deleted
        createDefaultTask();
        assertEquals(1, taskRepository.count());
        final Long taskId = taskRepository.findAll().get(0).getId();
        utils.perform(delete(utils.getBaseUrl() + TASK_CONTROLLER_PATH + ID, taskId), TEST_USERNAME)
                .andExpect(status().isOk());
        assertEquals(0, taskRepository.count());

        // forbidden
        createDefaultTask();
        final Long forbiddenTaskId = statusRepository.findAll().get(0).getId();
        utils.perform(delete(utils.getBaseUrl() + TASK_CONTROLLER_PATH + ID, forbiddenTaskId))
                .andExpect(status().isForbidden());
        assertEquals(1, taskRepository.count());

        // unprocessable entity
        utils.perform(delete(utils.getBaseUrl() + TASK_CONTROLLER_PATH + ID, "id"), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity());
        assertEquals(1, taskRepository.count());
    }

    private ResultActions createDefaultTask() throws Exception {
        final Long executorId = userRepository.findAll().get(0).getId();
        final Long taskStatusId = statusRepository.findAll().get(0).getId();
        final Long labelId = labelRepository.findAll().get(0).getId();
        final TaskDto taskDto = new TaskDto("name", "description", taskStatusId,
                List.of(labelId), executorId);
        final MockHttpServletRequestBuilder request = post(utils.getBaseUrl() + TASK_CONTROLLER_PATH)
                .content(TestUtils.asJson(taskDto))
                .contentType(APPLICATION_JSON);

        return utils.perform(request, TEST_USERNAME);
    }

}
