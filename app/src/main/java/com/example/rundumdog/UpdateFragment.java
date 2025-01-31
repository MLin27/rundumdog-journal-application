package com.example.rundumdog;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class UpdateFragment extends Fragment{
    private EntryListModel viewModel;

    private EntryModel viewEntry;

    LocalDate ldt;
    private DatabaseReference mDatabase;

    //generates a navigation toolbar for the Entry Fragment screen
    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);

        }
    }

    //generates navigation menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.header_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    //creates Entry Fragment view
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //defines required view elements
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        Button btn = view.findViewById(R.id.save);
        EditText title = view.findViewById(R.id.editTitle);
        TextView date = view.findViewById(R.id.editTextDate);
        EditText message = view.findViewById(R.id.editText);
        Calendar c = Calendar.getInstance();


        //defines a view model connected to Main Activity and retrieves Entry values stored in it
        viewEntry = new ViewModelProvider(requireActivity()).get(EntryModel.class);
        String savedTitle = viewEntry.getEntry().getValue().title;
        DateFormat dtf = new SimpleDateFormat("dd-MM-yyyy");
        String savedDate = dtf.format(viewEntry.getEntry().getValue().date);
        String savedMessage = viewEntry.getEntry().getValue().message;
        Double latitude = viewEntry.getEntry().getValue().latitude;
        Double longitude = viewEntry.getEntry().getValue().longitude;

        title.setText(savedTitle);
        date.setText(savedDate);
        message.setText(savedMessage);




        //sets on Click listener to add a new entry to the database
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    //adds a new Entry to a database and redirects back to Home Fragment
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(true);
                    builder.setTitle("Update entry");
                    builder.setMessage("Are you sure you would like to update this entry?");
                    builder.setPositiveButton("Update",
                            new DialogInterface.OnClickListener() {
                                //redirects user to Entry creation Fragment upon agreement and passes the coordinates
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://reise-mit-hund-app-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                                    Query query =  mDatabase.child("entries").orderByChild("message").equalTo(savedMessage);
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot filteredEntries : dataSnapshot.getChildren()) {

                                                Log.d("test", "Found entry" + dataSnapshot.toString());

                                                Double l = filteredEntries.getValue(Entry.class).latitude;
                                                if ((Objects.equals(l, latitude))) {
                                                    Log.d("test", "Found this entry" + filteredEntries.getValue(Entry.class).title);
                                                    mDatabase.child("entries").child(filteredEntries.getKey()).child("title").setValue(title.getText().toString());
                                                    mDatabase.child("entries").child(filteredEntries.getKey()).child("message").setValue(message.getText().toString());
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

            }
        });


        setUpToolbar(view);



        return view;

    }

    private void saveEntry(String title, Date date, String message, Double latitude, Double longitude) {
        Entry entry = new Entry(title, date, message, latitude, longitude);
        mDatabase = FirebaseDatabase.getInstance("https://reise-mit-hund-app-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mDatabase.child("entries").push().setValue(entry);


    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

}
