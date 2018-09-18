package com.KM;

import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * HW1 EX4 - Kuhn-Munkres Algorithm.
 */

public class KM {
//	private static int N = 50;			// Max number of vertices in one partition, or max number of items and bidders
	private static int inf = 1000000000;// Infinity
	public int[][] weight;				// Weight matrix, where w_ij is weight of edge between item i and bidder j
	public int n;						// Number of items and bidders: n = |I| = |J|
	public int matching_size;			// Current matching size, or number of vertices in the matching
	public int matching_weight;			// Current matching weight
	private int[] li;					// Labels for I (items) partition, l(i)
	private int[] lj;					// Labels for J (bidders) partition, l(j)
	private int[] ij;					// ij[i] is a vertex in J that is matched with i
	private int[] ji;					// ji[j] is a vertex in I that is matched with j
	private boolean[] S, T;				// S and T sets
	private int[] slack;				// Slack defined as: l(i) + l(j) - w(i,j)
	private int[] slacki;				// slacki[j] is a vertex such that l(slacki[j]) + l(j) - w(slacki[j],j) = slack[j]
	private int[] prev;					// array for memorizing alternating paths
	
	/**
	 * Read input file and initialize the instance variables arrays.
	 * 
	 * @param inputFile
	 */
	public KM (String inputFile) {
		// Read input file to obtain n and the weight matrix
		this.readInputFile(inputFile);
		
		// Initialize all arrays with size n
		this.li = new int[this.n];
		this.lj = new int[this.n];
		this.ij = new int[this.n];
		this.ji = new int[this.n];
		this.S = new boolean[this.n];
		this.T = new boolean[this.n];
		this.slack = new int[this.n];
		this.slacki = new int[this.n];
		this.prev = new int[this.n];
	}
	
	/**
	 * Add edges to the alternating tree: (previ, ij[i]), (ij[i], i).
	 * 
	 * @param i		Current vertex.
	 * @param previ	Vertex from I before i is in the alternating path.
	 */
	void addEdgeToTree(int i, int previ) {
		// Add i to S
		this.S[i] = true;
		
		// We need prev[i] when augmenting
		this.prev[i] = previ;
		
		// Update slacks, because we added a new vertex to S
		for (int j = 0; j < this.n; j++) {
			if (this.li[i] + this.lj[j] - this.weight[i][j] < this.slack[j]) {
				this.slack[j] = this.li[i] + this.lj[j] - this.weight[i][j];
				this.slacki[j] = i;
			}
		}
	}
	
	/**
	 * Main algorithm function using recursion.
	 */
	private void augmentMatching () {
		// Check is we found perfect matching, and return.
		if (this.matching_size == this.n) {
			return;
		}
		
		Arrays.fill(this.S, false);	// Initialize S and T arrays to false
		Arrays.fill(this.T, false);
		int i = 0;					// Initialize i, j counters and root vertex of BFS tree
		int j = 0;
		int root = 0;
		int[] q = new int[this.n];	// Queue for BFS tree
		int r = 0;					// Read and write for position in queue
		int w = 0;
		Arrays.fill(this.prev, -1);	// Initialize prev for alternating tree with all entries -1
		
		// Find an exposed vertex (root for the BFS tree)
		for (i=0; i<this.n; i++) {
			if (this.ij[i] == -1) {
				q[w++] = i; // Add i to the queue and increase the write position
				root = i;
				this.prev[i] = -2;	// Add it to prev
				this.S[i] = true;	// Add i to the S set
				break;
			}
		}
		
		// Initialize slack arrays
		for (j=0; j<this.n; j++) {
			this.slack[j] = this.li[root] + this.lj[j] - this.weight[root][j];
			this.slacki[j] = root;
		}
		
		// Main loop
		while (true) {
			// Building the tree with BFS loop
			while (r < w) {
				i = q[r++]; // Current vertex from I partition
				
				// Iterate through all edges in the equality graph
				for (j = 0; j < this.n; j++) {
					if (this.weight[i][j] == this.li[i] + this.lj[j] && !this.T[j]){
						// Break if we found an exposed vertex in J, because an augmenting path exists
						if (this.ji[j] == -1) {
							break;
						}
						
						/*
						 * Else just add j to T, add vertex ji[j] (which is matched with j) to the
						 * queue, and add edges (i,j) and (j,ji[j]) to the tree
						 */
						this.T[j] = true;
						q[w++] = this.ji[j];
						this.addEdgeToTree(this.ji[j], i);
					}
				}
				// We found an augmenting path
				if (j < this.n) {
					break;
				}
			}
			// We found an augmenting path
			if (j < this.n) {
				break;
			}
			// We did not find an augmenting path, so update the labels
			this.updateLabels();
			w = r = 0;
			
			/*
			 * In this loop, we add edges that were added to the equality graph as a
			 * result of improving the labeling, we add edge (slacki[j], j) to the tree if
			 * and only if !T[j] && slack[j] == 0, also with this edge we add another one
			 * (j, ji[j]) or augment the matching, if j was exposed
			*/
			for (j = 0; j < this.n; j++) {
				if (!this.T[j] && this.slack[j] == 0) {
					if (ji[j] == -1) {//exposed vertex in J found - augmenting path exists!
						i = this.slacki[j];
						break;
					}
					else {
						this.T[j] = true; //else just add j to T,
						if (!this.S[this.ji[j]]) {
							q[w++] = this.ji[j]; //add vertex ji[j], which is matched with j, to the queue
							addEdgeToTree(this.ji[j], this.slacki[j]); //and add edges (i,j) and (j,ji[j]) to the tree
						}
					}
				}
			}
			// We found an augmenting path
			if (j < this.n) {
				break;
			}
		}
		
		if (j < this.n) {//we found augmenting path!
			this.matching_size++; //increment matching
			//in this cycle we inverse edges along augmenting path
			for (int ci = i, cj = j, tj; ci != -2; ci = this.prev[ci], cj = tj) {
				tj = ij[ci];
				ji[cj] = ci;
				ij[ci] = cj;
			}
			this.augmentMatching(); //recall function, go to step 1 of the algorithm
		}
	}
	
	/**
	 * Find the total weight of a matching.
	 */
	private void findMatchingWeight() {
		for (int i=0; i<this.n; i++) {
			this.matching_weight += this.weight[i][this.ij[i]];
		}
	}
	
	/**
	 * Print formatted output.
	 */
	private void printResults() {
		System.out.println(this.matching_weight + " // weight of the matching");
		
		for (int i=0; i<this.n; i++) {
			System.out.println("(" + (i+1) + "," + (ij[i]+1) + ")");
		}
	}
	
	/**
	 * Set the initial labels for l(i) and l(j) and an empty matching.
	 * 
	 * The initial labels are feasible, and they are initialized as follows:
	 * 
	 *  - l(i) is the maximum weight of any edge that exist between i and any j in J.
	 *  - l(j) is 0 for all j in J.
	 */
	private void setInitialLabelsAndMatching () {
		Arrays.fill(this.lj, 0);	// Initialize lj to all 0
		/*
		 * Initialize li. Loop over all entries of the weight matrix and save the highest weight[i][j]
		 * for a fixed i to li[i].
		 */
		for (int i=0; i<this.n; i++) {
			for (int j=0; j<this.n; j++) {
				if (this.weight[i][j] > this.li[i]) {
					this.li[i] = this.weight[i][j];
				}
			}
		}
		
		// Initialize an empty matching, so the size and weight are 0
		this.matching_size = 0;
		this.matching_weight = 0;
		
		// There are no matched edges. Use -1 to represent an exposed vertex.
		Arrays.fill(this.ij, -1);
		Arrays.fill(this.ji, -1);
	}
	
	/**
	 * Read the input text file to obtain n and the weight matrix.
	 * 
	 * The file should be in the format where n is specified first. Then, the weight matrix is
	 * specified. There might be comments inside the file, which start by "//". If a comment
	 * delimiter is found, the rest of the line is ignored. For example:
	 * 
	 * 3 // number of rows and columns
	 * 12 2 4
	 * 8 7 6
	 * 7 5 2
	 * 
	 * @param inputFile
	 */
	private void readInputFile (String inputFile) {
		String line;
        int j_size = 0;
        int i = 0;
        this.n = -1;		// Initialize n to -1 as a default value
		
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(inputFile));
			
			try {
				while ((line = buffer.readLine()) != null) {
				    String[] vals = line.trim().split("\\s+");
				    j_size = vals.length;
				    
				    // If n is not found yet look for it first
				    if (this.n == -1) {
				    	// Iterate over all the words in the line
				    	for (int j=0; j<j_size; j++) {
				    		// Ignore comments
				    		if (vals[j].matches("//.*")) {
				    			break;
				    		} else if (vals[j].matches("\\d+")) {	// If n has not been set yet, the first int we find is n
				    			this.n = Integer.parseInt(vals[j]);
				    			
				    			// Now that we know the size of n, we can initialize the weight matrix before we fill it
				    			this.weight = new int[this.n][this.n];
				    		}
				    	}
				    } // We found n, now let's look for the weight matrix
				    else if (vals[0].matches("\\d+")) {	// The next int after n is assumed to be part of the matrix
				    	for (int j=0; j<this.n; j++) {
				    		// Ignore comments
				    		if (vals[j].matches("//.*")) {
				    			break;
				    		} else if (vals[j].matches("\\d+")) {
				    			this.weight[i][j] = Integer.parseInt(vals[j]);
				    		}
				    	}
				    	i++;
				    }
				    
				}
			} catch (NumberFormatException | IOException e) {
				System.out.println("Error parsing the file");
				try {
					buffer.close();
				} catch (IOException e1) {
					// Do nothing because we are exiting in error already
				}
				System.exit(1);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + inputFile);
			System.exit(1);
		}
	}
	
	/**
	 * Update labels.
	 */
	private void updateLabels () {
		// Initialize i, j counters, and initialize alpha as infinity to ensure the default value is not used
		int i;
		int j;
		int alpha = KM.inf;
		
		// Calculate alpha using slack (alpha = min(slack[j] for j not in T))
		for (j = 0; j < this.n; j++) {
			if (!this.T[j]) {
				alpha = Math.min(alpha, this.slack[j]);
			}
		}
		
		// Update I labels (li'[i] = li[i] - alpha)
		for (i = 0; i < this.n; i++) {
			if (this.S[i]) {
				this.li[i] -= alpha;
			}
		}
		
		// Update J labels (lj'[j] = lj[j] + alpha)
		for (j = 0; j < this.n; j++) {
			if (this.T[j]) {
				this.lj[j] += alpha;
			}
		}
		
		// Update slack array (slack'[j] = slack[j] - alpha)
		for (j = 0; j < this.n; j++) {
			if (!this.T[j]) {
				this.slack[j] -= alpha;
			}
		}
	}
	
	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main (String[] args) {
		long startTime = System.nanoTime();
		
		// Check we have 1 argument
		if (args.length != 1) {
			System.out.println("Wrong number of arguments provided. Please, run the program as follows:\n\njava KM <input file name>");
			System.exit(1);
		}
		
		KM km = new KM(args[0]);
		
		km.setInitialLabelsAndMatching();
		km.augmentMatching();
		km.findMatchingWeight();
		km.printResults();
		
		long endTime = System.nanoTime();
		long totalTime = endTime-startTime;
		System.out.println("Total time taken for KM is " + totalTime);
	}
}
