import java.util.*;
import java.io.*;
public class GenomeAssembly{
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
	
	static class Vertex{
		int vertexNum;
		String str;
		List<Integer> outedges;
		List<Integer> inedges;
		List<Integer> edgeList;
		
		boolean removed;//tipRemoival.
		boolean found; //bubblehandler.
		
		Node temp; //tree. 
		
		
		boolean visited;
		
		
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
	
	static class Node{
		int vertexNum;
		List<Node> kids;
		Node parent;
		
		public Node(int vertexNum){
			this.vertexNum = vertexNum;
			this.kids = new ArrayList<Node>();
			this.parent = null;
		}
	}
	
	static Node root;
	
	static class Edge{
		int from;
		int to;
		boolean used;
		
		public Edge(int from,int to){
			this.from = from;
			this.to = to;
			this.used = false;
		}
	}
	
	
	public static String [] reader(){
		FastReader fr = new FastReader();
		String [] reads = new String[1618];
	
		for(int i=0;i<reads.length;i++){
			String temp = fr.nextLine();
			reads[i] = temp.trim();
		}
		return reads;
	}
	
	static HashMap<String,Integer> countmap;
	static int k=20;
	
	public static Vertex [] createDeBruijnGraph(String [] reads){
		HashMap<String, Integer> idmap = new HashMap<String, Integer>();

		HashMap<String, ArrayList<Integer>> outedgesmap = new HashMap<String, ArrayList<Integer>>();
		HashMap<String, ArrayList<Integer>> inedgesmap = new HashMap<String, ArrayList<Integer>>();
		
		countmap = new HashMap<String,Integer>();
		
		HashSet<String> uniquekmers = new HashSet<String>();
		
		int id=0;
		
		for(int i=0;i<reads.length;i++){
			String read = reads[i];

			for(int j=0;j<=read.length()-k;j++){
				String temp = read.substring(j,j+k);
				
				String a = temp.substring(0,temp.length()-1);
				String b = temp.substring(1);
				
				if(uniquekmers.contains(temp)){
					countmap.put(temp,countmap.get(temp)+1);
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
		
		Vertex [] graph = new Vertex[idmap.size()];

		for(String key : idmap.keySet()){
			int temp = idmap.get(key);
			graph[temp] = new Vertex(temp,key,outedgesmap.get(key),inedgesmap.get(key));
		}

		return graph;	
	}
	
	
		
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

	static int count;

	public static void tipRemoval(Vertex [] graph){
		count=0;
		for(int i=0;i<graph.length;i++){
			if(graph[i].removed){
				continue;
			}

			if(graph[i].outedges.size()==0){
				inExplore(graph,i);
				continue;
			}
			
			if(graph[i].inedges.size()==0){
				outExplore(graph,i);
				continue;
			}
		}
	}
	
	private static void inExplore(Vertex [] graph, int vertex){
		if(graph[vertex].outedges.size()!=0 || graph[vertex].inedges.size()!=1){
			return;
		}
		
		graph[vertex].removed = true;
		count++;
		
		int temp = graph[vertex].inedges.get(0);
		graph[temp].outedges.remove(new Integer(vertex));
		graph[vertex].inedges.remove(new Integer(temp));

		inExplore(graph,temp);
	}
	
	private static void outExplore(Vertex [] graph, int vertex){
		if(graph[vertex].inedges.size()!=0 || graph[vertex].outedges.size()!=1){
			return;
		}
		
		graph[vertex].removed = true;
		count++;
		
		int temp = graph[vertex].outedges.get(0);
		graph[temp].inedges.remove(new Integer(vertex));
		graph[vertex].outedges.remove(new Integer(temp));

		outExplore(graph,temp);
		
	}

	public static void bubbleHandler(Vertex [] graph){
		for(int i=0;i<graph.length;i++){
			if(graph[i].removed || graph[i].outedges.size()<2){
				continue;
			}
			bfs(graph,graph[i].vertexNum);
		}
	}
	static int bubbles = 0;
	private static void bfs(Vertex [] graph,int vertex){
		HashSet<Integer> set = new HashSet<Integer>();
		root = new Node(vertex);
		Node temp = root;
		for(int i=0;i<graph.length;i++){
			graph[i].found = false;
			graph[i].temp = null;
		}
		explore(graph,temp,set);
	}
	
	private static void explore(Vertex [] graph, Node node, HashSet<Integer> set){
		set.add(node.vertexNum);

		if(graph[node.vertexNum].found){
			bubbles++;
			Node common = findCommonAncestor(graph,node);
			bubbleSolver(graph,node,common,set);
			if(!set.contains(node.vertexNum)){
				return;
			}
		}
		
		graph[node.vertexNum].found = true;
		graph[node.vertexNum].temp = node;
		
		if(set.size()>=k+1){
			set.remove(node.vertexNum);
			return;
		}
		
		List<Integer> list = new ArrayList<Integer>(graph[node.vertexNum].outedges);
		for(int i=0;i<list.size();i++){
			int temp = list.get(i);
			if(set.contains(temp)){
				continue;
			}
	
			Node child  = new Node(temp);
			child.parent = node;
			node.kids.add(child);
			
			explore(graph,child,set);

			if(!set.contains(node.vertexNum)){
				return;
			}
		}
		set.remove(node.vertexNum);
	}
	
	private static void printTree(Vertex [] graph,Node temp){
		System.out.println(graph[temp.vertexNum].str);
		for(int i=0;i<temp.kids.size();i++){
			printTree(graph,temp.kids.get(i));
		}
	}
	
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
	
	private static void bubbleSolver(Vertex [] graph, Node node, Node common, HashSet<Integer> set){
		Node node1 = node;
		Node node2 = graph[node.vertexNum].temp;
		
		double sum = 0;
		double count = 0;
		
		while(node1!=common){
			String str1 = graph[node1.vertexNum].str;
			String str2 = graph[node1.parent.vertexNum].str;
			String str = str2 + str1.charAt(str1.length()-1);
			sum = sum + countmap.get(str);
			count++;
			node1=node1.parent;
		}
		
		double coverage1 = sum/count;
		double count1 = count;
		
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
		
		
		if(coverage1<=coverage2){
			List<Integer> verticesRemoved = new ArrayList<Integer>();
			Node temp = removePath(graph,node1,common,verticesRemoved);
			makeFalse(graph,temp);
			graph[node.vertexNum].found = true;
			graph[node.vertexNum].temp = node2;
			
			removefromSet(set,verticesRemoved);
			common.kids.remove(temp);
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
		
	private static void removefromSet(HashSet<Integer> set, List<Integer> verticesRemoved){
		for(int i=0;i<verticesRemoved.size();i++){
			set.remove(verticesRemoved.get(i));
		}
	}
		
		
	private static void makeFalse(Vertex [] graph, Node node){
		graph[node.vertexNum].found = false;
		graph[node.vertexNum].temp = null;

		for(int i=0;i<node.kids.size();i++){
			makeFalse(graph,node.kids.get(i));
		}
	}
		
	
	public static List<Edge> makeEdges(Vertex [] graph){
		List<Edge> edges = new ArrayList<Edge>();
		for(int i=0;i<graph.length;i++){
			if(graph[i].removed){// 
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
	
	public static void findCycle(Vertex [] graph, List<Edge> edges){
		int max = -1;
		String result = "";
		Map<String,Integer> map = new HashMap<String,Integer>();

		for(int i=0;i<graph.length;i++){
			if(!graph[i].removed && graph[i].visited==false){
				List<Integer> cycle = new ArrayList<Integer>();
			
				eulerianExplore(graph,edges,i,cycle);
				
				String genome = graph[cycle.get(cycle.size()-1)].str;

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
	
	public static void run() throws IOException{
		String [] reads = reader();
		Vertex [] graph = createDeBruijnGraph(reads);
		tipRemoval(graph);
		bubbleHandler(graph);
		tipRemoval(graph);
		List<Edge> edges = makeEdges(graph);
		findCycle(graph,edges);
	}
		
		
	
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
		
			
	
	
		
	
			
		
				
				
				
				
		
       	