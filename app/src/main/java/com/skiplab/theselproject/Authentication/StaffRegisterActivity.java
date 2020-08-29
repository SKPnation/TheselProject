package com.skiplab.theselproject.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.skiplab.theselproject.PrivacyPolicy;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.User;

public class StaffRegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Context mContext;
    private TextInputEditText mUsername, mPhone, mPassword, mEmail, mAddress;
    private TextView mAgreementTv, mLoginTv;
    // private TextView mCategoryTv;
    private Button btnRegister;

    private FirebaseAuth mAuth;

    private String text = "By clicking register, you are indicating that you have read and agreed to the Terms of Service and Privacy Policy";

    //Progressbar to display while registering the user
    ProgressDialog progressDialog;

    private String email, password, username, phone, address;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_register);

        mAuth = FirebaseAuth.getInstance();

        mLoginTv = findViewById(R.id.loginTv);
        mEmail = findViewById(R.id.emailEt);
        mPassword = findViewById(R.id.passwordEt);
        mUsername = findViewById(R.id.uNameEt);
        mPhone =  findViewById(R.id.phoneEt);
        mAddress = findViewById(R.id.addressEt);
        btnRegister = (Button) findViewById(R.id.signUpBtn);

        mAgreementTv = findViewById(R.id.reg_agreementTv);
        SpannableString ss = new SpannableString(text);

        ForegroundColorSpan fcsGray = new ForegroundColorSpan(Color.GRAY);
        ForegroundColorSpan fcsGray2 = new ForegroundColorSpan(Color.GRAY);
        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(Color.YELLOW);
        ForegroundColorSpan fcsGreen2 = new ForegroundColorSpan(Color.YELLOW);

        ss.setSpan(fcsGray, 0,77, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(fcsGreen,78,94, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(fcsGray2,95,98, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(fcsGreen2, 99,113, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //ss.setSpan(fcsGray, 99,9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mAgreementTv.setText(ss);
        mAgreementTv.setOnClickListener(v -> startActivity(new Intent(StaffRegisterActivity.this, PrivacyPolicy.class)));

        mLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StaffRegisterActivity.this, LoginActivity.class));
            }
        });

        mContext = StaffRegisterActivity.this;
        progressDialog = new ProgressDialog(this);

        btnRegister.setOnClickListener(v -> {
            username = mUsername.getText().toString();
            email = mEmail.getText().toString();
            password = mPassword.getText().toString();
            phone = mPhone.getText().toString();
            address = mAddress.getText().toString();


            //validate
            if (username.isEmpty()){
                //set error and focus to username editText
                mUsername.setError("Username is required");
                mUsername.setFocusable(true);
            }
            else if (phone.isEmpty()){
                //set error and focus to password editText
                mPhone.setError("Please type a valid phone number");
                mPhone.setFocusable(true);
            }
            else if (!phone.startsWith("+")){
                //set error and focus to password editText
                mPhone.setError("Missing country code");
                mPhone.setFocusable(true);
                Toast.makeText(mContext, "Your country code is required before your phone number(e.g, +234)", Toast.LENGTH_SHORT).show();
            }
            else if (email.isEmpty()){
                mEmail.setError("Email is required");
                mEmail.setFocusable(true);
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                //set error and focus to email editText
                mEmail.setError("Invalid Email");
                mEmail.setFocusable(true);
            }
            else if (!password.equals("jabani")){
                mPassword.setError("Wrong Password");
                mPassword.setFocusable(true);
                Toast.makeText(mContext, "Contact Thesel Admin for the consultant password", Toast.LENGTH_SHORT).show();
            }
            else if (address.isEmpty()){
                mAddress.setError("Your home address is required");
                mAddress.setFocusable(true);
            }
            else{
                register(username, email, phone, address);
            }
        });


    }

    private void register(final String username, final String email, final String phone, final String address)
    {
        progressDialog.setMessage("Registering staff...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        Log.d( TAG, "onComplete: onComplete: " + task.isSuccessful());

                        if (task.isSuccessful())
                        {
                            Log.d( TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser()
                                    .getUid());

                            User user = new User();
                            user.setUsername( username );
                            user.setEmail(email);
                            user.setPhone( phone );
                            user.setProfile_photo( "" );
                            user.setUid( FirebaseAuth.getInstance().getCurrentUser().getUid() );
                            user.setBio("Edit this bio from the account settings...");
                            user.setAddress(address);
                            user.setCost(0);
                            user.setIsStaff("true");
                            user.setOnlineStatus("online");
                            user.setPosts(0);
                            user.setSelectedCategory("");

                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                progressDialog.dismiss();

                                                // Send email verification
                                                sendVerificationEmail();

                                                FirebaseAuth.getInstance().signOut();
                                                startActivity( new Intent( mContext, LoginActivity.class ) );
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            FirebaseAuth.getInstance().signOut();
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
        {
            user.sendEmailVerification().addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
