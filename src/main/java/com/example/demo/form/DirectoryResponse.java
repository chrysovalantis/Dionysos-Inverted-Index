package com.example.demo.form;

public class DirectoryResponse {

    private Boolean status;
    private String message;

    public Boolean getStatus() {
        return status;
    }

    public DirectoryResponse(Boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
