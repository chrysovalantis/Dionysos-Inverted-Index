package com.example.demo.model;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

/** Every single query has 2 terms and one operation. Thus, this class represent that approach
 *  and execute the AND, OR, NOT operations.
 *
 * @author Chrysovalantis Christodoulou
 */
public class Query {
	
	private PostingList termA;
	private PostingList termB;
	public enum Operations {AND, OR, NOT}
	
	
	
	public Query(PostingList termA, PostingList termB) {
		this.termA = termA;
		this.termB = termB;
	}
	
	
	public Query() {
		// TODO Auto-generated constructor stub
	}


	public PostingList getTermA() {
		return termA;
	}
	public void setTermA(PostingList termA) {
		this.termA = termA;
	}
	public PostingList getTermB() {
		return termB;
	}
	public void setTermB(PostingList termB) {
		this.termB = termB;
	};


	/** Execute the given boolean operation on the 2 posting list.
	 *
	 * @param oper
	 * @return
	 */
	public PostingList operation(Operations oper){
		
		PostingList result = new PostingList();
		
		switch (oper) {

		case AND:
			if (this.termA == null || this.termB == null)
				break;
			Iterator<Entry<Integer, TreeSet<Integer>>> posItA = this.termA.getDocs().entrySet().iterator();
			Iterator<Entry<Integer, TreeSet<Integer>>> posItB = this.termB.getDocs().entrySet().iterator();

	        int l=-1,r=-1;
	        while(posItA.hasNext() || posItB.hasNext()){
	            if(posItA.hasNext() && l<r){
	                l = posItA.next().getKey();
	            }else if(posItB.hasNext() && r<l){
	                r = posItB.next().getKey();
	            }else if(posItA.hasNext() && posItB.hasNext()){
	                l = posItA.next().getKey();
	                r = posItB.next().getKey();
	            }
	            else {
	            	break;
	            }
	            if(l==r) {
	            	result.addDocumentQuery(l, this.termA.getDocs().get(l));
	            	result.addDocumentQuery(r, this.termB.getDocs().get(r));
	            }
	        }
			break;
			
		case OR:
			
			result.getDocs().putAll(this.termA.getDocs());
			for (Map.Entry<Integer, TreeSet<Integer>> e: this.termB.getDocs().entrySet()) {
				result.getDocs().merge(e.getKey(), e.getValue(), (oldValue, newValue) ->{
					TreeSet<Integer> newPositions = new TreeSet<Integer>();
					newPositions.addAll(oldValue);
					newPositions.addAll(newValue);
					return newPositions;
				});
			}
			result.setFreq(result.getDocs().size());
			
			break;
			
		case NOT:

	        if(this.termA==null || this.termA.getDocs().isEmpty()){
	        	break;
	        }
	        if(this.termB==null || this.termB.getDocs().isEmpty()) {
	            result.getDocs().putAll(this.termA.getDocs());
	            break;
	        }
	        
	        Iterator<Entry<Integer, TreeSet<Integer>>> map = this.termA.getDocs().entrySet().iterator();
	        
	        while(map.hasNext()){
	            int value = map.next().getKey();
	            if(!this.termB.getDocs().containsKey(value))
	                result.addDocumentQuery(value, this.termA.getDocs().get(value));
	        }
			
			break;

		default:
			break;
		}
		
		
		return result;
		
	}
	
}
