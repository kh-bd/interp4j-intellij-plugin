package inspection.non_literal;

import static dev.khbd.interp4j.core.Interpolations.fmt;

public class Main {

    static String TEMPLATE = "Hello %s${name}";

    public static void main(String[] args) {
        String name = "Alex";

        System.out.println(fmt(<error descr="Wrong expression type. Only string literal value or concatenation of string literals can be used in this place">null</error>));
        System.out.println(fmt(<error descr="Wrong expression type. Only string literal value or concatenation of string literals can be used in this place">TEMPLATE</error>));
        System.out.println(fmt(<error descr="Wrong expression type. Only string literal value or concatenation of string literals can be used in this place">"hello" + null</error>));
    }
}
