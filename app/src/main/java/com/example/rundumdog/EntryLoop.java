package com.example.rundumdog;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


    public class EntryLoop extends RecyclerView.Adapter<EntryCard> {


        private final List<Entry> entryList;

        Context context;

        private OnItemClickListener mListener;

        //initialises list of objects of Entry class
        public EntryLoop(List<Entry> entryList) {
            this.entryList = entryList;


        }

        //creates a view with Entry card layout
        @NonNull
        @Override
        public EntryCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_card, parent, false);
            return new EntryCard(layoutView);
        }

        //fills out the card layout with data from Entry list
        @Override
        public void onBindViewHolder(@NonNull EntryCard holder, @SuppressLint("RecyclerView") int position) {
            if (entryList != null && position < entryList.size()) {
                Entry entry = entryList.get(position);
                holder.entryTitle.setText(entry.title);
                DateFormat dtf = new SimpleDateFormat("dd-MM-yyyy");
                String formDate = dtf.format(entry.date);
                holder.entryDate.setText(formDate);
                holder.entryString.setText(entry.message);





            }
        }


        //gets the size of the Entry list
        @Override
        public int getItemCount() {

            return entryList.size();
        }

        public void setOnClickListener(OnItemClickListener mListener) {
            this.mListener = mListener;
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }











    }



