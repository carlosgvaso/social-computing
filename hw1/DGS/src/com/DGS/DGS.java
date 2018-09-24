package com.DGS;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;


/* 
 * HW1 EX5 - DGS Algorithm.
 * 
 */

public class DGS {

	private static int N = 50;			// max number of vertices in one partition, or max number of items and bidders
	private static double inf = 1000000000;// infinity
	private double[][] weight;				// weight matrix, where w_ij is weigtht of edge between item i and bidder j
	private int n;			// n = |I| = |J| (number of items and bidders)
	private Queue<Integer> bidders; 
	private double delta;
	private Good[] goods;
	
	private class Good {
		double price;
		int owner;
		Good(double p, int o) {
			price = p;
			owner = o;
		}
	}
	
	private void auctionGoods() {
		int i = -1;
		while (!bidders.isEmpty()) {
			i = bidders.remove();
			double max = -inf;
			int maxGood = -1;
			for (int j = 0; j < n; j++) {
				double diff = weight[i][j] - goods[j].price;
				if (diff > max) {
					maxGood = j;
					max = diff;
				}
					
			}
			if (max >= 0) {
				if (goods[maxGood].owner != -1) 
					bidders.add(goods[maxGood].owner);
				goods[maxGood].owner = i;
				goods[maxGood].price += delta;
			}
		}
	}
	
	
	private void printResults() {
		int[] results = new int[n];
		int finalWeight = 0;
		
		for (int i=0; i<this.n; i++) {
			results[goods[i].owner] = i;
			finalWeight += weight[goods[i].owner][i];
		}
		System.out.println(finalWeight + " // weight of the matching");
		for (int i=0; i<this.n; i++) {
			System.out.println("(" + (i+1) + "," + (results[i]+1) + ")");
		}
	}
	
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
				    			this.weight = new double[this.n][this.n];
				    			bidders = new LinkedList<>();
				    			for (int k = 0; k < this.n; k++) {
				    				bidders.add(k);
				    			}
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
		delta = (double)1 / (double)(this.n + 1);
		goods = new Good[n];
		for (int k = 0; k < n; k++) {
			goods[k] = new Good(0,-1);
		}
	}
	
	
	
	
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		// Check we have 1 argument
		if (args.length != 1) {
			System.out.println("Wrong number of arguments provided. Please, run the program as follows:\n\njava KM <input file name>");
			System.exit(1);
		}
		DGS dgs = new DGS();
			
			dgs.readInputFile(args[0]);
			dgs.auctionGoods();
			dgs.printResults();
			long endTime = System.nanoTime();
			long totalTime = endTime-startTime;
			System.out.println("Total time taken for DGS is " + totalTime);
		}

}