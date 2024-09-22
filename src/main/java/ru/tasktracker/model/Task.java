package ru.tasktracker.model;

import lombok.Getter;
import ru.tasktracker.model.enums.TaskStatus;

import java.time.LocalDateTime;

@Getter
public class Task {

    private final long id;

    private String description;

    private TaskStatus status;

    private final LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Task(long id, String description) {
        this.id = id;
        this.description = description;
        this.status = TaskStatus.TODO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "task_" + id + "_" + status.toString();
    }
}
