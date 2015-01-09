package simple.server.extension.d20.misc;

public abstract class AbstractMisc implements D20Misc {

    @Override
    public String getDefaultValue() {
        return "";
    }

    @Override
    public String getName() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }

    @Override
    public String getShortName() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }
}
