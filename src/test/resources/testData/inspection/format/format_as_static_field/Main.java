package inspection.format.format_as_static_field;

public class Main {

    static final String TEMPLATE = "hello %s, Who are you? I am %s";

    public static void main(String... args) {
        String yourName = "Alex";
        String myName = "Sergei";

        // not nighlight such usage
        String.format(TEMPLATE, yourName, myName);
    }

}
