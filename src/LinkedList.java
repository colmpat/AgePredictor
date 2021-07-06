
//I kept this LinkedList very lightweight beacause we have no use for any other functions or variables.
public class LinkedList<T> {
    Node<T> head;

    public LinkedList() {
        this.head = null;
    }

    //Add new node to the front of the list for fastest operation as data order is not important
    public void add(T data) {
        Node<T> node = new Node<T>(data, head);
        head = node;
    }

}