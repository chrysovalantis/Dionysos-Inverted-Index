package com.example.demo.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.example.demo.service.PorterStemmer;

import java.util.TreeSet;


public class InvertedIndex implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, PostingList> invertedIndex;
	private PorterStemmer porterStemmer;
	
	public InvertedIndex() {
		invertedIndex = new HashMap<>();
		porterStemmer = new PorterStemmer();
	}

	public HashMap<String, PostingList> getInvertedIndex() {
		return invertedIndex;
	}

	public void setInvertedIndex(HashMap<String, PostingList> invertedIndex) {
		this.invertedIndex = invertedIndex;
	}
	
	private String prepareTerm(String term) {
//		System.out.println("Before Term: "+term);
		term = term.toLowerCase();
		term = term.replaceAll("\\p{Punct}", "");
		term = porterStemmer.executeStemming(term);
//        System.out.println("After Term: "+term);
		return term;
	}

	public void addTerm(String term, int docID, int position) {
		term = prepareTerm(term);
		PostingList postingList = invertedIndex.getOrDefault(term,new PostingList());
		postingList.addDocument(docID,position);
		invertedIndex.put(term, postingList);
	}
	
	public boolean deleteFile(int docID) {
		
		Iterator<Entry<String, PostingList>> termIt = this.invertedIndex.entrySet().iterator();
		while (termIt.hasNext()) {
			Map.Entry<String, PostingList> entry =  termIt.next();
			PostingList value = entry.getValue();
			Iterator<Entry<Integer, TreeSet<Integer>>> posIt = value.getDocs().entrySet().iterator();
			while (posIt.hasNext()) {
				Map.Entry<Integer, TreeSet<Integer>> posentry = posIt.next();
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
	
	public PostingList search(String term) {
		return this.invertedIndex.get(prepareTerm(term));
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
