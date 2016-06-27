---
date: 2016-06-27T09:48:49-05:00
title: RPAction
---
Actions in action!
===

Actions must implement the ActionListener interface as defined within Marauroa.

So we can start with the following:

```java
package games.jwrestling.server.core.action;

import games.simple.server.core.action.ActionListener;

@ServiceProvider(service = ActionProvider.class)
public class CRUDRoomAction implements ActionProvider {

    @Override
    public void register() {
        CommandCenter.register("name", new CRUDRoomAction());
    }

    /**
     * Handle the /name action.
     *
     * @param rpo
     * @param action
     */
    @Override
    public void onAction(RPObject rpo, RPAction action) {
    }
}
```

The register method registers the action so the server knows how to react when an object request the action. Simple Server automagically register all defined actions on start up on it's own. No more tweaking needed!

I forgot about to tell you about the CommandCenter. It's basically another Manager type class, that in this case, manages the actions. It's implemented in simple classes and extended in this example. basically if you need more actions besides private chat and normal chat you need to create more actions.

Isn't it great? Already our server knows about the CUDRoomAction!

Now back to the details of the register method at the action class.

Is **REALLY IMPORTANT** that each action has an unique type name. In this case "CRUDRoom". So basically

```java
register(TYPE, new CRUDRoomAction());
```

Is telling the server: "when someone tells you about CRUDRoom he means CRUDRoomAction action". Makes sense? I hope so. If not go back and read more carefully...

So far the server knows the new action and how to translate it. Now we need to show the server how to perform that action.

For that purpose we override the onAction method:

```java
@Override
    public void onAction(RPObject object, RPAction action) {
}
```

Basically when a registered action in the CommandCenter is detected, the appropriate onAction method is called.

Actions are basically a property. They extend the class Attribute from Marauroa but are easier to understand if you know [java properties](http://java.sun.com/j2se/1.5.0/docs/api/java/util/Properties.html).

Think of them as a list of name/value pairs. i.e. color= blue, hair=brown, etc...

Actions in specific need to have the following:

* **type**: the unique name I talked about
* **parameters**: as needed

The only thing you need to call an action from the client (any class that extends ClientFramework) is something like:

```java
RPAction action;
action = new RPAction();
action.put("type", "chat");
action.put("text", text);
send(action);
```

This example is for the normal chat action already implemented within the simple server.

The unique action name is chat and text is it's parameter. When the server receives the action it also knows who requested it.

The send method (inherited from ClientFramework) takes care of sending the action to the server.

That said, let's take a look at my onAction method:

```java
@Override
    public void onAction(Player player, RPAction action) {
        int op = action.getInt(OPERATION);
        try {
            switch (op) {
                case CREATE:
                    create(player, action);
                    break;
                case UPDATE:
                    update(action);
                    break;
                case DELETE:
                    remove(player, action);
                    break;
                default:
                    logger.warn("Invalid CUD operation: " + op);
            }
        } catch (Exception e) {
            logger.error("Error processing CUD room operation: " + op + ".", e);
        }
    }
```

First I get the operation being requested from the action object.

```java
int op = action.getInt(OPERATION);
```

OPERATION is just a String defined previously to name this parameter. I strongly suggest declaring those keywords as public static final variables and used like that everywhere. If you limit yourself to use strings if for some reason you change your mind about the name you might need to change it in 100+ places. If you do it this way you only change it in the action's class once.

Based on that value I use a [switch statement](http://java.sun.com/docs/books/tutorial/java/nutsandbolts/switch.html). For simplicity each action is implemented in a separate method.

Create:

```java
private void create(Player player, RPAction action) {
        AppRPWorld world = Lookup.getDefault().lookup(IRPWorld.class).getRPWorld();
        AppRPZone zone = new AppRPZone(action.get(ROOM));
        if (action.get(DESC) != null && !action.get(DESC).isEmpty()) {
            zone.setDescription(action.get(DESC));
        }
        world.addRPZone(zone);
        world.changeZone(action.get(ROOM), player);
    }
```

Basically we create the zone to be affected

```java
AppRPWorld world = Lookup.getDefault().lookup(IRPWorld.class).getRPWorld();
AppRPZone zone = new AppRPZone(action.get(ROOM));
```

Set it's description if valid

```java
if (action.get(DESC) != null && !action.get(DESC).isEmpty()) {
    zone.setDescription(action.get(DESC));
}
```

Add it to the world

```java
world.addRPZone(zone);
```

Remove the player from it's current zone (if any) and add the player to the new zone

```java
world.changeZone(action.get(ROOM), player);
```

Update:

```java
private void update(RPAction action) {
        AppRPWorld world = Lookup.getDefault().lookup(IRPWorld.class).getRPWorld();
        world.updateRPZoneDescription(action.get(ROOM), action.get(DESC));
}
```

Just call the update method previously discussed.

Remove:

```java
private void remove(Player player, RPAction action) {
        AppRPWorld world = Lookup.getDefault().lookup(IRPWorld.class).getRPWorld();
        AppRPZone zone = world.getRPZone(new ID(action.get(ROOM)));
        for (ClientObject clientObject : zone.getPlayers()) {
            world.changeZone(jWrestlingRPWorld.DEFAULT_ROOM, (Player) clientObject);
        }
        if (zone != null) {
            try {
                world.removeRPZone(new ID(action.get(ROOM)));
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(CUDRoomAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        player.notifyWorldAboutChanges();
    }
```

Basically change the player's zone and remove the zone from the world making sure to move everyone in the zone as well. Otherwise someone might end in the [limbo](http://en.wikipedia.org/wiki/Limbo).

Next: [RPEvent](/developer/RPEvent/)

[Home](/developer/start/)


