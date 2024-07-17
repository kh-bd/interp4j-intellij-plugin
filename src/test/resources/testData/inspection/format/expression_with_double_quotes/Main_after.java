package inspection.format.expression_with_double_quotes;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static void main(String... args) {
        s("\"Hello ${message(\"Sergei\")}! How are you?\"");
    }

    private static String message(String message) {
        return message;
    }

}
