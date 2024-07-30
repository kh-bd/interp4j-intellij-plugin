package inspection.format.only_string_conversions;

import static dev.khbd.interp4j.core.Interpolations.fmt;

public class Main {

    public static void main(String... args) {
        String yourName = "Alex";
        String myName = "Sergei";
        fmt("hello %s${yourName}, Who are you? I am %s${myName}");
    }

}
