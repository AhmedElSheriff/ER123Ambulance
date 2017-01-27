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
