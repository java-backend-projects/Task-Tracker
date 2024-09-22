package ru.tasktracker.repository.implementations;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.tasktracker.model.Task;
import ru.tasktracker.result.EmptyResult;
import ru.tasktracker.result.Result;
import ru.tasktracker.repository.TaskRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FileSystemTaskRepository implements TaskRepository {

    private static final String TASKS_DIRECTORY_PATH = System.getProperty("user.dir") + "/tasks";
    private static final Pattern TASK_FILE_NAME_REGEX = Pattern.compile("task_\\d+_(todo|in-progress|done).json");
    private final Gson gson;

    @Contract(pure = true)
    public FileSystemTaskRepository(Gson gson) {
        this.gson = gson;
    }

    @SneakyThrows(IOException.class)
    private static Path ensureTasksDirectoryExists() {
        Path directory = Paths.get(TASKS_DIRECTORY_PATH);
        if (Files.exists(directory) && Files.isDirectory(directory)) {
            return directory;
        }

        return Files.createDirectory(directory);
    }

    private static Optional<Path> fileByTaskId(long id) {
        Path directory = ensureTasksDirectoryExists();

        try (Stream<Path> files = Files.list(directory)) {
            return files
                    .filter(path -> path.getFileName().toString().startsWith("task_" + id) &&
                            path.getFileName().toString().endsWith(".json"))
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> getLastAddedId() {
        Path directory = ensureTasksDirectoryExists();

        try (Stream<Path> files = Files.list(directory)) {
            Long[] tasksIds = files
                    .filter(this::fileIsTask)
                    .map(
                            path -> path
                                    .getFileName()
                                    .toString()
                    )
                    .map(
                            fileName -> Long.parseLong(
                                    fileName.split("_")[1]
                            )
                    )
                    .toArray(Long[]::new);

            Optional<Long> optionalLastAddedId = Arrays.stream(tasksIds).max(Long::compareTo);
            return optionalLastAddedId.isPresent()
                    ? optionalLastAddedId
                    : Optional.of(0L);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Task> findById(long id) {
        ensureTasksDirectoryExists();

        Optional<Path> optionalFileName = fileByTaskId(id);
        if (optionalFileName.isEmpty()) {
            return Optional.empty();
        }

        try {
            Path fileName = optionalFileName.get();

            String taskAsJsonString = Files.readString(fileName);
            Task task = gson.fromJson(taskAsJsonString, Task.class);

            return Optional.of(task);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public Result<Task[]> findAll() {
        Path directory = ensureTasksDirectoryExists();

        try (Stream<Path> files = Files.list(directory)) {
            return Result.success(
                    files.filter(this::fileIsTask)
                            .map(this::readTaskFromFile)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toArray(Task[]::new)
            );
        } catch (IOException e) {
            return Result.failure(e.getMessage());
        }
    }

    @Override
    public Result<Long> save(@NotNull Task task) {
        Path directory = ensureTasksDirectoryExists();

        String taskFileName = task + ".json";
        Path taskFile = directory.resolve(taskFileName);

        try {
            String taskAsJson = gson.toJson(task);
            Files.writeString(taskFile, taskAsJson);

            return Result.success(task.getId());
        } catch (IOException e) {
            return Result.failure(e.getMessage());
        }
    }

    @Override
    public EmptyResult deleteById(long id) {
        ensureTasksDirectoryExists();

        Optional<Path> optionalFileName = fileByTaskId(id);
        if (optionalFileName.isEmpty()) {
            return EmptyResult.failure("Task with id " + id + " not found");
        }

        try {
            Files.delete(optionalFileName.get());

            return EmptyResult.success();
        } catch (IOException e) {
            return EmptyResult.failure(e.getMessage());
        }
    }

    private boolean fileIsTask(@NotNull Path file) {
        return TASK_FILE_NAME_REGEX.matcher(file.getFileName().toString()).matches();
    }

    private Optional<Task> readTaskFromFile(@NotNull Path path) {
        try {
            String taskAsJson = Files.readString(path);

            return Optional.of(gson.fromJson(taskAsJson, Task.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}