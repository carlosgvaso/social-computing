package com.Stable_Marriage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Stable_Marriage {
	private int[][] mChoices;				// weight matrix, where w_ij is weigtht of edge between item i and bidder j
	private int[][] fChoices;
	private Tuple[] couples;
	private Proposer[] Paired;
	
	private int n;			// n = |I| = |J| (number of men and women)
	
	
	private class Proposer {
		
		Proposer(int ind, int[] preferences) {
			index = ind;
			proposals = new LinkedList<Integer>();
			spouse = -1;
			preferenceArr = preferences;
		}
		
		int index;
		List<Integer> proposals;
		int spouse;
		int[] preferenceArr;
	}
	
	private class Acceptor {
		
		Acceptor(int ind, int[] preferences) {
			index = ind;
			Free = true;
			spouse = -1;
			preferenceArr = preferences;
		}
		int index;
		boolean Free;
		int spouse;
		int[] preferenceArr;
	}
	
	private class Tuple{
		@SuppressWarnings("unused")
		int p1 = -1;
		@SuppressWarnings("unused")
		int p2 = -1;
	}
	
	private void marryPairs(int[][] optPref, int[][] secPref) {
		List<Proposer> freeList = new LinkedList<Proposer>();
		Paired = new Proposer[this.n];
		Acceptor[] Options = new Acceptor[this.n];
		
		for (int i = 0; i < this.n; i++) {
			freeList.add(new Proposer(i, optPref[i]));
			Options[i] = new Acceptor(i, secPref[i]);
		}
		
		while (freeList.size() > 0) {
			Proposer proposer = freeList.remove(0);
			int choice = -1;
			boolean propose = false;
			int i = 0;
			//go through preferences to find the next one not proposed to
			while (!propose) {
				int trial = proposer.preferenceArr[i];
				if (!proposer.proposals.contains(trial)) {
					propose = true;
					choice = trial;
				}
				i++;
			}
			
			if (Options[choice].Free) {
				proposer.spouse = choice;
				Options[choice].Free = false;
				Options[choice].spouse = proposer.index;
				Paired[proposer.index] = proposer;
				//System.out.println(proposer.index + 1 + " proposed to " + (choice + 1) + " -- FREE - ACCEPTED");
			}
			else {
				int rankProposer = -1;
				int rankCurrent = -1;
				
				Acceptor accept = Options[choice];
				for (int j = 0; j < this.n; j++) {
					int nextPref = accept.preferenceArr[j];
					if (nextPref == proposer.index)
						rankProposer = j;
					if (nextPref == accept.spouse)
						rankCurrent = j;
				}
				
				if (rankCurrent < rankProposer) {
					proposer.proposals.add(choice);
					freeList.add(proposer);
					//System.out.println(proposer.index + 1 + " proposed to " + (choice + 1) + " -- TAKEN BY "+ accept.spouse + " - REJECTED");
					
				}
				else {
					proposer.proposals.add(choice);
					freeList.add(Paired[accept.spouse]);
					Paired[accept.spouse] = null;
					proposer.spouse = choice;
					accept.spouse = proposer.index;
					Options[accept.index].spouse = proposer.index;
					
					Paired[proposer.index] = proposer;

					//System.out.println(proposer.index + 1 + " proposed to " + (choice + 1) + " -- TAKEN BY "+ accept.spouse + " - ACCEPTED");
				}
			}
		}
	}
	
	
	private void printResults() {
		int[] results = new int[n];
		
		for (int i=0; i<this.n; i++) {
			results[i] = Paired[i].spouse;
		}
		for (int i=0; i<this.n; i++) {
			System.out.println("(" + (i+1) + "," + (results[i]+1) + ")");
		}
	}
	
	private void readInputFile (String inputFile) {
		String line;
        int j_size = 0;
        this.n = -1;		// Initialize n to -1 as a default value
        int mCounter = 0;
        int fCounter = 0;
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
				    			this.couples = new Tuple[this.n];
				    			this.mChoices = new int[n][n];
				    			this.fChoices = new int[n][n];
				    		}
				    	}
				    } // We found n, now let's look for the weight matrix
				    else if (vals[0].matches("\\d+")) {	// The next int after n is assumed to be part of the matrix
				    	for (int j=0; j<this.n; j++) {
				    		// Ignore comments
				    		if (vals[j].matches("//.*")) {
				    			break;
				    		} else if (vals[j].matches("\\d+")) {
				    			if (mCounter < n) {
				    				this.mChoices[mCounter][j] = Integer.parseInt(vals[j]) - 1;
				    			}
				    			else {
				    				this.fChoices[fCounter][j] = Integer.parseInt(vals[j]) - 1;
				    			}
				    		}
				    	}
				    	if (mCounter < n) {
				    		mCounter++;
				    	}
				    	else 
				    		fCounter++;
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
		for (int k = 0; k < n; k++) {
			couples[k] = new Tuple();
		}
	}
	
	
	
	
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		// Check we have 1 argument
		if (args.length != 2) {
			System.out.println("Wrong number of arguments provided. Please, run the program as follows:\n\njava -jar SMP.jar <input file name> <m / w>");
			System.exit(1);
		}
			
		Stable_Marriage sm = new Stable_Marriage();
		
			sm.readInputFile(args[0]);
			if (args[1].equals("m"))
				sm.marryPairs(sm.mChoices, sm.fChoices);
			else if (args[1].equals("w"))
				sm.marryPairs(sm.fChoices, sm.mChoices);
			sm.printResults();
			long endTime = System.nanoTime();
			long totalTime = endTime-startTime;
			System.out.println("Total time taken for SMP is " + totalTime);
		}
}
