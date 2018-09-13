package com.KM;

/**
 * HW1 EX4 - Kuhn-Munkres Algorithm.
 */

public class KM {
	private static int N = 50;			// max number of vertices in one partition, or max number of items and bidders
	private static int inf = 1000000000;// infinity
	private int[][] weight;				// weight matrix, where w_ij is weigtht of edge between item i and bidder j
	private int n, max_match;			// n = |I| = |J| (number of items and bidders)
	private int[] li;					// labels for I (items) partition, l(i)
	private int[] lj;					// labels for J (bidders) partition, l(j)
	private int[] ij;					// ij[i] is a vertex in J that is matched with i
	private int[] ji;					// ji[j] is a vertex in I that is matched with j
	private boolean[] S, T;				// S and T sets
	private int[] slack;				// l(i)+l(j)-w(i,j)
	private int[] slacki;				// slacki[j] is a vertex such that l(slacki[j])+l(j)-w(slacki[j],j) = slack[j]
	private int[] prev;					// array for memorizing alternating paths
	
	/**
	 * Initialize empty instance variables.
	 * 
	 * @param inputFile
	 */
	public KM () {
		// Initialize all arrays with value 0
		this.weight = new int[KM.N][KM.N];
		this.li = new int[KM.N];
		this.lj = new int[KM.N];
		this.ij = new int[KM.N];
		this.ji = new int[KM.N];
		this.S = new boolean[KM.N];
		this.T = new boolean[KM.N];
		this.slack = new int[KM.N];
		this.slacki = new int[KM.N];
		this.prev = new int[KM.N];
	}
	
	/**
	 * Set the initial labels for l(i) and l(j).
	 * 
	 * The initial labels are feasible, and they are initialized as follows:
	 * 
	 *  - l(i) is the maximum weight of any edge that exist between i and any j in J.
	 *  - l(j) is 0 for all j in J.
	 */
	private void initLabels () {
		/*
		 * lj was already set to all 0 in the constructor, so nothing to do there.
		 * Let's initialize li, which is currently also all 0. Loop over all entries
		 * of the weight matrix and save the highest weight[i][j] for a fixed i to li[i].
		 */
		for (int i=0; i<n; i++) {
			for (int j=0; j<n; j++) {
				if (this.weight[i][j] > this.li[i]) {
					this.li[i] = this.weight[i][j];
				}
			}
		}
		
		// TODO: Remove after testing
		System.out.print("li = ");
		for (int i=0; i<this.n; i++) {
			System.out.print(li[i] + " ");
		}
		System.out.print("\nlj = ");
		for (int j=0; j<this.n; j++) {
			System.out.print(lj[j] + " ");
		}
		System.out.println();
		// ===
	}
	
	/**
	 * Read the input text file to obtain n and the weight matrix.
	 * 
	 * TODO:
	 * - Read n and weight matrix from text file.
	 * 
	 * @param inputFile
	 */
	private void readInputFile (String inputFile) {
		System.out.println("Input file: " + inputFile);
		
		// For the time being return the example n and weight matrix.
		this.n = 3;	// set n
		
		// set the example weight matrix
		this.weight[0][0] = 12;
		this.weight[1][0] = 8;
		this.weight[2][0] = 7;
		this.weight[0][1] = 2;
		this.weight[1][1] = 7;
		this.weight[2][1] = 5;
		this.weight[0][2] = 4;
		this.weight[1][2] = 6;
		this.weight[2][2] = 2;
		
		// TODO: Remove after testing
		System.out.println("n = " + this.n);
		
		System.out.println("weight =");
		
		for (int i=0; i<this.n; i++) {
			for (int j=0; j<this.n; j++) {
				if (j == this.n - 1) {
					System.out.println(this.weight[i][j]);
				} else {
					System.out.print(this.weight[i][j] + " ");
				}
			}
		}
		// ===
	}
	
	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main (String[] args) {
		KM km = new KM();
		
		km.readInputFile(args[0]);
		km.initLabels();
	}
}
