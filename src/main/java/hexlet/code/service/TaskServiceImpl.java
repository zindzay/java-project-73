package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.exeption.TaskNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    private final StatusService statusService;

    private final LabelService labelService;

    @Override
    public Iterable<Task> findAllTasks(final Predicate predicate) {
        return taskRepository.findAll(predicate);
    }

    @Override
    public Task findTaskById(final long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(String.format("Not found task with 'id': %d", id)));
    }

    @Override
    public Task createTask(final TaskDto taskDto) {
        final Task task = fromDto(taskDto);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTaskById(final long id, final TaskDto taskDto) {
        final Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(String.format("Not found task with 'id': %d", id)));
        merge(task, taskDto);
        return taskRepository.save(task);
    }

    @Override
    public void deleteTaskById(final long id) {
        taskRepository.deleteById(id);
    }

    private void merge(final Task task, final TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);
        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());
        task.setTaskStatus(newTask.getTaskStatus());
        task.setLabels(newTask.getLabels());
        task.setExecutor(newTask.getExecutor());
    }

    private Task fromDto(final TaskDto dto) {
        final User author = userService.getCurrentUser();
        final Status taskStatus = statusService.findStatusById(dto.taskStatusId());

        final Task.TaskBuilder taskBuilder = Task.builder()
                .name(dto.name())
                .description(dto.description())
                .taskStatus(taskStatus)
                .author(author);

        if (dto.executorId() != null) {
            final User executor = userService.findUserById(dto.executorId());
            taskBuilder.executor(executor);
        }

        if (!dto.labelIds().isEmpty()) {
            final List<Label> labels = labelService.findAllLabelById(dto.labelIds());
            taskBuilder.labels(new HashSet<>(labels));
        }

        return taskBuilder.build();
    }
}
