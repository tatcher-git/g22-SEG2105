package com.example.localloopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OrganizerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);  // ton layout XML avec les boutons

        String firstName = getIntent().getStringExtra("firstName");
        String role = getIntent().getStringExtra("role");

        TextView welcomeText = findViewById(R.id.textViewOrganizer);
        welcomeText.setText("Welcome " + firstName + "! You are logged in as \"" + role + "\".");
    }

    public void addEvent(View view) {
        startActivity(new Intent(this, AddEventActivity.class));
    }

    public void viewRequest(View view) {
        startActivity(new Intent(this, ManageRequest.class));
    }

    public void viewEvent(View view) {
        startActivity(new Intent(this, MyEvents.class));
    }

    public void logout(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
