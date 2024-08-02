package inspection.format.only_string_conversions;

public class Main {

    public static void main(String... args) {
        String yourName = "Alex";
        String myName = "Sergei";
        <weak_warning descr="String format call might be replaced with `fmt` interpolation"><weak_warning descr="String format call might be replaced with `s` interpolation">String.format("hello %s, Who are you? I am %s", yourName, myName)</weak_warning></weak_warning>;
    }

}
