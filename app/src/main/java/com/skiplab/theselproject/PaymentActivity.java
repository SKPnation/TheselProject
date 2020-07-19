package com.skiplab.theselproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.AddPost.NewPostActivity;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.models.Cards;
import com.skiplab.theselproject.models.ReferenceNumber;
import com.skiplab.theselproject.models.Sessions;
import com.skiplab.theselproject.models.User;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class PaymentActivity extends AppCompatActivity {

    DatabaseReference usersRef, sessionsRef, requestsRef, cardsRef;
    FirebaseAuth mAuth;
    FirebaseUser fUser;

    private AppCompatEditText etEmail, etMastercardName, etMastercardCard, etVerveName, etVerveCard, etVisaName, etVisaCard, etCvv;
    private LinearLayoutCompat spContainer;
    private AppCompatSpinner spMonth, spYear;
    private AppCompatButton btnProceed;
    private ImageView mastercard, verve, visa, backBtn;
    private CheckBox cbRemember;

    ArrayAdapter<String> adapter;

    long cost;
    String strCost;
    String counsellor_id;
    String requestPlan;

    String AES = "AES";

    String fiveThousand = "N5,000";
    String tenThousand = "N10,000";
    String fifteenThousand = "N15,000";
    String twentyThousand = "N20,000";
    String twentyFiveThousand = "N25,000";
    String thirtyThousand = "N30,000";

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        final Intent intent = getIntent();
        cost = intent.getLongExtra("cost",0);
        counsellor_id = intent.getStringExtra("counsellor_id");
        requestPlan = intent.getStringExtra("plan");
        strCost = String.valueOf(cost);


        cardsRef = FirebaseDatabase.getInstance().getReference("cards");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
        requestsRef = FirebaseDatabase.getInstance().getReference("requests");
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();

        backBtn = findViewById(R.id.backArrow);
        mastercard = findViewById(R.id.mastercard);
        verve = findViewById(R.id.verve);
        visa = findViewById(R.id.visa);

        etEmail = findViewById(R.id.et_main_email);
        etMastercardName = findViewById(R.id.et_mastercard_name);
        etVerveName = findViewById(R.id.et_verve_name);
        etVisaName = findViewById(R.id.et_visa_name);
        etMastercardCard = (AppCompatEditText) findViewById(R.id.et_mastercard_card);
        etVerveCard = (AppCompatEditText) findViewById(R.id.et_verve_card);
        etVisaCard = (AppCompatEditText) findViewById(R.id.et_visa_card);
        spMonth = (AppCompatSpinner) findViewById(R.id.sp_main_month);
        spYear = (AppCompatSpinner) findViewById(R.id.sp_main_year);
        etCvv = (AppCompatEditText) findViewById(R.id.et_main_cvv);
        spContainer = findViewById(R.id.sp_container);
        cbRemember = findViewById(R.id.cbRemember);
        btnProceed = (AppCompatButton) findViewById(R.id.bt_main_proceed);

        adapter = new ArrayAdapter<String>(PaymentActivity.this, android.R.layout.simple_list_item_1, (String[]) getYears());
        //here we set the adapter to the year spinner
        spYear.setAdapter(adapter);

        etEmail.setText(fUser.getEmail());

        if (strCost.equals("20000"))
            btnProceed.setText(tenThousand);
        else if (strCost.equals("1000000"))
            btnProceed.setText(tenThousand);
        else if (strCost.equals("1500000"))
            btnProceed.setText(fifteenThousand);
        else if (strCost.equals("2000000"))
            btnProceed.setText(twentyThousand);
        else if (strCost.equals("2500000"))
            btnProceed.setText(twentyFiveThousand);
        else if (strCost.equals("3000000"))
            btnProceed.setText(thirtyThousand);

        backBtn.setOnClickListener(v -> finish());

        mastercard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etMastercardName.setVisibility(View.VISIBLE);
                etMastercardCard.setVisibility(View.VISIBLE);
                etVerveName.setVisibility(View.GONE);
                etVerveName.setText("");
                etVerveCard.setVisibility(View.GONE);
                etVerveCard.setText("");
                etVisaName.setVisibility(View.GONE);
                etVisaName.setText("");
                etVisaCard.setVisibility(View.GONE);
                etVisaCard.setText("");
                etCvv.setVisibility(View.VISIBLE);
                etCvv.setText("");
                spContainer.setVisibility(View.VISIBLE);

                cardsRef.orderByKey().equalTo(mAuth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    Cards cards = ds.getValue(Cards.class);

                                    if (!cards.getCardName1().isEmpty()){
                                        etMastercardName.setText(cards.getCardName1()); }
                                    if (!cards.getCardNumber1().isEmpty()) {
                                        etMastercardCard.setText(cards.getCardNumber1()); }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //..
                            }
                        });
            }
        });

        verve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMastercardName.setVisibility(View.GONE);
                etMastercardName.setText("");
                etMastercardCard.setVisibility(View.GONE);
                etMastercardCard.setText("");
                etVerveName.setVisibility(View.VISIBLE);
                etVerveCard.setVisibility(View.VISIBLE);
                etVisaName.setVisibility(View.GONE);
                etVisaName.setText("");
                etVisaCard.setVisibility(View.GONE);
                etVisaCard.setText("");
                etCvv.setVisibility(View.VISIBLE);
                etCvv.setText("");
                spContainer.setVisibility(View.VISIBLE);

                cardsRef.orderByKey().equalTo(mAuth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    Cards cards = ds.getValue(Cards.class);

                                    if (!cards.getCardName2().isEmpty()){
                                        etVerveName.setText(cards.getCardName2()); }
                                    if (!cards.getCardNumber2().isEmpty()) {
                                        etVerveCard.setText(cards.getCardNumber2()); }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //...
                            }
                        });
            }
        });

        visa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMastercardName.setVisibility(View.GONE);
                etMastercardName.setText("");
                etMastercardCard.setVisibility(View.GONE);
                etMastercardCard.setText("");
                etVerveName.setVisibility(View.GONE);
                etVerveName.setText("");
                etVerveCard.setVisibility(View.GONE);
                etVerveCard.setText("");
                etVisaName.setVisibility(View.VISIBLE);
                etVisaCard.setVisibility(View.VISIBLE);
                etCvv.setVisibility(View.VISIBLE);
                etCvv.setText("");
                spContainer.setVisibility(View.VISIBLE);

                cardsRef.orderByKey().equalTo(mAuth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    Cards cards = ds.getValue(Cards.class);

                                    if (!cards.getCardName3().isEmpty()){
                                        etVisaName.setText(cards.getCardName3()); }
                                    if (!cards.getCardNumber3().isEmpty()) {
                                        etVisaCard.setText(cards.getCardNumber3()); }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //..
                            }
                        });
            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestPlan.equals("Free"))
                {
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("uid",fUser.getUid());

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("trials");
                    reference.child(fUser.getUid()).setValue(hashMap);

                    Intent intent = new Intent(PaymentActivity.this, ChatActivity.class);
                    intent.putExtra("hisUid", counsellor_id);
                    intent.putExtra("plan", requestPlan);
                    startActivity(intent);
                }
                else
                {
                    String cardName1 = etMastercardName.getText().toString().trim();
                    String cardNumber1 = etMastercardCard.getText().toString().trim();
                    String cardName2 = etVerveName.getText().toString().trim();
                    String cardNumber2 = etVerveCard.getText().toString().trim();
                    String cardName3 = etVisaName.getText().toString().trim();
                    String cardNumber3 = etVisaCard.getText().toString().trim();

                    if (cbRemember.isChecked())
                    {
                        if (!cardName1.isEmpty() && !cardNumber1.isEmpty()){
                            cardsRef.child(fUser.getUid()).child("cardName1").setValue(cardName1);
                            cardsRef.child(fUser.getUid()).child("cardNumber1").setValue(cardNumber1);
                        }
                        else if (!cardName2.isEmpty() && !cardNumber2.isEmpty()){
                            cardsRef.child(fUser.getUid()).child("cardName2").setValue(cardName2);
                            cardsRef.child(fUser.getUid()).child("cardNumber2").setValue(cardNumber2);
                        }
                        else if (!cardName3.isEmpty() && !cardNumber3.isEmpty()){
                            cardsRef.child(fUser.getUid()).child("cardName3").setValue(cardName3);
                            cardsRef.child(fUser.getUid()).child("cardNumber3").setValue(cardNumber3);
                        }
                    }

                    usersRef.orderByKey().equalTo(fUser.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                                        User user = new User();
                                        String username  = user.getUsername();
                                        String uDp = user.getProfile_photo();

                                        if (!cardName1.isEmpty() && !cardNumber1.isEmpty())
                                        {
                                            if (Common.isConnectedToTheInternet(getBaseContext())) {
                                                chargeCard(cost, cardName1, cardNumber1, spMonth, spYear, etCvv, username, uDp);
                                            } else{
                                                i++;

                                                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                                                builder.setMessage("Please check your internet connection");
                                                builder.show();

                                                Handler handler1 = new Handler();
                                                handler1.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(PaymentActivity.this, DashboardActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }, 1000);

                                            }
                                        }
                                        else if (!cardName2.isEmpty() && !cardNumber2.isEmpty())
                                        {
                                            if (Common.isConnectedToTheInternet(getBaseContext())) {
                                                chargeCard(cost, cardName2, cardNumber2, spMonth, spYear, etCvv, username, uDp);
                                            } else{
                                                i++;

                                                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                                                builder.setMessage("Please check your internet connection");
                                                builder.show();

                                                Handler handler1 = new Handler();
                                                handler1.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(PaymentActivity.this, DashboardActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }, 1000);
                                            }
                                        }
                                        else if (!cardName3.isEmpty() && !cardNumber3.isEmpty())
                                        {
                                            if (Common.isConnectedToTheInternet(getBaseContext()))
                                            {
                                                chargeCard(cost, cardName3, cardNumber3, spMonth, spYear, etCvv, username, uDp);
                                            } else{
                                                i++;

                                                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                                                builder.setMessage("Please check your internet connection");
                                                builder.show();

                                                Handler handler1 = new Handler();
                                                handler1.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(PaymentActivity.this, DashboardActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }, 1000);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    //...
                                }
                            });
                }

            }
        });
    }

    private void chargeCard(long cost, String cardName, String cardNumber, AppCompatSpinner spMonth, AppCompatSpinner spYear, AppCompatEditText etCvv, String username, String uDp) {

        String cardCvv = etCvv.getText().toString().trim();
        String month, year;

        if (spMonth.getSelectedItemPosition() > 0 && spYear.getSelectedItemPosition() > 0)
        {
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
                        btnProceed.setEnabled(false);

                        ProgressDialog pd = new ProgressDialog(PaymentActivity.this);
                        pd.setMessage("please wait...");
                        pd.show();

                        //every transaction requires you to send along a unique reference
                        String customRef = generateReference();

                        //setup a charge object to set values like amount, reference etc
                        Charge charge = new Charge();
                        charge.setEmail("ayomideseaz@gmail.com");
                        //the amount(in KOBO eg 1000 kobo = 10 Naira) the customer is to pay for the product or service
                        // basically add 2 extra zeros at the end of your amount to convert from kobo to naira.
                        charge.setAmount((int) cost);
                        charge.setReference(customRef);
                        charge.setCurrency("NGN");
                        charge.setCard(card);

                        //Charge the card
                        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
                            @Override
                            public void onSuccess(Transaction transaction) {
                                btnProceed.setEnabled(true);

                                sessionsRef.orderByKey().equalTo(counsellor_id)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                    Sessions sessions = ds.getValue(Sessions.class);

                                                    //convert timestamp to dd/mm/yyyy hh:mm am/pm
                                                    String timestamp = String.valueOf(System.currentTimeMillis());
                                                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                                                    calendar.setTimeInMillis(Long.parseLong(timestamp));
                                                    String paymentTime = DateFormat.format("dd/MM/yyyy   hh:mm aa", calendar).toString();

                                                    ReferenceNumber referenceNumber = new ReferenceNumber();
                                                    referenceNumber.setUid(mAuth.getUid());
                                                    referenceNumber.setuDp(uDp);
                                                    referenceNumber.setAmount(cost);
                                                    referenceNumber.setuName(username);
                                                    referenceNumber.setReferenceNumber(customRef);
                                                    referenceNumber.setTimestamp(paymentTime);

                                                    FirebaseDatabase.getInstance().getReference("referenceNumbers")
                                                            .child(timestamp).setValue(referenceNumber);

                                                    Intent intent = new Intent(PaymentActivity.this, ChatActivity.class);
                                                    intent.putExtra("hisUid", counsellor_id);
                                                    intent.putExtra("plan", requestPlan);
                                                    startActivity(intent);

                                                    Long consultations = sessions.getCompleted();
                                                    sessionsRef.child(counsellor_id).child("completed").setValue(consultations+1);

                                                    pd.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                //...
                                            }
                                        });

                            }

                            @Override
                            public void beforeValidate(Transaction transaction) {
                                pd.dismiss();

                                //snackBar("beforeValidate");
                            }

                            @Override
                            public void onError(Throwable error, Transaction transaction) {
                                btnProceed.setEnabled(true);
                                Toast.makeText(PaymentActivity.this, ""+error.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        });
                    } else {
                        Toast.makeText(PaymentActivity.this, "Invalid Card", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else {
            Toast.makeText(PaymentActivity.this, "Select Card Expiry Date", Toast.LENGTH_LONG).show();
        }

    }


    /*private String encrypt(String cardNumber) throws Exception{
        SecretKeySpec key = generateKey(cardNumber);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encval = c.doFinal(cardNumber.getBytes());
        String encryptedValue = Base64.encodeToString(encval, Base64.DEFAULT);
        return encryptedValue;
    }

    private SecretKeySpec generateKey(String cardNumber) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte [] bytes = cardNumber.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte [] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }*/

    private Object getYears() {
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
}
