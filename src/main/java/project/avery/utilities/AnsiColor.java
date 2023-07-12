package project.avery.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created by Pascal K. on 14.06.2023.
 */
@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public enum AnsiColor {

    RESET("\u001b[0m"),

    BLACK("\u001b[30m"),
    RED("\u001b[31m"),
    GREEN("\u001b[32m"),
    YELLOW("\u001b[33m"),
    BLUE("\u001b[34m"),
    PURPLE("\u001b[35m"),
    CYAN("\u001b[36m"),
    WHITE("\u001b[37m");

    private final String code;
}