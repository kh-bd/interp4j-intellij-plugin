package inspection.format.with_integer_conversion;

public class Main {

    public static void main(String... args) {
        String name = "Alex";
        int age = 30;
        <weak_warning descr="String format call might be replaced with `s` interpolation"><weak_warning descr="String format call might be replaced with `fmt` interpolation">String.format("hello %s, Who old are you? I am %d.", name.toUpperCase(), age)</weak_warning></weak_warning>;
    }

}
