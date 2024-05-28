package inspection.without_any_expression;

import static dev.khbd.interp4j.core.Interpolations.fmt;

public class Main {

    public static void main(String[] args) {
        String s1 = <warning descr="String interpolation is redundant">fmt("Hello world")</warning>;
        String s2 = <warning descr="String interpolation is redundant">fmt("Hello %s$${name}")</warning>;
        String s3 = <warning descr="String interpolation is redundant">fmt("Hello" + " world")</warning>;
    }
}
