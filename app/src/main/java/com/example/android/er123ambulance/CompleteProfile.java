package com.example.android.er123ambulance;

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

import com.example.android.er123ambulance.callbacks.CompleteProfileCallback;
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
    private Spinner mSpinner;
    private ArrayAdapter adapter;
    private boolean flag = true;


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

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.international_code));
        mSpinner.setAdapter(adapter);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData(mName);
                checkData(mPassword);
                checkData(mPlateChars);
                checkData(mPlateNums);
                if(!flag)
                {
                    return;
                }

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

                Log.e("TAGKEY","Complete Profile Click Listener");

                FirebaseHandler.completeDriverProfile(mDriver, FirebaseDatabase.getInstance(OfficeApp.officeApp(CompleteProfile.this)),
                        new CompleteProfileCallback() {
                    @Override
                    public void onProfileComplete(boolean isCompleted) {
                        Toast.makeText(CompleteProfile.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CompleteProfile.this,MainActivity.class));
                    }
                });
            }
        });

    }

    private void checkData(EditText editText)
    {
        if(editText.getId() == R.id.driver_name_complete_profile)
        {
            int len = editText.getText().length();
            if(len < 7)
            {
                editText.setError("Name must contain at least 7 letters");
                flag = false;
            }
            else
                flag = true;
        }
        else if(editText.getId() == R.id.password_complete_profile)
        {
            int len = editText.getText().length();
            String text = editText.getText().toString();
            boolean isUpper = false;
            boolean isLetter = false;
            for(char ch : text.toCharArray())
            {
                if(Character.isUpperCase(ch))
                {
                    isUpper = true;
                }
                if(Character.isLetter(ch))
                {
                    isLetter = true;
                }
            }
            if(!isUpper)
            {
                editText.setError("Must contain at least 1 Uppercase letter");
                flag = false;
            }
            else
            flag = true;
            if(!isLetter)
            {
                editText.setError("Must contain at least 1 letter");
                flag = false;
            }
            else
            flag = true;
            if(len < 9)
            {
                editText.setError("Must contain at least 9 digits");
                flag = false;
            }
            else
                flag = true;

        }
        else if(editText.getId() == R.id.plate_chars_complete_profile)
        {
            int len = editText.getText().length();
            if(len < 3 || len > 3)
            {
                editText.setError("Must be 3 characters long");
                flag = false;
            }
            else
                flag = true;
        }
        else if(editText.getId() == R.id.plate_num_complete_profile)
        {
            int len = editText.getText().length();
            if(len < 3 || len > 3)
            {
                editText.setError("Must be 3 characters long");
                flag = false;
            }
            else
                flag = true;
        }
    }
}
