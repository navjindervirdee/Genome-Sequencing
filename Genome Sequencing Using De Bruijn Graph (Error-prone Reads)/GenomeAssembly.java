//Program to Assemble Phi-X174 genome from Error-Prone reads.

import java.util.*;
import java.io.*;
public class GenomeAssembly{

	//class for reading the input reads of the genome.
	static class FastReader{
		BufferedReader br;
		StringTokenizer st;
		
		public FastReader(){
			br = new BufferedReader(new InputStreamReader(System.in));
		}
		
		String nextLine(){
			String temp = "";
			try{
				temp = br.readLine();
			}
			catch(IOException e){
			}
			return temp;
		}
	}
	
	//class for a vertex of the de-bruijn graph.
	static class Vertex{
		int vertexNum;				//id of the vertex.
		String str;				//string of length k-1 of k-mer formed from the reads.
		List<Integer> outedges;			//list of outgoing adjacent vertices.
		List<Integer> inedges;			//list of incoming adjacent vertices.
		List<Integer> edgeList;			//
		
		boolean removed;			//to check whether this vertex is removed in tipRemoival algorithm.
		boolean found; 				//to check whether this is vertex is removed in bubble removal algorithm.
		
		Node temp; 				//it points to the node where this vertex is in tree in bubble removal algorithm. 
		
		boolean visited;			//
		
		
		public Vertex(int vertexNum, String str, List<Integer> outedges, List<Integer> inedges){
			this.vertexNum = vertexNum;
			this.outedges= outedges;
			this.inedges = inedges;
			this.removed = false;
			this.str = str;
			this.found = false;	
			this.edgeList = new ArrayList<Integer>();
			this.temp=null;
			this.visited = false;
		}
	}
	
	
	//tree used in bubble removal algorithm.
	static class Node{
		int vertexNum;				//id of the vertex in the graph.
		List<Node> kids;			//list of children of this node.
		Node parent;				//parent of this node.
		
		public Node(int vertexNum){
			this.vertexNum = vertexNum;
			this.kids = new ArrayList<Node>();
			this.parent = null;
		}
	}
	
	static Node root;				//root	of the tree.
	
	
	//class for edge in the de-bruijn graph used in finding the eulerian circuit.
	static class Edge{
		int from;				//starting point of edge.
		int to;					//ending point of the edge.
		boolean used;				//to check whether this edge is used in eulerian circuit algorithm.
		
		public Edge(int from,int to){
			this.from = from;
			this.to = to;
			this.used = false;
		}
	}
	
	
	//function to read the input reads of the genome.
	public static String [] reader(){
		FastReader fr = new FastReader();
		String [] reads = new String[1618];	//array to store the reads of the genome.
	
		for(int i=0;i<reads.length;i++){
			String temp = fr.nextLine();
			reads[i] = temp.trim();
		}
		return reads;
	}
	
	static HashMap<String,Integer> countmap;	//map to count the number of occurences of a k-mer. Helps in deleting the bubbles in the graph.
	static int k=20;				//size of k-mer i.e all reads will be broken into size 20 to create the de-bruijn graph.
	
	
	//function to create the de-bruijn graph from the reads.
	public static Vertex [] createDeBruijnGraph(String [] reads){
		HashMap<String, Integer> idmap = new HashMap<String, Integer>();				//map to give each string of length k-1 unique id.

		HashMap<String, ArrayList<Integer>> outedgesmap = new HashMap<String, ArrayList<Integer>>();	//map to store outgoing edges.
		HashMap<String, ArrayList<Integer>> inedgesmap = new HashMap<String, ArrayList<Integer>>();	//map to store incoming edges.
		
		countmap = new HashMap<String,Integer>();
		
		HashSet<String> uniquekmers = new HashSet<String>();						//to not repeat already seen k-mer.
		
		int id=0;											//initialize id to 0.
		
		for(int i=0;i<reads.length;i++){
			String read = reads[i];

			//all sub-strings of length 20=k of the read.
			for(int j=0;j<=read.length()-k;j++){
				String temp = read.substring(j,j+k);					
				
				//break the k-mer into parts length(0-k-1) and length(1-k)
				String a = temp.substring(0,temp.length()-1);				
				String b = temp.substring(1);
				
				//if already seen k-mer continue.
				if(uniquekmers.contains(temp)){
					countmap.put(temp,countmap.get(temp)+1);				//increase the count of k-mer by one.
					continue;
				}
				
				uniquekmers.add(temp);
				countmap.put(temp, new Integer(1));
				
				if(!idmap.containsKey(a)){
					idmap.put(a,id);
					outedgesmap.put(a,new ArrayList<Integer>());
					inedgesmap.put(a,new ArrayList<Integer>());
					id++;
				}
					
				if(!idmap.containsKey(b)){
					idmap.put(b,id);
					outedgesmap.put(b,new ArrayList<Integer>());
					inedgesmap.put(b,new ArrayList<Integer>());
					id++;
				}
				
				boolean overlap = isOverlap(a,b);
				
				if(overlap){
					outedgesmap.get(a).add(idmap.get(b));
					inedgesmap.get(b).add(idmap.get(a));
				}
			}
		}
		
		//create the de-bruijn graph.
		Vertex [] graph = new Vertex[idmap.size()];

		for(String key : idmap.keySet()){
			int temp = idmap.get(key);
			graph[temp] = new Vertex(temp,key,outedgesmap.get(key),inedgesmap.get(key));
		}

		return graph;	
	}
	
	
		
	//function to check the overlap between two strings.
	private static boolean isOverlap(String a, String b){
		int j=0;
		for(int i=1;i<a.length();i++){
			if(a.charAt(i)!=b.charAt(j)){
				return false;
			}
			j++;
		}
		return true;
	}

	static int count;											//to count the number of tips removed.

	//function to remove the tips from the graph.
	public static void tipRemoval(Vertex [] graph){
		count=0;
		for(int i=0;i<graph.length;i++){
			//if vertex already removed continue.
			if(graph[i].removed){
				continue;
			}
			
			//if outedges are 0 then explore inwards and remove vertices on the ingoing path.
			if(graph[i].outedges.size()==0){
				inExplore(graph,i);
				continue;
			}
			
			//if edges are 0 then explore outwards and remove vertices on the outgoing path.
			if(graph[i].inedges.size()==0){
				outExplore(graph,i);
				continue;
			}
		}
	}
	

	//function to explore graph inwards from the given vertex.
	private static void inExplore(Vertex [] graph, int vertex){
		if(graph[vertex].outedges.size()!=0 || graph[vertex].inedges.size()!=1){
			return;
		}
		
		graph[vertex].removed = true;
		count++;

		//get the previous vertex from the list of inedges of the vertex.		
		int temp = graph[vertex].inedges.get(0);
		graph[temp].outedges.remove(new Integer(vertex));
		graph[vertex].inedges.remove(new Integer(temp));

		inExplore(graph,temp);
	}
	
	
	//function to explore the graph outwards from the given vertex.
	private static void outExplore(Vertex [] graph, int vertex){
		if(graph[vertex].inedges.size()!=0 || graph[vertex].outedges.size()!=1){
			return;
		}
		
		graph[vertex].removed = true;
		count++;
		
		//get the next vertex from the list of outedges of the vertex.
		int temp = graph[vertex].outedges.get(0);
		graph[temp].inedges.remove(new Integer(vertex));
		graph[vertex].outedges.remove(new Integer(temp));

		outExplore(graph,temp);
	}

	
	//function to handle the bubbles in the graph.
	public static void bubbleHandler(Vertex [] graph){
		for(int i=0;i<graph.length;i++){
			if(graph[i].removed || graph[i].outedges.size()<2){
				continue;
			}
			bfs(graph,graph[i].vertexNum);
		}
	}
	
	static int bubbles = 0;											//count the bubbles in the graph.

	//this function is not breadth first search. It just creates a new tree and reinitialize the graphs found and temp variables.
	private static void bfs(Vertex [] graph,int vertex){
		HashSet<Integer> set = new HashSet<Integer>();							//set to keep track of already visited vertices along the path.
		root = new Node(vertex);									//root is set to the given vertex.
		Node temp = root;		
		
		//re-initialize the graph.
		for(int i=0;i<graph.length;i++){
			graph[i].found = false;
			graph[i].temp = null;
		}
		
		//explore as in depth first search (DFS).
		explore(graph,temp,set);
	}
	
	
	//function to explore the graph as done in DFS algorithm.
	private static void explore(Vertex [] graph, Node node, HashSet<Integer> set){
		set.add(node.vertexNum);									//add the node into the set.

		//if vertex's found variable is already true then bubble detected.
		if(graph[node.vertexNum].found){
			bubbles++;
			Node common = findCommonAncestor(graph,node);						//find the common ancestor from which two paths originate and reach this found node.
			bubbleSolver(graph,node,common,set);							//remove one of the two paths.
			if(!set.contains(node.vertexNum)){
				return;
			}
		}
		
		//otherwise set found variable of this vertex to true.
		graph[node.vertexNum].found = true;
		graph[node.vertexNum].temp = node;
		
		
		//stop exploring if path length is greater than k+1.
		if(set.size()>=k+1){
			set.remove(node.vertexNum);
			return;
		}
		
		//get the outgoing edges of the given vertex.
		List<Integer> list = new ArrayList<Integer>(graph[node.vertexNum].outedges);
		for(int i=0;i<list.size();i++){
			int temp = list.get(i);
			
			//if already present in the path then continue.
			if(set.contains(temp)){
				continue;
			}
	
			//create child
			Node child  = new Node(temp);
			child.parent = node;
			node.kids.add(child);
			
			explore(graph,child,set);								//explore from this child.

			if(!set.contains(node.vertexNum)){
				return;
			}
		}
		set.remove(node.vertexNum);
	}
	
	
	//helper function to to see that algorithm is working correct.
	private static void printTree(Vertex [] graph,Node temp){
		System.out.println(graph[temp.vertexNum].str);
		for(int i=0;i<temp.kids.size();i++){
			printTree(graph,temp.kids.get(i));
		}
	}
	
	
	//function to find the common ancestor of the given node.
	private static Node findCommonAncestor(Vertex [] graph,Node node){
		Node temp = graph[node.vertexNum].temp;
		List<Node> list = new ArrayList<Node>();
		while(temp!=null){
			list.add(temp);
			temp = temp.parent;
		}
		
		temp = node;
		while(temp!=null){
			for(int i=list.size()-1;i>-1;i--){
				if(temp==list.get(i)){	
					return temp;
				}
			}
			temp =temp.parent;
		}
		return null;
	}
	
	
	//function to remove one of the two paths that forms the bubble.
	private static void bubbleSolver(Vertex [] graph, Node node, Node common, HashSet<Integer> set){
		Node node1 = node;
		Node node2 = graph[node.vertexNum].temp;
		
		double sum = 0;
		double count = 0;
		
		//traverse the path from node1 to common node. Calculate the coverage.
		while(node1!=common){
			String str1 = graph[node1.vertexNum].str;
			String str2 = graph[node1.parent.vertexNum].str;
			String str = str2 + str1.charAt(str1.length()-1);					//str is the k-mer formed
			sum = sum + countmap.get(str);								//sum the counts of all k-mers along the path using countmap.
			count++;										//count the number of edges along the path.
			node1=node1.parent;
		}
		
		double coverage1 = sum/count;									//coverage = sum/count.
		double count1 = count;
		
		
		//repeat the same for the other path to common node.
		sum=0;
		count=0;
		while(node2!=common){
			String str1 = graph[node2.vertexNum].str;
			String str2 = graph[node2.parent.vertexNum].str;
			String str = str2 + str1.charAt(str1.length()-1);
			sum = sum + countmap.get(str);
			count++;
			node2 = node2.parent;
		}
		
		double coverage2 = sum/count;							

		node1 = node;
		node2 = graph[node.vertexNum].temp;
		
		
		//remove the path whose coverage is less.
		if(coverage1<=coverage2){
			List<Integer> verticesRemoved = new ArrayList<Integer>();				//list to store the removed vertices.
			Node temp = removePath(graph,node1,common,verticesRemoved);				//remove the path with less coverage.
			makeFalse(graph,temp);									//to make false found variable and make temp variable null after the common node.
			graph[node.vertexNum].found = true;
			graph[node.vertexNum].temp = node2;
			
			removefromSet(set,verticesRemoved);							//remove the path from the set as well.
			common.kids.remove(temp);								//remove from common ancestor the buggy kid.
		}
		else{
			List<Integer> verticesRemoved = new ArrayList<Integer>();
			Node temp = removePath(graph,node2,common,verticesRemoved);
			makeFalse(graph,temp);
			graph[node.vertexNum].found = true;
			graph[node.vertexNum].temp = node1;
			
			common.kids.remove(temp);
		}
	}
	
	
	//function to remove the buggy path.
	private static Node removePath(Vertex [] graph,Node node, Node common, List<Integer> verticesRemoved){
		Node parent = node.parent;
		Node child = node;
		Node temp = null;
		while(child!=common){
			graph[parent.vertexNum].outedges.remove(new Integer(child.vertexNum));
			verticesRemoved.add(child.vertexNum);
			temp = child;
			child = parent;
			parent = parent.parent;
		}
		return temp;
	}
	
		
	//function to remove the vertices from the set as well using the removed vertices list.	
	private static void removefromSet(HashSet<Integer> set, List<Integer> verticesRemoved){
		for(int i=0;i<verticesRemoved.size();i++){
			set.remove(verticesRemoved.get(i));
		}
	}
		
	
	//function to make false the found variable and temp=null of all vertices after the common node.		
	private static void makeFalse(Vertex [] graph, Node node){
		graph[node.vertexNum].found = false;
		graph[node.vertexNum].temp = null;

		for(int i=0;i<node.kids.size();i++){
			makeFalse(graph,node.kids.get(i));
		}
	}
		
	
	//function to make edges from the cleaned graph.
	public static List<Edge> makeEdges(Vertex [] graph){
		List<Edge> edges = new ArrayList<Edge>();
		for(int i=0;i<graph.length;i++){
			if(graph[i].removed){
				continue;
			}
			List<Integer> list = graph[i].outedges;
			for(int j=0;j<list.size();j++){
				Edge edge = new Edge(i,list.get(j));
				graph[i].edgeList.add(edges.size());
				edges.add(edge);
			}
		}
		return edges;
	}
	
	
	//function to find the eulerian cycle in the graph.
	public static void findCycle(Vertex [] graph, List<Edge> edges){
		int max = -1;
		String result = "";
		Map<String,Integer> map = new HashMap<String,Integer>();

		for(int i=0;i<graph.length;i++){
			if(!graph[i].removed && graph[i].visited==false){
				List<Integer> cycle = new ArrayList<Integer>();
			
				eulerianExplore(graph,edges,i,cycle);							//function to explore the graph for finding the eulerian cycle.
				
				String genome = graph[cycle.get(cycle.size()-1)].str;

				//make the genome from the found cycle.
				for(int j=cycle.size()-2;j>-1;j--){
					String temp = graph[cycle.get(j)].str;
					genome = genome + temp.charAt(temp.length()-1);
				}
				
				if(genome.length()>=5396){
					map.put(genome,genome.length()-5396);
				}
				
				if(max<genome.length()){
					result = genome;
					max = genome.length();
				}
			}
		}

		int min = Integer.MAX_VALUE;
		for(String key : map.keySet()){
			if(min>map.get(key)){
				min = map.get(key);
				result = key;
			}
		}
		System.out.println(result.substring(14,5410));
	}
	
	
	//function to explore the graph for finding the eulerian cycle.
	private static void eulerianExplore(Vertex [] graph, List<Edge> edges,int vertex, List<Integer> cycle){
		List<Integer> list = graph[vertex].edgeList;
		graph[vertex].visited=true;
		for(int i=0;i<list.size();i++){
			Edge edge = edges.get(list.get(i));
			if(!edge.used){
				edge.used = true;
				eulerianExplore(graph,edges,edge.to,cycle);
			}	
		}
		cycle.add(vertex);
	}
	
	
	//function to run the program so that there is no StackOverflow problem.
	public static void run() throws IOException{
		String [] reads = reader();							//read the reads of the genome from the input.
		Vertex [] graph = createDeBruijnGraph(reads);					//create the de-bruijn graph.
		tipRemoval(graph);								//remove the tips from the graph.
		bubbleHandler(graph);								//remove the bubbles from the graph.
		tipRemoval(graph);								//again remove the tips from the graph.
		List<Edge> edges = makeEdges(graph);						//create edges from the cleaned graph.
		findCycle(graph,edges);								//find the eulerian cycle and print the genome.
	}


	//main function to run the program.	
	public static void main(String [] args) throws IOException{
		new Thread(null, new Runnable(){
			public void run(){
				try{
					new GenomeAssembly().run();
				}
				catch(Exception e){
				}
			}
		}, "1", 1<<26).start();
	}
}       	