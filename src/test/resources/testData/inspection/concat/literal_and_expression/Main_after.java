package inspection.concat.literal_and_expression;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static void main(String... args) {
        Person person = new Person("Alex");

        String literalFirst = s("Hello, ${person.name}");
        String expressionFirst = s("${person.name}, Hello!");
    }

    static class Person {
        String name;

        Person(String name) {
            this.name = name;
        }
    }
}
