package com.example.demo.form;

import com.example.demo.model.PostingList;

public class SearchResponse {

    private double time;
    private String query;
    private PostingList response;

    public SearchResponse(double time, String query, PostingList response) {
        this.time = time;
        this.query = query;
        this.response = response;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public PostingList getResponse() {
        return response;
    }

    public void setResponse(PostingList response) {
        this.response = response;
    }
}
