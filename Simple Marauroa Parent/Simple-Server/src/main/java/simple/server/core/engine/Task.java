package simple.server.core.engine;

public interface Task<T> {

    void execute(T object);
}