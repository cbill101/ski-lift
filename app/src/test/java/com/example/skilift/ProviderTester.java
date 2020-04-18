package com.example.skilift;

import com.example.skilift.models.Provider;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ProviderTester {
    private static final double DELTA = 0.0001;
    private Provider provider;
    private Map<String, Object> data = new HashMap<>();

    @Before
    public void initialize() {
        Prov prv = new Prov("Anderson", "8011234567", "100.25",4.2456548, 2.3564897, "Park City Resort");
        data.put("name", prv.getName());
        data.put("phone", prv.getPhone());
        data.put("price", prv.getPrice());
        data.put("dest_latitude", prv.getDestLatitude());
        data.put("dest_longitude", prv.getDestLongitude());
        data.put("place_name", prv.getDestName());

        provider = new Provider(data);
    }

    @Test
    public void getName() { assertEquals("Anderson", provider.getName()); }

    @Test
    public void getPhone() { assertEquals("8011234567", provider.getPhone()); }

    @Test
    public void getPrice() { assertEquals("100.25", provider.getPrice()); }

    @Test
    public void getDestLatitude() { assertEquals(4.2456548, provider.getDest_latitude(), DELTA); }

    @Test
    public void getDestLongitude() { assertEquals(2.3564897, provider.getDest_longitude(), DELTA); }

    @Test
    public void getDestName() { assertEquals("Park City Resort", provider.getPlace_name()); }

    @Test
    public void getNameNotEqual() { assertNotEquals("Justin", provider.getName()); }

    @Test
    public void getPhoneNotEqual() { assertNotEquals("8015484567", provider.getPhone()); }

    @Test
    public void getPriceNotEqual() { assertNotEquals("485", provider.getPrice()); }

    @Test
    public void getDestLatitudeNotEqual() { assertNotEquals(48, provider.getDest_latitude(), DELTA); }

    @Test
    public void getDestLongitudeNotEqual() { assertNotEquals(897, provider.getDest_longitude(), DELTA); }

    @Test
    public void getDestNameNotEqual() { assertNotEquals("Ogden Resort", provider.getPlace_name()); }
}

class Prov {
    private String name;
    private String phone;
    private String price;
    private double destLatitude;
    private double destLongitude;
    private String destName;

    public Prov(String name, String phone, String price, double destLatitude, double destLongitude, String destName) {
        this.name = name;
        this.phone = phone;
        this.price = price;
        this.destLatitude = destLatitude;
        this.destLongitude = destLongitude;
        this.destName = destName;
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

    public double getDestLongitude() {
        return destLongitude;
    }

    public void setDestLongitude(double destLongitude) {
        this.destLongitude = destLongitude;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}