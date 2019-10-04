package com.example.demo.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;


public class InvertedIndex implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, PostingList> invertedIndex;
	
	public InvertedIndex() {
		invertedIndex = new HashMap<>();
	}

	public HashMap<String, PostingList> getInvertedIndex() {
		return invertedIndex;
	}

	public void setInvertedIndex(HashMap<String, PostingList> invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

	public void addTerm(String term, int docID, int position) {
		
		PostingList postingList = invertedIndex.getOrDefault(term,new PostingList());
		postingList.addDocument(docID,position);
		invertedIndex.put(term, postingList);
	}
	
	public boolean deleteFile(int docID) {
		
		Iterator termIt = this.invertedIndex.entrySet().iterator();
		while (termIt.hasNext()) {
			Map.Entry<String, PostingList> entry = (Entry<String, PostingList>) termIt.next();
			String key = entry.getKey();
			PostingList value = entry.getValue();
			Iterator posIt = value.getDocs().entrySet().iterator();
			while (posIt.hasNext()) {
				Map.Entry<Integer, TreeSet<Integer>> posentry = (Entry<Integer, TreeSet<Integer>>) posIt.next();
				Integer poskey = posentry.getKey();
				if ( poskey == docID) {
					posIt.remove();
					value.decreaseFreq();
				}
			}
			if (value.getDocs().size() == 0) {
				termIt.remove();
			}
		}
		
		return true;
	}

	@Override
	public String toString() {
		String ret = "---- Inverted Index ----\n";
		for (Entry<String, PostingList> entry : this.invertedIndex.entrySet()) {
			String key = entry.getKey();
			PostingList value = entry.getValue();
			ret = ret + "Term: "+ key + "\n";
			ret = ret + value.toString() + "\n";
		}
		return ret;
	}

	
	
	
}
