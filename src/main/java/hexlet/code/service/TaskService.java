package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

public interface TaskService {

    Iterable<Task> findAllTasks(Predicate predicate);

    Task findTaskById(long id);

    Task createTask(TaskDto taskDto);

    Task updateTaskById(long id, TaskDto taskDto);

    void deleteTaskById(long id);

}
