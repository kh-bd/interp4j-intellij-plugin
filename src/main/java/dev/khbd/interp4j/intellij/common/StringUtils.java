package dev.khbd.interp4j.intellij.common;

import lombok.experimental.UtilityClass;

import java.util.Objects;

/**
 * @author Sergei Khadanovich
 */
@UtilityClass
public class StringUtils {

    /**
     * Escape double quotes.
     */
    public static String escapeDoubleQuotes(String str) {
        if (Objects.isNull(str) || str.isEmpty()) {
            return str;
        }

        StringBuilder builder = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c == '"') {
                builder.append('\\');
            }
            builder.append(c);
        }

        return builder.toString();
    }
}
