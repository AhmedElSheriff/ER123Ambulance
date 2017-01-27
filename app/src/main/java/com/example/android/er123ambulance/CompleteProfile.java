package com.example.android.er123ambulance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.er123ambulance.data.Driver;
import com.example.android.er123ambulance.firebase.FirebaseHandler;
import com.example.android.er123ambulance.utilities.OfficeApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class CompleteProfile extends AppCompatActivity {

    private EditText mName;
    private EditText mPassword;
    private EditText mPlateChars;
    private EditText mPlateNums;
    private EditText mPhoneNum;
    private Button mButton;
    private Driver mDriver;
    private FirebaseDatabase officeDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);


        mDriver = new Driver();
        mName = (EditText) findViewById(R.id.driver_name_complete_profile);
        mPassword = (EditText) findViewById(R.id.password_complete_profile);
        mPlateChars = (EditText) findViewById(R.id.plate_chars_complete_profile);
        mPlateNums = (EditText) findViewById(R.id.plate_num_complete_profile);
        mPhoneNum = (EditText) findViewById(R.id.driver_phone_number_complete_profile);
        mButton = (Button) findViewById(R.id.complete_profile_btn);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDriver.setDriverEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                mDriver.setDriverPassword(mPassword.getText().toString());
                mDriver.setDriverName(mName.getText().toString());
                mDriver.setPlateChars(mPlateChars.getText().toString());
                mDriver.setPlateNums(mPlateNums.getText().toString());

                FirebaseHandler.completeDriverProfile(mDriver, FirebaseDatabase.getInstance(OfficeApp.officeApp(CompleteProfile.this))
                        ,new com.example.android.er123ambulance.callbacks.CompleteProfile() {
                    @Override
                    public void onProfileComplete() {
                        Toast.makeText(CompleteProfile.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
