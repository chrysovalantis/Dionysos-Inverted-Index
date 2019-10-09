package com.example.demo.model;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import com.example.demo.service.PorterStemmer;

/** Each directory has an Inverted Index. This class is the implementation of the inverted index
 *  and handles all the activities on it.
 *
 * @author Chrysovalantis Christodoulou
 */
public class InvertedIndex implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private TreeMap<String, PostingList> invertedIndex;			// Inverted Index is a Tree Map(Key=term, value = PostingList contains all the docs)
	private PorterStemmer porterStemmer;						// The Porter Stemmer service.
	
	public InvertedIndex() {
		invertedIndex = new TreeMap<>();
		porterStemmer = new PorterStemmer();
	}

	public TreeMap<String, PostingList> getInvertedIndex() {
		return invertedIndex;
	}

	public void setInvertedIndex(TreeMap<String, PostingList> invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

	/** Take each word and make some pre-processing on it, before adding it
	 *  to the index and before searching.
	 *
	 * @param term
	 * @return
	 */
	private String prepareTerm(String term) {
//		System.out.println("Before Term: "+term);
		term = term.toLowerCase();
		term = term.replaceAll("\\p{Punct}", "");  //remove punctuations
		term = porterStemmer.executeStemming(term);					 //execute stemming
//        System.out.println("After Term: "+term);
		return term;
	}

	/** Add term to the index.
	 *
	 * @param term
	 * @param docID
	 * @param position
	 */
	public void addTerm(String term, int docID, int position) {
		term = prepareTerm(term);
		PostingList postingList = invertedIndex.getOrDefault(term,new PostingList());  //Returns the the value if the key exists or new Posting list if it is not.
		postingList.addDocument(docID,position);
		invertedIndex.put(term, postingList);
	}

	/** Delete a file from the index and update it. If the term refers only in the deleted
	 *  document then the term is removed.
	 *
	 * @param docID
	 * @return
	 */
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

	/** Return the posting list of a given term
	 *
	 * @param term
	 * @return
	 */
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
