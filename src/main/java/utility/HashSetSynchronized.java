package utility;

import java.util.HashSet;
import java.util.Set;

public class HashSetSynchronized<T> {
    private HashSet<T> hashSet;

    public HashSetSynchronized(){
        this.hashSet = new HashSet<>();
    }

    public synchronized boolean add(T element){
        return this.hashSet.add(element);
    }

    public synchronized boolean remove(T element){
        return this.hashSet.remove(element);
    }

    public synchronized Set<T> getList(){
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
