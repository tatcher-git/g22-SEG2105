package com.example.localloopapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import java.util.List;
import java.util.ArrayList;


public class ManageUser extends AppCompatActivity {
    private ListView listViewUsers;
    private List<User> userList;
    private UserAdapter userAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_user);
        listViewUsers = findViewById(R.id.listView_users);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(ManageUser.this, userList);
        listViewUsers.setAdapter(userAdapter);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null && !user.getEmail().equals("admin")) {
                        user.setId(userSnapshot.getKey()); // important pour delete/disable
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageUser.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void back (View view){
        startActivity(new Intent(ManageUser.this,AdminActivity.class));

    }
    public void logout (View view){
        startActivity(new Intent(ManageUser.this,MainActivity.class));

    }
}



