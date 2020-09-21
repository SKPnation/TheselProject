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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Deposits;
import com.skiplab.theselproject.models.User;

import java.util.Calendar;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class DepositActivity extends AppCompatActivity {

    Context mContext = DepositActivity.this;

    CardView threetCv, sixtCv, twelvetCv, eighteentCv;

    private AppCompatEditText etEmail,etName,etCard,etCvv;
    private AppCompatSpinner spMonth,spYear;
    private AppCompatButton btProceed;
    private ImageView closeBtn;

    int three_thousand = 300000;
    int six_thousand = 600000;
    int twelve_thousand = 1200000;
    int eighteen_thousand = 1800000;

    FirebaseAuth mAuth;
    DatabaseReference usersRef, depositsRef;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        depositsRef = FirebaseDatabase.getInstance().getReference("deposits");

        progressDialog = new ProgressDialog(this);

        threetCv = findViewById(R.id.three_thousand_cv);
        sixtCv = findViewById(R.id.six_thousand_cv);
        twelvetCv = findViewById(R.id.twelve_thousand_cv);
        eighteentCv = findViewById(R.id.eighteen_thousand_cv);
        closeBtn = findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WalletActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        usersRef.orderByKey().equalTo(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            User user = ds.getValue(User.class);
                            if (user.getIsStaff().equals("false"))
                            {
                                int wallet = Integer.parseInt(ds.child("wallet").getValue().toString());

                                threetCv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        int result = wallet + 3000;
                                        if (result > 18000)
                                        {
                                            AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                                                    .setMessage("Your Thesel wallet can't hold more than #18,000")
                                                    .create();
                                            alertDialog.show();
                                        }
                                        else
                                        {
                                            usersRef.child(mAuth.getCurrentUser().getUid()).child("wallet").setValue(result);
                                            /*AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                            View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.card_payment_dialog, null);

                                            etEmail = mView.findViewById(R.id.et_main_email);
                                            etName = mView.findViewById(R.id.et_main_name);
                                            etCard = mView.findViewById(R.id.et_main_card);
                                            etCvv = mView.findViewById(R.id.et_main_cvv);
                                            spMonth= mView.findViewById(R.id.sp_main_month);
                                            spYear = mView.findViewById(R.id.sp_main_year);
                                            btProceed = mView.findViewById(R.id.bt_main_proceed);

                                            etEmail.setText(mAuth.getCurrentUser().getEmail());
                                            btProceed.setText("PROCEED:  N3000");

                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getYears());
                                            //here we set the adapter to the year spinner
                                            spYear.setAdapter(adapter);

                                            btProceed.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    chargeCard(three_thousand, wallet);
                                                }
                                            });

                                            builder.setView(mView);
                                            builder.show();*/
                                        }

                                    }
                                });

                                sixtCv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        int result = wallet + 6000;
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

                                            etEmail.setText(mAuth.getCurrentUser().getEmail());
                                            btProceed.setText("PROCEED:  N6000");

                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getYears());
                                            //here we set the adapter to the year spinner
                                            spYear.setAdapter(adapter);

                                            btProceed.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    chargeCard(six_thousand, wallet);
                                                }
                                            });

                                            builder.setView(mView);
                                            builder.show();
                                        }

                                    }
                                });

                                twelvetCv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int result = wallet + 12000;
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

                                            etEmail.setText(mAuth.getCurrentUser().getEmail());
                                            btProceed.setText("PROCEED:  N12000");

                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getYears());
                                            //here we set the adapter to the year spinner
                                            spYear.setAdapter(adapter);

                                            btProceed.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    chargeCard(twelve_thousand, wallet);
                                                }
                                            });

                                            builder.setView(mView);
                                            builder.show();
                                        }
                                    }
                                });

                                eighteentCv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int result = wallet + 18000;
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

                                            etEmail.setText(mAuth.getCurrentUser().getEmail());
                                            btProceed.setText("PROCEED:  N18000");


                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getYears());
                                            //here we set the adapter to the year spinner
                                            spYear.setAdapter(adapter);

                                            btProceed.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    chargeCard(eighteen_thousand, wallet);
                                                }
                                            });

                                            builder.setView(mView);
                                            builder.show();
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }
                });
    }

    private void chargeCard(int amountInNaira, int wallet) {

        String userEmail = etEmail.getText().toString().trim();
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
                        charge.setEmail(userEmail);
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

                                String timestamp = String.valueOf(System.currentTimeMillis());

                                if (amountInNaira == 300000)
                                {
                                    int result = wallet + 3000;

                                    Deposits deposits = new Deposits();
                                    deposits.setUid(mAuth.getUid());
                                    deposits.setAmount((amountInNaira/100));
                                    deposits.setReferenceNumber(customRef);
                                    deposits.setTimestamp(timestamp);

                                    depositsRef.child(timestamp).setValue(deposits);

                                    usersRef.child(mAuth.getCurrentUser().getUid()).child("wallet").setValue(result)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent intent = new Intent(mContext, WalletActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });

                                    progressDialog.dismiss();
                                }
                                else if (amountInNaira == 600000)
                                {
                                    int result = wallet + 6000;

                                    Deposits deposits = new Deposits();
                                    deposits.setUid(mAuth.getUid());
                                    deposits.setAmount((amountInNaira/100));
                                    deposits.setReferenceNumber(customRef);
                                    deposits.setTimestamp(timestamp);

                                    depositsRef.child(timestamp).setValue(deposits);

                                    usersRef.child(mAuth.getCurrentUser().getUid()).child("wallet").setValue(result)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent intent = new Intent(mContext, WalletActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                }
                                else if (amountInNaira == 1200000)
                                {
                                    int result = wallet + 12000;

                                    Deposits deposits = new Deposits();
                                    deposits.setUid(mAuth.getUid());
                                    deposits.setAmount((amountInNaira/100));
                                    deposits.setReferenceNumber(customRef);
                                    deposits.setTimestamp(timestamp);

                                    depositsRef.child(timestamp).setValue(deposits);

                                    usersRef.child(mAuth.getCurrentUser().getUid()).child("wallet").setValue(result)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent intent = new Intent(mContext, WalletActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                }
                                else if (amountInNaira == 1800000)
                                {
                                    int result = wallet + 18000;

                                    Deposits deposits = new Deposits();
                                    deposits.setUid(mAuth.getUid());
                                    deposits.setAmount((amountInNaira/100));
                                    deposits.setReferenceNumber(customRef);
                                    deposits.setTimestamp(timestamp);

                                    depositsRef.child(timestamp).setValue(deposits);

                                    usersRef.child(mAuth.getCurrentUser().getUid()).child("wallet").setValue(result)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent intent = new Intent(mContext, WalletActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
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
