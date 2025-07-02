package com.example.localloopapp;

public class Event {
    private String name;
    private String date;
    private String description;
    private String categoryId;
    private String organizerId;
    private String key;

    public Event() {}

    public Event(String name, String date, String description, String categoryId, String organizerId) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.categoryId = categoryId;
        this.organizerId = organizerId;
    }

    // Getters et setters
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public String getCategoryId() { return categoryId; }
    public String getOrganizerId() { return organizerId; }
    public String getKey() { return key; }

    public void setName(String name) { this.name = name; }
    public void setDate(String date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
    public void setKey(String key) { this.key = key; }
}
