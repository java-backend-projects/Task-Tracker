package ru.tasktracker.usecase.implementation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.tasktracker.model.Task;
import ru.tasktracker.model.enums.TaskStatus;
import ru.tasktracker.result.EmptyResult;
import ru.tasktracker.result.Result;
import ru.tasktracker.repository.TaskRepository;
import ru.tasktracker.usecase.TaskUseCase;

import java.util.Arrays;
import java.util.Optional;

public class TaskUseCaseImpl implements TaskUseCase {

    private final TaskRepository taskRepository;

    @Contract(pure = true)
    public TaskUseCaseImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Result<Task[]> getAll() {
        return taskRepository.findAll();
    }

    @Override
    public Result<Task[]> getAllWithStatus(TaskStatus status) {
        Result<Task[]> tasksResult = taskRepository.findAll();

        return tasksResult.isSuccess()
                ? Result.success(
                Arrays.stream(tasksResult.value())
                        .filter(task -> task.getStatus() == status)
                        .toArray(Task[]::new))
                : tasksResult;
    }

    @Override
    public Optional<Long> add(@NotNull String description) {
        Optional<Long> optionalId = getNextId();

        if (optionalId.isEmpty()) {
            return Optional.empty();
        }

        long id = optionalId.get();
        Task newlyCreatedTask = new Task(id, description);
        Result<Long> result = taskRepository.save(newlyCreatedTask);

        return result.isSuccess()
                ? optionalId
                : Optional.empty();
    }

    @Override
    public EmptyResult updateDescription(long id, String description) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            return EmptyResult.failure("Task with ID " + id + " not found");
        }

        Task task = optionalTask.get();
        task.setDescription(description);

        taskRepository.save(task);

        return EmptyResult.success();
    }

    @Override
    public EmptyResult updateStatus(long id, TaskStatus status) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            return EmptyResult.failure("Task with ID " + id + " not found");
        }

        Task task = optionalTask.get();
        task.setStatus(status);

        taskRepository.deleteById(id);
        taskRepository.save(task);

        return EmptyResult.success();
    }

    @Override
    public EmptyResult delete(long id) {
        return taskRepository.deleteById(id);
    }

    private Optional<Long> getNextId() {
        return taskRepository.getLastAddedId().map(
                lastAddedId -> lastAddedId + 1L
        );
    }
}
