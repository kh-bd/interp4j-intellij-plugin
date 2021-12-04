package inspection.without_any_expression;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static void main(String[] args) {
        String s1 = <warning descr="String interpolation without any expression is redundant">s("Hello world")</warning>;
        String s2 = <warning descr="String interpolation without any expression is redundant">s("Hello $${name}")</warning>;
    }
}
