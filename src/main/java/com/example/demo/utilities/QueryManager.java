package com.example.demo.utilities;

import java.util.Map;

import com.example.demo.model.InvertedIndex;
import com.example.demo.model.PostingList;
import com.example.demo.model.Query;
import com.example.demo.model.Query.Operations;

import exceptions.QueryParserException;

/** This class handles the query given by the user
 *
 * @author Chrysovalantis Christodoulou
 */
public class QueryManager {

	private String query;
	
	public QueryManager(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	/** This function parse the query from the user and response with the answer
	 *
	 * @param index
	 * @return
	 * @throws QueryParserException
	 */
	public PostingList queryParser(InvertedIndex index) throws QueryParserException {
		
		Query q = new Query();
		String termA = null;
		String termB = null;
		Operations op = null;
		PostingList valueA = new PostingList();
		PostingList valueB = new PostingList();
		PostingList result = new PostingList();
		PostingList allDocs = new PostingList();
		
		this.query = this.query.trim().replaceAll(" +", " ");		//remove all the extra space from the query
		String queryArray[] = this.query.split(" ");							//split the query
		Boolean ornotflag = false;
		//Traverse through query. You can add as much terms and operations as you want.
		for (int i = 0; i < queryArray.length; i++) {
			//check the first term
			if (termA == null && op == null ) {
				if (queryArray[i].compareTo("AND") == 0 || queryArray[i].compareTo("OR") == 0) { //the first term cannot be AND or OR
					throw new QueryParserException("Query Not Valid: "+ this.query);
				}
				if (queryArray[i].compareTo("NOT") == 0) {
					op = Operations.NOT;
				}
				else {
					termA = queryArray[i];							//save the term as string
					valueA = index.search(termA);					//get the posting list of the term
					q.setTermA(valueA);
				}
				continue;
			}
			// check if the first term was NOT and the termA is not set
			if (termA == null && op != null) {
				if (queryArray[i].compareTo("AND") == 0 || queryArray[i].compareTo("OR") == 0 || queryArray[i].compareTo("NOT") == 0) { //not allowed to have to operations next to each other
					throw new QueryParserException("Query Not Valid: "+ this.query);
				}
				//Execute NOT termA query. Gives you all the docs that don't include the termA
				else {
					Query tmp = new Query();
					termA = queryArray[i];
					valueA = index.search(termA); 
					tmp.setTermB(valueA);
					if (allDocs.getDocs().size() == 0) {
						for (Map.Entry<String, PostingList> e: index.getInvertedIndex().entrySet() ) {
							allDocs.getDocs().putAll(e.getValue().getDocs());
						}
					}
					tmp.setTermA(allDocs);
					result = tmp.operation(op);
					q.setTermA(result);
					termB = null;
					op = null;
					continue;
				}				
			}
			//If termA is set and operation is not then this term must be an operation
			if (termA != null && op == null) {
				if(queryArray[i].compareTo("AND") == 0) {
					op = Operations.AND;
				}
				else if(queryArray[i].compareTo("OR") == 0) {
					op = Operations.OR;
				}
				else if(queryArray[i].compareTo("NOT") == 0) {
					op = Operations.NOT;
				}
				else {
					throw new QueryParserException("Query Not Valid: "+ this.query);
				}
				continue;
			}
			//if termA and operation is set then we have to check for the second term (termB) and execute the query
			if (termA != null && op !=null) {
				if (op == Operations.NOT && (queryArray[i].compareTo("AND") == 0 || queryArray[i].compareTo("OR") == 0 || queryArray[i].compareTo("NOT") == 0)) {
					throw new QueryParserException("Query Not Valid: "+ this.query);
				}
				if (op == Operations.AND && (queryArray[i].compareTo("AND") == 0 || queryArray[i].compareTo("OR") == 0)) {
					throw new QueryParserException("Query Not Valid: "+ this.query);
				}
				if (op == Operations.OR && (queryArray[i].compareTo("AND") == 0 || queryArray[i].compareTo("OR") == 0)) {
					throw new QueryParserException("Query Not Valid: "+ this.query);
				}
				//check the combination AND NOT
				if (op == Operations.AND && queryArray[i].compareTo("NOT") == 0 ) {
					op = Operations.NOT;
					continue;
				}
				//check the comination OR NOT
				if (op == Operations.OR && queryArray[i].compareTo("NOT") == 0 ) {
					op = Operations.OR;
					ornotflag = true;
					continue;
				}
				termB = queryArray[i];				//save termB
				valueB = index.search(termB);		//get the posting list of the termB
				//if there is an OR NOT query we are executing the following code. Return everything that is not include termB union termA
				if(ornotflag) {
					Query tmp = new Query();
					if (allDocs.getDocs().size() == 0) {
						for (Map.Entry<String, PostingList> e: index.getInvertedIndex().entrySet() ) {
							allDocs.getDocs().putAll(e.getValue().getDocs());
						}
					}
					tmp.setTermA(allDocs);
					tmp.setTermB(valueB);
					q.setTermB(tmp.operation(Operations.NOT));
					ornotflag = false;
				}
				//else execute normal query
				else {
					q.setTermB(valueB);
				}
				result = q.operation(op);
				q.setTermA(result);
				termB = null;
				op = null;
			}
		}
		//if it is the first term
		if (termA!=null && queryArray.length == 1) {
			result = valueA;
		}
		if(result == null) {
			result = new PostingList();
		}
		
		return result;
	}
	
}
