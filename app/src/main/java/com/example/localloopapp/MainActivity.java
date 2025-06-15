package com.example.localloopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public void register(View view) {
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
    }

    public void loginUser(View view) {
        EditText usernameField = findViewById(R.id.editTextUsername);
        EditText passwordField = findViewById(R.id.editTextPassword);

        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter a username and a password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Admin case
        if (username.equals("admin") && password.equals("XPI76SZUqyCjVxgnUjm0")) {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            intent.putExtra("firstName", "Admin");
            intent.putExtra("role", "admin");
            startActivity(intent);
            return;
        }

        // Look up user by username in Firebase
        dbRef.orderByChild("username").equalTo(username).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                for (DataSnapshot userSnap : dataSnapshot.getChildren()) {
                    String email = userSnap.child("email").getValue(String.class);
                    String firstName = userSnap.child("firstName").getValue(String.class);
                    String role = userSnap.child("role").getValue(String.class);

                    if (email == null || email.isEmpty()) {
                        Toast.makeText(this, "No email found for this username", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Try login with email + password
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(authResult -> {
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                                userRef.get().addOnSuccessListener(snapshot -> {
                                    if (snapshot.exists()) {
                                        Boolean isActive = snapshot.child("active").getValue(Boolean.class);
                                        if (isActive != null && !isActive) {
                                            Toast.makeText(MainActivity.this, "This account is disabled", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                        } else {
                                            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                                            intent.putExtra("firstName", firstName);
                                            intent.putExtra("role", role);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "User not found in the database", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(MainActivity.this, "Error reading active state", Toast.LENGTH_SHORT).show();
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Connection error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }
            } else {
                Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Database access error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}