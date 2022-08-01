package com.example.MyHealth.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.MyHealth.models.Callers;
import com.example.MyHealth.R;

import java.util.List;

public class CallerAdapter extends RecyclerView.Adapter<CallerAdapter.UserViewHolder> {

    private List<Callers> callers;

    public CallerAdapter(List<Callers> callers) {
        this.callers = callers;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_user,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(callers.get(position));
    }

    @Override
    public int getItemCount() {
        return callers.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView textFirstChar, textUsername, textEmail;
        ImageView imageAudioMeeting, imageVideoMeeting;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textFirstChar = itemView.findViewById(R.id.textFirstChar);
            textUsername = itemView.findViewById(R.id.textUsername);
            textEmail = itemView.findViewById(R.id.textEmail);
            imageAudioMeeting = itemView.findViewById(R.id.imageAudioMeeting);
        }

        void setUserData(Callers callers){
            textFirstChar.setText(callers.firstName.substring(0, 1));
            textUsername.setText(String.format("%s %s", callers.firstName, callers.lastName));
            textEmail.setText(callers.email);
        }
    }
}

