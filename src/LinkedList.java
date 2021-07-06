public class LinkedList<T> {
    Node<T> head;
    int size;

    public LinkedList() {
        this.head = null;
        this.size = 0;
    }

    //Add new node to the front of the list for fastest operation as data order is not important
    public void add(T data) {
        Node<T> node = new Node<T>(data);
        if(head == null) {
            head = node;
            return;
        }

        node.next = head;
        head = node;
        size++;
    }

}