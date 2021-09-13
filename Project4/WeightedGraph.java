/*
    Implementation of a Edge Weighted Graph using ArrayList
    This implementation is undirected
    Chase Lane
*/

package cs1501_p4;
import java.util.Iterator;
import java.util.NoSuchElementException;

//each edge will store all the information passed in by the text input file
class DirectedEdge{
    //starting vertex
    private int v;
    //ending vertex
    private int w;
    //cable type
    private String cableType;
    //bandwith allocation
    private int bandwith;
    //length of wire
    private int length;
    //length of wire / speed of data can be sent along a path
    private double timeToTravel;
    
    //default constructor for Edge
    public DirectedEdge(int v, int w, String cableType, int bandwith, int length, double timeToTravel){
        if(v < 0 || w < 0){
            throw new IllegalArgumentException("Vertex inputs must be non negative");
        }
        this.v = v;
        this.w = w;
        this.cableType = cableType;
        this.bandwith = bandwith;
        this.length = length;
        this.timeToTravel = timeToTravel;
    }
    public int from(){
        return this.v;
    }
    public int to(){
        return this.w;
    }
    public String getCableType(){
        return this.cableType;
    }
    public int getBandwith(){
        return this.bandwith;
    }
    public int getLength(){
        return this.length;
    }
    public double getTimeToTravel(){
        return this.timeToTravel;
    }
}

class Bag<T> implements Iterable<T>{
    //first node in linked list
    private Node<T> first;
    //how many items are present in the linked list
    private int n;

    //helper node class for linked list
    private static class Node<T>{
        private T item;
        private Node<T> next;
    }

    //empty constructor initializes empty bag
    public Bag(){
        this.first = null;
        this.n = 0;
    }

    //returns true if the bag is empty
    public boolean isEmpty(){
        return first == null;
    }

    //returns the size of the bag
    public int size(){
        return this.n;
    }
    //adds a new node to the bag linked list
    public void add(T item){
        Node<T> oldFirst = first;
        first = new Node<T>();
        first.item = item;
        first.next = oldFirst;
        n++;
    }

    //to iterate over all items in the linked list
    public Iterator<T> iterator(){
        return new BagIterator(first);
    }

    private class BagIterator implements Iterator<T>{
        private Node<T> current;

        public BagIterator(Node<T> first) {
            current = first;
        }
        public boolean hasNext()  { return current != null;                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            T item = current.item;
            current = current.next; 
            return item;
        }
    }
}

public class WeightedGraph{
    final int copperSpeed = 230000000;
    final int fiberOpticSpeed = 200000000;

    //total number of vertices in the graph
    private int v;
    //total number of edges in the graph
    private int e;
    //adjacency list is an array of bags that store edges
    private Bag<DirectedEdge>[] adj;

    /**
     * Default constructor for the weighted graph which initializes an empty graph representation of size numberOfVertices
     * @param numberOfVertices the total number of unique vertices 
     */
    public WeightedGraph(int numberOfVertices){
        if(numberOfVertices < 0){
            throw new IllegalArgumentException("Number of vertices to initialize graph must be non-negative");
        }
        this.v = numberOfVertices;
        this.e = 0;

        //initialize the adjacency list to be an array of empty bags
        adj = (Bag<DirectedEdge>[]) new Bag[this.v];
        for(int i = 0; i < numberOfVertices; i++){
            adj[i] = new Bag<DirectedEdge>();
        }
    }

    //return the # of vertices in the graph
    public int V(){
        return this.v;
    }

    //return the number of edges in the graph
    public int E(){
        return this.e;
    }
    
    //adds an edge to our graph by reading a line from the input file
    public void addEdge(int firstV, int secondV, String cableType, int bandwith, int length){
        double travelTime;
        if(cableType.equals("copper")) travelTime = (double)length / (double)copperSpeed;
        else if(cableType.equals("optical")) travelTime = (double)length / (double)fiberOpticSpeed;
        else throw new IllegalArgumentException("Cable Type must be copper or optical only");

        this.e+=2;
        DirectedEdge newEdge = new DirectedEdge(firstV, secondV, cableType, bandwith, length, travelTime);
        DirectedEdge otherEdge = new DirectedEdge(secondV, firstV, cableType, bandwith, length, travelTime);
        //have to consider network could travel either way in an undirected graph
        adj[firstV].add(newEdge);
        adj[secondV].add(otherEdge);
    }

    //return all edges coming out of a specific vertex
    public Iterable<DirectedEdge> edgesOfVertex(int v){
        return adj[v];
    }

    //return the degree of a specific vertex
    public int degree(int v){
        return adj[v].size();
    }

    /**
     * @param v Starting vertex
     * @param w Ending vertex
     * @return Edge with starting vertex v and ending vertex w, null if no such edge exists
     */
    public DirectedEdge getEdge(int v, int w){
        for(DirectedEdge e : adj[v]){
            if(e.to() == w) return e;
        }
        return null;
    }

}
