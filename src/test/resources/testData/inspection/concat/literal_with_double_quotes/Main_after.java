package inspection.concat.literal_with_double_quotes;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static void main(String... args) {
        String text = s("${message()}\"");
    }

    private static String message() {
        return "message";
    }
}
