
package simple.server.core.entity;

/**
 * A data structure that represents the outfit of an RPEntity. This RPEntity can
 * either be an NPC which uses the outfit sprite system, or of a player.
 * 
 * You can use this data structure so that you don't have to deal with the way
 * outfits are stored internally.
 * 
 * An outfit can contain of up to four parts: hair, head, dress, and base.
 * 
 * Note, however, that you can create outfit objects that consist of less than
 * four parts by setting the other parts to <code>null</code>. For example,
 * you can create a dress outfit that you can combine with the player's current
 * so that the player gets the dress, but keeps his hair, head, and base.
 * 
 * @author daniel
 * 
 */
public class Outfit {

    /** The hair index, as a value between 0 and 99, or null. */
    private Integer hair;
    /** The head index, as a value between 0 and 99, or null. */
    private Integer head;
    /** The dress index, as a value between 0 and 99, or null. */
    private Integer dress;
    /** The base index, as a value between 0 and 99, or null. */
    private Integer base;

    /**
     * Creates a new default outfit (naked person).
     */
    public Outfit() {
        this(0, 0, 0, 0);
    }

    /**
     * Creates a new outfit. Set some of the parameters to null if you want an
     * entity that put on this outfit to keep on the corresponding parts of its
     * current outfit.
     *
     * @param hair
     *            The index of the hair style, or null
     * @param head
     *            The index of the head style, or null
     * @param dress
     *            The index of the dress style, or null
     * @param base
     *            The index of the base style, or null
     */
    public Outfit(Integer hair, Integer head, Integer dress, Integer base) {
        this.hair = hair;
        this.head = head;
        this.dress = dress;
        this.base = base;
    }

    /**
     * Creates a new outfit based on a numeric code.
     *
     * @param code
     *            A 8-digit decimal number where the first pair (from the left)
     *            of digits stand for hair, the second pair for head, the third
     *            pair for dress, and the fourth pair for base.
     */
    public Outfit(int code) {
        int remainder = code;
        this.base = remainder % 100;
        remainder /= 100;
        this.dress = remainder % 100;
        remainder /= 100;
        this.head = remainder % 100;
        remainder /= 100;
        this.hair = remainder;
    }

    /**
     * Gets the index of this outfit's base style.
     *
     * @return The index, or null if this outfit doesn't contain a base.
     */
    public Integer getBase() {
        return base;
    }

    /**
     * Sets the index of this outfit's base style.
     *
     * @param base
     *            The index, or null if this outfit shouldn't contain a base.
     */
    public void setBase(Integer base) {
        this.base = base;
    }

    /**
     * Gets the index of this outfit's dress style.
     *
     * @return The index, or null if this outfit doesn't contain a dress.
     */
    public Integer getDress() {
        return dress;
    }

    /**
     * Sets the index of this outfit's dress style.
     *
     * @param dress
     *            The index, or null if this outfit shouldn't contain a dress.
     */
    public void setDress(Integer dress) {
        this.dress = dress;
    }

    /**
     * Gets the index of this outfit's hair style.
     *
     * @return The index, or null if this outfit doesn't contain hair.
     */
    public Integer getHair() {
        return hair;
    }

    /**
     * Sets the index of this outfit's hair style.
     *
     * @param hair
     *            The index, or null if this outfit shouldn't contain hair.
     */
    public void setHair(Integer hair) {
        this.hair = hair;
    }

    /**
     * Gets the index of this outfit's head style.
     *
     * @return The index, or null if this outfit doesn't contain a head.
     */
    public Integer getHead() {
        return head;
    }

    /**
     * Sets the index of this outfit's head style.
     *
     * @param head
     *            The index, or null if this outfit shouldn't contain a head.
     */
    public void setHead(Integer head) {
        this.head = head;
    }

    /**
     * Represents this outfit in a numeric code.
     *
     * @return A 8-digit decimal number where the first pair of digits stand for
     *         hair, the second pair for head, the third pair for dress, and the
     *         fourth pair for base.
     */
    public int getCode() {
        return hair * 1000000 + head * 10000 + dress * 100 + base;
    }

    /**
     * Gets the result that you get when you wear this outfit over another
     * outfit. Note that this new outfit can contain parts that are marked as
     * NONE; in this case, the parts from the other outfit will be used.
     *
     * @param other
     *            the outfit that should be worn 'under' the current one
     * @return the combined outfit
     */
    public Outfit putOver(Outfit other) {
        int newHair;
        int newHead;
        int newDress;
        int newBase;
        // wear the this outfit 'over' the other outfit;
        // use the other outfit for parts that are not defined for this outfit.
        if (this.hair == null) {
            newHair = other.hair;
        } else {
            newHair = this.hair;
        }
        if (this.head == null) {
            newHead = other.head;
        } else {
            newHead = this.head;
        }
        if (this.dress == null) {
            newDress = other.dress;
        } else {
            newDress = this.dress;
        }
        if (this.base == null) {
            newBase = other.base;
        } else {
            newBase = this.base;
        }
        return new Outfit(newHair, newHead, newDress, newBase);
    }

    /**
     * Checks whether this outfit is equal to or part of another outfit.
     *
     * @param other
     *            Another outfit.
     * @return true iff this outfit is part of the given outfit.
     */
    public boolean isPartOf(Outfit other) {
        return (hair == null || hair.equals(other.hair)) && (head == null || head.equals(other.head)) && (dress == null || dress.equals(other.dress)) && (base == null || base.equals(other.base));
    }

    /**
     * Checks whether this outfit may be selected by a normal player as normal
     * outfit. It @return false for special event and GM outfits.
     *
     * @return true if it is a normal outfit
     */
    public boolean isChoosableByPlayers() {
        return (hair < 50) && (hair >= 0) && (head < 50) && (head >= 0) && (dress < 50) && (dress >= 0) && (base < 50) && (base >= 0);
    }

    /**
     * Is outfit missing a dress?
     *
     * @return true if naked, false if dressed
     */
    public boolean isNaked() {
        return (dress == null) || dress.equals(0);
    }
}
