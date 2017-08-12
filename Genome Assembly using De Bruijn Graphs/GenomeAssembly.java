import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.StringTokenizer;

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
				e.printStackTrace();
			}
			return temp;
		}
	}
	
	static class Vertex{
		int vertexNum;
		String str;
		List<Integer> edgeList;
		
		public Vertex(int vertexNum, String str, ArrayList<Integer> list){
			this.vertexNum = vertexNum;
			this.str = str;
			this.edgeList = list;
		}
	}
	
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
	
	static List<Edge> edges;
		
		
	public static Vertex [] reader(){
		FastReader fr = new FastReader();
		
		edges = new ArrayList<Edge>();

		HashMap<String,Integer> idmap = new HashMap<String,Integer>();
		HashMap< String, ArrayList<Integer> > edgesmap = new HashMap< String, ArrayList<Integer> >();

		int id=0; 
		
		for(int i=0;i<5396;i++){
			String str = fr.nextLine();
			String a = str.substring(0,str.length()-1);
			String b = str.substring(1);
			
			if(!idmap.containsKey(a)){
				idmap.put(a,id);
				id++;
				edgesmap.put(a,new ArrayList<Integer>());
			}
				
			if(!idmap.containsKey(b)){
				idmap.put(b,id);
				id++;
				edgesmap.put(b,new ArrayList<Integer>());
			}
			
			boolean overlap = isOverlap(a,b);
			if(overlap){
				Edge edge = new Edge(idmap.get(a),idmap.get(b));
				edgesmap.get(a).add(edges.size());
				edges.add(edge);
			}
		}
		
		Vertex [] graph = maptoArray(idmap,edgesmap);
		return graph;
	} 
	
	private static Vertex [] maptoArray(HashMap<String,Integer> idmap, HashMap<String, ArrayList<Integer>> edgesmap){
		Vertex [] graph = new Vertex[idmap.size()];
		
		for(String temp : edgesmap.keySet()){
			graph[idmap.get(temp)] = new Vertex(idmap.get(temp),temp,edgesmap.get(temp));
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
	
	public static void findCycle(Vertex [] graph){
		List<Integer> cycle = new ArrayList<Integer>();
		explore(graph,0,cycle);
		
		String genome = graph[cycle.get(cycle.size()-1)].str;
		for(int i=cycle.size()-2; i>-1; i--){
			String temp = graph[cycle.get(i)].str;
			genome = genome + temp.charAt(temp.length()-1);
		}
		System.out.println(genome.substring(9)); 
	}
	
	private static void explore(Vertex [] graph, int vertex, List<Integer> cycle){
		List<Integer> edgeList = graph[vertex].edgeList;
		for(int i=0;i<edgeList.size();i++){
			Edge edge = edges.get(edgeList.get(i));
			if(!edge.used){
				edge.used = true;
				explore(graph,edge.to,cycle);
			}
		}
		
		cycle.add(vertex);
	}
	
	public void run() throws IOException{
		Vertex [] graph = reader();
		findCycle(graph);
	}
	
	public static void main(String [] args) throws IOException{
		new Thread(null, new Runnable(){
			public void run(){
				try{
					new GenomeAssembly().run();
				}
				catch(IOException e){
				}
			}
		},"1",1<<26).start();

	}
}
		
			
			
		
			
		
			