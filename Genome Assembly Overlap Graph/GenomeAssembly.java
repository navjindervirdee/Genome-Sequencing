//Program to Assemble the Phi-X174 genome using Overlap Graph.

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.StringTokenizer;


public class GenomeAssembly{
	//function to get the input reads of the genome.
	public static Vertex [] getReads(){
		FastReader s=new FastReader();
		Vertex [] graph = new Vertex[1618];

		for(int i=0;i<graph.length;i++){
			graph[i] = new Vertex(i,s.nextLine());
		}
		return graph;
	}


	//class for a Vertex of a Graph.
	static class Vertex{
		int vertexNum;			//id of the vertex.
		String read;			//read of the vertex.
		Map<Integer,Integer> edges; 	//Keys are indexes of adjacent vertices, and Values are length of overlap between the two strings (read).
		boolean found;			//if found while traversing the graph.
		
		public Vertex(int vertexNum, String read){
			this.vertexNum = vertexNum;
			this.read = read;
			this.edges = new HashMap<Integer,Integer>();
			this.found = false;
		}
	}
	
	//class for reading the input.
	static class FastReader{
        	BufferedReader br;
        	StringTokenizer st;
 
        	public FastReader(){
            		br = new BufferedReader(new InputStreamReader(System.in));
        	}
 
		String next(){
			while (st == null || !st.hasMoreElements()){
            	     		try{
            				st = new StringTokenizer(br.readLine());
                     		}
	             		catch (IOException  e){
        	       	     		e.printStackTrace();
               	     		}
                   	}
                   	return st.nextToken();
		}
 
 	       	int nextInt(){
        	    	return Integer.parseInt(next());
               	}
 
	       	long nextLong(){
        	   	return Long.parseLong(next());
               	}
 
              	double nextDouble(){
                   	return Double.parseDouble(next());
              	}
 
        	String nextLine(){
            		String str = "";
            		try{
	                	str = br.readLine();
        	    	}
            		catch (IOException e){
               	 		e.printStackTrace();
            		}
            		return str;
        	}
    	}
	
	//function to make an Overlap graph.
	private static void makeOverlapGraph(Vertex [] graph){
		for(int i=0; i<graph.length; i++){
			for(int j=0; j<graph.length; j++){
				if(j==i){
					continue;
				}
				char [] str1 = graph[i].read.toCharArray();
				char [] str2 = graph[j].read.toCharArray();
				for(int k=0; k<str2.length; k++){
					int length = str1.length-k;
					int error = (int)(length*0.03);   	//considering an error of only 3% in the overlap between the reads.
					
					boolean overlap = true;
					int m = 0;
					for(int l=k; l<str1.length;l++){
						if(error==0){
							overlap=false;
							break;
						}
						if(str1[l]!=str2[m]){
							error--;			
						}
						m++;
					}
						
					if(overlap){
						graph[i].edges.put(j,length);
						break;
					}
				}
			}
		}
	}

	static int last = -1;	//store the index of the last explored vertex.

	//function to find hamiltonian path in the graph using a greedy approach. i.e choosing the next vertex with max overlap length.				
	private static String findHamiltonianPath(Vertex [] graph){
		String genome = "";
		int random = (int)(Math.random()*(graph.length-2) + 1); 	//starting with randomly picked vertex.
		int first = random;
		
		genome = genome + graph[random].read;
		genome = explore(graph,random,genome);
		
		for(int i=0;i<graph.length;i++){
			if(!graph[i].found){
				genome = genome + graph[i].read;
				last = i;
			}
		}
		
		if(graph[last].edges.containsKey(first)){
			int length = graph[last].edges.get(first);
			return genome.substring(length);
		}
		
		return genome;
	}
	
	//function to explore the given vertex index.
	private static String explore(Vertex [] graph, int random, String genome){
		graph[random].found = true;
		int max = -1;
		int index = -1;
		for(int key : graph[random].edges.keySet()){
			int temp = graph[random].edges.get(key);
			if(graph[key].found==false && max<temp){
				max = temp;
				index = key;
			}
		}
		
		if(index==-1){
			return genome;
		}

		genome = genome + graph[index].read.substring(max);
		last = index;
		return explore(graph,index,genome);
	}

	//main function to run the program.
	public static void main(String [] args){
		Vertex [] graph = getReads();  			//get the reads.
		makeOverlapGraph(graph);			//make overlap graph.
		String genome = findHamiltonianPath(graph);	//find hamiltonian path.
		System.out.println(genome);			//print the genome.
	}
}			