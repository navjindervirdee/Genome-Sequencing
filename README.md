# Assembled Phi-X174 genome using Overlap Graph, Kmer Composition and De-Bruijn Graph.


## Problem Description. ##

  * **Input**: A collection of Strings called reads of the original genome. Each read is a sub-string of the original genome(Genome can be                circular also).

  * **Output**: A string S of minimum length that cantains all the strings(reads) given in the input as its sub-strings.



## Algorithms ##

* **Overlap Graph Algorithm**: 
  * Construct an overlap graph. Two reads are joined by a directed edge of weight equal to the length of the maximum overlap of these two     strings.
  
  * Then construct a Hamiltonian path in this graph in a greedy fashion.
  
  * Greedy Strategy : For each read select an outgoing edge of maximum weight. Why? Because the more the overlap between the reads shorter
    shorter will be the length of the combined string made of these reads.
    
  * Then read a string spelled by this path. i.e combine to form a super string.
  
  * Sometimes choosing the wrong first vertex may result in longer superstring. So you should generate random index probably 2-3 times and     find minimum length super string.
  
  * Now in the last step since genome can be circular also so remove the overlap length between last and first read.
  
  **Note**: This greedy algorithm does not work with every genome as it might not give optimal solution every time.



* **K-Mer Composition Algorithm Using De-Bruijn Graph**:
 
   *What is K-mer Composition? --> Given a String ACGTACTAT. Its 3-mer Composition is (ACG, CGT, GTA, TAC, ACT, CTA, TAT).*
   
   **Its De-Bruijn graph**:
   
    ![alt text](http://4.bp.blogspot.com/-Z5LkYQfEvtQ/U0ZfUqQNA6I/AAAAAAAAAWM/TUxVVWcdA6Q/s1600/graph.png)
      
   
  **STEPS**:
  
     * Read the k-mer composition of the graph.
  
     * Create the De-Bruijn graph from the k-mer composition.
 
     * Find an eulerian cycle in the graph.
  
     * Construct the genome from the found cycle.
  
