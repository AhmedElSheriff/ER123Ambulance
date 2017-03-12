package com.example.android.er123ambulance.utilities;

/**
 * Created by Abshafi on 3/10/2017.
 */

public class Distance {

    public static float calculateDistance(double driverlat, double driverln, double patientlat, double patientlng) {
        double latDistance = Math.toRadians(driverlat - patientlat);
        double lngDistance = Math.toRadians(driverln - patientlng);
        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(driverlat))) *
                        (Math.cos(Math.toRadians(patientlat))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return  (Math.round(7471 * c));
    }
}
