package simple.server.extension.d20.alignment;

public class AbstractAlignment implements D20Alignment {

    @Override
    public String getName() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }

    @Override
    public String getShortName() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }

    @Override
    public String getDescription() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }
}
