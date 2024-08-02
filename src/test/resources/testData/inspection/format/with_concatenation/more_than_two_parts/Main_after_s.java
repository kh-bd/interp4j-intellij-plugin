package inspection.format.with_concatenation.more_than_two_parts;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static void main(String... args) {
        String yourName = "Sergei";
        String myName = "Kristina";
        s("Hello ${yourName}, " + "this is ${myName}. " + "How are you " + "?");
    }

}
