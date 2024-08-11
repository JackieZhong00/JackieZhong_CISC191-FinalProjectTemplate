package edu.sdccd.cisc191.template;


import java.util.Iterator;

/**
 * doubly linked list that's used to store newly added questions
 */
public class QuestionsLinkedList implements Iterable<Question> {
    Node head;
    Node tail;

    /**
     * represents one node (one element) in the linked list
     */
    public class Node {
        Node next;
        Node prev;
        Question question;

        /**
         * constructor of linked list node
         * @param question represents data of the node
         */
        public Node(Question question) {
            this.question = question;
            this.next = null;
            this.prev = null;
        }

        public Question getData(){
            return question;
        }
        public void setData(Question question){
            this.question = question;
        }
        public Node getNext(){
            return next;
        }
        public void setNext(Node next){
            this.next = next;
        }
        public Node getPrev(){
            return prev;
        }
        public void setPrev(Node prev){
            this.prev = prev;
        }
    }

    /**
     * used to iterate over linked list for convenience and ease of traversal
     * @param <Question> iterates over linked list and returns a Question object
     */
    class QuestionsIterator<Question> implements Iterator<Question> {
        Node current;

        // initialize pointer to head of the list for iteration
        public QuestionsIterator(QuestionsLinkedList list)
        {
            current = list.getHead();
        }

        // returns false if next element does not exist
        public boolean hasNext()
        {
            return current != null;
        }

        // return current data and update pointer
        public Question next()
        {
            Question data = (Question) current.getData();
            current = current.getNext();
            return data;
        }
    }

    //linked list constructor, where head dummy node points to tail dummy node
    public QuestionsLinkedList() {
        this.head = new Node(null);
        this.tail = new Node(null);
        this.head.next = this.tail;
        this.head.prev = null;
        this.tail.next = null;
        this.tail.prev = this.head;
    }

    /**
     * @return dummy head node
     */
    public Node getHead() {
        return head;
    }

    /**
     * creates iterator object with current list instance passed in
     * @return iterator object that you can use to traverse list
     */
    public Iterator<Question> iterator()
    {
        return new QuestionsIterator<Question>(this);
    }

    /**
     * @param question to prepend to list
     */
    public void add(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }
        Node newNode = new Node(question);
        newNode.next = this.head.next;
        newNode.next.prev = newNode;
        newNode.prev = this.head;
        this.head.next = newNode;
    }

    /**
     * @param question to append to list
     */
    public void append(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }
        Node newNode = new Node(question);
        newNode.next = this.tail;
        newNode.prev = this.tail.prev;
        newNode.prev.next = newNode;
    }

    /**
     * @param idx index to remove at - list uses 0th indexing
     * @return question that is removed
     */
    public Question remove(int idx) {
        Node curr = this.head;
        Question data = null;
        while(idx > -1) {
            curr = curr.next;
            idx--;
        }
        data = curr.getData();
        curr.prev.next = curr.next;
        curr.next.prev = curr.prev;

        return data;
    }

    /**
     * @param question to get index position of
     * @return integer representing the index of question in list
     */
    public int getIndexOfQuestion(Question question) {
        Node curr = this.head;
        int idx = 0;
        while(curr != this.tail) {
            curr = curr.next;
            if (curr.getData() == question){
                return idx;
            }
            idx++;
        }
        return -1;
    }

    /**
     * empty list of its elements
     */
    public void clear() {
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    /**
     * @return boolean representing if the list is empty or not
     */
    public boolean isEmpty(){
        if (this.head.next != this.tail){
            return false;
        }
        return true;
    }



}


