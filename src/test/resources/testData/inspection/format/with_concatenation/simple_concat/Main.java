package inspection.format.with_concatenation.simple_concat;

public class Main {

    public static void main(String... args) {
        String yourName = "Sergei";
        String myName = "Kristina";
        <weak_warning descr="String format call might be replaced with string interpolation">String.format("Hello %s, " + "this is %s. How are you?", yourName, myName)</weak_warning>;
    }

}
