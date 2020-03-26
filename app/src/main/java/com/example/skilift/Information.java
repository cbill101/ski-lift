package com.example.skilift;

public class Information {
    private String name;
    private String phone;
    private String price;

    public Information(){
    }

    public Information(String name, String phone, String price){
        this.name = name;
        this.phone = phone;
        this.price = price;
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
}
