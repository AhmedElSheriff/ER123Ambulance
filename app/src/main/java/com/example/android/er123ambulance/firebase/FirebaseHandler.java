package com.example.android.er123ambulance.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.android.er123ambulance.callbacks.CheckExistance;
import com.example.android.er123ambulance.callbacks.CompleteProfileCallback;
import com.example.android.er123ambulance.callbacks.GetDriverData;
import com.example.android.er123ambulance.callbacks.GetPatientData;
import com.example.android.er123ambulance.callbacks.LoginCallBack;
import com.example.android.er123ambulance.data.Driver;
import com.example.android.er123ambulance.data.PendingRequests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Abshafi on 1/27/2017.
 */

public class FirebaseHandler {

    public static void checkIfDriverExist(final String driverEmail, final CheckExistance listener)
    {
        Log.e("TAGKEY","Check If Driver Exist Handler");
        String email = driverEmail.substring(0,driverEmail.indexOf("@"));
        final String emailNode = email.replace(".","");
        Log.e("TAGKEY","Email Node" + emailNode);
        FirebaseHelper.getDatabase().getReference("allDrivers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void checkIfDriverExistOnceOpened(final String driverEmail, final CheckExistance listener)
    {
        String email = driverEmail.substring(0,driverEmail.indexOf("@"));
        final String emailNode = email.replace(".","");
        final boolean[] flag = {false};
        FirebaseHelper.getDatabase().getReference("allDrivers").child(emailNode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String temp = dataSnapshot.getKey();
                        if(temp.equals(emailNode))
                        {
                            listener.onSearchComplete(true);
                            flag[0] = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        if(!flag[0])
        {
            listener.onSearchComplete(false);
        }
    }


    public static void completeDriverProfile(final Driver driver, final CompleteProfileCallback listener)
    {
        Log.e("TAGKEY","Complete Profile Handler");
        String mEmail = driver.getDriverEmail().substring(0,driver.getDriverEmail().indexOf("@"));
        final String emailNode = mEmail.replace(".","");
        FirebaseHelper.getDatabase().getReference()/*.child("DriverDatabase")*/.child("allDrivers").child(emailNode).setValue(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Log.e("TAGKEY","Complete Profile Handler Task Successful");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updatePassword(driver.getDriverPassword());
                    listener.onProfileComplete(true);
                }
            }
        });
    }

    public static void getDriverInfo(String useremail, final GetDriverData listener)
    {

        String email =useremail.substring(0,useremail.indexOf("@"));
        final String emailNode = email.replace(".","");

        FirebaseHelper.getDatabase().getReference()/*.child("DriverDatabase")*/.child("allDrivers").child(emailNode).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void sendDriverLocationToBackOffice(Driver driver, String lat, String lng)
    {
        String email = driver.getDriverEmail();
        String mEmail = email.substring(0,email.indexOf("@"));
        final String emailNode = mEmail.replace(".","");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("latPosition",lat);
        hashMap.put("longPosition",lng);
        FirebaseHelper.getDatabase().getReference()/*.child("DriverDatabase")*/.child("allDrivers").child(emailNode).updateChildren(hashMap);
    }

    public static void getPatientLocation(String email, final GetPatientData listener)
    {

        String mEmail = email.substring(0,email.indexOf("@"));
        final String emailNode = mEmail.replace(".","");

        FirebaseHelper.getDatabase().getReference().child("Requests").child(emailNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PendingRequests request = dataSnapshot.getValue(PendingRequests.class);
                if(request != null) {
                    listener.getDriverData(request);
                    Log.e("GetPatient", request.getNumberOfInjuries());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void removePatient(String email)
    {
        String mEmail = email.substring(0,email.indexOf("@"));
        final String emailNode = mEmail.replace(".","");
        FirebaseHelper.getDatabase().getReference().child("Requests").child(emailNode).removeValue();
    }

    public static void checkIfPatientExist(String email, final CheckExistance listener)

    {
        String mEmail = email.substring(0,email.indexOf("@"));
        final String emailNode = mEmail.replace(".","");
        FirebaseHelper.getDatabase().getReference()./*child("DriverDatabase").*/child("Requests").child(emailNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PendingRequests requests = dataSnapshot.getValue(PendingRequests.class);
                if(requests != null)
                {
                    listener.onSearchComplete(true);
                }
                else
                    listener.onSearchComplete(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void setDriverAvailability(Driver driver, String trueOrFalse)
    {
        String email = driver.getDriverEmail();
        String mEmail = email.substring(0,email.indexOf("@"));
        final String emailNode = mEmail.replace(".","");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("driverAvailable",trueOrFalse);
        FirebaseHelper.getDatabase().getReference(/*"DriverDatabase"*/).child("allDrivers").child(emailNode).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    public static void signIn(FirebaseAuth auth, final Driver driver, final Context context, final LoginCallBack listener)
    {
        auth.signInWithEmailAndPassword(driver.getDriverEmail(),driver.getDriverPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(context,"Signed In",Toast.LENGTH_SHORT).show();
                    listener.onLoggedIn();
                }

                else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    listener.onFail();
                }
            }
        });
    }

    public static void updateLocation(HashMap<String,Object> hashMap, String useremail)
    {
        String email =useremail.substring(0,useremail.indexOf("@"));
        final String emailNode = email.replace(".","");
        FirebaseHelper.getDatabase().getReference("allDrivers").child(emailNode).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }

    public static void signOut(FirebaseAuth auth, final Context context)
    {
        auth.signOut();
    }

}
