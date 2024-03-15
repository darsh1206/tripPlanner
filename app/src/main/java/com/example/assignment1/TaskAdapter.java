package com.example.assignment1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {
    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_element, parent, false);
        }

        // initializing variables
        TextView titleText = convertView.findViewById(R.id.title);
        TextView statusText = convertView.findViewById(R.id.status);
        TextView descriptionText = convertView.findViewById(R.id.description);

        // setting required properties
        titleText.setText(task.getTitle());
        statusText.setText(task.getStatus());
        statusText.setVisibility(View.GONE);

        // changing colour of status element
        switch (statusText.getText().toString()){
            case "Pending":
                statusText.setTextColor(getContext().getResources().getColor(R.color.orange));
                break;
            case "In Progress":
                statusText.setTextColor(getContext().getResources().getColor(R.color.yellow));
                break;
            case "Completed":
                statusText.setTextColor(getContext().getResources().getColor(R.color.green));
                break;
        }
        descriptionText.setText(task.getDescription());
        descriptionText.setVisibility(View.GONE);

        // flipping the task element to display hidden data
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of the description
                if (descriptionText.getVisibility() == View.GONE && statusText.getVisibility() == View.GONE) {
                    descriptionText.setVisibility(View.VISIBLE);
                    statusText.setVisibility(View.VISIBLE);
                } else {
                    descriptionText.setVisibility(View.GONE);
                    statusText.setVisibility(View.GONE);
                }
            }
        });

        ImageView deleteButton = convertView.findViewById(R.id.delete_button);
        // Deleting the element
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the item at 'position'
                remove(getItem(position));
                notifyDataSetChanged(); // Refresh the list view
                Toast t = Toast.makeText(TaskAdapter.this.getContext(), "Task deleted", Toast.LENGTH_LONG);
                t.show();
            }
        });
        return convertView;
    }
}
