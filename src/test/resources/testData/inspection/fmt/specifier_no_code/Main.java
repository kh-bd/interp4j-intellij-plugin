package inspection.specifier_no_code;

import static dev.khbd.interp4j.core.Interpolations.fmt;

public class Main {

    public static void main(String[] args) {
        String greet1 = fmt("Hello, <error descr="Code block must be used after specifier">%s</error>");

        // valid, %% and %n must be used without code blocks
        String greet2 = fmt("Hello, %%");
        fmt("Hello, %%. Text after");

        String greet3 = fmt("Hello, %n");
        fmt("Hello, %n. Text after");
    }
}
