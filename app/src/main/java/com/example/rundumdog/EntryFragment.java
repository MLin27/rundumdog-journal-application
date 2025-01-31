package com.example.rundumdog;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;


public class EntryFragment extends Fragment {
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
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        Button btn = view.findViewById(R.id.save);
        EditText title = view.findViewById(R.id.editTitle);
        EditText date = view.findViewById(R.id.editTextDate);
        EditText message = view.findViewById(R.id.editText);
        Calendar c = Calendar.getInstance();

        //defines a view model connected to Main Activity and retrieves the coordinates stored in it
        viewModel = new ViewModelProvider(requireActivity()).get(EntryListModel.class);
        Double lat = viewModel.getPoint().getValue().getLatitude();
        Double lon = viewModel.getPoint().getValue().getLongitude();

        //defines a view model connected to Main Activity and retrieves the coordinates stored in it
        viewEntry = new ViewModelProvider(requireActivity()).get(EntryModel.class);
        String savedTitle = viewEntry.getEntry().getValue().title;
        DateFormat dtf = new SimpleDateFormat("dd-MM-yyyy");
        String savedDate = dtf.format(viewEntry.getEntry().getValue().date);
        String savedMessage = viewEntry.getEntry().getValue().message;
        Double latitude = viewEntry.getEntry().getValue().latitude;
        Double longitude = viewEntry.getEntry().getValue().longitude;







        //sets on Click listener to add a new entry to the database
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    //formats variable of a local date class  to a date class
                    Date dt = Date.from(ldt.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    //adds a new Entry to a database and redirects back to Home Fragment
                    saveEntry(title.getText().toString(), dt, message.getText().toString(), lat, lon);
                    Toast.makeText(getContext(),"Entry was successfully added",Toast.LENGTH_LONG).show();
                    replaceFragment(new HomeFragment());

                }

            }
        });

        //sets on Click listener, that generates a date picker
        date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // initiates the date picker and passes the results to the Edit Test filed
                       getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int yearYear,
                                                  int monthOfYear, int dayOfMonth) {
                                //passes date on a text view
                                date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + yearYear);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    ldt = LocalDate.of(yearYear, monthOfYear + 1, dayOfMonth);
                                }

                            }
                        },
                        year, month, day);
                datePickerDialog.show();

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
