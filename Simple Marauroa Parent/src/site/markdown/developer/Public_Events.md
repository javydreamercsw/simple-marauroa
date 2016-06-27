---
date: 2016-06-27T10:07:33-05:00
title: Public Events
---
Public Events
====

----

This is a concept introduced with Simple Server to the Marauroa picture. The Event system is currently on diapers in Stendhal. As part of the implementation of normal chat as an event the Public Event system was created.

How it works?
===

The SimpleRPZone have a new method: applyPublicEvent. Here's the code:

~~~~~
:::java
public void applyPublicEvent(RPEvent event) {
        Iterator playerList = getPlayers().iterator();
        while (playerList.hasNext()) {
            ClientObject p = (ClientObject) playerList.next();
            p.addEvent(event);
            p.notifyWorldAboutChanges();
        }
}
~~~~~

Nothing fancy but powerful. Basically gets all players in the zone and adds the event to each one. With this the event will be sent to each player on the next perception and the client side just needs to react to it accordingly. Yes, it's that simple.

In the SimpleRPWorld we have the magic part, not fancy either:

~~~~~
:::java
public boolean applyPublicEvent(SimpleRPZone zone, RPEvent event) {
        Vector<SimpleRPZone> zones = new Vector<SimpleRPZone>();
        if (zone != null) {
            zones.add(zone);
        } else {
            Iterator zoneList = iterator();
            while (zoneList.hasNext()) {
                zones.add((SimpleRPZone) zoneList.next());
            }
        }
        while (!zones.isEmpty()) {
            zones.get(0).applyPublicEvent(event);
            zones.remove(0);
        }
        return true;
    }
~~~~~

Basically if a zone is specified (zone not null) the event is sent to that zone only. This is the case of public chat because only people on the same room are able to hear you.

When no zone is specified the event is sent to all players in all registered zones. This might be useful to send system messages or notifications.

To finish the loop we modify the CUDRoomAction CUD methods to send the appropriate event:

~~~~~
:::java
private void create(ClientObjectInterface player, RPAction action) {
        ...
        Lookup.getDefault().lookup(IRPWorld.class).applyPublicEvent(
                new ZoneEvent((ISimpleRPZone) zone, ZoneEvent.ADD));
    }

    private void update(RPAction action) {
        ...
        world.applyPublicEvent(null,
                    new ZoneEvent(updated, ZoneEvent.UPDATE));
    }

    private void remove(Player player, RPAction action) {
        ...
        world.applyPublicEvent(null,
                    new ZoneEvent(new SimpleRPZone(action.get(ROOM)),
                            ZoneEvent.REMOVE));
        ...
    }
~~~~~
**Note**: See the [Zone Extension](https://bitbucket.org/javydreamercsw/simple-marauroa-java/src/45975ccd19d641bf285ffd502290a75e011a815e/Simple%20Marauroa%20Parent/Zone-Extension/src/main/java/simple/server/extension/?at=default) for details.

We use a null zone to send the update to everyone.

I almost forgot! Since the new event will be sent as part of the Player's object, it's rpclass must be updated as follows:

~~~~~
:::java
public static void generateRPClass() {
        RPClass player = new RPClass("player");
        player.isA("client_object");
        /**
         * Add event
         * player.addRPEvent("<Event RPClassName>", Definition.VOLATILE);
         */
        player.addRPEvent(RoomEvent.getRPClassName(), Definition.VOLATILE);
 }
~~~~~

By stating that is a client_object everything defined in the client object's generateRPClass method is inherited by player. And in addition the new event is added.

Now we'll turn this new server functionality in a server extension!

Next: [Server Extension](/developer/Server_Extension/)

Back to [Events](/developer/RPEvent/)

