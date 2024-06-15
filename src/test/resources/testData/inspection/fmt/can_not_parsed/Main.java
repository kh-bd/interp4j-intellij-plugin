package inspection.can_not_parsed;

import static dev.khbd.interp4j.core.Interpolations.fmt;

public class Main {

    public static void main(String[] args) {
        System.out.println(fmt(<error descr="Expression cannot be parsed">"%s$"</error>));
        System.out.println(fmt(<error descr="Expression cannot be parsed">"%s$"</error> + <error descr="Expression cannot be parsed">"%s${"</error>));
    }
}
