package com.blackdogs.merchantfooderbooktable.Pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BlackDogs on 18-09-2016.
 */
@IgnoreExtraProperties
public class HotelList {

    String hotelName, hotelImageUrl, hotelLocation;
    double hotelRating, latitude, longitude;

    public HotelList() {
    }

    public HotelList(String hotelName, String hotelImageUrl, String hotelLocation, double hotelRating, double latitude, double longitude) {
        this.hotelName = hotelName;
        this.hotelImageUrl = hotelImageUrl;
        this.hotelLocation = hotelLocation;
        this.hotelRating = hotelRating;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getHotelName() {
        return hotelName;
    }

    public String getHotelImageUrl() {
        return hotelImageUrl;
    }

    public String getHotelLocation() {
        return hotelLocation;
    }

    public double getHotelRating() {
        return hotelRating;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("hotelName", hotelName);
        result.put("hotelImageUrl", hotelImageUrl);
        result.put("hotelLocation", hotelLocation);
        result.put("hotelRating", hotelRating);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }

}
