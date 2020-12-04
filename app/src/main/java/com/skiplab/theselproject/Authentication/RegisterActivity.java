package com.skiplab.theselproject.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.hbb20.CountryCodePicker;
import com.skiplab.theselproject.PrivacyPolicy;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.R;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Context mContext;
    private TextInputEditText mEmail, mPassword, mConfirmPwd, mAge;
    private TextView mAgreementTv, mBirthDateTv;
    private EditText mPhone;
    private Button btnRegister;
    private CountryCodePicker ccp;
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private FirebaseAuth mAuth;

    //Progressbar to display while registering the user
    ProgressDialog progressDialog;

    private String text = "By clicking register, you are indicating that you have read and agreed to the Terms of Service and Privacy Policy";
    private String email, phone, password, confirmPwd;
    private int age, birth_year, birth_month, birth_day;
    private int yearI, dayOfMonthI;
    private String monthS, dayOfMonthS, dateOfBirth;
    private String eDate;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = RegisterActivity.this;
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.emailEt);
        mPhone =  findViewById(R.id.phoneEt);
        mPassword =  findViewById(R.id.passwordEt);
        mConfirmPwd = findViewById(R.id.confirmPwdEt);
        mBirthDateTv = findViewById(R.id.birth_date_tv);

        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(mPhone);

        Calendar calendar = Calendar.getInstance();
        birth_year = calendar.get(Calendar.YEAR);
        birth_month = calendar.get(Calendar.MONTH);
        birth_day = calendar.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        eDate = simpleDateFormat.format(Calendar.getInstance().getTime());


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

        mBirthDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(
                        mContext,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,birth_year,birth_month,birth_day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                birth_month = month + 1;
                yearI = year;
                dayOfMonthI = dayOfMonth;

                if (birth_month < 10 && dayOfMonthI < 10){
                    monthS = "0"+birth_month;
                    dayOfMonthS = "0"+dayOfMonthI;
                    dateOfBirth = dayOfMonthS+"-"+monthS+"-"+yearI;
                    mBirthDateTv.setText(dateOfBirth);
                }
                else if (dayOfMonthI < 10){
                    dayOfMonthS = "0"+dayOfMonthI;
                    dateOfBirth = dayOfMonthS+"-"+birth_month+"-"+yearI;
                    mBirthDateTv.setText(dateOfBirth);
                }
                else if (birth_month < 10){
                    monthS = "0"+birth_month;
                    dateOfBirth = dayOfMonthI+"-"+monthS+"-"+yearI;
                    mBirthDateTv.setText(dateOfBirth);
                }
                else
                {
                    dateOfBirth = dayOfMonthI+"-"+birth_month+"-"+yearI;
                    mBirthDateTv.setText(dateOfBirth);
                }

                try {
                    Date date1 = simpleDateFormat.parse(dateOfBirth);
                    Date date2 = simpleDateFormat.parse(eDate);

                    long startDate = date1.getTime();
                    long endDate = date2.getTime();

                    if (startDate <= endDate)
                    {
                        Period period = new Period(startDate, endDate, PeriodType.yearMonthDay());
                        age = period.getYears();

                        Toast.makeText(mContext, ""+age,Toast.LENGTH_SHORT).show();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        };

        btnRegister = (Button) findViewById(R.id.signUpBtn);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                phone = ccp.getFullNumberWithPlus();
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
                else if (mPhone.getText().toString().isEmpty()){
                    //set error and focus to password editText
                    mPhone.setError("Please type a valid phone number");
                    mPhone.setFocusable(true);
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
                else if (mBirthDateTv.getText().toString().isEmpty()){
                    mBirthDateTv.setError("Date of birth is required");
                    mBirthDateTv.setFocusable(true);
                }
                else if (age < 16)
                {
                    AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                            .setMessage("You must be 16 or older")
                            .create();
                    alertDialog.show();

                    mBirthDateTv.setError("You must be 16 or older");
                    mBirthDateTv.setFocusable(true);
                }
                else{
                    //registerUser(email, phone, password);
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

    private void registerUser(final String email, String phone, String password) {
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

                            String timestamp = String.valueOf(System.currentTimeMillis());

                            User user = new User();
                            user.setUid( FirebaseAuth.getInstance().getCurrentUser().getUid() );
                            user.setUsername( email.substring( 0, email.indexOf( "@" ) ) );
                            user.setEmail(email);
                            user.setPhone_number(phone);
                            user.setProfile_photo( "" );
                            user.setBio("Edit this bio from the account settings...");
                            user.setDate_created("");
                            user.setIsStaff("false");
                            user.setOnline_status("offline");
                            user.setSelectedCategory("");
                            user.setDay_of_birth(dayOfMonthI);
                            user.setMonth_of_birth(birth_month);
                            user.setYear_of_birth(yearI);
                            user.setPosts(0);
                            user.setEverify(false);
                            user.setDate_created(timestamp);

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

                                                FirebaseAuth.getInstance().signOut();
                                                startActivity( new Intent( RegisterActivity.this, LoginActivity.class ) );
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
