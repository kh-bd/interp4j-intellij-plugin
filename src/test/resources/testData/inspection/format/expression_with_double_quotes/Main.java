package inspection.format.expression_with_double_quotes;

public class Main {

    public static void main(String... args) {
        <weak_warning descr="String format call might be replaced with string interpolation">String.format("\"Hello %s! How are you?\"", message("Sergei"))</weak_warning>;
    }

    private static String message(String message) {
        return message;
    }

}
