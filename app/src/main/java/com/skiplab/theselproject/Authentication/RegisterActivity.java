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
import com.skiplab.theselproject.models.Cards;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.R;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Context mContext;
    private TextInputEditText mEmail, mPassword, mConfirmPwd, mAge;
    private TextView mAgreementTv;
    private Button btnRegister;

    private FirebaseAuth mAuth;

    //Progressbar to display while registering the user
    ProgressDialog progressDialog;

    private String text = "By clicking register, you are indicating that you have read and agreed to the Terms of Service and Privacy Policy";
    private String email, password, confirmPwd;
    private int age;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = RegisterActivity.this;
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.emailEt);
        mPassword =  findViewById(R.id.passwordEt);
        mConfirmPwd = findViewById(R.id.confirmPwdEt);
        mAge = findViewById(R.id.ageEt);

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
        mAgreementTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, PrivacyPolicy.class));
            }
        });

        btnRegister = (Button) findViewById(R.id.signUpBtn);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                confirmPwd = mConfirmPwd.getText().toString();
                try{
                    age = Integer.parseInt(mAge.getText().toString());

                } catch(NumberFormatException ex){ // handle your exception

                }

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
                else if (password.isEmpty()){
                    mPassword.setError("Password is required");
                    mPassword.setFocusable(true);
                }
                else if (password.length()<6){
                    //set error and focus to password editText
                    mPassword.setError("Password length at least 6 characters");
                    mPassword.setFocusable(true);
                }
                else if (confirmPwd.isEmpty()){
                    mConfirmPwd.setError("Please confirm password");
                    mConfirmPwd.setFocusable(true);
                }
                else if (!password.equals(confirmPwd)){
                    Toast.makeText(mContext, "Your passwords don't match, please verify", Toast.LENGTH_SHORT).show();
                }
                else if (mAge.getText().toString().isEmpty()){
                    mAge.setError("Your age is required");
                    mAge.setFocusable(true);
                }
                else if (!(age >= 16)){
                    mAge.setError("User must be 16 or older");
                    mAge.setFocusable(true);
                }
                else{
                    registerUser(email, password, confirmPwd, age);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("A verification link has been sent to your email");
                        builder.setCancelable(true);
                        builder.show();

                        Toast.makeText(mContext, "A verification link has been sent to your email", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText( RegisterActivity.this, "Couldn't send verification email",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } );
        }
    }

    private void registerUser(final String email, String password, String confirmPwd, final int age) {
        progressDialog.setMessage("Registering user...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        Log.d( TAG, "onComplete: onComplete: " + task.isSuccessful());

                        if (task.isSuccessful())
                        {
                            Log.d( TAG, "onComplete: AuthState: " + mAuth.getCurrentUser()
                                    .getUid());

                            User user = new User();
                            user.setUsername( email.substring( 0, email.indexOf( "@" ) ) );
                            user.setProfile_photo( "" );
                            user.setEmail(email);
                            user.setPhone("1");
                            user.setUid( FirebaseAuth.getInstance().getCurrentUser().getUid() );
                            user.setBio("Edit this bio from the account settings...");
                            user.setIsStaff("false");
                            user.setAge(String.valueOf(age+" years old"));
                            user.setOnlineStatus("online");
                            user.setSelectedCategory("");
                            user.setPosts(0);

                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                progressDialog.dismiss();

                                                // Send email verification
                                                sendVerificationEmail();

                                                Cards cards = new Cards();
                                                cards.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                cards.setCardName1("");
                                                cards.setCardNumber1("");
                                                cards.setCardName2("");
                                                cards.setCardNumber2("");
                                                cards.setCardName3("");
                                                cards.setCardNumber3("");

                                                FirebaseDatabase.getInstance().getReference("cards")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .setValue(cards)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                FirebaseAuth.getInstance().signOut();
                                                                startActivity( new Intent( RegisterActivity.this, LoginActivity.class ) );
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            FirebaseAuth.getInstance().signOut();
                                            startActivity( new Intent( RegisterActivity.this, LoginActivity.class ) );
                                            task.getException().getMessage();
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText( RegisterActivity.this, "Unable to Register", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
