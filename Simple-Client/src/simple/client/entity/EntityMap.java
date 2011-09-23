/*
 * $Rev: 308 $
 * $LastChangedDate: 2010-05-02 17:45:46 -0500 (Sun, 02 May 2010) $
 * $LastChangedBy: javydreamercsw $
 */
package simple.client.entity;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Pair;

/**
 * Registers the relationship between Type, eclass and java class of entity
 * Objects.
 * <p>
 * eclass represents a subtype of type
 * <p>
 * EntityMap encapsulates the implementation
 * 
 */
public final class EntityMap {

    private static Map<Pair<String, String>, Class<? extends ClientEntity>> entityMap = new HashMap<Pair<String, String>, Class<? extends ClientEntity>>();


    static {
        register();
    }

    /**
     * Fills EntityMap with initial values.
     */
    private static void register() {
        register("player", null, ClientRPEntity.class);

        /*
         * Not sure whether to register individual pets from child classes, or
         * the whole parent class Pet. suggestions welcome.
         */

        register("npc", null, NPC.class);

        /* I might add items later, belts?
        register("chest", null, Chest.class);

        register("item", null, Item.class);
        register("item", "box", Box.class);
        register("item", "drink", StackableItem.class);
         * */
    }

    /**
     * @param type
     *            the type of the entity to be created, such as Item, creature
     * @param eclass
     *            the subtype of type such as book, drink, food , ,
     *            small_animal, huge_animal
     * @param entityClazz
     *            the java class of the ClientEntity
     */
    private static void register(final String type, final String eclass,
            final Class<? extends ClientEntity> entityClazz) {
        entityMap.put(new Pair<String, String>(type, eclass), entityClazz);
    }

    /**
     * @param type
     *            the type of the entity to be created, such as Item, creature
     * @param eclass
     *            the subtype of type such as book, drink, food , ,
     *            small_animal, huge_animal
     * 
     * @return the java class of the ClientEntity belonging to type and eclass
     */
    public static Class<? extends ClientEntity> getClass(final String type,
            final String eclass) {
        return entityMap.get(new Pair<String, String>(type, eclass));
    }
}
