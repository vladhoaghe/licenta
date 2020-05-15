package com.example.visualpost_it.util;

import com.example.visualpost_it.dtos.Place;

import java.util.Comparator;

public class PlaceComparator implements Comparator<Place> {
    @Override
    public int compare(Place o1, Place o2) {
        return Float.compare(o1.getDistance(), o2.getDistance());
    }
}
