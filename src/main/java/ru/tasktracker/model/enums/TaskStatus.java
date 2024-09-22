package ru.tasktracker.model.enums;

import org.jetbrains.annotations.NotNull;

public enum TaskStatus {

    TODO,
    IN_PROGRESS,
    DONE;

    @Override
    public String toString() {
        return this
                .name()
                .toLowerCase()
                .replace("_", "-");
    }

    public static TaskStatus fromString(@NotNull String s) {
        return TaskStatus.valueOf(s.toUpperCase().replace("-", "_"));
    }
}