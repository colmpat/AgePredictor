public class ArrayList<T> {
    T[] arr;
    int size;

    public ArrayList() {
        this.size = 0;
        this.arr = (T[]) new Object[10];
    }

    public T get(int pos) throws Exception {
        if(pos >= size || pos < 0)
            throw new Exception("Index out of bounds exception");
        return arr[pos];
    }

    public void add(T data) {
        if(size == arr.length)
            growArray();
        arr[size] = data;
        size++;
    }

    private void growArray() {
        T[] newArr = (T[]) new Object[arr.length * 2];
        for(int i = 0; i < size; i++)
            newArr[i] = arr[i];
        arr = newArr;
    }
}
