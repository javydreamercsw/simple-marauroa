/*
 * $Rev: 313 $
 * $LastChangedDate: 2010-05-17 07:32:53 -0500 (Mon, 17 May 2010) $
 * $LastChangedBy: javydreamercsw $
 */
package simple.client.entity;

import simple.common.Grammar;
import simple.common.ItemTools;
import simple.common.NotificationType;
import simple.common.Rand;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import marauroa.common.Log4J;
import marauroa.common.Logger;

import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;

import simple.client.HeaderLessEventLine;
import simple.client.SimpleUI;
import simple.client.soundreview.SoundMaster;

/**
 * This class is a link between client graphical objects and server attributes
 * objects.<br>
 * You need to extend this object in order to add new elements to the game.
 */
public abstract class ClientRPEntity extends ClientEntity {

    private static final Logger logger = Log4J.getLogger(ClientRPEntity.class);
    /**
     * Admin Level property.
     */
    public static final Property PROP_ADMIN_LEVEL = new Property();
    /**
     * ghostmode property.
     */
    public static final Property PROP_GHOSTMODE = new Property();
    /**
     * Indicator text property.
     */
    public static final Property PROP_TEXT_INDICATORS = new Property();
    /**
     * Outfit property.
     */
    public static final Property PROP_OUTFIT = new Property();
    /**
     * Title Type property.
     */
    public static final Property PROP_TITLE_TYPE = new Property();
    /**
     * The value of an outfit that isn't set.
     */
    public static final int OUTFIT_UNSET = -1;
    private boolean showBladeStrike;
    String[] attackSounds = {"punch-1.wav", "punch-2.wav", "punch-3.wav",
        "punch-4.wav", "punch-5.wav", "punch-6.wav", "swingaxe-1.wav",
        "slap-1.wav", "arrow-1.wav"
    };

    /**
     *
     */
    public enum Resolution {

        /**
         *
         */
        HIT,
        /**
         *
         */
        BLOCKED,
        /**
         *
         */
        MISSED;
    };
    private int atk;
    private int def;
    private int xp;
    private int hp;
    private int adminlevel;
    /**
     * The outfit code.
     */
    private int outfit;
    private int base_hp;
    private float hp_base_hp;
    private int level;
    private boolean eating;
    private boolean poisoned;
    private long combatIconTime;
    private List<TextIndicator> textIndicators;
    private RPObject.ID attacking;
    private int mana;
    private int base_mana;
    private boolean ghostmode;
    private String guild;
    private String titleType;
    /**
     * ClientEntity we are attacking. (need to reconcile this with 'attacking')
     */
    protected ClientRPEntity attackTarget;
    /**
     * The entities attacking this entity.
     */
    protected List<ClientEntity> attackers;
    /**
     * The type of effect to show.
     *
     * These are NOT mutually exclusive - Maybe use bitmask and apply in
     * priority order.
     */
    private Resolution resolution;
    private int atkXp;
    private int defXp;
    private int atkItem = -1;
    private int defItem = -1;

    /** Creates a new game entity. */
    public ClientRPEntity() {
        textIndicators = new LinkedList<TextIndicator>();
        attackTarget = null;
        attackers = new LinkedList<ClientEntity>();
    }

    //
    // RPEntity
    //
    /**
     * Create/add a text indicator message.
     *
     * @param text
     *            The text message.
     * @param type
     *            The indicator type.
     */
    protected void addTextIndicator(final String text,
            final NotificationType type) {
        textIndicators.add(new TextIndicator(text, type));
        fireChange(PROP_TEXT_INDICATORS);
    }

    /**
     * Get the admin level.
     *
     * @return The admin level.
     */
    public int getAdminLevel() {
        return adminlevel;
    }

    /**
     * @return Returns the atk.
     */
    public int getAtk() {
        return atk;
    }

    /**
     * @return Returns the atk of items
     */
    public int getAtkItem() {
        return atkItem;
    }

    /**
     * @return the attack xp
     */
    public int getAtkXp() {
        return atkXp;
    }

    /**
     * @return Returns the base_hp.
     */
    public int getBase_hp() {
        return base_hp;
    }

    /**
     * @return Returns the base mana value
     */
    public int getBaseMana() {
        return base_mana;
    }

    /**
     * @return Returns the def.
     */
    public int getDef() {
        return def;
    }

    /**
     * @return Returns the def of items
     */
    public int getDefItem() {
        return defItem;
    }

    /**
     * @return the defence xp
     */
    public int getDefXp() {
        return defXp;
    }

    /**
     *
     * @return
     */
    public String getGuild() {
        return guild;
    }

    /**
     *
     * @return
     */
    public int getHP() {
        return hp;
    }

    /**
     * Get the ratio of HP to base HP.
     *
     * @return The HP ratio (0.0 - 1.0).
     */
    public float getHPRatio() {
        return hp_base_hp;
    }

    /**
     * Get the list of text indicator elements.
     *
     * @return An iterator of text indicators.
     */
    public Iterator<TextIndicator> getTextIndicators() {
        return textIndicators.iterator();
    }

    /**
     *
     * @return
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return Returns the total mana of a player
     */
    public int getMana() {
        return mana;
    }

    /**
     * Get the outfit code.
     *
     * @return The outfit code.
     */
    public int getOutfit() {
        return outfit;
    }

    /**
     *
     * @return
     */
    public Resolution getResolution() {
        return resolution;
    }

    /**
     * Get the nicely formatted entity title.
     *
     * This searches the follow attribute order: title, name (w/o underscore),
     * class (w/o underscore), type (w/o underscore).
     *
     * @return The title, or <code>null</code> if unknown.
     */
    @Override
    public String getTitle() {
        if (title != null) {
            return title;
        } else if (name != null) {
            return name;
        } else if (clazz != null) {
            // replace underscores in clazz and type without calling the function UpdateConverter.transformItemName() located in server code
            return ItemTools.itemNameToDisplayName(clazz);
        } else if (type != null) {
            return ItemTools.itemNameToDisplayName(type);
        } else {
            return null;
        }
    }

    /**
     * Get title type.
     *
     * @return The title type.
     */
    public String getTitleType() {
        return titleType;
    }

    /**
     * @return Returns the xp.
     */
    public int getXp() {
        return xp;
    }

    /**
     *
     * @return
     */
    public boolean isAttacking() {
        return (attacking != null);
    }

    /**
     *
     * @return
     */
    public boolean isAttackingUser() {
        return ((attacking != null) && attacking.equals(User.get().getID()));
    }

    /**
     *
     * @return
     */
    public boolean isBeingAttacked() {
        return !attackers.isEmpty();
    }

    /**
     *
     * @return
     */
    public boolean isBeingAttackedByUser() {
        User user = User.get();

        if (user == null) {
            return false;
        }

        return attackers.contains(user);
    }

    /**
     *
     * @return
     */
    public boolean isBeingStruck() {
        return showBladeStrike;
    }

    /**
     *
     */
    public void doneStriking() {
        showBladeStrike = false;
    }

    /**
     *
     * @return
     */
    public boolean isDefending() {
        return (isBeingAttacked() && (System.currentTimeMillis() - combatIconTime < 4 * 300));
    }

    /**
     *
     * @return
     */
    public boolean isEating() {
        return eating;
    }

    /**
     * Determine if in full ghostmode.
     *
     * @return <code>true</code> is in full ghostmode.
     */
    public boolean isGhostMode() {
        return ghostmode;
    }

    /**
     *
     * @return
     */
    public boolean isPoisoned() {
        return poisoned;
    }

    // Creature
    /**
     *
     * @param text
     */
    protected void nonCreatureClientAddEventLine(final String text) {
        SimpleUI.get().addEventLine(getTitle(), text);
    }

    // When this entity attacks target.
    /**
     *
     * @param target
     */
    public void onAttack(final ClientEntity target) {
        attacking = target.getID();
    }

    // When this entity's attack is blocked by the adversary
    /**
     *
     * @param target
     */
    public void onAttackBlocked(final ClientEntity target) {
        showBladeStrike = true;
    }

    // When this entity causes damaged to adversary, with damage amount
    /**
     *
     * @param target
     * @param damage
     */
    public void onAttackDamage(final ClientEntity target, final int damage) {
        showBladeStrike = true;
    }

    // When this entity's attack is missing the adversary
    /**
     *
     * @param target
     */
    public void onAttackMissed(final ClientEntity target) {
        showBladeStrike = true;
    }

    // When attacker attacks this entity.
    /**
     *
     * @param attacker
     */
    public void onAttacked(final ClientEntity attacker) {
        attackers.remove(attacker);
        attackers.add(attacker);
    }

    // When this entity blocks the attack by attacker
    /**
     *
     * @param attacker
     */
    public void onBlocked(final ClientEntity attacker) {
        combatIconTime = System.currentTimeMillis();
        resolution = Resolution.BLOCKED;
    }

    // When this entity is damaged by attacker with damage amount
    /**
     *
     * @param attacker
     * @param damage
     */
    public void onDamaged(final ClientEntity attacker, final int damage) {
        combatIconTime = System.currentTimeMillis();
        resolution = Resolution.HIT;
        try {

            SoundMaster.play(attackSounds[Rand.rand(attackSounds.length)]);
        } catch (NullPointerException e) {
        }
    }

    /*
     * Handles loss of entity caused by winner.
     *
     */
    /**
     *
     * @param winner
     */
    public void onLoss(final ClientEntity winner) {
        if (winner != null) {
            SimpleUI.get().addEventLine(new HeaderLessEventLine(
                    getTitle() + " has been defeated by " + winner.getTitle(), NotificationType.INFORMATION));
        }
    }

    // When entity eats food
    /**
     *
     * @param amount
     */
    public void onEat(final int amount) {
        eating = true;
    }

    /**
     *
     */
    public void onEatEnd() {
        eating = false;
    }

    // When entity gets healed
    /**
     *
     * @param amount
     */
    public void onHealed(final int amount) {
    }

    // When entity adjusts HP
    /**
     *
     * @param amount
     */
    public void onHPChange(final int amount) {
        if (amount > 0) {
            addTextIndicator("+" + amount, NotificationType.POSITIVE);
        } else {
            addTextIndicator(String.valueOf(amount),
                    NotificationType.NEGATIVE);
        }
    }

    // Called when entity kills another entity
    /**
     *
     * @param killed
     */
    public void onKill(final ClientEntity killed) {
    }

    // Called when entity listen to text from talker
    /**
     *
     * @param texttype
     * @param text
     */
    public void onPrivateListen(String texttype, String text) {
        NotificationType nType;
        try {
            nType = NotificationType.valueOf(texttype);
        } catch (RuntimeException e) {
            logger.error("Unkown texttype: ", e);
            nType = NotificationType.PRIVMSG;
        }

        SimpleUI.get().addEventLine(text, nType);
    }

    // Called when entity says text
    /**
     *
     * @param text
     */
    public void onTalk(final String text) {
        if (User.isAdmin()) {
            nonCreatureClientAddEventLine(text);

            String line = text.replace("|", "");

            // Allow for more characters and cut the text if possible at the
            // nearest space etc. intensifly@gmx.com
            if (line.length() > 84) {
                line = line.substring(0, 84);
                int l = line.lastIndexOf(" ");
                int ln = line.lastIndexOf("-");

                if (ln > l) {
                    l = ln;
                }

                ln = line.lastIndexOf(".");

                if (ln > l) {
                    l = ln;
                }

                ln = line.lastIndexOf(",");

                if (ln > l) {
                    l = ln;
                }

                if (l > 0) {
                    line = line.substring(0, l);
                }

                line = line + " ...";
            }
        }
    }

    //
    // ClientEntity
    //
    /**
     * Get the resistance this has on other entities (0-100).
     *
     * @return The resistance, or 0 if in ghostmode.
     */
    @Override
    public int getResistance() {
        return (isGhostMode() ? 0 : super.getResistance());
    }

    /**
     * Initialize this entity for an object.
     *
     * @param object
     *            The object.
     *
     * @see #release()
     */
    @Override
    public void initialize(final RPObject object) {
        super.initialize(object);

        /*
         * Base HP
         */
        if (object.has("base_hp")) {
            base_hp = object.getInt("base_hp");
        } else {
            base_hp = 0;
        }

        /*
         * HP
         */
        if (object.has("hp")) {
            hp = object.getInt("hp");
        } else {
            hp = 0;
        }

        /*
         * HP ratio
         */
        if (hp >= base_hp) {
            hp_base_hp = 1.0f;
        } else if (hp <= 0) {
            hp_base_hp = 0.0f;
        } else {
            hp_base_hp = hp / (float) base_hp;
        }

        /*
         * Public chat
         */
        if (object.has("text")) {
            onTalk(object.get("text"));
        }

        /*
         * Private message
         */
        for (RPEvent event : object.events()) {
            if (event.getName().equals("private_text")) {
                onPrivateListen(event.get("texttype"), event.get("text"));
            }
        }

        /*
         * Outfit
         */
        if (object.has("outfit")) {
            outfit = object.getInt("outfit");
        } else {
            outfit = OUTFIT_UNSET;
        }

        /*
         * Eating
         */
        if (object.has("eating")) {
            onEat(0);
        }

        /*
         * Poisoned
         */
        if (object.has("poisoned")) {
            // To remove the - sign on poison.
            // onPoisoned(Math.abs(object.getInt("poisoned")));
        }

        /*
         * Ghost mode feature.
         */
        if (object.has("ghostmode")) {
            ghostmode = true;
        }

        /*
         * Healed
         */
        if (object.has("heal")) {
            onHealed(object.getInt("heal"));
        }

        /*
         * Admin level
         */
        if (object.has("adminlevel")) {
            adminlevel = object.getInt("adminlevel");
        } else {
            adminlevel = 0;
        }

        /*
         * Title type
         */
        if (object.has("title_type")) {
            titleType = object.get("title_type");
        } else {
            titleType = null;
        }
    }

    /**
     * Release this entity. This should clean anything that isn't automatically
     * released (such as unregister callbacks, cancel external operations, etc).
     *
     * @see #initialize(RPObject)
     */
    @Override
    public void release() {

        if (attackTarget != null) {
            attackTarget = null;
        }

        super.release();
    }

    /**
     * Update cycle.
     *
     * @param delta
     *            The time (in ms) since last call.
     */
    @Override
    public void update(final int delta) {
        super.update(delta);

        if (!textIndicators.isEmpty()) {
            Iterator<TextIndicator> iter = textIndicators.iterator();

            while (iter.hasNext()) {
                TextIndicator textIndicator = iter.next();

                if (textIndicator.addAge(delta) > 2000L) {
                    iter.remove();
                }
            }

            fireChange(PROP_TEXT_INDICATORS);
        }
    }

    //
    // RPObjectChangeListener
    //
    /**
     * The object added/changed attribute(s).
     *
     * @param object
     *            The base object.
     * @param changes
     *            The changes.
     */
    @Override
    public void onChangedAdded(final RPObject object, final RPObject changes) {
        super.onChangedAdded(object, changes);

        if (!inAdd) {
            /*
             * Public chat
             */
            if (changes.has("text")) {
                onTalk(changes.get("text"));
            }

            /*
             * Private message
             */
            for (RPEvent event : changes.events()) {
                if (event.getName().equals("private_text")) {
                    onPrivateListen(event.get("texttype"), event.get("text"));
                }
            }

            /*
             * Outfit
             */
            if (changes.has("outfit")) {
                outfit = changes.getInt("outfit");
                fireChange(PROP_OUTFIT);
            }

            /*
             * Eating
             */
            if (changes.has("eating")) {
                onEat(0);
            }

            /*
             * Healed
             */
            if (changes.has("heal")) {
                onHealed(changes.getInt("heal"));
            }

            boolean hpRatioChange = false;

            /*
             * Base HP
             */
            if (changes.has("base_hp")) {
                base_hp = changes.getInt("base_hp");
                hpRatioChange = true;
            }

            /*
             * HP
             */
            if (changes.has("hp")) {
                int newHP = changes.getInt("hp");
                int change = newHP - hp;

                hp = newHP;

                if (object.has("hp") && (change != 0)) {
                    onHPChange(change);
                }

                hpRatioChange = true;
            }

            /*
             * HP ratio
             */
            if (hpRatioChange) {
                if (hp >= base_hp) {
                    hp_base_hp = 1.0f;
                } else if (hp <= 0) {
                    hp_base_hp = 0.0f;
                } else {
                    hp_base_hp = hp / (float) base_hp;
                }

                if (hp == 0) {
                    onLoss(attackers.isEmpty() ? null : attackers.get(0));
                }
            }

            /*
             * Admin level
             */
            if (changes.has("adminlevel")) {
                adminlevel = changes.getInt("adminlevel");
                fireChange(PROP_ADMIN_LEVEL);
            }

            /*
             * Title type
             */
            if (changes.has("title_type")) {
                titleType = changes.get("title_type");
                fireChange(PROP_TITLE_TYPE);
            }

            /*
             * Title
             */
            if (changes.has("class") || changes.has("name") || changes.has("title") || changes.has("type")) {
                fireChange(PROP_TITLE);
            }
        }

        if (changes.has("atk")) {
            atk = changes.getInt("atk");
        }

        if (changes.has("def")) {
            def = changes.getInt("def");
        }

        if (changes.has("xp")) {
            xp = changes.getInt("xp");
        }

        if (changes.has("level")) {
            level = changes.getInt("level");
        }

        if (changes.has("atk_xp")) {
            atkXp = changes.getInt("atk_xp");
        }

        if (changes.has("def_xp")) {
            defXp = changes.getInt("def_xp");
        }

        if (changes.has("atk_item")) {
            atkItem = changes.getInt("atk_item");
        }

        if (changes.has("def_item")) {
            defItem = changes.getInt("def_item");
        }

        if (changes.has("mana")) {
            mana = changes.getInt("mana");
        }

        if (changes.has("base_mana")) {
            base_mana = changes.getInt("base_mana");
        }

        if (changes.has("ghostmode")) {
            ghostmode = true;
            fireChange(PROP_GHOSTMODE);
        }

        if (changes.has("guild")) {
            guild = changes.get("guild");
        }

        if (changes.has("xp") && object.has("xp")) {
            int amount = (changes.getInt("xp") - object.getInt("xp"));
            if (amount > 0) {
                addTextIndicator("+" + amount,
                        NotificationType.SIGNIFICANT_POSITIVE);

                SimpleUI.get().addEventLine(
                        getTitle() + " earns " + Grammar.quantityplnoun(amount,
                        "experience point") + ".",
                        NotificationType.SIGNIFICANT_POSITIVE);
            } else if (amount < 0) {
                addTextIndicator("" + amount,
                        NotificationType.SIGNIFICANT_NEGATIVE);

                SimpleUI.get().addEventLine(
                        getTitle() + " loses " + Grammar.quantityplnoun(-amount,
                        "experience point") + ".",
                        NotificationType.SIGNIFICANT_NEGATIVE);
            }
        }

        if (changes.has("level") && object.has("level")) {
            String text = getTitle() + " reaches Level " + getLevel();

            SimpleUI.get().addEventLine(text,
                    NotificationType.SIGNIFICANT_POSITIVE);
        }
    }

    /**
     * The object removed attribute(s).
     *
     * @param object
     *            The base object.
     * @param changes
     *            The changes.
     */
    @Override
    public void onChangedRemoved(final RPObject object, final RPObject changes) {
        super.onChangedRemoved(object, changes);

        /*
         * Outfit
         */
        if (changes.has("outfit")) {
            outfit = OUTFIT_UNSET;
            fireChange(PROP_OUTFIT);
        }

        /*
         * No longer eating?
         */
        if (changes.has("eating")) {
            onEatEnd();
        }

        if (changes.has("ghostmode")) {
            ghostmode = false;
            fireChange(PROP_GHOSTMODE);
        }
    }

    //
    //
    /**
     *
     */
    public static class TextIndicator {

        /**
         * The age of the message (in ms).
         */
        protected int age;
        /**
         * The message text.
         */
        protected String text;
        /**
         * The indicator type.
         */
        protected NotificationType type;

        /**
         * Create a floating message.
         *
         * @param text
         *            The text to drawn.
         * @param type
         *            The indicator type.
         */
        public TextIndicator(final String text, final NotificationType type) {
            this.text = text;
            this.type = type;

            age = 0;
        }

        //
        // TextIndicator
        //
        /**
         * Add to the age of this message.
         *
         * @param time
         *            The amout to add.
         *
         * @return The new age (in milliseconds).
         */
        public int addAge(final int time) {
            age += time;

            return age;
        }

        /**
         * Get the age of this message.
         *
         * @return The age (in milliseconds).
         */
        public int getAge() {
            return age;
        }

        /**
         * Get the text message.
         *
         * @return The text message.
         */
        public String getText() {
            return text;
        }

        /**
         * Get the indicator type.
         *
         * @return The indicator type.
         */
        public NotificationType getType() {
            return type;
        }
    }
}
