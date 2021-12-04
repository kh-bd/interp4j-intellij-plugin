package inspection.interpolate_method_invocation;

import dev.khbd.interp4j.core.internal.s.SInterpolator;

public class Main {

    public static String greet(String name) {
        String result = <warning descr="SInterpolator class is not for public use. Use Interpolations.s function instead.">new SInterpolator("Hello, ", "").interpolate(name)</warning>;
        return result;
    }
}
