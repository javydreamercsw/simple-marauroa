package simple.server.extension.d20.alignment;

import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.iD20Definition;

/**
 * A creature’s general moral and personal attitudes are represented by its
 * alignment: lawful good, neutral good, chaotic good, lawful neutral, neutral,
 * chaotic neutral, lawful evil, neutral evil, or chaotic evil.
 *
 * Alignment is a tool for developing your character’s identity. It is not a
 * straitjacket for restricting your character. Each alignment represents a
 * broad range of personality types or personal philosophies, so two characters
 * of the same alignment can still be quite different from each other. In
 * addition, few people are completely consistent.
 *
 * Good Vs. Evil
 *
 * Good characters and creatures protect innocent life. Evil characters and
 * creatures debase or destroy innocent life, whether for fun or profit.
 *
 * "Good" implies altruism, respect for life, and a concern for the dignity of
 * sentient beings. Good characters make personal sacrifices to help others.
 *
 * "Evil" implies hurting, oppressing, and killing others. Some evil creatures
 * simply have no compassion for others and kill without qualms if doing so is
 * convenient. Others actively pursue evil, killing for sport or out of duty to
 * some evil deity or master.
 *
 * People who are neutral with respect to good and evil have compunctions
 * against killing the innocent but lack the commitment to make sacrifices to
 * protect or help others. Neutral people are committed to others by personal
 * relationships.
 *
 * Being good or evil can be a conscious choice. For most people, though, being
 * good or evil is an attitude that one recognizes but does not choose. Being
 * neutral on the good-evil axis usually represents a lack of commitment one way
 * or the other, but for some it represents a positive commitment to a balanced
 * view. While acknowledging that good and evil are objective states, not just
 * opinions, these folk maintain that a balance between the two is the proper
 * place for people, or at least for them.
 *
 * Animals and other creatures incapable of moral action are neutral rather than
 * good or evil. Even deadly vipers and tigers that eat people are neutral
 * because they lack the capacity for morally right or wrong behavior.
 *
 * Law Vs. Chaos Lawful characters tell the truth, keep their word, respect
 * authority, honor tradition, and judge those who fall short of their duties.
 *
 * Chaotic characters follow their consciences, resent being told what to do,
 * favor new ideas over tradition, and do what they promise if they feel like
 * it.
 *
 * "Law" implies honor, trustworthiness, obedience to authority, and
 * reliability. On the downside, lawfulness can include close-mindedness,
 * reactionary adherence to tradition, judgmentalness, and a lack of
 * adaptability. Those who consciously promote lawfulness say that only lawful
 * behavior creates a society in which people can depend on each other and make
 * the right decisions in full confidence that others will act as they should.
 *
 * "Chaos" implies freedom, adaptability, and flexibility. On the downside,
 * chaos can include recklessness, resentment toward legitimate authority,
 * arbitrary actions, and irresponsibility. Those who promote chaotic behavior
 * say that only unfettered personal freedom allows people to express themselves
 * fully and lets society benefit from the potential that its individuals have
 * within them.
 *
 * Someone who is neutral with respect to law and chaos has a normal respect for
 * authority and feels neither a compulsion to obey nor a compulsion to rebel.
 * She is honest but can be tempted into lying or deceiving others.
 *
 * Devotion to law or chaos may be a conscious choice, but more often it is a
 * personality trait that is recognized rather than being chosen. Neutrality on
 * the lawful-chaotic axis is usually simply a middle state, a state of not
 * feeling compelled toward one side or the other. Some few such neutrals,
 * however, espouse neutrality as superior to law or chaos, regarding each as an
 * extreme with its own blind spots and drawbacks.
 *
 * Animals and other creatures incapable of moral action are neutral. Dogs may
 * be obedient and cats free-spirited, but they do not have the moral capacity
 * to be truly lawful or chaotic.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface D20Alignment extends D20Characteristic, iD20Definition {

    public static final String ALIGNMENT = "alignment";
}
