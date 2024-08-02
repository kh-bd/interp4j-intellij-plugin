package inspection.format.with_integer_conversion;

import static dev.khbd.interp4j.core.Interpolations.fmt;

public class Main {

    public static void main(String... args) {
        String name = "Alex";
        int age = 30;
        fmt("hello %s${name.toUpperCase()}, Who old are you? I am %d${age}.");
    }

}
