/**
 * Implementation of a Priority Queue to streamline Graph algorithms
 * Chase Lane
 */
package cs1501_p4;

public class PriorityQueue {
    //create the PQs
    //size is the total amount of edges in graph
	private DirectedEdge EdgePQ[];
	private int count;
    //constructor accepts size of edges and list of edges
    public PriorityQueue(WeightedGraph networkGraph){
		count = 0;
		//PQ can have max size of total Edges
		EdgePQ = new DirectedEdge[networkGraph.E()];
    }

	//add edge to PQ
	public void add(DirectedEdge e) throws IllegalStateException{
		if(e == null) throw new IllegalArgumentException("Edge to add to PQ is null");
		EdgePQ[count] = e;
		swim(EdgePQ, count);
		count++;
	}

	//retrieve the min of the PQ
	public DirectedEdge getMin(){
		return EdgePQ[0];
	}

	//remove the min of the PQ
	public void removeMin(){
		//swap the min that we want deleted with the last element in the PQ
		exchange(EdgePQ, 0, (count-1));
		//remove this item from the PQ and decrement the count
		EdgePQ[count-1] = null;
		count--;
		//perform the sink operations
		sink(EdgePQ, 0);
	}

	public boolean isEmpty(){
		if(count == 0) return true;
		return false;
	}
	
	//swap the positions of parent and k in the PQ
	private void exchange(DirectedEdge[] inputPQ, int parent, int k){
		//perform the swap in the PQ
		DirectedEdge temp = inputPQ[parent];
		inputPQ[parent] = inputPQ[k];
		inputPQ[k] = temp;
	}

	private void swim(DirectedEdge[] inputPQ, int k){
		//the parent in the PQ is given by the following equation
		int parent = (int)Math.floor((k-1)/2);
		while(k > 0 && (inputPQ[parent].getTimeToTravel() > (inputPQ[k].getTimeToTravel()))){
			//since the parent is > current k we must perform a swap
			exchange(inputPQ, parent, k);
			k = parent;
			parent = (int)Math.floor((k-1)/2);
		}
	}

	private void sink(DirectedEdge[] inputPQ, int k){
		while((2 * k) + 1 <= (count-1)){
			int j = (2*k) + 1;
			//now we must determine which of the children of the current car has a smaller price
			//left child is given by 2k + 1, and right child is given by 2k+2
			//so if left child is greater than right, we will increment j
			if(j < (count-1) && (inputPQ[j].getTimeToTravel() > (inputPQ[j+1]).getTimeToTravel())) j++;
			//if k is not greater than its smallest child - j - we break
			if(!(inputPQ[k].getTimeToTravel() > inputPQ[j].getTimeToTravel())) break;
			//otherwise we must swap k and j
			exchange(inputPQ, k, j);
			k = j;
		}
	}

	public String testprintPQ(){
		String temp = "";
       
        return testprintPQ(temp, this.EdgePQ);
	}
	private String testprintPQ(String temp, DirectedEdge[] inputPQ){
		if(inputPQ == null) return "no";

		for(int i = 0; i < count; i++){
			temp = temp + " " + inputPQ[i].from() + ":" + inputPQ[i].to() + ":" + inputPQ[i].getTimeToTravel();
		}
		return temp;
	}
}
