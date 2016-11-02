Basic Building Blocks
======

Engine: [Marauroa](/developer/Marauroa)

Server: [Simple Server](/developer/Simple_Server)

Client: [Simple Client](/developer/Simple_Client)

Specialized Chapters (We'll get there, they might be too complicated now)

    Event
    Actions
    Extensions
    Marauroa Heritage

Customization
======

Basically I'll be documenting each new system implemented to extend Simple-Client and Simple-server in order to have my game completed. I'll try to be as generic as possible with the new building blocks so they are reusable. It'll focus on the how to using jWrestling implementation as an example.

What tools we have?
======

   **RPEvents**: Stuff that happens at the server are notified to the client so it can react accordingly. It's really important that the events are added to the RPClass generation code for all entities that need to be aware of them. Otherwise there will be runtime errors.

   **RPAction**: Usually a RPEvent reaction in the server consists of a series of actions (might be one, many or none) and possible new RPEvents as a result of each action or group of actions. RPActions can be called on their own from the client using the client send(RPAction) method.

First Steps
======

There are a couple of classes that won't make sense right now since they'll be basically empty but they won't be for long unless you don't plan on extending Marauroa-Simple-Server's functionality. Is a good practice to extend existing simple classes so you can take full advantage of simple newer versions just by swapping the jar files. Besides it'll avoid much work being redone.

Overwrite the constructor just in case we need to add stuff. Most of the hard stuff is already done in the Simple classes.

Create a Database Manager: Create a class in the games.app.server.core.engine and name it something like AppDatabase.

Note: Marauroa-Simple-Server Database Manager uses JPA layer to connect to databases. This allows Marauroa to be used on any Database Engine supported by JPA without changing the code.

This will handle the persistence of object from and to the server. This will be modified if new tables are needed and any new logic for managing those.

For example the AppPlayerDatabase:

```java
package games.app.server.core.engine;

import games.simple.server.core.engine.SimpleDatabase;
import java.util.Properties;

@ServiceProvider(service = IDatabase.class)
public class AppPlayerDatabase extends SimpleDatabase {
//Just in case we need to add some functionality

    protected AppPlayerDatabase() {
        super();
    }
}
```

**Note**: You probably noticed the *@ServiceProvider* annotation. This is part of the Lookup library used in the application. It allows for dynamic and automatic discovery of classes. This allows for overriding functionality and enhancing the game with simple annotations for ease of use. You can read more about *@ServiceProvider* [here](http://bits.netbeans.org/8.0/javadoc/org-openide-util-lookup/org/openide/util/lookup/Lookups.html) and *Lookup* [here](http://bits.netbeans.org/8.0/javadoc/org-openide-util-lookup/org/openide/util/lookup/ServiceProvider.html).

Create a RPRuleProcessor: Create a class in the games.app.server.core.engine and name it something like AppRPRuleProcessor.

This is basically the brain of the server. It decides what happens, when and to who. So this file will be really modified in any application.

For example the AppRPRuleProcessor:

```java
package games.app.server.core.engine;

@ServiceProvider(service = IRPRuleProcessor.class)
import games.simple.server.core.engine.SimpleRPRuleProcessor;

public class AppRPRuleProcessor extends SimpleRPRuleProcessor {
//Just in case we need to add some functionality

    public jWrestlingRPRuleProcessor() {
        super();
    }
}
```
Create a RPWorld: Create a class in the games.app.server.core.engine and name it something like AppRPWorld.

This handles the abstract world. It manages all Zones and their creation. Will be modified to add behavior to the world.

For example the AppRPWorld:

```java
package games.jwrestling.server.core.engine;

import games.simple.server.core.engine.SimpleRPWorld;

@ServiceProvider(service = IRPWorld.class)
public class AppRPWorld extends SimpleRPWorld {
//Just in case we need to add some functionality

    public jWrestlingRPWorld() {
        super();
    }
}
```

Create a RPZone: Create a class in the games.app.server.core.engine and name it something like AppRPZone.

This handles the abstract zones, or areas, where entities interact. So all zone logic will be within this class or its children.

For example the AppRPZone:

```java
package games.app.server.core.engine;

import games.simple.server.core.engine.SimpleRPZone;


public class AppRPZone extends SimpleRPZone {
//Just in case we need to add some functionality

    public jWrestlingRPZone(String name) {
        super(name);
    }
}
```

Let's start with the CRUD operations in the zones. CRUD is based on the CRUD term. The term is stolen from database operations. As you might notice, I'm not good assigning names to things.

First we need to create the new field in the zone, so we can have something to CRUD. This is already implemented in the simple classes but let's take a look at it so we can understand it.

```java
.
.
private String description = "";
.
.
.
/**
 * @return the description
 */
  public String getDescription() {
        return description;
  }
.
.
.
/**
  * @param description the description to set
  */
  public void setDescription(String description) {
        this.description = description;
  }
```

This is the getter/setter way of doing things which I recommend. There are many arguments about this being a good practice or not, but for me they are a safe way of making sure that the field isn't modified by mistake. You can change the public method type to protected making this method available to classes from the same package.

In summary the get method returns the field value and the set method changes it.

Now that our object has the required characteristic we now go ahead and implement the way of performing the change.

Since this is a common task that might be performed on various objects I'll place such method in the object's type manager, in this case RPWorld.

Again, this is already implemented in the simple classes but will be explained for tutorial purposes.

```java
public SimpleRPZone updateRPZoneDescription(String zone, String desc) {
        logger.debug("Updating room: " + zone + " with desc: " + desc);
        SimpleRPZone sZone = null;
        if (hasRPZone(new ID(zone))) {
            sZone = (SimpleRPZone) getRPZone(zone);
        }
        if (sZone != null) {
            sZone.setDescription(desc);
            logger.info("Updated: " + sZone.toString());
        } else {
            logger.info("Couldn't find zone: " + zone);
        }
        return sZone;
    }

    zone is a string with the zone's name
    desc is the new description for the zone.
    logger is just a [log4j](http://en.wikipedia.org/wiki/Log4j) logger.
```

First we make sure that the world knows about the zone to avoid errors.

```java
if (hasRPZone(new ID(zone)))
```

The hasRPZone basically looks in a "list" of know zones to the world. If found return true, false otherwise.

Then we get the Zone know by that name. If zone exists

```java
sZone != null;
```

Then we modify it's description.

```java
sZone.setDescription(desc);
```

The we return the zone to comply with the inherited method signature.

```java
return sZone;
```

As it is the client, or anyone that needs to know about the change won't notice it cause we haven't noticed them. That's where Actions and Events come in handy.

Basically this is an overview of how the event system work:

![Event System](/resources/Events2.jpg "Optional title")

Events
======

The client does something like request a zone description change. This is done by sending an action to the server. Then the server performs that action and informs to anyone that needs to know about the results by adding an event to each object that might be interested. Those events are then part of the perception sent back to those objects. you can read more about the action/perception principle [here](https://stendhalgame.org/wiki/RolePlayingDesign).

So with that clear let's create our first action!

Next: [RPActions](/developer/RPAction)
