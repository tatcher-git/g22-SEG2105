package com.example.localloopapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import android.app.AlertDialog;
import android.widget.EditText;
import androidx.annotation.NonNull;


public class CategoryAdapter extends ArrayAdapter<Category> {

    private Context mContext;
    private List<Category> categoryList;

    public CategoryAdapter(Context context, List<Category> list) {
        super(context, 0, list);
        mContext = context;
        categoryList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.category_item, parent, false);
        }

        Category currentCategory = categoryList.get(position);

        TextView textViewName = convertView.findViewById(R.id.textView_category_name);
        TextView textViewDescription = convertView.findViewById(R.id.textView_category_description);
        Button buttonEdit = convertView.findViewById(R.id.button_edit_category);
        Button buttonDelete = convertView.findViewById(R.id.button_delete_category);

        textViewName.setText(currentCategory.getName());
        textViewDescription.setText(currentCategory.getDescription());

        // Action bouton Edit (à personnaliser si tu veux faire une modification)
        buttonEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Edit Category");

            View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_category, null);
            builder.setView(dialogView);

            EditText editName = dialogView.findViewById(R.id.edit_category_name);
            EditText editDescription = dialogView.findViewById(R.id.edit_category_description);

            editName.setText(currentCategory.getName());
            editDescription.setText(currentCategory.getDescription());

            builder.setPositiveButton("Save", (dialog, which) -> {
                String newName = editName.getText().toString().trim();
                String newDescription = editDescription.getText().toString().trim();

                if (!newName.isEmpty() && !newDescription.isEmpty()) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories").child(currentCategory.getKey());
                    ref.child("name").setValue(newName);
                    ref.child("description").setValue(newDescription);
                    Toast.makeText(mContext, "Category updated", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.create().show();
        });

        // Action bouton Delete
        buttonDelete.setOnClickListener(v -> {
            DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");
            categoriesRef.child(currentCategory.getKey()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(mContext, "Category deleted!", Toast.LENGTH_SHORT).show();
                    });
        });

        return convertView;
    }
}