package simple.server.extension.d20.requirement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.rpclass.D20Class;

public class AbstractD20Requirement implements D20Requirement {

    protected List<Class<? extends D20Class>> exclusiveClasses
            = new ArrayList<>();
    protected Map<Class<? extends D20Characteristic>, Integer> requirements
            = new HashMap<>();

    @Override
    public List<Class<? extends D20Class>> getExclusiveClasses() {
        return exclusiveClasses;
    }

    @Override
    public Map<Class<? extends D20Characteristic>, Integer> getRequirements() {
        return requirements;
    }

    @Override
    public int levelRequirement() {
        return 0;
    }
}
