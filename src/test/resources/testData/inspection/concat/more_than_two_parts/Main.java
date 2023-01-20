package inspection.concat.more_than_two_parts;

public class Main {

    public static void main(String... args) {
        Person person = new Person("Alex", 20);

        String greeting = <weak_warning descr="String concatenation might be replaced with string interpolation">"Hello, My name is " + person.name + " and I'm " + person.age + " years old"</weak_warning>;
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
