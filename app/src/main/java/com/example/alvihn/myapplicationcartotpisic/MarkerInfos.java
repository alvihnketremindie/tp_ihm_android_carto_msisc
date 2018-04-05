package com.example.alvihn.myapplicationcartotpisic;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class MarkerInfos implements Parcelable {

    private String title;
    private LatLng position;
    private Bitmap image;

    // Constructor
    public MarkerInfos(String title, LatLng position, Bitmap image) {
        this.title = title;
        this.position = position;
        this.image = image;
    }

    public MarkerInfos(String title, LatLng position) {
        this.title = title;
        this.position = position;
    }

    // Getter and setter methods
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    protected MarkerInfos(Parcel in) {
        title = in.readString();
        position = in.readParcelable(LatLng.class.getClassLoader());
        image = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public String toString() {
        String bitmapText;
        if (this.image != null) bitmapText = "Bitmap OK";
        else bitmapText = "NoBitmap";
        return "Latitude : " + position.latitude
                + "\r\n"
                + "Longitude : " + position.longitude
                + "\r\n"
                + "Title : " + title
                + "\r\n"
                + bitmapText;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeParcelable(position, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeParcelable(image, PARCELABLE_WRITE_RETURN_VALUE);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MarkerInfos> CREATOR = new Creator<MarkerInfos>() {
        @Override
        public MarkerInfos createFromParcel(Parcel in) {
            return new MarkerInfos(in);
        }

        @Override
        public MarkerInfos[] newArray(int size) {
            return new MarkerInfos[size];
        }
    };
}
