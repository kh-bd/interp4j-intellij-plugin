package inspection.code_without_specifiers;

import static dev.khbd.interp4j.core.Interpolations.fmt;

public class Main {

    public static void main(String[] args) {
        String name = "Alex";

        fmt("Hello ${<error descr="Code block must be used after specifier">name</error>}");
        fmt("${<error descr="Code block must be used after specifier">name</error>}");
    }
}
