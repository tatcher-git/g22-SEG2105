package com.example.localloopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageRequest extends AppCompatActivity {

    Spinner eventSpinner;
    ListView requestListView;
    TextView infoText;

    DatabaseReference eventsRef, joinRequestsRef, usersRef;
    ArrayList<String> organizerEvents = new ArrayList<>();
    ArrayList<String> eventIds = new ArrayList<>();
    ArrayAdapter<String> spinnerAdapter;

    ArrayList<String> userList = new ArrayList<>();
    ArrayList<String> userIds = new ArrayList<>();
    ArrayAdapter<String> requestAdapter;

    String selectedEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_request);

        eventSpinner = findViewById(R.id.eventSpinner);
        requestListView = findViewById(R.id.requestListView);
        infoText = findViewById(R.id.infoText);

        eventsRef = FirebaseDatabase.getInstance().getReference("events");
        joinRequestsRef = FirebaseDatabase.getInstance().getReference("joinRequests");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, organizerEvents);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventSpinner.setAdapter(spinnerAdapter);

        requestAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        requestListView.setAdapter(requestAdapter);

        loadOrganizerEvents();

        eventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEventId = eventIds.get(position);
                loadJoinRequests(selectedEventId);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        requestListView.setOnItemClickListener((parent, view, position, id) -> {
            String userId = userIds.get(position);
            showDecisionDialog(selectedEventId, userId);
        });
    }

    private void loadOrganizerEvents() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                organizerEvents.clear();
                eventIds.clear();

                for (DataSnapshot eventSnap : snapshot.getChildren()) {
                    String organizerId = eventSnap.child("organizerId").getValue(String.class);
                    String eventName = eventSnap.child("name").getValue(String.class);
                    if (organizerId != null && organizerId.equals(currentUserId) && eventName != null) {
                        organizerEvents.add(eventName);
                        eventIds.add(eventSnap.getKey());
                    }
                }
                spinnerAdapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadJoinRequests(String eventId) {
        userList.clear();
        userIds.clear();
        requestAdapter.notifyDataSetChanged();

        joinRequestsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String status = userSnap.getValue(String.class);
                    if ("pending".equals(status)) {
                        String userId = userSnap.getKey();
                        userIds.add(userId);
                        loadUserDisplayName(userId);
                    }
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadUserDisplayName(String userId) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("username").getValue(String.class);
                userList.add(name != null ? name : userId);
                requestAdapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showDecisionDialog(String eventId, String userId) {
        new AlertDialog.Builder(this)
                .setTitle("Decision")
                .setMessage("Accept or reject this request?")
                .setPositiveButton("Accept", (dialog, which) -> updateRequestStatus(eventId, userId, "accepted"))
                .setNegativeButton("Reject", (dialog, which) -> updateRequestStatus(eventId, userId, "rejected"))
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void updateRequestStatus(String eventId, String userId, String status) {
        joinRequestsRef.child(eventId).child(userId).setValue(status).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ManageRequest.this, "Request " + status, Toast.LENGTH_SHORT).show();
                loadJoinRequests(eventId); // Refresh
            } else {
                Toast.makeText(ManageRequest.this, "Failed to update request", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void logout (View view){
        startActivity(new Intent(ManageRequest.this,MainActivity.class));

    }
    public void back (View view){
        startActivity(new Intent(ManageRequest.this,OrganizerActivity.class));

    }
}