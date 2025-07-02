package com.example.localloopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    private EditText eventNameInput, eventDescriptionInput, eventDateInput;
    private Spinner categorySpinner;
    private Button addEventButton;

    private DatabaseReference categoriesRef, eventsRef;
    private List<String> categoryNames = new ArrayList<>();
    private List<String> categoryKeys = new ArrayList<>(); // pour relier à Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventNameInput = findViewById(R.id.editTextEventName);
        eventDescriptionInput = findViewById(R.id.editTextEventDescription);
        eventDateInput = findViewById(R.id.editTextDateTime);
        categorySpinner = findViewById(R.id.spinnerCategory);
        addEventButton = findViewById(R.id.buttonSubmitEvent);

        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");
        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        loadCategoriesIntoSpinner();

        addEventButton.setOnClickListener(v -> addEvent());
    }

    private void loadCategoriesIntoSpinner() {
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryNames.clear();
                categoryKeys.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String name = snap.child("name").getValue(String.class);
                    if (name != null) {
                        categoryNames.add(name);
                        categoryKeys.add(snap.getKey());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddEventActivity.this,
                        android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddEventActivity.this, "Error loading categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addEvent() {
        String name = eventNameInput.getText().toString().trim();
        String description = eventDescriptionInput.getText().toString().trim();
        String date = eventDateInput.getText().toString().trim();
        int selectedPosition = categorySpinner.getSelectedItemPosition();

        if (name.isEmpty() || description.isEmpty() || date.isEmpty() || selectedPosition == -1) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoryKey = categoryKeys.get(selectedPosition);
        String organizerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String key = eventsRef.push().getKey();

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("name", name);
        eventData.put("description", description);
        eventData.put("date", date);
        eventData.put("categoryId", categoryKey);
        eventData.put("organizerId", organizerId);

        eventsRef.child(key).setValue(eventData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event added!", Toast.LENGTH_SHORT).show();
                    eventNameInput.setText("");
                    eventDescriptionInput.setText("");
                    eventDateInput.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public void logout (View view){
        startActivity(new Intent(AddEventActivity.this,MainActivity.class));

    }
    public void back (View view){
        startActivity(new Intent(AddEventActivity.this,OrganizerActivity.class));

    }
}