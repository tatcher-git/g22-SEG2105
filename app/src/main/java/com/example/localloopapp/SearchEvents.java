package com.example.localloopapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchEvents extends AppCompatActivity {

    private HashMap<String, String> categoryMap = new HashMap<>();
    private DatabaseReference categoriesRef;
    private DatabaseReference eventsRef;

    private EditText searchEditText;
    private ListView eventsListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> eventList;
    private ArrayList<String> eventIds;
    private List<Event> filteredEvents = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_events);

        searchEditText = findViewById(R.id.searchEditText);
        eventsListView = findViewById(R.id.eventsListView);


        eventList = new ArrayList<>();
        eventIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventList);
        eventsListView.setAdapter(adapter);

        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");
        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        loadCategoriesThenEvents("");

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadEvents(s.toString().toLowerCase().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        eventsListView.setOnItemClickListener((parent, view, position, id) -> {
            String eventId = eventIds.get(position);
            showJoinRequestDialog(eventId);
        });
    }

    private void loadCategoriesThenEvents(String query) {
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryMap.clear();
                for (DataSnapshot catSnap : snapshot.getChildren()) {
                    String id = catSnap.getKey();
                    String name = catSnap.child("name").getValue(String.class);
                    if (id != null && name != null) {
                        categoryMap.put(id, name);
                    }
                }
                loadEvents(query);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchEvents.this, "Failed to load categories.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEvents(String query) {
        eventList.clear();
        eventIds.clear();

        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    String name = eventSnapshot.child("name").getValue(String.class);
                    String categoryId = eventSnapshot.child("categoryId").getValue(String.class);

                    if (name == null) continue;
                    if (categoryId == null) categoryId = "(no category)";

                    String categoryName = categoryMap.getOrDefault(categoryId, "(unknown)");

                    String lowerQuery = query.toLowerCase().trim();
                    if (lowerQuery.isEmpty() ||
                            name.toLowerCase().contains(lowerQuery) ||
                            categoryName.toLowerCase().contains(lowerQuery)) {

                        String display = "Name: " + name + "\nCategory: " + categoryName;
                        eventList.add(display);
                        eventIds.add(eventSnapshot.getKey());
                    }
                }

                if (eventList.isEmpty()) {
                    eventList.add("No events found.");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchEvents.this, "Failed to load events.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showJoinRequestDialog(String eventId) {
        new AlertDialog.Builder(this)
                .setTitle("Join Request")
                .setMessage("Do you want to request to join this event?")
                .setPositiveButton("Yes", (dialog, which) -> sendJoinRequest(eventId))
                .setNegativeButton("No", null)
                .show();
    }

    private void sendJoinRequest(String eventId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference joinRef = FirebaseDatabase.getInstance().getReference("joinRequests").child(eventId).child(userId);
        joinRef.setValue("pending").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SearchEvents.this, "Request sent!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SearchEvents.this, "Failed to send request.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void logout (View view){
        startActivity(new Intent(SearchEvents.this,MainActivity.class));

    }
    public void back (View view){
        startActivity(new Intent(SearchEvents.this,WelcomeActivity.class));

    }
}
