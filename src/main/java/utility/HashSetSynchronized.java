package utility;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HashSetSynchronized<T> {
    private HashSet<T> hashSet;

    public HashSetSynchronized(){
        this.hashSet = new HashSet<>();
    }

    public HashSetSynchronized(List<T> list){
        this();
        synchronized (this){
            for (T element : list)
                this.hashSet.add(element);
        }
    }

    public synchronized boolean add(T element){
        return this.hashSet.add(element);
    }

    public synchronized boolean remove(T element){
        return this.hashSet.remove(element);
    }

    public synchronized Set<T> getSet(){
        return (Set<T>) this.hashSet.clone();
    }

    @Override
    public HashSetSynchronized<T> clone(){
        HashSetSynchronized<T> ret;
        synchronized (this){
            ret = (HashSetSynchronized<T>) this.hashSet.clone();
        }
        return ret;
    }

}
