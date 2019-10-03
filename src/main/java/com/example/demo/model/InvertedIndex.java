package com.example.demo.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

public class InvertedIndex {
	
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
