package com.example.android.er123ambulance.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.er123ambulance.callbacks.CompleteProfileCallback;
import com.example.android.er123ambulance.callbacks.DriverExistance;
import com.example.android.er123ambulance.callbacks.GetDriverData;
import com.example.android.er123ambulance.data.Driver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Abshafi on 1/27/2017.
 */

public class FirebaseHandler {

    public static void checkIfDriverExist(final String driverEmail, final DriverExistance listener)
    {
        Log.e("TAGKEY","Check If Driver Exist Handler");
        String email = driverEmail.substring(0,driverEmail.indexOf("@"));
        final String emailNode = email.replace(".","");
        FirebaseHelper.getDatabase().getReference("allDrivers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child:dataSnapshot.getChildren())
                {
                    Log.e("TAGKEY","Check If Driver Exist On Data Change Loop");
                    String temp = child.getKey();
                    if(temp.equals(emailNode))
                    {
                        Log.e("TAGKEY","Check If Driver Exist Key Equals Email Node");
                        listener.onSearchComplete(true);
                        return;
                    }
                }
                Log.e("TAGKEY","Check If Driver Exist On Search Complete False");
                listener.onSearchComplete(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void completeDriverProfile(final Driver driver, final FirebaseDatabase officeDatabase , final CompleteProfileCallback listener)
    {
        Log.e("TAGKEY","Complete Profile Handler");
        String mEmail = driver.getDriverEmail().substring(0,driver.getDriverEmail().indexOf("@"));
        final String emailNode = mEmail.replace(".","");
        FirebaseHelper.getDatabase().getReference("allDrivers").child(emailNode).setValue(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Log.e("TAGKEY","Complete Profile Handler Task Successful");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updatePassword(driver.getDriverPassword());
                    officeDatabase.getReference("allDrivers").child(emailNode).setValue(driver);
                    listener.onProfileComplete(true);
                }
            }
        });
    }

    public static void getDriverInfo(String useremail, final GetDriverData listener)
    {

        String email =useremail.substring(0,useremail.indexOf("@"));
        final String emailNode = email.replace(".","");

        FirebaseHelper.getDatabase().getReference("allDrivers").child(emailNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Driver driver = dataSnapshot.getValue(Driver.class);
                listener.getDriverData(driver);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void sendDriverLocationToBackOffice(Driver driver, String email, FirebaseDatabase officeDatabase)
    {
        String mEmail = email.substring(0,email.indexOf("@"));
        final String emailNode = mEmail.replace(".","");
        officeDatabase.getReference("allDrivers").child(emailNode).setValue(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Log.e("Office","Location Sent");
                }
            }
        });
    }

}
