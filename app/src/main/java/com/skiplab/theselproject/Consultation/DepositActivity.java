package com.skiplab.theselproject.Consultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.Home.SelectCategory;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.models.Wallet;

import java.util.Calendar;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class DepositActivity extends AppCompatActivity {

    Context mContext = DepositActivity.this;

    CardView threetCv, sixtCv, twelvetCv, eighteentCv;

    private Card card;
    private Charge charge;

    AppCompatEditText etEmail,etName,etCard,etCvv;
    AppCompatSpinner spMonth,spYear;
    AppCompatButton btProceed;

    private String email, cardNumber, cvv;
    private int expiryMonth, expiryYear;

    int three_thousand = 300000;
    int six_thousand = 600000;
    int twelve_thousand = 1200000;
    int eighteen_thousand = 1800000;

    FirebaseAuth mAuth;
    DatabaseReference walletRef, usersRef;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        mAuth = FirebaseAuth.getInstance();
        walletRef = FirebaseDatabase.getInstance().getReference("wallet");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        progressDialog = new ProgressDialog(this);

        threetCv = findViewById(R.id.three_thousand_cv);
        sixtCv = findViewById(R.id.six_thousand_cv);
        twelvetCv = findViewById(R.id.twelve_thousand_cv);
        eighteentCv = findViewById(R.id.eighteen_thousand_cv);

        usersRef.orderByKey().equalTo(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            User user = ds.getValue(User.class);
                            if (user.getIsStaff().equals("false"))
                            {
                                threetCv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        int wallet = Integer.parseInt(ds.child("wallet").getValue().toString());
                                        int result = wallet + 3000;
                                        usersRef.child(mAuth.getCurrentUser().getUid()).child("wallet").setValue(result);


                /*walletRef.orderByKey().equalTo(mAuth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren())
                                {
                                    Wallet wallet = ds.getValue(Wallet.class);

                                    int balance = wallet.getBalance();
                                    int result = balance + 3000;
                                    if (result > 18000)
                                    {
                                        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                                                .setMessage("Your Thesel wallet can't hold more than #18,000")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.card_payment_dialog, null);

                                        etEmail = mView.findViewById(R.id.et_main_email);
                                        etName = mView.findViewById(R.id.et_main_name);
                                        etCard = mView.findViewById(R.id.et_main_card);
                                        etCvv = mView.findViewById(R.id.et_main_cvv);
                                        spMonth= mView.findViewById(R.id.sp_main_month);
                                        spYear = mView.findViewById(R.id.sp_main_year);
                                        btProceed = mView.findViewById(R.id.bt_main_proceed);

                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getYears());
                                        //here we set the adapter to the year spinner
                                        spYear.setAdapter(adapter);

                                        btProceed.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                chargeCard(three_thousand, balance);
                                            }
                                        });

                                        builder.setView(mView);
                                        builder.show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //..
                            }
                        });*/
                                    }
                                });

                                sixtCv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        int wallet = Integer.parseInt(ds.child("wallet").getValue().toString());
                                        int result = wallet + 6000;
                                        usersRef.child(mAuth.getCurrentUser().getUid()).child("wallet").setValue(result);
                /*walletRef.orderByKey().equalTo(mAuth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren())
                                {
                                    Wallet wallet = ds.getValue(Wallet.class);

                                    int balance = wallet.getBalance();
                                    int result = balance + 6000;
                                    if (result > 18000)
                                    {
                                        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                                                .setMessage("Your Thesel wallet can't hold more than #18,000")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.card_payment_dialog, null);

                                        etEmail = mView.findViewById(R.id.et_main_email);
                                        etName = mView.findViewById(R.id.et_main_name);
                                        etCard = mView.findViewById(R.id.et_main_card);
                                        etCvv = mView.findViewById(R.id.et_main_cvv);
                                        spMonth= mView.findViewById(R.id.sp_main_month);
                                        spYear = mView.findViewById(R.id.sp_main_year);
                                        btProceed = mView.findViewById(R.id.bt_main_proceed);

                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getYears());
                                        //here we set the adapter to the year spinner
                                        spYear.setAdapter(adapter);

                                        btProceed.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                chargeCard(six_thousand, balance);
                                            }
                                        });

                                        builder.setView(mView);
                                        builder.show();
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //..
                            }
                        });*/

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



        twelvetCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletRef.orderByKey().equalTo(mAuth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren())
                                {
                                    Wallet wallet = ds.getValue(Wallet.class);

                                    int balance = wallet.getBalance();
                                    int result = balance + 12000;
                                    if (result > 18000)
                                    {
                                        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                                                .setMessage("Your Thesel wallet can't hold more than #18,000")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.card_payment_dialog, null);

                                        etEmail = mView.findViewById(R.id.et_main_email);
                                        etName = mView.findViewById(R.id.et_main_name);
                                        etCard = mView.findViewById(R.id.et_main_card);
                                        etCvv = mView.findViewById(R.id.et_main_cvv);
                                        spMonth= mView.findViewById(R.id.sp_main_month);
                                        spYear = mView.findViewById(R.id.sp_main_year);
                                        btProceed = mView.findViewById(R.id.bt_main_proceed);

                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getYears());
                                        //here we set the adapter to the year spinner
                                        spYear.setAdapter(adapter);

                                        btProceed.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                chargeCard(twelve_thousand, balance);
                                            }
                                        });

                                        builder.setView(mView);
                                        builder.show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //..
                            }
                        });
            }
        });

        eighteentCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletRef.orderByKey().equalTo(mAuth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren())
                                {
                                    Wallet wallet = ds.getValue(Wallet.class);

                                    int balance = wallet.getBalance();
                                    int result = balance + 18000;
                                    if (result > 18000)
                                    {
                                        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                                                .setMessage("Your Thesel wallet can't hold more than #18,000")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.card_payment_dialog, null);

                                        etEmail = mView.findViewById(R.id.et_main_email);
                                        etName = mView.findViewById(R.id.et_main_name);
                                        etCard = mView.findViewById(R.id.et_main_card);
                                        etCvv = mView.findViewById(R.id.et_main_cvv);
                                        spMonth= mView.findViewById(R.id.sp_main_month);
                                        spYear = mView.findViewById(R.id.sp_main_year);
                                        btProceed = mView.findViewById(R.id.bt_main_proceed);

                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getYears());
                                        //here we set the adapter to the year spinner
                                        spYear.setAdapter(adapter);

                                        btProceed.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                chargeCard(eighteen_thousand, balance);
                                            }
                                        });

                                        builder.setView(mView);
                                        builder.show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    private void chargeCard(int amountInNaira, int balance) {
        String cardName = etName.getText().toString().trim();
        String cardCvv = etCvv.getText().toString().trim();
        String cardNumber = etCard.getText().toString().trim();
        String month, year;


        if (spMonth.getSelectedItemPosition() > 0 && spYear.getSelectedItemPosition() > 0) {
            month = spMonth.getSelectedItem().toString();
            year = spYear.getSelectedItem().toString();

            if (cardName.isEmpty()) {
                Toast.makeText(this, "Please enter the name on the card", Toast.LENGTH_LONG).show();
            } else {
                if (!cardNumber.isEmpty() && !cardCvv.isEmpty()) {

                    //here we pass the details to the card object
                    Card card = new Card(cardNumber, Integer.valueOf(month), Integer.valueOf(year), cardCvv, cardName);

                    //check if the card is valid before attempting to charge the card
                    if (card.isValid()) {
                        //we disable the button so the user doesn't tap multiple times and create a duplicate transaction
                        btProceed.setEnabled(false);

                        //every transaction requires you to send along a unique reference
                        String customRef = generateReference();

                        //setup a charge object to set values like amount, reference etc
                        Charge charge = new Charge();
                        charge.setEmail(mAuth.getCurrentUser().getEmail());
                        //the amount(in KOBO eg 1000 kobo = 10 Naira) the customer is to pay for the product or service
                        // basically add 2 extra zeros at the end of your amount to convert from kobo to naira.
                        charge.setAmount(amountInNaira);
                        charge.setReference(customRef);
                        charge.setCurrency("NGN");
                        charge.setCard(card);

                        //Charge the card
                        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
                            @Override
                            public void onSuccess(Transaction transaction) {
                                btProceed.setEnabled(true);

                                progressDialog.dismiss();

                                if (amountInNaira == 300000){
                                    int result = balance + 3000;
                                    walletRef.child(mAuth.getCurrentUser().getUid()).child("balance").setValue(result);
                                }
                                else if (amountInNaira == 600000){
                                    int result = balance + 6000;
                                    walletRef.child(mAuth.getCurrentUser().getUid()).child("balance").setValue(result);
                                }
                                else if (amountInNaira == 1200000){
                                    int result = balance + 12000;
                                    walletRef.child(mAuth.getCurrentUser().getUid()).child("balance").setValue(result);
                                }
                                else
                                {
                                    int result = balance + 18000;
                                    walletRef.child(mAuth.getCurrentUser().getUid()).child("balance").setValue(result);
                                }

                                Toast.makeText(mContext, "Successful deposit!", Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void beforeValidate(Transaction transaction) {
                                progressDialog.setMessage("This might take a minute...");
                                progressDialog.show();

                                //snackBar("beforeValidate");
                            }

                            @Override
                            public void onError(Throwable error, Transaction transaction) {
                                btProceed.setEnabled(true);
                                progressDialog.dismiss();
                                Toast.makeText(mContext, "Failed deposit!!!", Toast.LENGTH_LONG).show();

                            }
                        });
                    } else {
                        Toast.makeText(mContext, "Invalid Card", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else {
            Toast.makeText(mContext, "Select Card Expiry Date", Toast.LENGTH_LONG).show();
        }
    }


    private String[] getYears() {
        String[] years = new String[10];
        Integer year = Calendar.getInstance().get(Calendar.YEAR);
        years[0] = "Year";
        for (int x = 1; x < years.length; x++) {
            String currentYear = String.valueOf(year++);
            years[x] = currentYear;
        }
        return years;
    }

    private String generateReference() {
        String keys = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            int index = (int)(keys.length() * Math.random());
            sb.append(keys.charAt(index));
        }

        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(mContext, WalletActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
