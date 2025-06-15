package com.example.localloopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageEvent extends AppCompatActivity {

    private ListView listViewCategories;
    private List<Category> categoryList;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event); // Assure-toi que le nom est correct

        listViewCategories = findViewById(R.id.listView_categories);

        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList);
        listViewCategories.setAdapter(categoryAdapter);

        // Charger les catégories depuis Firebase
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        category.setKey(categorySnapshot.getKey());
                        categoryList.add(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageEvent.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void back (View view){
        startActivity(new Intent(ManageEvent.this,AdminActivity.class));

    }
    public void logout (View view){
        startActivity(new Intent(ManageEvent.this,MainActivity.class));

    }
    public void addcategory(View view) {
        TextInputEditText nameField = findViewById(R.id.editTextFirstName);
        TextInputEditText descriptionField = findViewById(R.id.editTextLastName);

        String name = nameField.getText().toString().trim();
        String description = descriptionField.getText().toString().trim();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }


        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");
        String key = categoriesRef.push().getKey();

        Category category = new Category(name, description); // pas besoin d'id dans le constructeur
        categoriesRef.child(key).setValue(category)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Category added!", Toast.LENGTH_SHORT).show();
                    nameField.setText("");
                    descriptionField.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}