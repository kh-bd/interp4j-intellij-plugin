package inspection.concat.literal_and_expression;

public class Main {

    public static void main(String... args) {
        Person person = new Person("Alex");

        String literalFirst = <weak_warning descr="String concatenation might be replaced with string interpolation">"Hello, " + person.name</weak_warning>;
        String expressionFirst = <weak_warning descr="String concatenation might be replaced with string interpolation">person.name + ", Hello!"</weak_warning>;
    }

    static class Person {
        String name;

        Person(String name) {
            this.name = name;
        }
    }
}
