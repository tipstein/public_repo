package assignment06;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class ChainingHashTable implements Set<String>{
    //***************MEMBER VARIABLES*******************//
    //    private LinkedList<String>[] storage -> the array of linkedLists
    //    int capacity_ -> capacity passed as param
    //    MyHashFunctor functor_ -> hashing function either passed or standard
    //    static int collisionCount_ -> collisions recorded when duplicate is detected
    //    static int storageSize_ -> size is increased throgh add/addAll
    //    static ArrayList<Integer>index_ -> an array of all added hash values
    //    public int discreetItems_ -> number of non-duplicates

    //************MY METHODS******************//
//    public ChainingHashTable(int capacity, HashFunctor functor) -> constructor
//    public ArrayList getIndex() -> returns a list of added hashValues
//    public int getCollisionCount() -> returns number of collisions
//    public int getDiscreetItems() -> returns number of discreet items
//    public Object peek(int index) -> returns Object at index

    //************IMPLEMENTED METHODS******************//


//        public boolean add(Type item);
//        public boolean addAll(Collection<? extends Type> items);
//        public void clear();
//        public boolean contains(Type item);
//        public boolean containsAll(Collection<? extends Type> items);
//        public boolean isEmpty();
//        public boolean remove(Type item);
//        public boolean removeAll(Collection<? extends Type> items);
//        public int size();



    /***
     *
     * @param capacity : the size of the array
     * @param functor : the hashing operation
     * @ param storage : the array
     * @ param storageSize : how many items have been added
     * @ param index : an arraylist containing the hash values of
     *                all added items
     * @ param collisionCount: collisions
     * @ param discreetItems: total items - collisions
     */
    public ChainingHashTable(int capacity, HashFunctor functor){

       if (capacity == 0) {
           throw new NullPointerException();
       }
        capacity_ = capacity;
        functor_ =  functor;
        storage = (LinkedList<String>[]) new LinkedList[capacity];
        storageSize_ = 0;
        index_ = new ArrayList<>();
        collisionCount_ = 0;
        discreetItems_ = 0;
    }

    /***
     *
     * @return the arrayList containing hashValues
     */
    public ArrayList getIndex() {
        return index_;
    }

    /***
     *
     * @return number of collisions
     */
    public int getCollisionCount() {
        return collisionCount_;
    }

    /***
     *
     * @return number of discreet items
     */
    public int getDiscreetItems() {
        return discreetItems_;
    }

    /***
     * useful for verifying contents of storage
     * index values are accessed in arrayList index_
     * @param index
     * @return
     */
    public Object peek(int index){
        return storage[index].peek();
    }

    /***
     *
     * @param item - the item whose presence is ensured in this set
     * @return
     */
    @Override
    public boolean add(String item) {

        if (item.isEmpty()) {return false;}
        if (item == null) { return false;}

        int functorReturn = functor_.hash(item);
        index_.add(functorReturn);
        if (storage[functorReturn] == null) {
            storage[functorReturn] = new LinkedList<String>();
            storage[functorReturn].add(item);
            discreetItems_++;
        } else {
            collisionCount_++;
            storage[functorReturn].add(item);
        }
        storageSize_++;
        return true;
    }

    /***
     *
     * @param items - the collection of items whose presence is ensured in this set
     * @return
     */
    @Override
    public boolean addAll(Collection<? extends String> items) {

        int initialSize = items.size();
        int finalSize = 0;

        //if collection is empty don't proceed
        if (items.isEmpty()) {return false;}

        for (String item : items) {

            int functorReturn = functor_.hash(item);
            index_.add(functorReturn);

            if (storage[functorReturn] == null) {
                finalSize++;
                storage[functorReturn] = new LinkedList<String>();
                storage[functorReturn].add(item);
                storageSize_++;
                discreetItems_++;
            } else {
                finalSize++;
                collisionCount_++;
                storage[functorReturn].add(item);
                storageSize_++;
            }
        }
        //guarantees all items have been added
        if (initialSize == finalSize) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void clear() {
        storage = null;
    }

    /***
     *
     * @param item - the item sought in this set
     * @return
     */
    @Override
    public boolean contains(String item) {
        int hashVal = functor_.hash(item);
        if (storage[hashVal] == null){
            return false;
        } else {
            if (storage[hashVal].peekFirst() == item) {
                return true;

            }
            else {
                if (storage[hashVal].indexOf(item) != -1) {
                    return true;
                }
            }
        }
        return false;
    }

    /***
     *
     * @param items - the collection of items sought in this set
     * @return false if any item is not identified
     */
    @Override
    public boolean containsAll(Collection<? extends String> items) {

        for (String item: items) {
            int hashVal = functor_.hash(item);
            if (storage[hashVal] == null) {
                return false;
            } else {
                if (storage[hashVal].peekFirst() != item && storage[hashVal].indexOf(item) != -1) {
                    return false;
                }
            }
        }
        return true;
    }

    /***
     * if no items have been added to array
     * @return false
     */
    @Override
    public boolean isEmpty() {
        if (storageSize_ == 0) {
        return false;
        }
        return true;
    }

    /***
     *
     * @param item - the item whose absence is ensured in this set
     * @return
     */
    @Override
    public boolean remove(String item) {
        int hashVal = functor_.hash(item);
        if (storage[hashVal] == null){
            return false;
        }

        if (hashVal > capacity_) {
            return false;
        }

        else {
            if (storage[hashVal].peekFirst() == item) {
                storage[hashVal].remove(0);
                return true;

            }
            else {
                if (storage[hashVal].indexOf(item) != -1) {
                    int desiredIndex = storage[hashVal].indexOf(item);
                    storage[hashVal].remove(desiredIndex);
                    return true;
                }
            }
        }
        return false;
    }

    /***
     *
     * @param items - the collection of items whose absence is ensured in this set
     * @return
     */
    @Override
    public boolean removeAll(Collection<? extends String> items) {
        for (String item: items) {
            item = null;
        }
        return true;
    }

    /***
     * size is increased with every add/addAll
     * @return size
     */
    @Override
    public int size() {
        return storageSize_;
    }

    private LinkedList<String>[] storage;
    int capacity_;
    HashFunctor functor_;
    static int collisionCount_;
    static int storageSize_;
    static ArrayList<Integer>index_;
    public int discreetItems_;

    public static class MediocreHashFunctor implements HashFunctor {


        @Override
        public int hash(String item) {
            int sum = item.length();
            return sum;
        }
    }
}
