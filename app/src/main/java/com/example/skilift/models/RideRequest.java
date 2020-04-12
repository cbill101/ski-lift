package com.example.skilift.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class RideRequest implements Parcelable {
    private String uID;
    private String name;
    private String phone;
    private double destLatitude;
    private double destLongitude;
    private double pickupLatitude;
    private double pickupLongitude;
    private String price;
    private String destName;
    private boolean checked;

    public RideRequest(){
        this.checked = false;
    }

    public RideRequest(Map<String, Object> dbData) {
        this.name = (String) dbData.get("name");
        this.phone = (String) dbData.get("phone");
        this.destLatitude = (double) dbData.get("dest_latitude");
        this.destLongitude = (double) dbData.get("dest_longitude");
        this.pickupLatitude = (double) dbData.get("pickup_latitude");
        this.pickupLongitude = (double) dbData.get("pickup_longitude");
        this.destName = (String) dbData.get("place_name");
        this.price = (String) dbData.get("price");
        this.checked = false;
    }

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

    public double getDestLatitude() {
        return destLatitude;
    }

    public void setDestLatitude(double destLatitude) {
        this.destLatitude = destLatitude;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getDestLongitude() {
        return destLongitude;
    }

    public void setDestLongitude(double destLongitude) {
        this.destLongitude = destLongitude;
    }

    public double getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public double getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public String getUID() { return uID; };

    public void setUID(String uID) { this.uID = uID; }

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
        sb.append("Destination: ").append(destName).append("\r\n");
        sb.append("Price: ").append(price);

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
        dest.writeDouble(destLatitude);
        dest.writeDouble(destLongitude);
        dest.writeDouble(pickupLatitude);
        dest.writeDouble(pickupLongitude);
        dest.writeString(price);
        dest.writeString(destName);
    }
}
