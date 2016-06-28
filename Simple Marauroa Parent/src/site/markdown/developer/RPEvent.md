---
date: 2016-06-27T10:02:42-05:00
title: RPEvent
---
Sending events back
===

So our action have been executed in the server so now we need to inform the user(s) about the outcome.

For this purpose we use Events by extending RPEvent as defined within Marauroa.

So we start with something like:

~~~~~
:::java
package games.app.server.event;

import marauroa.common.game.RPEvent;

@ServiceProvider(service = IRPEvent.class)
public class RoomEvent extends RPEvent {

}
~~~~~

As we did with the action we just created, let's add some static String fields for descriptors to avoid using plain text everywhere and some methods explained briefly...

~~~~~
:::java
package games.app.server.core.event;

import games.app.server.core.engine.AppRPZone;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

@ServiceProvider(service = IRPEvent.class)
public class RoomEvent extends RPEvent {

    public static final String RPCLASS_NAME = "room_event";
    private static final String ROOM = "room",  ACTION = "action",  DESC = "description";
    public static final String ADD = "add",  REMOVE = "remove",  UPDATE = "update";

    /**
     * Creates the rpclass.
     */
    public static void generateRPClass() {
        RPClass rpclass = new RPClass(getRPClassName());
        rpclass.add(DefinitionClass.ATTRIBUTE, ROOM, Type.STRING);
        rpclass.add(DefinitionClass.ATTRIBUTE, DESC, Type.LONG_STRING);
        rpclass.add(DefinitionClass.ATTRIBUTE, ACTION, Type.STRING);
    }

    /**
     * @return the RPCLASS_NAME
     */
    public static String getRPClassName() {
        return RPCLASS_NAME;
    }

    /**
     * @return the ROOM
     */
    public static String getRoom() {
        return ROOM;
    }

    /**
     * @return the ACTION
     */
    public static String getAction() {
        return ACTION;
    }

    /**
     * Creates a new room event.
     *
     * @param zone room added/deleted from server
     * @param action either add or remove
     */
    public RoomEvent(AppRPZone zone, String action) {
        super(RPCLASS_NAME);
        put(ROOM, zone.getName());
        //Don't add the description if deleting the room...
        if (zone.getDescription() != null && !zone.getDescription().isEmpty() &&
                !action.equals(REMOVE)) {
            put(DESC, zone.getDescription());
        }
        put(ACTION, action);
    }
}
~~~~~

Like entities, events needs to be defined to the RPManager since they are part of the perception. For that purpose we add a method to generate the proper rpclass info

~~~~~
:::java
/**
     * Creates the rpclass.
     */
    public static void generateRPClass() {
        RPClass rpclass = new RPClass(getRPClassName());
        rpclass.add(DefinitionClass.ATTRIBUTE, ROOM, Type.STRING);
        rpclass.add(DefinitionClass.ATTRIBUTE, DESC, Type.LONG_STRING);
        rpclass.add(DefinitionClass.ATTRIBUTE, ACTION, Type.STRING);
    }
~~~~~

A unique class name and 3 attributes in this example. ROOM to hold the room that was affected by the CRUD, DESC to hold the room's desciption and ACTION to state which of the CRUD operations took place. There are 3 getter methods not worth mentioning and then the class constructor.

~~~~~
:::java
/**
     * Creates a new room event.
     *
     * @param zone room added/deleted from server
     * @param action either add or remove
     */
    public RoomEvent(AppRPZone zone, String action) {
        super(RPCLASS_NAME);
        put(ROOM, zone.getName());
        //Don't add the description if deleting the room...
        if (zone.getDescription() != null && !zone.getDescription().isEmpty() &&
                !action.equals(REMOVE)) {
            put(DESC, zone.getDescription());
        }
        put(ACTION, action);
    }
~~~~~

First we call the parent's method and place the proper values for it's parameters: ROOM, DESC and ACTION. And that's it! The event is created.

Now we need to send it...

Next: [Public Events](/developer/Public_Events)

Back to [RPAction](/developer/RPAction)


