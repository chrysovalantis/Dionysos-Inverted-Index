package com.example.demo.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class PostingList {

	private int freq;
	private TreeMap<Integer, TreeSet<Integer>> docs;
	
	
	public PostingList() {
		
		this.docs = new TreeMap<>();
		this.freq = 0;
		
	}
	
	public int getFreq() {
		return freq;
	}
	public void setFreq(int freq) {
		this.freq = freq;
	}
	
	public TreeMap<Integer, TreeSet<Integer>> getDocs() {
		return docs;
	}
	public void setDocs(TreeMap<Integer, TreeSet<Integer>> docs) {
		this.docs = docs;
	}

	public void addDocument(int docID, int position) {
		//System.out.println(docID + " "+ position);
		TreeSet<Integer> positions = docs.getOrDefault(docID, new TreeSet<Integer>());
		positions.add(position);
		docs.put(docID, positions);
		this.freq ++;
	}

	@Override
	public String toString() {
		String ret = "PostingList("+freq+")\n";
		for (Map.Entry<Integer, TreeSet<Integer>> entry : this.docs.entrySet()) {
			Integer key = entry.getKey();
			TreeSet<Integer> value = entry.getValue();
			ret = ret + "DocumentID: " + key + " --> ";
			int tmpcount=0;
			for (int i:value) {
				if(tmpcount == 0) {
					ret = ret + " "+ i;
				}
				else {
					ret = ret + ", "+ i;
				}
				tmpcount++;
			}
			ret = ret+ "\n";
		}
		System.out.println(ret);
		return ret;
		
	}
	
	
	
}
