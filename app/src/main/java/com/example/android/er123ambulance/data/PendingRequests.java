package com.example.android.er123ambulance.data;

/**
 * Created by Abshafi on 3/10/2017.
 */

public class PendingRequests {

    private String LatPosition;
    private String LongPosition;
    private String NumberOfInjuries;

    public void setLatPosition(String latPosition) {
        LatPosition = latPosition;
    }

    public void setLongPosition(String longPosition) {
        LongPosition = longPosition;
    }

    public void setNumberOfInjuries(String numberOfInjuries) {
        NumberOfInjuries = numberOfInjuries;
    }

    public String getLatPosition() {
        return LatPosition;
    }

    public String getLongPosition() {
        return LongPosition;
    }

    public String getNumberOfInjuries() {
        return NumberOfInjuries;
    }
}
