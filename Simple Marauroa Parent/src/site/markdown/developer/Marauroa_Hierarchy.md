---
date: 2016-06-27T10:38:46-05:00
title: Marauroa_Hierarchy
---
If you're used to object programming you should be used to the inheritance term. In short terms **inheritance** is a way to form new [classes](http://en.wikipedia.org/wiki/Class_%28computer_science%29) (instances of which are called [objects](http://en.wikipedia.org/wiki/Object_%28computer_science%29)) using classes that have already been defined.

You can find more details in [wikipedia](http://en.wikipedia.org/wiki/Inheritance_%28computer_science%29), [wikipedia2](http://en.wikipedia.org/wiki/Hierarchy_%28object-oriented_programming%29) and a brief explanation from Java itself [here](http://java.sun.com/docs/books/tutorial/java/concepts/inheritance.html).

If that's too complicated think of it this way: in object oriented programming, an object automatically learns and know how to do all the things his parent(s) knew at the moment the new object is born.

Why I trouble you with this? Well, the fact is that in Marauroa we have two hierarchies going on at the same time! One is normal Java hierarchy and the other one is Marauroa's.

There can be objects in the code that are related Java-wise but not Marauroa-wise and viceversa. That said, it's not enough to have Java inheritance to have Marauroa's. You have to explicitly declare that hierarchy.

For example we have Player. It has a Java hierarchy with ClientObject. That hierarchy is done simply like:

~~~~~
:::java
public class Player extends ClientObject
~~~~~

Doing the above will allow you to access ClientObject's methods in the Java world and compile without issues. But if you try to use attributes from ClientObject from Player in the Marauroa world the errors start flying in the server.

But to really make the Marauroa hierarchy you need to add the following in the generateRPClass() method:

~~~~~
:::java
RPClass player = new RPClass("player");
player.isA("client_object");
~~~~~

It's is very important the the parent generateRPClass() it's called before the child's and that each generateRPClass() it's called once! Each RPClass name must be unique otherwise the definitions get overwritten.

Both situations will cause Marauroa errors.

If you keep this in mind you won't have bigger issues. The bad thing is this kind of mistakes is really hard to track as you get lost between the both hierarchies.

This is a really short note but can save you from tons of hours of frustration.

Trust me, been there, done that.

