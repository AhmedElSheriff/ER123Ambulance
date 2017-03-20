package com.example.android.er123ambulance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.er123ambulance.callbacks.CheckExistance;
import com.example.android.er123ambulance.firebase.FirebaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mButton;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please Wait");
        mProgressDialog.setCancelable(false);

//        if(FirebaseAuth.getInstance().getCurrentUser() != null)
//        {
//           FirebaseHandler.checkIfDriverExist(FirebaseAuth.getInstance().getCurrentUser().getEmail(), new CheckExistance() {
//               @Override
//               public void onSearchComplete(boolean isFound) {
//                   if(isFound) {
//                       SignIn.this.finish();
//                       startActivity(new Intent(SignIn.this, MainActivity.class));
//                   }
//                   else
//                   {
//                       FirebaseAuth.getInstance().signOut();
//                   }
//               }
//           });
//        }


        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mEmail = (EditText) findViewById(R.id.email_login);
        mPassword = (EditText) findViewById(R.id.password_login);
        mButton = (Button) findViewById(R.id.login_btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                final String email,password;
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Log.e("TAGKEY","Sign In With Email Is Successful");
                            FirebaseHandler.checkIfDriverExist(email, new CheckExistance() {
                                @Override
                                public void onSearchComplete(boolean isFound) {
                                    if(isFound)
                                    {
                                        Log.e("TAGKEY","Driver Exists");
                                        Toast.makeText(SignIn.this,"Logged In",Toast.LENGTH_SHORT).show();
                                        SignIn.this.finish();
                                        startActivity(new Intent(SignIn.this,MainActivity.class));
                                    }
                                    else
                                    {
                                        Log.e("TAGKEY","Driver Does Not Exist, GO TO COMPLETE PROFILE");
                                        Toast.makeText(SignIn.this,"Complete Your Profile",Toast.LENGTH_LONG).show();
                                        SignIn.this.finish();
                                        startActivity(new Intent(SignIn.this,CompleteProfile.class));
                                    }
                                }
                            });
                        }
                        else
                        {
                            Log.e("TAGKEY","FAILED TO SIGN IN");
                            Toast.makeText(SignIn.this,"Failed to Login",Toast.LENGTH_SHORT).show();
                        }
                        mProgressDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
