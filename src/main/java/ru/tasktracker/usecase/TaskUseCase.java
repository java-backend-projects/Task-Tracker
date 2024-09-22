package ru.tasktracker.usecase;

import ru.tasktracker.model.Task;
import ru.tasktracker.model.enums.TaskStatus;
import ru.tasktracker.result.EmptyResult;
import ru.tasktracker.result.Result;

import java.util.Optional;

public interface TaskUseCase {

    Result<Task[]> getAll();

    Result<Task[]> getAllWithStatus(TaskStatus status);

    Optional<Long> add(String description);

    EmptyResult updateDescription(long id, String description);

    EmptyResult updateStatus(long id, TaskStatus status);

    EmptyResult delete(long id);
}