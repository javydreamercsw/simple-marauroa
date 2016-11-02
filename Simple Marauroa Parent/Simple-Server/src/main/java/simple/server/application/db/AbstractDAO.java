package simple.server.application.db;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDAO implements DAO {

    protected final Map<String, Object> parameters = new HashMap<>();

    @Override
    public void init() {
        //Donothing by default.
    }
}
