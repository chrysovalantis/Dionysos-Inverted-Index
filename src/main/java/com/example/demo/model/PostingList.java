package com.example.demo.model;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/** Each term in the inverted index has a Posting List. The posting list contains the frequency of the word and
 *  documents you cant find it. Also there are the positions of the word inside the document.
 *
 * @author Chrysovalantis Christodoulou
 */
public class PostingList implements Serializable{


	private static final long serialVersionUID = 1L;
	
	private int freq;
	private TreeMap<Integer, TreeSet<Integer>> docs;			// TreeMap <docID, TreeSet<Positions>>
	
	
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
	
	public void decreaseFreq() {
		this.freq--;
	}
	
	
	public TreeMap<Integer, TreeSet<Integer>> getDocs() {
		return docs;
	}
	public void setDocs(TreeMap<Integer, TreeSet<Integer>> docs) {
		this.docs = docs;
	}

	/** Add a document to the posting list
	 *
	 * @param docID
	 * @param position
	 */
	public void addDocument(int docID, int position) {
		//System.out.println(docID + " "+ position);
		TreeSet<Integer> positions = docs.getOrDefault(docID, new TreeSet<Integer>());
		positions.add(position);
		docs.put(docID, positions);
		this.freq ++;
	}

	/** This method used only for the creation of the search response
	 *
	 * @param docID
	 * @param positions
	 */
	public void addDocumentQuery(int docID, TreeSet<Integer> positions) {
		//System.out.println(docID + " "+ position);
		if (docs.containsKey(docID)) {
			docs.merge(docID, positions, (oldValue, newValue) ->{
				TreeSet<Integer> newPositions = new TreeSet<Integer>();
				newPositions.addAll(oldValue);
				newPositions.addAll(newValue);
				return newPositions;
			});
		}
		else {
			docs.put(docID, positions);
			this.freq ++;
		}
		
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
