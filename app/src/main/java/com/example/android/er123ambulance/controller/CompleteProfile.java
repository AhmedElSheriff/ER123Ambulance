package com.example.android.er123ambulance.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.er123ambulance.R;
import com.example.android.er123ambulance.callbacks.CompleteProfileCallback;
import com.example.android.er123ambulance.data.Driver;
import com.example.android.er123ambulance.firebase.FirebaseHandler;
import com.google.firebase.auth.FirebaseAuth;

public class CompleteProfile extends AppCompatActivity {

    private EditText mName;
    private EditText mPassword;
    private EditText mPlateChars;
    private EditText mPlateNums;
    private EditText mPhoneNum;
    private Button mButton;
    private Driver mDriver;
    private Spinner mSpinner;
    private ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);


        mDriver = new Driver();
        mSpinner = (Spinner) findViewById(R.id.international_code_spinner);
        mName = (EditText) findViewById(R.id.driver_name_complete_profile);
        mPassword = (EditText) findViewById(R.id.password_complete_profile);
        mPlateChars = (EditText) findViewById(R.id.plate_chars_complete_profile);
        mPlateNums = (EditText) findViewById(R.id.plate_num_complete_profile);
        mPhoneNum = (EditText) findViewById(R.id.driver_phone_number_complete_profile);
        mButton = (Button) findViewById(R.id.complete_profile_btn);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.international_code));
        mSpinner.setAdapter(adapter);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                mDriver.setDriverEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                mDriver.setDriverPassword(mPassword.getText().toString());
                mDriver.setDriverName(mName.getText().toString());
                mDriver.setPlateChars(mPlateChars.getText().toString());
                mDriver.setPlateNums(mPlateNums.getText().toString());
                mDriver.setPhoneNumber(mPhoneNum.getText().toString());
                mDriver.setLatPosition("null");
                mDriver.setLongPosition("null");
                mDriver.setDistance(0f);
                mDriver.setDriverAvailable("false");

                Log.e("TAGKEY", "Complete Profile Click Listener");

                FirebaseHandler.completeDriverProfile(mDriver, new CompleteProfileCallback() {
                    @Override
                    public void onProfileComplete(boolean isCompleted) {
                        Toast.makeText(CompleteProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CompleteProfile.this, MainActivity.class));
                    }
                });
            }
        });

    }

}
