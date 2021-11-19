package inspection.non_literal;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    static String TEMPLATE = "Hello ${name}";

    public static void main(String[] args) {
        String name = "Alex";

        System.out.println(s(<error descr="Only string literal value can be used in this place">null</error>));
        System.out.println(s(<error descr="Only string literal value can be used in this place">TEMPLATE</error>));
        System.out.println(s(<error descr="Only string literal value can be used in this place">"Hello" + " ${name}"</error>));
    }
}
