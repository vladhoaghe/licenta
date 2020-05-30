package com.example.visualpost_it.dtos;

import android.graphics.Bitmap;

import com.example.visualpost_it.util.BitmapDataObject;

import java.io.Serializable;

public class PlaceAdvertised implements Serializable {

    private BitmapDataObject imageBitmap;
    private String filenameMessage;
    private String placeName;
    private String placeType;
    private String description;

    public PlaceAdvertised(BitmapDataObject imageBitmap, String placeName, String placeType, String description) {
        this.imageBitmap = imageBitmap;
        this.placeName = placeName;
        this.placeType = placeType;
        this.description = description;
    }

    public String getFilenameMessage() {
        return filenameMessage;
    }

    public void setFilenameMessage(String filenameMessage) {
        this.filenameMessage = filenameMessage;
    }


    public BitmapDataObject getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(BitmapDataObject imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "PlaceAdvertised{" +
//                "imageBitmap=" + imageBitmap +
                ", filenameMessage='" + filenameMessage + '\'' +
                ", placeName='" + placeName + '\'' +
                ", placeType='" + placeType + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
