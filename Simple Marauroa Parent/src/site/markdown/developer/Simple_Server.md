---
date: 2016-06-27T10:32:50-05:00
title: Simple-Server
---

This is the most basic building block for any application using Marauroa.

Terms:
===

* **Account**: basically name, username, password and email
* **Character**: this is the object containing all the data of the player. There can be more than one character per account.
* **Entity**: Object (either human or AI controlled) that "lives" in the application
* **Private Text**: Chat for only one player (secret)
* **Text**: Normal chat, everyone can see it.
* **Zone**: Abstract area where multiple entities are at a certain time

What Simple-Server does?
===

* Connect to Marauroa engine
* Implement Player entity extension
* Implement Private and Normal text
* Implement Zones
* Verify game and version match the client

It might not be much but it's the most basic stuff.

Next: [Simple Client](/developer/Simple_Client/)

[Back to Learning Trail](developer/start/)

