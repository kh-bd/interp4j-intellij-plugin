package inspection.format.with_concatenation.simple_concat;

import static dev.khbd.interp4j.core.Interpolations.fmt;

public class Main {

    public static void main(String... args) {
        String yourName = "Sergei";
        String myName = "Kristina";
        fmt("Hello %s${yourName}, " + "this is %s${myName}. How are you?");
    }

}
