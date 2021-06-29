public class Wrapper<T> {
    private T data;
    public Wrapper<T> next;
    public Wrapper<T> prev;
    public Wrapper(T data){
        if (data != null)
        {
            this.data = data;
        }
    }
    public String toString()
    {
        return data.toString();
    }

    public void setPrev(Wrapper<T> prev) {
        this.prev = prev;
    }
    public void setNext(Wrapper<T> next) {
        this.next = next;
    }
    public Wrapper<T> getNext()
    {
        return next;
    }
    public Wrapper<T> getPrev()
    {
        return prev;
    }
    public T getInstance()
    {
        return data;
    }
}