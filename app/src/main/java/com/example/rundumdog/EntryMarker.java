package com.example.rundumdog;



import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;


public class EntryMarker extends OverlayItem {
    private static final String TAG = EntryMarker.class.getSimpleName();
    private double latitude;
    private double longitude;
    private String aTitle;
    private String aSnippet;



    //initialises a map Marker for Entry class
    public EntryMarker(String aTitle, String aSnippet, GeoPoint aGeoPoint, double latitude, double longitude) {
        super(aTitle, aSnippet, aGeoPoint);
        this.aTitle = aTitle;
        this.aSnippet = aSnippet;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    }





