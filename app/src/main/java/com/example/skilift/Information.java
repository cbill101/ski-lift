package com.example.skilift;

import java.util.Map;

public class Information {
    private String name;
    private String phone;
    private String price;
    private double latitude;
    private double longitude;
    private String placeName;

    public Information(){

    }

    public Information(Map<String, Object> dbData) {
        this.name = (String) dbData.get("name");
        this.phone = (String) dbData.get("phone");
        this.price = (String) dbData.get("price");
        this.latitude = (double) dbData.get("dest_latitude");
        this.longitude = (double) dbData.get("dest_longitude");
        this.placeName = (String) dbData.get("place_name");
    }

    public Information(String name, String phone, double latitude, double longitude, String placeName, String price){
        this.name = name;
        this.phone = phone;
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
