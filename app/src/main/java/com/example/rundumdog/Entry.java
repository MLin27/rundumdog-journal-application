package com.example.rundumdog;


import java.util.Date;


public class Entry {

    private static final String TAG = Entry.class.getSimpleName();

    public String title;

   public Date date;

    public String message;

    public Double latitude;

    public Double longitude;

    private Entry(){

    }

//initialises an Entry class using title, date, entry text and location coordinates
    public Entry(
            String title, Date date, String message, Double latitude, Double longitude) {
        this.title = title;

        this.date = date;

        this.message = message;

        this.latitude = latitude;
        this.longitude = longitude;

    }

}
