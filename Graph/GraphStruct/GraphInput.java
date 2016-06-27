package GraphStruct;
import java.io.*;
import java.util.*;


public class GraphInput {

    /**
     * Load graph data from a text file via user interaction.
     * This method asks the user for a directory and path name.
     * It returns a hashtable of (String, Vertex) pairs.
     * newgraph needs to already be initialized.
     * @param newgraph  a simple graph
     * @returns a hash table of (String, Vertex) pairs
     */
    public static Hashtable<String, Vertex> LoadSimpleGraph(SimpleGraph newgraph) {
        System.out.print("Please enter the full path and file name for the input data: ");
        String userinput;
        userinput = KeyboardReader.readString();
        return LoadSimpleGraph(newgraph, userinput);
    }

    /**
     * Load graph data from a text file.
     * The format of the file is:
     * Each line of the file contains 3 tokens, where the first two are strings
     * representing vertex labels and the third is an edge weight (a double).
     * Each line represents one edge.
     * 
     * This method returns a hashtable of (String, Vertex) pairs.
     * 
     * @param newgraph  a graph to add edges to. newgraph should already be initialized
     * @param pathandfilename  the name of the file, including full path.
     * @returns  a hash table of (String, Vertex) pairs
     */
    public static Hashtable<String, Vertex> LoadSimpleGraph(SimpleGraph newgraph, String pathandfilename){
        BufferedReader  inbuf = InputLib.fopen(pathandfilename);
        System.out.println("Opened " + pathandfilename + " for input.");
        String  line = InputLib.getLine(inbuf); // get first line
        StringTokenizer sTok;
        int n, linenum = 0;
        Hashtable<String, Vertex> table = new Hashtable<String, Vertex>();
        SimpleGraph sg = newgraph;
        double maxEdgeCapacity = Double.MIN_VALUE;
        while (line != null) {
            linenum++;
            sTok = new StringTokenizer(line);
            n = sTok.countTokens();
            if (n==3) {
                Double edgedata;
                Vertex v1, v2;
                String v1name, v2name;

                v1name = sTok.nextToken();
                v2name = sTok.nextToken();
                edgedata = new Double(Double.parseDouble(sTok.nextToken()));
                v1 = (Vertex) table.get(v1name);
                if (v1 == null) {
//                      System.out.println("New vertex " + v1name);
                        v1 = sg.insertVertex(null, v1name);
                        table.put(v1name, v1);
                }
                v2 = (Vertex) table.get(v2name);
                if (v2 == null) {
//                      System.out.println("New vertex " + v2name);
                    v2 = sg.insertVertex(null, v2name);
                    table.put(v2name, v2);
                }
                if(edgedata>maxEdgeCapacity)
                	maxEdgeCapacity = edgedata;
//              System.out.println("Inserting edge (" + v1name + "," + v2name + ")" + edgedata);
                sg.insertEdge(v1,v2,edgedata, null);
            }
            else {
                System.err.println("Error:invalid number of tokens found on line " +linenum+ "!");
                return null;
            }
            line = InputLib.getLine(inbuf);
        }
        sg.setMaxEdgeCapacity(maxEdgeCapacity);
        InputLib.fclose(inbuf);
        System.out.println("Successfully loaded "+ linenum + " lines. ");
        return table;
    }


    /**
     * Code to test the methods of this class.
     */
    public static void main (String args[]) {
          SimpleGraph G;
          G = new SimpleGraph();
          LoadSimpleGraph(G, args[0]);
    }
}