package com.example.localloopapp;

public class User {
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String role;
    private String id;
    private boolean active;


    //  Constructeur vide obligatoire pour Firebase
    public User() {

    }

    //  Constructeur principal utilisé à l'inscription
    public User(String username, String firstName, String lastName, String email, String role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.active = active;
    }
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setActive(boolean active) { this.active = active; }

    // Pour afficher le nom complet
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
