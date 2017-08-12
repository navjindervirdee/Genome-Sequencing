import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.StringTokenizer;


public class GenomeAssembly{
	public static Vertex [] getReads(){
		FastReader s=new FastReader();
		Vertex [] graph = new Vertex[1618];

		for(int i=0;i<graph.length;i++){
			graph[i] = new Vertex(i,s.nextLine());
		}
		return graph;
	}

	static class Vertex{
		int vertexNum;
		String read;
		Map<Integer,Integer> edges; //could remove duplicated strings. if not passed then can use list instead of map.
		boolean found;
		
		public Vertex(int vertexNum, String read){
			this.vertexNum = vertexNum;
			this.read = read;
			this.edges = new HashMap<Integer,Integer>();
			this.found = false;
		}
	}
	
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
            try
            {
                str = br.readLine();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return str;
        }
    }

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
						int error = (int)(length*0.03);
						//System.out.println(str1.substring(k) + " " + str2.substring(0,length) + " " + str1.substring(k).equals(str2.substring(0,length)));
						
						
						boolean overlap = true;
						int m = 0;
						for(int l=k; l<str1.length;l++){
							if(error==0){
								overlap=false;
								break;
							}
							if(str1[l]!=str2[m]){
								//System.out.println(i + " " + j + " " + k + " " +  str1[l] + " " + str2[m]);
								error--;			
							}
							//System.out.println(str1[l] + " " + str2[m]);
							m++;
						}
						
						if(overlap){
							graph[i].edges.put(j,length);
							break;
						}
							
								
						/*if(str1.substring(k).equals(str2.substring(0,length))){
							graph[i].edges.put(j,length);
							break;
						}*/
					}
			}
		}
	
		/*for(int i=0;i<graph.length;i++){
			System.out.println(graph[i].edges);
		}*/
	}

	static int last = -1;		
				
	private static String findHamiltonianPath(Vertex [] graph){
		String genome = "";
		int random = (int)(Math.random()*(graph.length-2) + 1);
		//System.out.println(5);
		int first = random;
		
		//System.out.println(first);
		
		genome = genome + graph[random].read;
		genome = explore(graph,random,genome);

		//System.out.println(last);
		
		//System.out.println(genome);
		
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
	
	private static String explore(Vertex [] graph, int random, String genome){
		graph[random].found = true;
		int max = -1;
		int index = -1;
		for(int key : graph[random].edges.keySet()){
			int temp = graph[random].edges.get(key);
			//System.out.println(temp);
			if(graph[key].found==false && max<temp){
				max = temp;
				index = key;
			}
		}
		//System.out.println(max + " " + index);
		if(index==-1){
			return genome;
		}
		genome = genome + graph[index].read.substring(max);
		last = index;
		//System.out.println(genome + " " + graph[index].read.substring(max));
		return explore(graph,index,genome);
		
	}

	public static void main(String [] args){
		Vertex [] graph = getReads();
		
		makeOverlapGraph(graph);
		String genome = findHamiltonianPath(graph);
		System.out.println(genome);
	}
}
			
		
		
		
		
		
					
					
		
			