package com.example.rundumdog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.GeoPoint;


public class EntryListModel extends ViewModel {
    //defines a LiveData containing GeoPoint class
    private final MutableLiveData<GeoPoint> point = new MutableLiveData<>(new GeoPoint(0.0, 0.0));

    //returns LiveData
    public LiveData<GeoPoint> getPoint() {
        return point;
    }

    //replaces a GeoPoint in the LiveData with a new one with defined coordinates
    public LiveData<GeoPoint> setPoint(double lat, double lon) {
        GeoPoint g = new GeoPoint(lat, lon);
        point.setValue(g);
        return point;
    }




}
