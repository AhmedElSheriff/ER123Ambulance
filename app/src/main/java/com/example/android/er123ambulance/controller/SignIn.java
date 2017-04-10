package com.example.android.er123ambulance.controller;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dd.CircularProgressButton;
import com.example.android.er123ambulance.R;
import com.example.android.er123ambulance.callbacks.CheckExistance;
import com.example.android.er123ambulance.callbacks.LoginCallBack;
import com.example.android.er123ambulance.data.Driver;
import com.example.android.er123ambulance.firebase.FirebaseHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class SignIn extends AppCompatActivity implements Validator.ValidationListener{

    @NotEmpty
    @Email
    private EditText mEmail;
    @NotEmpty
    @Password
    private EditText mPassword;
    private FirebaseAuth mAuth;
    private Validator validator;
    private Driver driver;
    private CircularProgressButton mButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
//            FirebaseHandler.checkIfDriverExistOnceOpened(FirebaseAuth.getInstance().getCurrentUser().getEmail(), new CheckExistance() {
//                @Override
//                public void onSearchComplete(boolean isFound) {
//                    if(isFound)
//                    {
                        startActivity(new Intent(SignIn.this,MainActivity.class));
//                    }
//                    else
//                    {
//                        FirebaseHandler.signOut(FirebaseAuth.getInstance(),SignIn.this);
//                        startActivity(new Intent(SignIn.this,SignIn.class));
//                        SignIn.this.finish();
//                    }
//                }
//            });
        }


        setContentView(R.layout.activity_sign_in);

        driver = new Driver();
        validator = new Validator(this);
        validator.setValidationListener(this);
        mAuth = FirebaseAuth.getInstance();
        mEmail = (EditText) findViewById(R.id.email_login);
        mPassword = (EditText) findViewById(R.id.password_login);

        mButton = (CircularProgressButton) findViewById(R.id.circleloginbtn);
        mButton.setIndeterminateProgressMode(true);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButton.setProgress(0);
                setUpUser();
                if (mEmail.getText().toString().trim().length() > 0 && mPassword.getText().toString().trim().length() > 0) {
                    mButton.setProgress(50);
                    FirebaseHandler.signIn(mAuth, driver, SignIn.this, new LoginCallBack() {
                        @Override
                        public void onLoggedIn() {
                            FirebaseHandler.checkIfDriverExist(driver.getDriverEmail(), new CheckExistance() {
                                @Override
                                public void onSearchComplete(boolean isFound) {
                                    if (isFound) {
                                        Log.e("TAGKEY", "Driver Exists");
                                        //animateSuccessSignin();
                                        startActivity(new Intent(SignIn.this,MainActivity.class));
                                        mButton.setProgress(100);

                                    } else {
                                        Log.e("TAGKEY", "Driver Does Not Exist, GO TO COMPLETE PROFILE");
                                        startActivity(new Intent(SignIn.this,CompleteProfile.class));
                                        //animateSuccessCompleteProfile();
                                        mButton.setProgress(100);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFail() {
                            animateError();
                            mButton.setProgress(-1);
                        }
                    });

                }
                else
                {
                    animateError();
                    mButton.setProgress(-1);
                }
            }

        });
    }

    private void setUpUser()
    {
        driver.setDriverEmail(mEmail.getText().toString().trim());
        driver.setDriverPassword(mPassword.getText().toString().trim());
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onValidationSucceeded() {

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

    }

    private void animateError()
    {
        YoYo.with(Techniques.Tada)
                .duration(700)
                .playOn(findViewById(R.id.edit_area));

    }

    private void animateSuccessSignin()
    {
        YoYo.with(Techniques.TakingOff)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        Toast.makeText(SignIn.this,"Logged In",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignIn.this,MainActivity.class));
                    }
                })
                .duration(700)
                .playOn(findViewById(R.id.edit_area));
    }

    private void animateSuccessCompleteProfile()
    {
        YoYo.with(Techniques.TakingOff)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        startActivity(new Intent(SignIn.this,CompleteProfile.class));
                    }
                })
                .duration(700)
                .playOn(findViewById(R.id.edit_area));
    }
}
