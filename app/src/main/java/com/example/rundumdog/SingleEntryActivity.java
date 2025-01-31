package com.example.rundumdog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class SingleEntryActivity extends Fragment {

    private EntryModel viewEntry;

    //creates toolbar widget for Home Fragment
    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);

        }
    }

    //creates navigation menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.header_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    //creates view and populates it with Entry objects
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_single_entry, container, false);

        //defines a view model connected to Main Activity and retrieves the coordinates stored in it
        viewEntry = new ViewModelProvider(requireActivity()).get(EntryModel.class);
        String title = viewEntry.getEntry().getValue().title;
        DateFormat dtf = new SimpleDateFormat("dd-MM-yyyy");
        String date = dtf.format(viewEntry.getEntry().getValue().date);
        String message = viewEntry.getEntry().getValue().message;
        Double latitude = viewEntry.getEntry().getValue().latitude;
        Double longitude = viewEntry.getEntry().getValue().longitude;

        TextView titleView = view.findViewById(R.id.title);
       TextView dateTextView = view.findViewById(R.id.date);
        TextView messageTextView = view.findViewById(R.id.message);

        titleView.setText(title);
       dateTextView.setText(date);
       messageTextView.setText(message);

        Button edit = view.findViewById(R.id.edit);
        Button delete = view.findViewById(R.id.delete);

        // sets on Click listener on Register button to redirect user to Upgrade fragment
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                replaceFragment(new UpdateFragment());

            }
        });

        // sets on Click listener on Delete button to trigger a confirmation dialog and delete an entry, if permitted
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Delete entry");
                builder.setMessage("Are you sure you would like to delete this entry?");
                builder.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            //deletes an entry from database and redirects user to Home fragment
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://reise-mit-hund-app-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                                Query query =  mDatabase.child("entries").orderByChild("message").equalTo(message);
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot filteredEntries : dataSnapshot.getChildren()) {

                                            Double l = filteredEntries.getValue(Entry.class).latitude;
                                            if ((Objects.equals(l, latitude))) {

                                                mDatabase.child("entries").child(filteredEntries.getKey()).removeValue();
                                                replaceFragment(new HomeFragment());


                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }

                                });

                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


        setUpToolbar(view);

        return view;


    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
