package com.example.localloopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;




public class OrganizerActivity extends AppCompatActivity {
    private List<Event> myEvents = new ArrayList<>();
    private EventAdapter eventAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer);
        String firstName = getIntent().getStringExtra("firstName");
        String role = getIntent().getStringExtra("role");
        ListView listView = findViewById(R.id.listView_my_events);
        eventAdapter = new EventAdapter(this, myEvents);
        listView.setAdapter(eventAdapter);

        loadMyEvents();


        TextView welcomeText = findViewById(R.id.textViewOrganizer);
        welcomeText.setText("Welcome " + firstName + "! You are logged in as \"" + role + "\".");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
                Toast.makeText(OrganizerActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addEvent (View view){
        startActivity(new Intent(OrganizerActivity.this,AddEventActivity.class));
    }
    public void logout (View view){
        startActivity(new Intent(OrganizerActivity.this,MainActivity.class));

    }
}