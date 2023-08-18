package inspection.format.with_concatenation.wrong_argument_count;

public class Main {

    public static void main(String... args) {
        // not nighlight such usage
        String.format("Hello" + "Sergei");
    }

}
