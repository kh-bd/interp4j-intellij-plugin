package inspection.format.with_concatenation.more_than_two_parts;

import static dev.khbd.interp4j.core.Interpolations.fmt;

public class Main {

    public static void main(String... args) {
        String yourName = "Sergei";
        String myName = "Kristina";
        fmt("Hello %s${yourName}, " + "this is %s${myName}. " + "How are you " + "?");
    }

}
