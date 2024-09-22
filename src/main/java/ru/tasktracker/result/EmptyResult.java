package ru.tasktracker.result;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record EmptyResult(boolean isSuccess, String errorMessage) {


    @NotNull
    @Contract(" -> new")
    public static EmptyResult success() {
        return new EmptyResult(true, null);
    }


    @NotNull
    @Contract("_ -> new")
    public static EmptyResult failure(String error) {
        return new EmptyResult(false, error);
    }

    @Contract(pure = true)
    public void match(Runnable onSuccess, Consumer<String> onFailure) {
        if (isSuccess) {
            onSuccess.run();
            return;
        }

        onFailure.accept(errorMessage);
    }
}