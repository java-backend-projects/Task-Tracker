package ru.tasktracker.result;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record Result<T>(T value, boolean isSuccess, String errorMessage) {

    @NotNull
    @Contract("_ -> new")
    public static <T> Result<T> success(T value) {
        return new Result<>(value, true, null);
    }

    @NotNull
    @Contract("_ -> new")
    public static <T> Result<T> failure(String error) {
        return new Result<>(null, false, error);
    }

    @Contract(pure = true)
    public void match(Consumer<T> onSuccess, Consumer<String> onFailure) {
        if (isSuccess) {
            onSuccess.accept(value);
            return;
        }

        onFailure.accept(errorMessage);
    }
}