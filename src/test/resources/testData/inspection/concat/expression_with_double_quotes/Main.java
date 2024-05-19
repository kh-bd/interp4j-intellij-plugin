package inspection.concat.expression_with_double_quotes;

public class Main {

    public static void main(String... args) {
        String text = <weak_warning descr="String concatenation might be replaced with string interpolation">message("hello") + ""</weak_warning>;
    }

    private static String message(String message) {
        return message;
    }
}
