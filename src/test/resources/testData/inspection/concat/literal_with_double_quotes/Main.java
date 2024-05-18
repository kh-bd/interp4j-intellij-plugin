package inspection.concat.literal_with_double_quotes;

public class Main {

    public static void main(String... args) {
        String text = <weak_warning descr="String concatenation might be replaced with string interpolation">message() + "\""</weak_warning>;
    }

    private static String message() {
        return "message";
    }
}
