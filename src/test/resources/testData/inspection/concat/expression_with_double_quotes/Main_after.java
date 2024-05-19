package inspection.concat.expression_with_double_quotes;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static void main(String... args) {
        String text = s("${message(\"hello\")}");
    }

    private static String message(String message) {
        return message;
    }
}
