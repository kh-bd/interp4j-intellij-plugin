# Interp4j support for IntelliJ IDEA

IntelliJ plugin to support [interp4j](https://github.com/kh-bd/interp4j) library.

## What is interp4j?

Interp4j is string interpolation library for java language.

For example, we use `String.format` to compose some greeting message.
This code works, but a bit cumbersome to write.

```java
class Greeter {
    public String describe(Person person) {
        String template = "Hello! My name is %s. I'm %d";
        return String.format(template, person.getName(), person.getAge());
    }
}
```

With string interpolation it will look like this

```java
class Greeter {
    public String describe(Person person) {
        return s("Hello! My name is ${person.getName()}. I'm ${person.getAge()}");
    }
}
```

To read more about string interpolation, look at it's [home page](https://github.com/kh-bd/interp4j). 

## How to install plugin?

It's intelliJ plugin, so to install it go to `Preferenses -> Plugins -> Marketplace`.
Type in `interp4j`. It should be there.

![Plugins' search window](docs/img/marketplace.png)

If you cannot find plugin in marketplace, it can mean your IntelliJ version is not supported.
Try to update to the latest IntelliJ version.