package com.example.localloopapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> list) {
        super(context, 0, list);
        this.context = context;
        this.userList = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        }

        User user = userList.get(position);

        TextView name = convertView.findViewById(R.id.textView_user_name);
        TextView email = convertView.findViewById(R.id.textView_user_email);
        TextView role = convertView.findViewById(R.id.textView_user_role);
        Button disable = convertView.findViewById(R.id.button_disable_user);
        Button delete = convertView.findViewById(R.id.button_delete_user);

        name.setText(user.getFirstName() + " " + user.getLastName());
        email.setText(user.getEmail());
        role.setText(user.getRole());

        disable.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getId())
                    .child("active")
                    .setValue(false);
            Toast.makeText(context, "User disabled", Toast.LENGTH_SHORT).show();
        });

        delete.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getId())
                    .removeValue();
            Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
