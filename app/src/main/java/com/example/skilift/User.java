package com.example.skilift;

import java.util.Map;

public class User {
    private String Name;
    private String Phone;
    private String Email;

    public User(){
    }

    public User(String name, String phone, String email){
        this.Name = name;
        this.Phone = phone;
        this.Email = email;
    }

    public User(Map<String, Object> document) {
        this.Name = (String) document.get("Name");
        this.Phone = (String) document.get("Phone");
        this.Email = (String) document.get("Email");
    }

    public String getName() {
        return Name;
    }

    public String getFirstName() {
        return Name.substring(0, Name.indexOf(' '));
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        this.Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }
}
