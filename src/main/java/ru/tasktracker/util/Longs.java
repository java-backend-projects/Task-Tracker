package ru.tasktracker.util;

import org.jetbrains.annotations.Contract;
import ru.tasktracker.result.Result;

public final class Longs {

    public static Result<Long> tryParse(String s) {
        if (canParse(s)) {
            return Result.success(Long.parseLong(s));
        }

        return Result.failure("Can't parse string: " + s);
    }

    @Contract(pure = true)
    private static boolean canParse(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
