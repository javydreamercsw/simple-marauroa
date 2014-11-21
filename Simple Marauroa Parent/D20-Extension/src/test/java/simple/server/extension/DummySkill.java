package simple.server.extension;

import simple.server.extension.skill.AbstractSkill;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DummySkill extends AbstractSkill {

    public DummySkill() {
        modifiers.clear();
        modifiers.put(DummyAttr.class, "1");
        modifiers.put(DummyAttr2.class, "2d4+1");
    }

    @Override
    public String getName() {
        return "Dummy Ability";
    }

    @Override
    public String getShortName() {
        return "DA";
    }
}
