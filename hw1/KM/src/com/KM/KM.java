package com.KM;

/**
 * HW1 EX4 - Kuhn-Munkres Algorithm.
 */

public class KM {
	private static int N = 50;			// max number of vertices in one partition, or max number of items and bidders
	private static int inf = 1000000000;// infinity
	private int[][] weight;				// weight matrix, where w_ij is weigtht of edge between item i and bidder j
	private int n, max_match;			// n = |A| = |B| (number of items and bidders)
	private int[] l_a;					// labels of A partition, l(a)
	private int[] l_b;					// labels of B partition, l(b)
	private int[] ab;					// ab[a] is a vertex in B that is matched with a
	private int[] ba;					// ba[b] is a vertex in A that is matched with b
	private boolean[] S, T;				// S and T sets
	private int[] slack;				// l(a)+l(b)-w(a,b)
	private int[] slack_a;				// slack_a[b] is a vertex such that l(slack_a[b])+l(b)-w(slack_a[b],b) = slack[b]
	private int[] prev;					// array for memorizing alternating paths
	
	/**
	 * Initialize empty instance variables.
	 * 
	 * @param inputFile
	 */
	public KM () {
		// Initialize all arrays with value 0
		this.weight = new int[KM.N][KM.N];
		this.l_a = new int[KM.N];
		this.l_b = new int[KM.N];
		this.ab = new int[KM.N];
		this.ba = new int[KM.N];
		this.S = new boolean[KM.N];
		this.T = new boolean[KM.N];
		this.slack = new int[KM.N];
		this.slack_a = new int[KM.N];
		this.prev = new int[KM.N];
	}
	
	/**
	 * Read the input text file to obtain n and the weight matrix.
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
	}
	
	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main (String[] args) {
		KM km = new KM();
		
		km.readInputFile(args[0]);
		
		System.out.println("n = " + km.n);
		
		for (int i=0; i<km.n; i++) {
			for (int j=0; j<km.n; j++) {
				if (j == km.n - 1) {
					System.out.println(km.weight[i][j]);
				} else {
					System.out.print(km.weight[i][j] + " ");
				}
			}
		}
	}
}
