package utility;

import java.util.ArrayList;

public class ArrayListSynchronized<T> {

    private ArrayList<T> arrayList;

    public ArrayListSynchronized(){
        this.arrayList = new ArrayList<>();
    }

    public synchronized boolean add(T element){
        return this.arrayList.add(element);
    }

    public synchronized boolean remove(T element){
        return this.arrayList.remove(element);
    }

    @Override
    public ArrayListSynchronized<T> clone(){
        ArrayListSynchronized<T> ret;
        synchronized (this){
            ret = (ArrayListSynchronized<T>) this.arrayList.clone();
        }
        return ret;
    }

}
