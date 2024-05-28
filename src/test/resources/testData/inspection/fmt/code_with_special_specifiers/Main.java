package inspection.code_with_special_specifiers;

import static dev.khbd.interp4j.core.Interpolations.fmt;

public class Main {

    public static void main(String[] args) {
        String name = "Alex";
        fmt("Hello, %n${<error descr="Code cannot be used after %% and %n specifiers">name</error>}");
        fmt("Hello, %%${<error descr="Code cannot be used after %% and %n specifiers">name</error>}");
    }
}
