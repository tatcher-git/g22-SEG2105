package com.example.localloopapp;

import com.google.firebase.database.Exclude;

public class Category {
    private String name;
    private String description;

    @Exclude
    private String key; // utilisé pour supprimer la catégorie dans Firebase

    public Category() {
        // Constructeur vide requis par Firebase
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Exclude
    public String getKey() { return key; }

    @Exclude
    public void setKey(String key) { this.key = key; }
}
