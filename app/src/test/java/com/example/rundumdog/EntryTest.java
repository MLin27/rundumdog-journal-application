package com.example.rundumdog;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Date;

public class EntryTest {

    Date testDate = new Date(124, 12,12);
    private static final double DELTA = 1e-15;
    private Entry entryTest = new Entry("Bern", testDate, "It was a good trip", 46.947456, 7.451123);

    @Test
    public void entryTitleIsValid() {
        assertEquals("Bern", entryTest.title);
    }

    @Test
    public void entryDateIsValid() {
        assertEquals(testDate, entryTest.date);
    }

    @Test
    public void entryMessageIsValid() {
        assertEquals("It was a good trip", entryTest.message);
    }

    @Test
    public void entryLatitudeIsValid() {
        assertEquals(46.947456, entryTest.latitude, DELTA);
    }

    @Test
    public void entryLongitudeIsValid() {
        assertEquals(46.947456, entryTest.longitude, DELTA);
    }



}