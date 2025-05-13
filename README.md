# JFlowty

A Java library for functional flow handling, based on Vavr's types and Control's static approach to functions. 

### Features
✔ Functional Pipelines – Chain operations fluently (map, flatMap, filter).  
✔ Flexibility - Allows easy adaptation for other sources.  
✔ Customizable - Easily extendable types and functions.  
✔ Safety – Multiple ways of handling unexpected results, such as exceptions or null values.  
✔ Lightweight – Zero dependencies, Java 8+ compatible.  

### Description
The idea for this project started from a presentation given by Tom Johnson at the London Java Community youtube channel, where he spoke about railway-oriented programming and his library, see links at the end.
His idea of inverting a function's input with it's output is great, allowing for better flexibility, but I've found it a little hard to use myself in some aspects, like for null or exception handling.
On the other side, Vavr already brings types made for handling those, such as Option and Try, but I've found them to be a little to much, especially since they couple most of their manipulation logic to the classes themselves.
This library is supposed to be a mix of both, bringing types for handling most use cases whilst decoupling the manipulation from the class itself.
Where Vavr has Either, Option and Try, I propose Group, Nullable and Attempt, and where Control has static functions organized by context, I propose diving those functions based on the types they handle.

### 1. Basic Group<L, R> usage as return type
```java
public static Group<String, Integer> example(int input) {
  if (input == 69) {
    return Group.left("Nice");
  }
  return Group.right(input);
}
```

### 2. Streamlined handling of Group
```java
import static *.example;

example(42) // Will return a Right of 42
  .then(onRight(add(1729))) // If it's a Right (which is), will apply the "add" function on its value
  .then(onLeft(message -> System.out.println(message))) // If it's a Left (which is not), will apply the given lambda to its value

// Changing the input value will change the overall outcome
example(69) // Will return a Left of "Nice"
  .then(onRight(add(1729))) // Expression ignored since it's not a Right
  .then(onLeft(message -> System.out.println(message))) // Will print "Nice"
```

### 3. Error/Exception wrapping
```java
try {
  return Attempt.success(Class.forName("Class Name"));
} catch (ClassNotFoundException e) {
  return Attempt.failure(e); // Delegates handling to caller
}
```

### 4. Null wrapping
```java
Nullable<MyObject> nonNullResult = Nullable.of(Database.find("object id")) // If query results in non null, a Nullable with the desired value is passes
Nullable<MyObject> nullResult = Nullable.of(Database.find("not the object id")) // If query results in null, an empty Nullable is passed instead

nonNullResult
  .then(map(Object::toString))
  .then(ifPresentDo(System.out::println)) // Will print "My Object"

nullResult
  .then(map(Object::toString))
  .then(ifPresentDo(System.out::println)) // Will not print anything
```

### Core Types
| Type				| Purpose																			| Vavr-Type 		|
|:----------------|:----------------------------------------------------------------|:----------------|
| `Group<L, R>`	| Represents a value which can be of two types							| `Either<L, R>`	|
| `Nullable<T>`	| Wraps nullable values and safely handles them						| `Option<T>`		|
| `Attempt<T, E>`	| Represents successes (desired value) and failures (exceptions)	| `Try<T>`			|

### Developer's note
I'm going to be upfront and say that most of this project's code is not new and I don't know much about licensing, therefore, any tips or clarifications about such topic are really welcome at my inbox.
Honestly, though not much original, transcribing it from both libraries at the same time was harder than expected, mostly due to Java's type erasure.

### See also
Control: [Repository](https://github.com/writeoncereadmany/control), [Author](https://github.com/writeoncereadmany)  
Vavr: [Repository](https://github.com/vavr-io/vavr), [Website](https://https://vavr.io)  
London Java Community: [Youtube](https://www.youtube.com/@LondonJavaCommunity)  
Inpiration Video: [Railway-oriented programming in Java](https://youtu.be/4zpDZ8gwmc4?si=-3zqZDYysPRE68y-)  
