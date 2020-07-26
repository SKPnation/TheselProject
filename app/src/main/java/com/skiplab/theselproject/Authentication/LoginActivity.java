package com.skiplab.theselproject.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.User;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    Context mContext = LoginActivity.this;

    FirebaseDatabase db;

    //views
    TextInputEditText mEmailEt, mPasswordEt;
    TextView forgotPwd, resendVerification;
    Button mLoginBtn;

    ProgressDialog progressDialog;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Now the listener will be actively listening for changes in the authentication state
        setupFirebaseAuth();
        mAuth = FirebaseAuth.getInstance();

        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mLoginBtn = findViewById(R.id.loginBtn);
        forgotPwd = findViewById(R.id.forgotPwdTv);
        resendVerification = findViewById( R.id.tvVerification );

        db = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);

        forgotPwd.setOnClickListener(v -> showRecoverPasswordDialog());

        mLoginBtn.setOnClickListener(v -> {
            progressDialog = new ProgressDialog( LoginActivity.this );
            progressDialog.setMessage( "Loading..." );
            progressDialog.show();

            //check for null valued editText fields
            if (!TextUtils.isEmpty( mEmailEt.getText().toString() ) &&
                    !TextUtils.isEmpty( mPasswordEt.getText().toString() ))
            {
                Log.d(TAG, "onClick: attempting to authenticate");

                //Initiate Firebase Auth
                FirebaseAuth.getInstance().signInWithEmailAndPassword( mEmailEt.getText().toString(),
                        mPasswordEt.getText().toString() )
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                try {
                                    progressDialog.dismiss();
                                }
                                catch (Exception e){
                                    Toast.makeText(mContext, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText( LoginActivity.this, "Login failed, please check your credentials.", Toast.LENGTH_SHORT ).show();
                    }
                } );

            } else {
                progressDialog.dismiss();
                mEmailEt.setError( "Both fields are required" );
                mPasswordEt.setError( "Both fields are required" );
                mEmailEt.requestFocus();
                mPasswordEt.requestFocus();
                return;
            }
        });

        resendVerification.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationDialog();
            }
        } );
    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);
        EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailEt.getText().toString().trim();

                if (TextUtils.isEmpty(email))
                    Toast.makeText(mContext, "Your email is required", Toast.LENGTH_SHORT).show();
                else
                    beginRecovery(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void beginRecovery(String email) {
        progressDialog = new ProgressDialog( LoginActivity.this );
        progressDialog.setMessage( "Sending email..." );
        progressDialog.show();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(mContext, "Email sent", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mContext, "Failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resendVerificationDialog()
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder( mContext );

        LayoutInflater inflater = this.getLayoutInflater();
        View resend_verification_layout = inflater.inflate( R.layout.resend_verification_layout, null );
        dialog.setView( resend_verification_layout );

        final EditText confirmEmail = resend_verification_layout.findViewById( R.id.email );
        final EditText confirmPassword = resend_verification_layout.findViewById( R.id.password );
        final TextView cancel = resend_verification_layout.findViewById( R.id.cancel );
        TextView confirm = resend_verification_layout.findViewById( R.id.confirm );

        confirm.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!confirmEmail.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty())
                {
                    authenticateAndResendEmail(confirmEmail.getText().toString(), confirmPassword.getText().toString());
                } else {
                    confirmEmail.setError( "Both fields are required" );
                    confirmPassword.setError( "Both fields are required" );
                    confirmEmail.requestFocus();
                    confirmPassword.requestFocus();
                    return;
                }
            }

        } );

        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //...
            }
        } );

        dialog.show();
    }

    private void authenticateAndResendEmail(String email, String password)
    {
        progressDialog = new ProgressDialog( mContext);
        progressDialog.setMessage( "Loading..." );
        progressDialog.show();

        AuthCredential credential = EmailAuthProvider.getCredential( email, password );
        FirebaseAuth.getInstance().signInWithCredential( credential ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Log.d( TAG, "onComplete: reauthenticate success.");
                    sendVerificationEmail();
                    FirebaseAuth.getInstance().signOut();
                    progressDialog.dismiss();
                }
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( getBaseContext(), "Invalid Credentials \nReset your password and try again",
                        Toast.LENGTH_SHORT ).show();
                progressDialog.dismiss();
            }
        } );
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
                        Toast.makeText( mContext, "Sent verification email",
                                Toast.LENGTH_SHORT).show();
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

    private void setupFirebaseAuth()
    {
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null)
            {
                Log.d( TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                /*Toast.makeText( LoginActivity.this, "Authenticated with: "+ user.getEmail(),
                        Toast.LENGTH_SHORT ).show();*/
                DatabaseReference usersRef = db.getReference("users");
                Query query = usersRef.orderByKey().equalTo(user.getUid());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            User user1 = ds.getValue(User.class);
                            if (user1.getOnlineStatus().equals("deactivated"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(user1.getUsername()+", your account has been deactivated.");

                                builder.show();

                                mAuth.signOut();
                            }
                            else
                            {
                                Intent intent = new Intent( LoginActivity.this, DashboardActivity.class );
                                startActivity( intent );
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }
                });

            } else {
                Log.d( TAG, "onAuthStateChanged: signed_out");
            }
        };
    }

    //Everything you need to use the authStateListener Object
    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
        isActivityRunning = false;
    }
}
