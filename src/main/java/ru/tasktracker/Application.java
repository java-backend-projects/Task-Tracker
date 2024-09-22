package ru.tasktracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import ru.tasktracker.model.Task;
import ru.tasktracker.model.enums.TaskStatus;
import ru.tasktracker.result.Result;
import ru.tasktracker.repository.TaskRepository;
import ru.tasktracker.usecase.TaskUseCase;
import ru.tasktracker.repository.implementations.FileSystemTaskRepository;
import ru.tasktracker.usecase.implementation.TaskUseCaseImpl;
import ru.tasktracker.gson.typeadapters.LocalDateTimeTypeAdapter;
import ru.tasktracker.utils.Longs;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

public class Application {

    private static TaskUseCase taskUseCase;

    public static void main(String @NotNull [] args) {
        initServices();

        String command = args[0];
        switch (command) {
            case "add" -> handleAddCommand(args);
            case "update" -> handleUpdateCommand(args);
            case "delete" -> handleDeleteCommand(args);
            case String s when s.startsWith("mark-") -> handleMarkCommand(args);
            case "list" -> handleListCommand(args);
            default -> throw new IllegalStateException("Unexpected command: " + args[0]);
        }
    }

    private static void initServices() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .setPrettyPrinting()
                .create();

        TaskRepository taskRepository = new FileSystemTaskRepository(gson);
        taskUseCase = new TaskUseCaseImpl(taskRepository);
    }

    private static void handleAddCommand(String @NotNull [] args) {
        String description = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Optional<Long> optionalId = taskUseCase.add(description);

        optionalId.ifPresentOrElse(
                id -> System.out.println("Task added successfully (ID: " + id + ")"),
                () -> System.out.println("An error has occurred when adding a task")
        );
    }

    private static void handleUpdateCommand(String @NotNull [] args) {
        Result<Long> parseResult = Longs.tryParse(args[1]);
        if (!parseResult.isSuccess()) {
            System.out.println("Please enter a valid task ID");
            return;
        }

        long id = parseResult.value();
        String newDescription = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        taskUseCase.updateDescription(id, newDescription)
                .match(
                        () -> System.out.println("Task updated successfully (ID: " + id + ")"),
                        System.out::println
                );
    }

    private static void handleDeleteCommand(String @NotNull [] args) {
        Result<Long> parseResult = Longs.tryParse(args[1]);
        if (!parseResult.isSuccess()) {
            System.out.println("Please enter a valid task ID");
            return;
        }

        long id = parseResult.value();
        taskUseCase.delete(id)
                .match(
                        () -> System.out.println("Task deleted successfully (ID: " + id + ")"),
                        System.out::println
                );
    }

    private static void handleMarkCommand(String @NotNull [] args) {
        TaskStatus newStatus = TaskStatus.fromString(args[0].substring(5));

        Result<Long> parseResult = Longs.tryParse(args[1]);
        if (!parseResult.isSuccess()) {
            System.out.println("Please enter a valid task ID");
            return;
        }

        long id = parseResult.value();
        taskUseCase.updateStatus(id, newStatus)
                .match(
                        () -> System.out.println("Task successfully marked as " + newStatus + " (ID: " + id + ")"),
                        System.out::println
                );
    }

    private static void handleListCommand(String @NotNull [] args) {
        Result<Task[]> tasksResult = args.length == 1
                ? taskUseCase.getAll()
                : taskUseCase.getAllWithStatus(TaskStatus.fromString(args[1]));

        tasksResult.match(
                tasks -> Arrays.stream(tasks)
                        .map(Task::getDescription)
                        .forEach(System.out::println),
                System.out::println
        );
    }
}