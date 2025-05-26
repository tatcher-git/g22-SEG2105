package com.example.localloopapp;

public class User {
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String role;

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
    }
}
