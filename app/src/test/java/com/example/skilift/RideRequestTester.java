package com.example.skilift;

import com.example.skilift.models.RideRequest;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class RideRequestTester {
    private static final double DELTA = 0.0001;
    private RideRequest rideRequest;
    private Map<String, Object> data = new HashMap<>();

    @Before
    public void initialize() {
        RQ rq = new RQ("Anderson", "8011234567", 4.2456548, 2.3564897, 3.4568987, 45.415648, "Park City Resort", "50.25");
        data.put("name", rq.getName());
        data.put("phone", rq.getPhone());
        data.put("dest_latitude", rq.getDestLatitude());
        data.put("dest_longitude", rq.getDestLongitude());
        data.put("pickup_latitude", rq.getPickupLatitude());
        data.put("pickup_longitude", rq.getPickupLongitude());
        data.put("place_name", rq.getDestName());
        data.put("price", rq.getPrice());

        rideRequest = new RideRequest(data);
    }

    @Test
    public void getName() { assertEquals("Anderson", rideRequest.getName()); }

    @Test
    public void getPhone() { assertEquals("8011234567", rideRequest.getPhone()); }

    @Test
    public void getDestLatitude() { assertEquals(4.2456548, rideRequest.getDestLatitude(), DELTA); }

    @Test
    public void getDestLongitude() { assertEquals(2.3564897, rideRequest.getDestLongitude(), DELTA); }

    @Test
    public void getPickupLatitude() { assertEquals(3.4568987, rideRequest.getPickupLatitude(), DELTA); }

    @Test
    public void getPickupLongitude() { assertEquals(45.415648, rideRequest.getPickupLongitude(), DELTA); }

    @Test
    public void getDestName() { assertEquals("Park City Resort", rideRequest.getDestName()); }

    @Test
    public void getPrice() { assertEquals("50.25", rideRequest.getPrice()); }

    @Test
    public void getNameNotEquals() { assertNotEquals("Jan", rideRequest.getName()); }

    @Test
    public void getPhoneNotEquals() { assertNotEquals("805128567", rideRequest.getPhone()); }

    @Test
    public void getDestLatitudeNotEquals() { assertNotEquals(6548, rideRequest.getDestLatitude(), DELTA); }

    @Test
    public void getDestLongitudeNotEquals() { assertNotEquals(4897, rideRequest.getDestLongitude(), DELTA); }

    @Test
    public void getPickupLatitudeNotEquals() { assertNotEquals(8987, rideRequest.getPickupLatitude(), DELTA); }

    @Test
    public void getPickupLongitudeNotEquals() { assertNotEquals(15648, rideRequest.getPickupLongitude(), DELTA); }

    @Test
    public void getDestNameNotEquals() { assertNotEquals("Ogden Resort", rideRequest.getDestName()); }

    @Test
    public void getPriceNotEquals() { assertNotEquals("51.25", rideRequest.getPrice()); }
}

class RQ {
    private String name;
    private String phone;
    private double destLatitude;
    private double destLongitude;
    private double pickupLatitude;
    private double pickupLongitude;
    private String destName;
    private String price;
    private boolean checked;

    public RQ(String name, String phone, double destLatitude, double destLongitude, double pickupLatitude, double pickupLongitude, String destName, String price) {
        this.name = name;
        this.phone = phone;
        this.destLatitude = destLatitude;
        this.destLongitude = destLongitude;
        this.pickupLatitude = pickupLatitude;
        this.pickupLongitude = pickupLongitude;
        this.destName = destName;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
