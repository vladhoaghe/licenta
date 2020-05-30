package com.example.visualpost_it.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class BitmapDataObject implements Serializable {

    private Bitmap currentImage;

    public Bitmap getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(Bitmap currentImage) {
        this.currentImage = currentImage;
    }

    public BitmapDataObject(Bitmap bitmap)
    {
        currentImage = bitmap;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        currentImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        out.writeInt(byteArray.length);
        out.write(byteArray);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        int bufferLength = in.readInt();
        byte[] byteArray = new byte[bufferLength];
        int pos = 0;
        do {
            int read = in.read(byteArray, pos, bufferLength - pos);

            if (read != -1) {
                pos += read;
            } else {
                break;
            }
        } while (pos < bufferLength);
        currentImage = BitmapFactory.decodeByteArray(byteArray, 0, bufferLength);
    }

}