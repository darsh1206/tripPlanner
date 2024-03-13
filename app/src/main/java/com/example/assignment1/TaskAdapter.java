package com.example.assignment1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment1.ui.theme.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {
    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_element, parent, false);
        }

        TextView titleText = convertView.findViewById(R.id.title);
        TextView statusText = convertView.findViewById(R.id.status);
        TextView descriptionText = convertView.findViewById(R.id.description);

        titleText.setText(task.getTitle());
        statusText.setText(task.getStatus());
        descriptionText.setText(task.getDescription());
        descriptionText.setVisibility(View.GONE); // Initially hide the description

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of the description
                if (descriptionText.getVisibility() == View.GONE) {
                    descriptionText.setVisibility(View.VISIBLE);
                } else {
                    descriptionText.setVisibility(View.GONE);
                }
            }
        });

        ImageView deleteButton = convertView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the item at 'position'
                remove(getItem(position));
                notifyDataSetChanged(); // Refresh the list view
            }
        });
        return convertView;
    }
}
