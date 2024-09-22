package ru.tasktracker.repository;

import ru.tasktracker.model.Task;
import ru.tasktracker.result.EmptyResult;
import ru.tasktracker.result.Result;

import java.util.Optional;

public interface TaskRepository {

    Optional<Long> getLastAddedId();

    Optional<Task> findById(long id);

    Result<Task[]> findAll();

    Result<Long> save(Task task);

    EmptyResult deleteById(long id);
}