package com.example.rundumdog;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class EntryCard extends RecyclerView.ViewHolder {

    public TextView entryTitle;
    public TextView entryDate;
    public TextView entryString;

    EntryLoop.OnItemClickListener mListener;




    //initialises a card layout for Home Fragment with Entry title and date
    public EntryCard(@NonNull View itemView) {
        super(itemView);

        entryTitle = itemView.findViewById(R.id.title);
        entryDate = itemView.findViewById(R.id.date);
        entryString = itemView.findViewById(R.id.message);


            }
        }












