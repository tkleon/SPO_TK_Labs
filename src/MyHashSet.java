public class MyHashSet {
    int num;
    MyLinkedList[] set;

    public MyHashSet() {
        num = 4;
        set = new MyLinkedList[num];
        for (int i = 0; i < num; i++) {
            set[i] = new MyLinkedList();
        }
    }

    private boolean needRehash() {
        int amount = 0;
        for (MyLinkedList list: set) {
            if (list != null && list.size() >= 2) {
                amount++;
            }
        }
        return amount / num >= 0.75;
    }

    private void rehash() {
        int newCount = num * 2;
        MyLinkedList[] newContent = new MyLinkedList[newCount];
        for (MyLinkedList list: set) {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    Object element = list.get(i);
                    int newIndex = element.hashCode() % newCount;
                    if (newContent[newIndex] == null) {
                        newContent[newIndex] = new MyLinkedList();
                    }
                    newContent[newIndex].add(element);
                }
            }
        }
        set = newContent;
        num = newCount;
    }

    private int h(Object object) {
        return object.hashCode() % num;
    }

    public void add(Object object) {
        if (needRehash()) {
            rehash();
        }
        int h = h(object);
        if (!set[h].contains(object)) {
            set[h].add(object);
        }
    }

    public boolean contains(Object object) {
        int h = h(object);
        return set[h] != null && set[h].contains(object);
    }

    public void remove(Object object) {
        int h = h(object);
        String objectString = object.toString();
        MyLinkedList list = set[h];
        if (list != null && list.contains(objectString)) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(objectString)) {
                    list.remove(i);
                }
            }
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        MyHashSet set = (MyHashSet) object;
        return hashCode() == set.hashCode();
    }

    public String toString() {
        String s = "{";
        for (MyLinkedList list: set) {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    s += list.get(i) + ", ";
                }
            }
        }
        if (s.length() > 1)
            s = s.substring(0, s.length() - 2); //удаление ненужной последней запятой
        s += "}";
        return s;
    }
}