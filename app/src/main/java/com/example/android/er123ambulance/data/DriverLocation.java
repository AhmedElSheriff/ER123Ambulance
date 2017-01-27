package com.example.android.er123ambulance.data;

/**
 * Created by Abshafi on 1/27/2017.
 */

public class DriverLocation {

    private String LatPosition;
    private String LongPosition;

    public void setLatPosition(String latPosition) {
        LatPosition = latPosition;
    }

    public void setLongPosition(String longPosition) {
        LongPosition = longPosition;
    }

    public String getLatPosition() {
        return LatPosition;
    }

    public String getLongPosition() {
        return LongPosition;
    }
}
