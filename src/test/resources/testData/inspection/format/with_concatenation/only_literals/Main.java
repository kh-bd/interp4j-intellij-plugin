package inspection.format.with_concatenation.only_literals;

public class Main {

    public static void main(String... args) {
        String name = "Sergei";

        // not nighlight such usage
        String.format("Hello, Sergei. " + "How are you?", name);
    }

}
