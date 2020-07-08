package com.skiplab.theselproject.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.User;

public class AdminRegisterActivity extends AppCompatActivity {

    private static final String TAG = "AdminRegisterActivity";

    private Context mContext;
    private TextInputEditText mEmail, mPassword, mConfirmPwd;
    private TextView mLoginTv;
    private Button btnRegister;

    private FirebaseAuth mAuth;


    //Progressbar to display while registering the user
    ProgressDialog progressDialog;

    private String email, password, confirmPwd;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        mContext = AdminRegisterActivity.this;
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.emailEt);
        mPassword =  findViewById(R.id.passwordEt);
        mConfirmPwd = findViewById(R.id.confirmPwdEt);
        mLoginTv = findViewById(R.id.loginTv);
        mLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminRegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister = (Button) findViewById(R.id.signUpBtn);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                confirmPwd = mConfirmPwd.getText().toString();

                //validate
                if (email.isEmpty()){
                    mEmail.setError("Email is required");
                    mEmail.setFocusable(true);
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //set error and focus to email editText
                    mEmail.setError("Invalid Email");
                    mEmail.setFocusable(true);
                }
                else if (!password.equals("ramani")){
                    mPassword.setError("Wrong Password");
                    mPassword.setFocusable(true);
                    Toast.makeText(mContext, "Contact Thesel CEO for the Admin password", Toast.LENGTH_SHORT).show();
                }
                else if (confirmPwd.isEmpty()){
                    mConfirmPwd.setError("Please confirm password");
                    mConfirmPwd.setFocusable(true);
                }
                else if (!password.equals(confirmPwd)){
                    Toast.makeText(mContext, "Your passwords don't match, please verify", Toast.LENGTH_SHORT).show();
                }
                else{
                    registerUser(email, password);
                }
            }
        });
    }

    private void registerUser(String email, String password)
    {
        progressDialog.setMessage("Registering user...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        Log.d( TAG, "onComplete: onComplete: " + task.isSuccessful());

                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Log.d( TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser()
                                    .getUid());

                            // Send email verification
                            sendVerificationEmail();

                            User user = new User();
                            user.setUsername( email.substring( 0, email.indexOf( "@" ) ) );
                            user.setEmail(email);
                            user.setProfile_photo("");
                            user.setPhone("1");
                            user.setUid( mAuth.getCurrentUser().getUid() );
                            user.setBio("Edit this bio from the account settings...");
                            user.setIsStaff("admin");
                            user.setOnlineStatus("online");
                            user.setPosts(0);
                            user.setSelectedCategory("");

                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mAuth.signOut();
                                            startActivity( new Intent( mContext, LoginActivity.class ) );
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mAuth.signOut();
                                            startActivity( new Intent( mContext, LoginActivity.class ) );
                                            task.getException().getMessage();
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText( mContext, "Unable to Register", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendVerificationEmail()
    {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null)
        {
            user.sendEmailVerification().addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("A verification link has been sent to your email");
                        builder.setCancelable(true);
                        builder.show();

                        Toast.makeText(mContext, "A verification link has been sent to your email", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText( mContext, "Couldn't send verification email",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } );
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isActivityRunning = false;
    }
}
