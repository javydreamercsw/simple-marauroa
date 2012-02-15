package simple.server.extension.card;

public interface IMergeable<T> {

    public void setMergeOnAdd(boolean v);

    public boolean getMergeOnAdd();
}