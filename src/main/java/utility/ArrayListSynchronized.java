package utility;

import java.util.ArrayList;
import java.util.List;

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

    public synchronized List<T> getList(){
        return (List<T>) this.arrayList.clone();
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
