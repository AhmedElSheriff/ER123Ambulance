package com.example.android.er123ambulance.data;

/**
 * Created by Abshafi on 1/27/2017.
 */

public class Driver {

    private String driverEmail;
    private String driverPassword;
    private String driverName;
    private String plateChars;
    private String plateNums;
    private String phoneNumber;
    private Float distance;

    private String LatPosition;
    private String LongPosition;
    private String driverAvailable;

    public void setDriverAvailable(String driverAvailable) {
        this.driverAvailable = driverAvailable;
    }

    public String getDriverAvailable() {
        return driverAvailable;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Float getDistance() {
        return distance;
    }

    public String getLatPosition() {
        return LatPosition;
    }

    public String getLongPosition() {
        return LongPosition;
    }

    public void setLatPosition(String latPosition) {

        LatPosition = latPosition;
    }

    public void setLongPosition(String longPosition) {
        LongPosition = longPosition;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public void setDriverPassword(String driverPassword) {
        this.driverPassword = driverPassword;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public void setPlateChars(String plateChars) {
        this.plateChars = plateChars;
    }

    public void setPlateNums(String plateNums) {
        this.plateNums = plateNums;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public String getDriverPassword() {
        return driverPassword;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getPlateChars() {
        return plateChars;
    }

    public String getPlateNums() {
        return plateNums;
    }
}
