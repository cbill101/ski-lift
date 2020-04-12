package com.example.skilift.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.Map;

public class Provider implements Parcelable {
    private String uID;
    private String name;
    private String phone;
    private String price;
    private double dest_latitude;
    private double dest_longitude;
    private String place_name;
    private boolean checked;

    public Provider(){
        this.checked = false;
    }

    public Provider(Map<String, Object> dbData) {
        this.name = (String) dbData.get("name");
        this.phone = (String) dbData.get("phone");
        this.price = (String) dbData.get("price");
        this.dest_latitude = (double) dbData.get("dest_latitude");
        this.dest_longitude = (double) dbData.get("dest_longitude");
        this.place_name = (String) dbData.get("place_name");
        this.checked = false;
    }

    protected Provider(Parcel in) {
        uID = in.readString();
        name = in.readString();
        phone = in.readString();
        price = in.readString();
        dest_latitude = in.readDouble();
        dest_longitude = in.readDouble();
        place_name = in.readString();
        checked = in.readByte() != 0;
    }

    public static final Creator<Provider> CREATOR = new Creator<Provider>() {
        @Override
        public Provider createFromParcel(Parcel in) {
            return new Provider(in);
        }

        @Override
        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getDest_latitude() {
        return dest_latitude;
    }

    public void setDest_latitude(double dest_latitude) {
        this.dest_latitude = dest_latitude;
    }

    public double getDest_longitude() {
        return dest_longitude;
    }

    public void setDest_longitude(double dest_longitude) {
        this.dest_longitude = dest_longitude;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getUID() { return uID; };

    public void setUID(String uID) { this.uID = uID; }

    @Exclude
    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name.substring(0, name.indexOf(' '))).append("\r\n");
        sb.append("Price: ").append(price).append("\r\n");
        sb.append("Destination: ").append(place_name);

        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uID);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(price);
        dest.writeDouble(dest_latitude);
        dest.writeDouble(dest_longitude);
        dest.writeString(place_name);
    }
}
