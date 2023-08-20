package inspection.format.with_concatenation.with_null;

public class Main {

    public static void main(String... args) {
        String name = "Sergei";

        // not nighlight such usage
        String.format("Hello %s. Did you know you can't divide by " + null + "?", name);
    }

}
