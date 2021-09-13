/**
 * NetAnalysis implementation using graphs
 * Chase Lane
 */
package cs1501_p4;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.io.File;

class Path{
	private double length;
	private int VIA;
	private boolean visited;

	public Path(double length, int VIA, boolean visited){
		this.length = length;
		this.VIA = VIA;
		this.visited = visited;
	}
	public double getLength(){
		return this.length;
	}
	public void setLength(double length){
		this.length = length;
	}
	public boolean getVisited(){
		return this.visited;
	}
	public void setVisited(boolean visited){
		this.visited = visited;
	}
	public int getVIA(){
		return this.VIA;
	}
	public void setVIA(int VIA){
		this.VIA = VIA;
	}
}

public class NetAnalysis implements NetAnalysis_Inter{

	public WeightedGraph networkGraph;
	private PriorityQueue Dijkstras;
	private Path[] Paths;
	private boolean done;
	private PriorityQueue MST;
	private int verticesCheck;

    //constructor of NetAnalysis accepts a string that is the location of the input file
    //input file takes the following format
    //first line is the # of vertices in the graph
    /*each proceeding line follows this format
    endpoint endpoint typeOfCable bandwithOfCable LengthofCable
    */
    public NetAnalysis(String inputFile){
        //populate the graph using a scanner
        try(Scanner s = new Scanner(new File(inputFile))){
            boolean firstLine = true;
            while(s.hasNext()){
                String temp = s.nextLine();
                //if we are reading the first line, we must determine the # of vertices
                if(firstLine == true){
					//initialize our graph of size # of vertices
					networkGraph = new WeightedGraph(Integer.parseInt(temp));
					firstLine = false;
                }
				//populate our graph with remaining edges
                else{
                    String arrInput[] = temp.split(" ");
                    int firstV = Integer.parseInt(arrInput[0]);
                    int secondV = Integer.parseInt(arrInput[1]);
                    String cableType = arrInput[2];
                    int bandwith = Integer.parseInt(arrInput[3]);
                    int length = Integer.parseInt(arrInput[4]);

					networkGraph.addEdge(firstV, secondV, cableType, bandwith, length);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Network file not found");
            e.printStackTrace();
        }
    }

    /**
	 * Find the lowest latency path from vertex `u` to vertex `w` in the graph
	 *
	 * @param	u Starting vertex
	 * @param	w Destination vertex
	 *
	 * @return	ArrayList<Integer> A list of the vertex id's representing the
	 * 			path (should start with `u` and end with `w`)
	 * 			Return `null` if no path exists
	 */
	public ArrayList<Integer> lowestLatencyPath(int u, int w){
		this.done = false;
		ArrayList<Integer> lowestLatencyPath = new ArrayList<Integer>();

		//initialize PQ for Dijkstras
		this.Dijkstras = new PriorityQueue(networkGraph);

		//Paths array will keep track of if we visited a vertex, the length it took to get to said vertex, and how we got to vertex
		//Initialize the paths array with all empty paths which have length 0, via -1(null), and visited false
		this.Paths = new Path[networkGraph.V()];
		for(int i = 0; i < networkGraph.V(); i++){
			Path emptypath = new Path(0, -1, false);
			this.Paths[i] = emptypath;
		}

		//call helper recursive method
		lowestLatencyPath(u, w, u, lowestLatencyPath);
		if(lowestLatencyPath.size() == 0) return null;
		//reverse the Arraylist b/c implementation adds recursing down the stack
		ArrayList<Integer> lowestLatencyPathRev = new ArrayList<Integer>();
		for(int i = 0; i < lowestLatencyPath.size(); i++){
			lowestLatencyPathRev.add(i, lowestLatencyPath.get(lowestLatencyPath.size() - i - 1));
		}
		return lowestLatencyPathRev;
	}

	/**
	 * Helper method to recursively find the lowestLatencyPath (minimum weightedpath)
	 * This is accomplished by checking all unvisited neighbors and calculating the lowest tentative path
	 * @param u Starting Vertex
	 * @param w Finishing Vertex
	 * @param curr Current Vertex we are on
	 * @param lowestLatencyPath ArrayList to append once the path is found
	 */
	private void lowestLatencyPath(int u, int w, int curr, ArrayList<Integer> lowestLatencyPath){
		if(this.done) return;
		//check if we are at the end, and then iterate through the path
		if(w == curr){
			while(curr != u){
				lowestLatencyPath.add(curr);
				curr = this.Paths[curr].getVIA();
			}
			lowestLatencyPath.add(curr);
			this.done = true;
			return;
		}

		//first step is add all the adjacent edges in the PQ
		for(DirectedEdge e : networkGraph.edgesOfVertex(curr)){
			//make sure we havent already visited the vertex before adding
			if(!(this.Paths[e.to()].getVisited())){
				this.Dijkstras.add(e);
			}
		}

		//mark that we have visited the curr vertex to avoid cycles
		this.Paths[curr].setVisited(true);

		//while there are elements in PQ, keep popping until we find the next min path
		while(!this.Dijkstras.isEmpty()){
			//next step is to retrieve the minimum from the PQ
			DirectedEdge min = this.Dijkstras.getMin();
			this.Dijkstras.removeMin();

			//calculate the tentativePath
			//this is calculated based on how we got to the current vertex + the length to the next vertex
			double tentativePath = this.Paths[min.from()].getLength() + min.getTimeToTravel();

			//first we need to see if havent reached this path yet
			//this is accomplished by check the VIA
			if(this.Paths[min.to()].getVIA() < 0){
				this.Paths[min.to()].setLength(tentativePath);
				this.Paths[min.to()].setVIA(min.from());
			}

			//else check if the current path is < the current one in Paths[]
			if(tentativePath < this.Paths[min.to()].getLength()){
				this.Paths[min.to()].setLength(tentativePath);
				this.Paths[min.to()].setVIA(min.from());
			}

			//go to the minimum distance and continue the recursion
			curr = min.to();
			lowestLatencyPath(u, w, curr, lowestLatencyPath);
		}
		return;
	}

	/**
	 * Find the bandwidth available along a given path through the graph
	 * (the minimum bandwidth of any edge in the path). Should throw an
	 * `IllegalArgumentException` if the specified path is not valid for
	 * the graph.
	 *
	 * @param	ArrayList<Integer> A list of the vertex id's representing the
	 * 			path
	 *
	 * @return	int The bandwidth available along the specified path
	 */
	public int bandwidthAlongPath(ArrayList<Integer> p) throws IllegalArgumentException{
		//first check if we are given an arraylist with only 1 element
		if(p.size() == 1) throw new IllegalArgumentException("bandwithAlongPath Arraylist contains only 1 vertex");
		int sum = 0;

		//iterate through the arraylist calculating the bandwith along each edge
		for (int i = 1; i < p.size(); i++) {
			//check and make sure if the edge exists, and if it does, add its bandwith to sum
			if(networkGraph.getEdge(p.get(i-1), p.get(i)) == null) throw new IllegalArgumentException("edge does not exist in graph" );
			int bandwith = networkGraph.getEdge(p.get(i-1), p.get(i)).getBandwith();
			sum = sum + bandwith;
		}
		return sum;
	}

	/**
	 * Return `true` if the graph is connected considering only copper links
	 * `false` otherwise
	 * Connected means a path exists between all vertex pairs
	 * Copper connected means there is a path that exists between all vertex pairs that is copper made
	 * @return	boolean Whether the graph is copper-only connected
	 */
	public boolean copperOnlyConnected(){
		//create an array to keep track if we visited the vertex after taking into account every possible edge
		boolean[] visited = new boolean[networkGraph.V()];
		//initialize array to have all false values
		for(int i = 0; i < networkGraph.V(); i++){
			visited[i] = false;
		}
		//call depth first search on the array
		DFS(0, visited, true);
		
		//if there are any vertices that are not visited in either direction, we have a disconnected graph
		for(int i = 0; i < networkGraph.V(); i++){
			if(!visited[i]){
				return false;
			}
		}
		return true;
	}

	/**
	 * Helper method which accomplishes depth first search for copper cables only
	 * DFS Dives as deep as possible and branches as it recurses back down the stack
	 * @param curr Current Vertex we are on
	 * @param visited Array which contains which vertices have been visited (true/false)
	 * @param copper whether we are using the DFS for copperonlyconnected or connectedtwovertfail
	 */
	private void DFS(int curr, boolean[] visited, boolean copper){
		//if we are using the DFS for copper only connected
		if(copper == true){
			//mark the vertex we are on as true
			visited[curr] = true;
			for(DirectedEdge e : networkGraph.edgesOfVertex(curr)){
				//only consider copper cables
				if(e.getCableType().equals("copper")){
					//if we havent visited this vertex yet, visit it
					if(!visited[e.to()]){
						DFS(e.to(), visited, true);
					}
				}	
			}
		}
		//if we are using the DFS for articulation points
		else{
			//mark the vertex we are on as true
			visited[curr] = true;
			for(DirectedEdge e : networkGraph.edgesOfVertex(curr)){
				//if we havent visited this vertex yet, visit it
				if(!visited[e.to()]){
					DFS(e.to(), visited, false);
				}
			}
		}

	}

	/**
	 * Return `true` if the graph would remain connected if any two vertices in
	 * the graph would fail, `false` otherwise
	 *
	 * @return	boolean Whether the graph would remain connected for any two
	 * 			failed vertices
	 */
	public boolean connectedTwoVertFail(){
		for(int i = 0; i < networkGraph.V(); i++){
			//if we find an articulation point return false
			if(articulationPoint(i)){
				return false;
			}
		}
		return true;
	}

	/**
	 * Algorithmn that uses DFS to find articulation points by looking at subsets of the graph by omitting 2 vertices at a time
	 * If any graph subset fails the DFS connected test, we fail on two vertex fail
	 * @param omit The vertex we are omitting
	 * @return true if the graph fails on 2 vertex omissions
	 */
	private boolean articulationPoint(int omit){
		//call depth first omitting a second different vertex each time
		for(int i = 0; i < networkGraph.V(); i++){
			//make sure we arent omitting the same vertex
			if(omit != i){
				//create an array to keep track if we visited the vertex after taking into account every possible edge
				boolean[] visited = new boolean[networkGraph.V()];
				//initialize array to have all false values
				for(int j = 0; j < networkGraph.V(); j++){
					visited[j] = false;
				}
				//update the values for omit and i (the two vertices that we are ignoring) as true so DFS excludes them
				visited[omit] = true;
				visited[i] = true;
				DFS(0, visited, false);
				//if there are any vertices that are not visited in either direction, we have a disconnected graph
				for(int k = 0; k < networkGraph.V(); k++){
					if(!visited[k]){
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Find the lowest average (mean) latency spanning tree for the graph
	 * (i.e., a spanning tree with the lowest average latency per edge). Return
	 * it as an ArrayList of STE edges.
	 *
	 * Note that you do not need to use the STE class to represent your graph
	 * internally, you only need to use it to construct return values for this
	 * method.
	 *
	 * @return	ArrayList<STE> A list of STE objects representing the lowest
	 * 			average latency spanning tree
	 * 			Return `null` if the graph is not connected
	 */
	public ArrayList<STE> lowestAvgLatST(){
		//create arraylist of STE
		ArrayList<STE> lowestAvgLatST = new ArrayList<STE>();
		//initialize PQ to use for Prims algorithm
		this.MST = new PriorityQueue(networkGraph);
		//initialize Paths data structure to keep track of to and from vertices and visited
		this.Paths = new Path[networkGraph.V()];
		for(int i = 0; i < networkGraph.V(); i++){
			Path emptypath = new Path(0, -1, false);
			this.Paths[i] = emptypath;
		}
		int curr = 0;
		verticesCheck = 0;
		lowestAvgLatST(curr);

		//now paths should be filled of all to and from paths
		for(int i = 0; i < this.Paths.length; i++){
			if(this.Paths[i].getVIA() >= 0){
				STE temp = new STE(i, this.Paths[i].getVIA());
				lowestAvgLatST.add(temp);
			}
		}
		return lowestAvgLatST;
	}

	/**
	 * Helper method to find the Minimum spanning tree of latency edges
	 * Utilizes PQ prim's algorithm which adds each adjacent edge to the PQ of the current vertex and recursively calls until all V are visited
	 * @param curr The current vertex we are on
	 */
	public void lowestAvgLatST(int curr){
		//check to see if we have visited all vertices first
		if(verticesCheck == networkGraph.V()) return;

		//mark the vertex as visited
		this.Paths[curr].setVisited(true);
		//increment the count of vertices we've visited thus far
		this.verticesCheck++;

		//add all adjacent edges to PQ
		for(DirectedEdge e : networkGraph.edgesOfVertex(curr)){
			this.MST.add(e);
		}

		//while edges are present in the PQ
		while(!this.MST.isEmpty()){
			//retrieve the next min from PQ
			DirectedEdge nextMin = this.MST.getMin();
			this.MST.removeMin();

			//make sure we havent visited there yet
			if(this.Paths[nextMin.to()].getVisited() == false){
				//update Paths data structure with VIA
				this.Paths[nextMin.to()].setVIA(nextMin.from());
				//recurse to the next vertex
				curr = nextMin.to();
				lowestAvgLatST(curr);
			}  
		}
	}	

	//test print to make sure graph is read properly into file
	public ArrayList<String> testPrintGraph(){
		ArrayList<String> testPrint = new ArrayList<String>();
		for(int i = 0; i < networkGraph.V(); i++){
			for(DirectedEdge e : networkGraph.edgesOfVertex(i)){
				String temp = e.from() + ":" + e.to() + ":" + e.getCableType();//":" + e.getBandwith() + ":" + e.getLength() + ":" + e.getTimeToTravel();
				testPrint.add(temp);
			}
		}
		return testPrint;
	}
	//test method to make sure PQ is working after loading in every edge in graph
	public String testPrintPriorityQ(){
		PriorityQueue testPQ = new PriorityQueue(networkGraph);

		for(int i = 0; i < networkGraph.V(); i++){
			for(DirectedEdge e : networkGraph.edgesOfVertex(i)){
				testPQ.add(e);
			}
		}
		return testPQ.testprintPQ();
	}
}
