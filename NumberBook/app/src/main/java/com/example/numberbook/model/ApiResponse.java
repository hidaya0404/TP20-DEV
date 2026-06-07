package com.example.numberbook.model;

import java.util.List;

public class ApiResponse {

    private boolean success;

    private String message;

    private List<Contact> contacts;

    private int inserted;

    private int failed;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public int getInserted() {
        return inserted;
    }

    public int getFailed() {
        return failed;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void setInserted(int inserted) {
        this.inserted = inserted;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }
}