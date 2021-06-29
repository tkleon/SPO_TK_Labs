public class MyLinkedList {

    private Wrapper first;
    private Wrapper last;
    private int count = 0;

    public void add(Object object) {
        if (first == null) {
            first = new Wrapper(object);
            last = first;
        } else {
            Wrapper wrapper = new Wrapper(object);
            wrapper.setPrev(last);
            last.setNext(wrapper);
            last = wrapper;
        }
        count++;
    }

    private Wrapper getNode(int index) throws IndexOutOfBoundsException {
        if (index >= count) {
            throw new IndexOutOfBoundsException();
        }
        int i;
        Wrapper wrapper;
        if (count == 1) {
            wrapper = first;
        } else if (count == 2) {
            wrapper = index == 0 ? first : last;
        } else if (index < count / 2) {
            i = 0;
            wrapper = first;
            while (i < index) {
                wrapper = wrapper.getNext();
                i++;
            }
        } else {
            i = count - 1;
            wrapper = last;
            while (i > index) {
                wrapper = wrapper.getPrev();
                i--;
            }
        }
        return wrapper;
    }

    public Object get(int index) {
        if (index >= count) {
            throw new IndexOutOfBoundsException();
        }
        return getNode(index).getInstance();
    }

    public void remove(int index) throws IndexOutOfBoundsException {
        if (index >= count) {
            throw new IndexOutOfBoundsException();
        }
        Wrapper wrapper = getNode(index);
        Wrapper prev = wrapper.getPrev();
        Wrapper next = wrapper.getNext();
        if (prev != null) {
            prev.setNext(next);
        } else {
            first = next;
        }
        if (next != null) {
            next.setPrev(prev);
        } else {
            last = prev;
        }
        count--;
    }

    public boolean contains(Object object) {
        for (Wrapper current = first; current != null; current = current.getNext()) {
            if (current.getInstance().hashCode() == object.hashCode()) {
                if (current.getInstance().equals(object)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int size() {
        return count;
    }

    public String toString() {
        if (first == null) {
            return "[]";
        }
        String string = "[";
        for (Wrapper current = first; current != last; current = current.getNext()) {
            string += current + ", ";
        }
        string += last.toString() + "]";
        return string;
    }
}