package inspection.can_not_parsed;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static void main(String[] args) {
        System.out.println(s(<error descr="Expression cannot be parsed">"$"</error>));
        System.out.println(s(<error descr="Expression cannot be parsed">"$"</error> + <error descr="Expression cannot be parsed">"$"</error>));
    }
}
