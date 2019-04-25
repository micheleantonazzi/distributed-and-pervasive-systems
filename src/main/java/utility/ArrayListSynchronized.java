package utility;

import java.util.ArrayList;

public class ArrayListSynchronized<T> {

    private ArrayList<T> arrayList;

    public ArrayListSynchronized(){
        this.arrayList = new ArrayList<>();
    }

    public synchronized void add(T element){
        this.arrayList.add(element);
    }

    public synchronized boolean remove(T element){
        return this.arrayList.remove(element);
    }

}
