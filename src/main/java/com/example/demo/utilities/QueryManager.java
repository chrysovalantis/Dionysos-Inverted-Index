package com.example.demo.utilities;

import java.util.Map;

import com.example.demo.model.InvertedIndex;
import com.example.demo.model.PostingList;
import com.example.demo.model.Query;
import com.example.demo.model.Query.Operations;

import exceptions.QueryParserException;

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
	
	
	public PostingList queryParser(InvertedIndex index) throws QueryParserException {
		
		Query q = new Query();
		String termA = null;
		String termB = null;
		Operations op = null;
		PostingList valueA = new PostingList();
		PostingList valueB = new PostingList();
		PostingList result = new PostingList();
		PostingList allDocs = new PostingList();
		
		this.query = this.query.trim().replaceAll(" +", " ");
		String queryArray[] = this.query.split(" ");
		Boolean ornotflag = false;
		for (int i = 0; i < queryArray.length; i++) {
			if (termA == null && op == null ) {
				if (queryArray[i].compareTo("AND") == 0 || queryArray[i].compareTo("OR") == 0) {
					throw new QueryParserException("Query Not Valid: "+ this.query);
				}
				if (queryArray[i].compareTo("NOT") == 0) {
					op = Operations.NOT;
				}
				else {
					termA = queryArray[i];
					valueA = index.search(termA);
					q.setTermA(valueA);
				}
				continue;
			}
			if (termA == null && op != null) {
				if (queryArray[i].compareTo("AND") == 0 || queryArray[i].compareTo("OR") == 0 || queryArray[i].compareTo("NOT") == 0) {
					throw new QueryParserException("Query Not Valid: "+ this.query);
				}
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
				if (op == Operations.AND && queryArray[i].compareTo("NOT") == 0 ) {
					op = Operations.NOT;
					continue;
				}
				if (op == Operations.OR && queryArray[i].compareTo("NOT") == 0 ) {
					op = Operations.OR;
					ornotflag = true;
					continue;
				}
				termB = queryArray[i];
				valueB = index.search(termB);
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
				else {
					q.setTermB(valueB);
				}
				result = q.operation(op);
				q.setTermA(result);
				termB = null;
				op = null;
			}
		}
		if (termA!=null && queryArray.length == 1) {
			result = valueA;
		}
		if(result == null) {
			result = new PostingList();
		}
		
		return result;
	}
	
}
