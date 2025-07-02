package com.example.localloopapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    private Context context;
    private List<Event> eventList;

    public EventAdapter(Context context, List<Event> list) {
        super(context, 0, list);
        this.context = context;
        this.eventList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        }

        Event event = eventList.get(position);

        TextView name = convertView.findViewById(R.id.text_event_name);
        TextView date = convertView.findViewById(R.id.text_event_date);
        TextView description = convertView.findViewById(R.id.text_event_description);
        Button editButton = convertView.findViewById(R.id.button_edit_event);
        Button deleteButton = convertView.findViewById(R.id.button_delete_event);

        name.setText(event.getName());
        date.setText("Date: " + event.getDate());
        description.setText(event.getDescription());

        editButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Edit Event");

            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_event, null);
            builder.setView(dialogView);

            EditText nameInput = dialogView.findViewById(R.id.edit_event_name);
            EditText dateInput = dialogView.findViewById(R.id.edit_event_date);
            EditText descInput = dialogView.findViewById(R.id.edit_event_description);

            nameInput.setText(event.getName());
            dateInput.setText(event.getDate());
            descInput.setText(event.getDescription());

            builder.setPositiveButton("Save", (dialog, which) -> {
                String newName = nameInput.getText().toString().trim();
                String newDate = dateInput.getText().toString().trim();
                String newDesc = descInput.getText().toString().trim();

                if (!newName.isEmpty() && !newDate.isEmpty() && !newDesc.isEmpty()) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("events").child(event.getKey());
                    ref.child("name").setValue(newName);
                    ref.child("date").setValue(newDate);
                    ref.child("description").setValue(newDesc);
                    Toast.makeText(context, "Event updated", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.show();
        });

        deleteButton.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("events").child(event.getKey()).removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show());
        });

        return convertView;
    }
}
