package com.example.localloopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        String firstName = getIntent().getStringExtra("firstName");
        String role = getIntent().getStringExtra("role");

        TextView welcomeText = findViewById(R.id.textViewWelcome);
        welcomeText.setText("Welcome " + firstName + "! You are logged in as \"" + role + "\".");
    }
    public void logout (View view){
        startActivity(new Intent(WelcomeActivity.this,MainActivity.class));

    }
}
