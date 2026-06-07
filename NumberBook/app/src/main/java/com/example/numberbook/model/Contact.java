package com.example.numberbook.model;

import com.google.gson.annotations.SerializedName;

public class Contact {

    private int id;

    private String name;

    private String phone;

    private String source;

    @SerializedName("created_at")
    private String createdAt;

    public Contact() {
    }

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.source = "android";
    }

    public Contact(String name, String phone, String source) {
        this.name = name;
        this.phone = phone;
        this.source = source;
    }

    public Contact(int id, String name, String phone, String source, String createdAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.source = source;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getSource() {
        return source;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}