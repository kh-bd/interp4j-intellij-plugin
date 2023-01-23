package inspection.concat.more_than_two_parts;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static void main(String... args) {
        Person person = new Person("Alex", 20);

        String greeting = s("Hello, My name is ${person.name} and I'm ${person.age} years old");
    }

    static class Person {
        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}
