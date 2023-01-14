package inspection.format.only_string_conversions;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static void main(String... args) {
        String yourName = "Alex";
        String myName = "Sergei";
        s("hello $yourName, Who are you? I am $myName");
    }

}
