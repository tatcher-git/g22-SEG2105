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
    public void register (View view){
        startActivity(new Intent(MainActivity.this,RegisterActivity.class));

    }

    public void loginUser(View view) {
        EditText usernameField = findViewById(R.id.editTextUsername);
        EditText passwordField = findViewById(R.id.editTextPassword);

        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Entrez un nom d'utilisateur et un mot de passe", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cas admin
        if (username.equals("admin") && password.equals("XPI76SZUqyCjVxgnUjm0")) {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            intent.putExtra("firstName", "Admin");
            intent.putExtra("role", "admin");
            startActivity(intent);
            return;
        }

        // Chercher l'utilisateur par username dans Realtime Database
        dbRef.orderByChild("username").equalTo(username).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                for (DataSnapshot userSnap : dataSnapshot.getChildren()) {
                    String email = userSnap.child("email").getValue(String.class);
                    String firstName = userSnap.child("firstName").getValue(String.class);
                    String role = userSnap.child("role").getValue(String.class);

                    if (email == null || email.isEmpty()) {
                        Toast.makeText(this, "Aucun email trouvé pour ce nom d'utilisateur", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Connexion avec email + mot de passe
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                                    intent.putExtra("firstName", firstName);
                                    intent.putExtra("role", role);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Erreur de connexion: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                    return;
                }
            } else {
                Toast.makeText(this, "Nom d'utilisateur introuvable", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erreur d'accès à la base: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

}
