package com.example.rundumdog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment{
    RecyclerView recyclerView;
    EntryLoop adapter;

    ArrayList<Entry> list;
    private EntryListModel viewModel;
    private EntryModel viewEntry;

    EntryLoop.OnItemClickListener mListener;

    public int markerValue;



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

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        viewEntry = new ViewModelProvider(requireActivity()).get(EntryModel.class);

        setUpToolbar(view);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://reise-mit-hund-app-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        // sets up the RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
       
        mDatabase.child("entries").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                list = new ArrayList<Entry>();
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());

                } else {

                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        Entry comment = ds.getValue(Entry.class);

                        list.add(comment);

                        adapter = new EntryLoop(list);

                        recyclerView.setAdapter(adapter);

                    }

                    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(1, ItemTouchHelper.RIGHT) {
                        @Override
                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                            return false;
                        }

                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                          Entry deletedMarker = list.get(viewHolder.getAdapterPosition());

                          if (deletedMarker != null) {

                              Log.d("entry", deletedMarker.latitude.toString());
                              String t = deletedMarker.title;
                              String m = deletedMarker.message;
                              Date d = deletedMarker.date;
                              Double lat = deletedMarker.latitude;
                              Double lon = deletedMarker.longitude;
                              viewEntry.setEntry(t, d, m, lat, lon);
                              replaceFragment(new SingleEntryActivity());
                          }


                      }

                        @Override
                        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                            try {

                                Bitmap icon;
                                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                                    View itemView = viewHolder.itemView;
                                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                                    float width = height / 5;
                                    viewHolder.itemView.setTranslationX(dX / 5);



                                } else {
                                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                        // adding to view
                    }).attachToRecyclerView(recyclerView);

                }
            }
        });

        recyclerView.setOnClickListener(v -> {

            mListener.onItemClick(2);

        });

        int largePadding = 20;
        int smallPadding = 20;
        recyclerView.addItemDecoration(new EntryCardDesign(largePadding, smallPadding));


        return view;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }


}
