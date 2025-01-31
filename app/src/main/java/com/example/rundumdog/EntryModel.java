package com.example.rundumdog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class EntryModel extends ViewModel {

    private Entry g;



        private final MutableLiveData<Entry> entry = new MutableLiveData<>(new Entry("", new Date(), "", 0.0, 0.0));

        //returns LiveData
        public LiveData<Entry> getEntry() {
            return entry;
        }

        //replaces a GeoPoint in the LiveData with a new one with defined coordinates
        public LiveData<Entry> setEntry(String title, Date date, String message, double latitude, double longitude) {
            g = new Entry(title, date, message, latitude, longitude);
            entry.setValue(g);
            return entry;
        }

    }
