/* Joshua Walker
 * 
 * Perform a modified Ford-Fulkerson method on a graph
 *   with vertex capacities (no edge capacities) to find 
 *   the maximum flow.
 *
 * Input is a text file formatted as such:
 * (1 2) (2 3) (2 5) (3 4) (4 5) (3 5) (5 6)
 * (2 1500) (3 1000) (4 800) (5 400) 
 * Where the first line denotes edges between nodes 
 * 	and the second line denotes vertex capacities.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

class MaxFlow{
   
   /* main
    *
    * gets input file from user, makes graph from input, then computes the max flow
    */
   public static void main(String[] args)throws FileNotFoundException{
      //Prompt user for file name that contains the input then try to read it
      Scanner prompt = new Scanner(System.in);
      System.out.print("Enter a filename: ");
      String filename = prompt.nextLine();
   
      File inFile = new File(filename); 
      if(!inFile.canRead()){
         System.out.println("File " + filename + " could not be read. Exiting...");
         System.exit(1);
      } else {
         //Grab both lines of input from the file to be processed
         Scanner textRead = new Scanner(inFile);
         String edges = textRead.nextLine().replaceAll("[()]", "");
         String[] edgeResult = edges.split("\\s");
         String caps = textRead.nextLine().replaceAll("[()]", "");
         String[] capResult = caps.split("\\s");
         
         //Create the hash maps holding the information for each vertex (adjacency lists and capacities)
         HashMap<Integer, List<Integer>> edgeMap = new HashMap<Integer, List<Integer>>();
         HashMap<Integer, Integer> resCapMap = new HashMap<Integer, Integer>();
         
         int source = 10000;  //The vertex you start from
         int sink = -10000;   //The vertex you end at
         int vertNum = 0;     //The number of vertices in the graph
         int outgoing = -1;   //Number of the outgoing vertexes in an edge
         
         //Build the adjacency list for each vertex
         for(int i = 0; i<edgeResult.length; i++){
            int vertex = Integer.parseInt(edgeResult[i]);
            boolean newKey = false;
            
            if(vertex < source) {
               source = vertex;
            }
            if(vertex > sink) {
               sink = vertex;
            }
            
            //If this vertex hasn't bee seen before, add it to the map
            if(!edgeMap.containsKey(vertex)){
                  List<Integer> adjList = new LinkedList<Integer>();
                  edgeMap.put(vertex, adjList);
                  resCapMap.put(vertex, Integer.MAX_VALUE);
                  vertNum++;
            }
            if(i % 2 == 0){     
               //This is where an edge is coming from, so set outgoing
               // to be able to add the node its directed to to the adjacency list 
               outgoing = vertex; 
            } else {                
               //This is where an edge is directed to, so add it 
               // to the corresponding adjacency list
               edgeMap.get(outgoing).add(vertex);
            }
         }
         
         //Fill in the capacities for each node
         // (cap set to MAX_INT by default if no capacity) 
         // is known for a given vertex
         int capVert = -1;
         for(int i = 0; i<capResult.length; i++){
            int value = Integer.parseInt(capResult[i]);
            
            if(i % 2 == 0){
               //the outgoing vertex in this edge
               capVert = value;
            } else {
               //the incoming vertex in this edge
               resCapMap.put(capVert, value);
            }
         }
         
         //Create the array that will contain the augmenting path
         // init everything to -1, breadthFirst will set things correctly later
         int pathArr[] = new int[vertNum];
         for(int i = 0; i < pathArr.length; i++) {
            pathArr[i]=-1;
         }
         int maxFlow = 0;
         
         //Continue looping until no augmenting path is found
         while(breadthFirst(edgeMap, resCapMap, source, sink, pathArr)) {
            int currVert = sink;
            int pathFlow = Integer.MAX_VALUE;
            //Find the max possible flow on the current augmenting path
            while(currVert != source) {
               pathFlow = Math.min(pathFlow,resCapMap.get(currVert));
               currVert = pathArr[currVert-1];   
            }
            
            maxFlow = maxFlow + pathFlow;
            
            //Update the residuals of the augmenting path with the max flow
            // on that path.
            int thing2 = sink;
            while(thing2 != source) {
               resCapMap.put(thing2, resCapMap.get(thing2)-pathFlow);
               thing2 = pathArr[thing2-1];
            }
         }
         System.out.println("The max flow of this graph is: " + maxFlow);
      }
   }
   
   /* breadthFirst
    *
    * Performs a breadth first search to to see if an augmenting path
    * from the given source to the given sink exists.
    */
   static boolean breadthFirst(HashMap<Integer, List<Integer>> graphMap,
                        HashMap<Integer, Integer> resCapMap,
                        int source,
                        int sink,
                        int[] pathArr){
                        
      //Build array to mark if a vertex has been visited
      boolean[] seen = new boolean[graphMap.size()];
      for(int i = 0; i < seen.length; i++) {
         seen[i] = false;
      }                  
      
      //Using a Linked List (as a queue), traverse the graph. If the 
      // sink gets marked as seen, then there is an augmenting path.
      LinkedList<Integer> queue = new LinkedList<Integer>();
      queue.add(source);
      seen[source-1] = true; 
      while(queue.size()!=0){
         int vert = queue.poll();
         
         for(int w: graphMap.get(vert)){
            if(!seen[w-1] && resCapMap.get(w) > 0){
               queue.add(w);
               seen[w-1] = true;
               pathArr[w-1] = vert;
            }
         }
      }  
      return seen[sink-1];
   } 
}