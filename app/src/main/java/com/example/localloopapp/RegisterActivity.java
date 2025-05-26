package com.example.localloopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public void registerUser(View view) {
        EditText firstNameField = findViewById(R.id.editTextFirstName);
        EditText lastNameField = findViewById(R.id.editTextLastName);
        EditText emailField = findViewById(R.id.editTextEmail);
        EditText usernameField = findViewById(R.id.editTextUsername);
        EditText roleField = findViewById(R.id.editTextRole);
        EditText passwordField = findViewById(R.id.editTextPassword);

        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String username = usernameField.getText().toString().trim();
        String role = roleField.getText().toString().trim().toLowerCase();
        String password = passwordField.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                username.isEmpty() || role.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Remplis tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!role.equals("organizer") && !role.equals("participant")) {
            Toast.makeText(this, "Rôle invalide (organizer ou participant)", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = auth.getCurrentUser().getUid();
                User newUser = new User(username, firstName, lastName, email, role);

                dbRef.child(uid).setValue(newUser).addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        Toast.makeText(this, "Compte créé avec succès ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("role", role);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Erreur base de données ", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Erreur Firebase Auth: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
