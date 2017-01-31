package com.example.android.er123ambulance.utilities;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.util.List;

/**
 * Created by Abshafi on 1/27/2017.
 */

public class OfficeApp {


    public static FirebaseApp officeApp(Context context)
    {
        FirebaseApp officeApp = null;

        boolean hasBeenInitialized = false;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(context);
        for(FirebaseApp app:firebaseApps)
        {
            if(app.getName().equals("Office App"))
            {
                hasBeenInitialized = true;
                officeApp = FirebaseApp.getInstance();
            }
        }

        if(!hasBeenInitialized) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId("er-123-office")
                    .setDatabaseUrl("https://er-123-office.firebaseio.com")
                    .build();
             officeApp = FirebaseApp.initializeApp(context, options, "Office App");

            return officeApp;
        }
        else
            return officeApp;
    }
}
