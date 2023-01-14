package inspection.format.wrong_argument_count;

public class Main {

    public static void main(String... args) {
        String yourName = "Alex";
        String.format("hello %s, Who are you? I am %s", yourName);
    }

}
