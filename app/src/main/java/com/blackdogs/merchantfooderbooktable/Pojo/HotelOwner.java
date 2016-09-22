package com.blackdogs.merchantfooderbooktable.Pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BlackDogs on 21-09-2016.
 */
@IgnoreExtraProperties
public class HotelOwner {

    String hotelImgUrl,hotelLocation,hotelName;
    double hotelRating;

    public HotelOwner() {
    }

    public HotelOwner(String hotelImgUrl, String hotelLocation, String hotelName, double hotelRating) {
        this.hotelImgUrl = hotelImgUrl;
        this.hotelLocation = hotelLocation;
        this.hotelRating = hotelRating;
        this.hotelName = hotelName;
    }

    public String getHotelImgUrl() {
        return hotelImgUrl;
    }

    public String getHotelLocation() {
        return hotelLocation;
    }

    public double getRating() {
        return hotelRating;
    }
    public String getHotelName() {
        return hotelName;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("hotelImgUrl", hotelImgUrl);
        result.put("hotelLocation", hotelLocation);
        result.put("hotelRating", hotelRating);
        result.put("hotelName",hotelName);
        return result;
    }



}
