package com.example.localloopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MyEvents extends AppCompatActivity {

    private List<Event> myEvents = new ArrayList<>();
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        ListView listView = findViewById(R.id.listView_my_events);
        eventAdapter = new EventAdapter(this, myEvents);
        listView.setAdapter(eventAdapter);

        loadMyEvents();
    }

    private void loadMyEvents() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("events");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myEvents.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Event event = snap.getValue(Event.class);
                    if (event != null && uid.equals(event.getOrganizerId())) {
                        event.setKey(snap.getKey());
                        myEvents.add(event);
                    }
                }
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyEvents.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }

        });
    }
    public void logout (View view){
        startActivity(new Intent(MyEvents.this,MainActivity.class));

    }
    public void back (View view){
        startActivity(new Intent(MyEvents.this,OrganizerActivity.class));

    }
}
