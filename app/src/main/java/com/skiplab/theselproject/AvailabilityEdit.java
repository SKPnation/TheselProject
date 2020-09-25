package com.skiplab.theselproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.skiplab.theselproject.models.Afternoon;
import com.skiplab.theselproject.models.Early;
import com.skiplab.theselproject.models.Evening;
import com.skiplab.theselproject.models.Morning;
import com.skiplab.theselproject.models.Night;

import java.util.HashMap;

import javax.annotation.Nullable;

public class AvailabilityEdit extends AppCompatActivity {

    Context mContext = AvailabilityEdit.this;

    private CollectionReference earlyDb, morningDb, afternoonDb, eveningDb, nightDb;

    private Button five_am_btn, six_am_btn;
    private Button seven_am_btn, eight_am_btn, nine_am_btn, ten_am_btn, eleven_am_btn;
    private Button twelve_pm_btn, one_pm_btn, two_pm_btn, three_pm_btn, four_pm_btn;
    private Button five_pm_btn, six_pm_btn, seven_pm_btn, eight_pm_btn;
    private Button nine_pm_btn, ten_pm_btn, eleven_pm_btn;

    private ImageButton addToEarlyBtn, addToMorningBtn, addToAfternoonBtn, addToEveningBtn, addToNightBtn;

    private ImageView closeBtn;

    private String TRUE = "true", FALSE="false";

    private String hisUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability_edit);

        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");

        closeBtn = findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addToEarlyBtn = findViewById(R.id.add_to_early_btn);
        addToMorningBtn = findViewById(R.id.add_to_morning_btn);
        addToAfternoonBtn = findViewById(R.id.add_to_afternoon_btn);
        addToEveningBtn = findViewById(R.id.add_to_evening_btn);
        addToNightBtn = findViewById(R.id.add_to_night_btn);

        five_am_btn = findViewById(R.id.five_am_btn);
        six_am_btn = findViewById(R.id.six_am_btn);

        seven_am_btn = findViewById(R.id.seven_am_btn);
        eight_am_btn = findViewById(R.id.eight_am_btn);
        nine_am_btn = findViewById(R.id.nine_am_btn);
        ten_am_btn = findViewById(R.id.ten_am_btn);
        eleven_am_btn = findViewById(R.id.eleven_am_btn);

        twelve_pm_btn = findViewById(R.id.twelve_pm_btn);
        one_pm_btn = findViewById(R.id.one_pm_btn);
        two_pm_btn = findViewById(R.id.two_pm_btn);
        three_pm_btn = findViewById(R.id.three_pm_btn);
        four_pm_btn = findViewById(R.id.four_pm_btn);

        five_pm_btn = findViewById(R.id.five_pm_btn);
        six_pm_btn = findViewById(R.id.six_pm_btn);
        seven_pm_btn = findViewById(R.id.seven_pm_btn);
        eight_pm_btn = findViewById(R.id.eight_pm_btn);

        nine_pm_btn = findViewById(R.id.nine_pm_btn);
        ten_pm_btn = findViewById(R.id.ten_pm_btn);
        eleven_pm_btn = findViewById(R.id.eleven_pm_btn);

        earlyDb = FirebaseFirestore.getInstance().collection("early");
        morningDb = FirebaseFirestore.getInstance().collection("morning");
        afternoonDb = FirebaseFirestore.getInstance().collection("afternoon");
        eveningDb = FirebaseFirestore.getInstance().collection("evening");
        nightDb = FirebaseFirestore.getInstance().collection("night");

        earlyDb.document(hisUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists())
                        {
                            five_am_btn.setVisibility(View.VISIBLE);
                            six_am_btn.setVisibility(View.VISIBLE);

                            if (documentSnapshot.toObject(Early.class).getFive_am().equals(TRUE))
                                five_am_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                five_am_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Early.class).getSix_am().equals(TRUE))
                                six_am_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                six_am_btn.setBackgroundColor(Color.parseColor("#393939"));

                            five_am_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Early.class).getFive_am().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("five_am", FALSE);
                                                earlyDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("five_am", TRUE);
                                                earlyDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }

                                }
                            });
                            six_am_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Early.class).getSix_am().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("six_am", FALSE);
                                                earlyDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("six_am", TRUE);
                                                earlyDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                        }
                        else
                        {
                            five_am_btn.setVisibility(View.GONE);
                            six_am_btn.setVisibility(View.GONE);
                        }
                    }
                });

        morningDb.document(hisUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists())
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);

                            if (documentSnapshot.toObject(Morning.class).getSeven_am().equals(TRUE))
                                seven_am_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                seven_am_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Morning.class).getEight_am().equals(TRUE))
                                eight_am_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                eight_am_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Morning.class).getNine_am().equals(TRUE))
                                nine_am_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                nine_am_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Morning.class).getTen_am().equals(TRUE))
                                ten_am_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                ten_am_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Morning.class).getEleven_am().equals(TRUE))
                                eleven_am_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                eleven_am_btn.setBackgroundColor(Color.parseColor("#393939"));

                            seven_am_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Morning.class).getSeven_am().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("seven_am", FALSE);
                                                morningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("seven_am", TRUE);
                                                morningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                            eight_am_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Morning.class).getEight_am().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("eight_am", FALSE);
                                                morningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("eight_am", TRUE);
                                                morningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                            nine_am_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Morning.class).getNine_am().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("nine_am", FALSE);
                                                morningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("nine_am", TRUE);
                                                morningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                            ten_am_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Morning.class).getTen_am().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("ten_am", FALSE);
                                                morningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("ten_am", TRUE);
                                                morningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                            eleven_am_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Morning.class).getEleven_am().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("eleven_am", FALSE);
                                                morningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("eleven_am", TRUE);
                                                morningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                    }
                });

        afternoonDb.document(hisUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists())
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.VISIBLE);

                            if (documentSnapshot.toObject(Afternoon.class).getTwelve_pm().equals(TRUE))
                                twelve_pm_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                twelve_pm_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Afternoon.class).getOne_pm().equals(TRUE))
                                one_pm_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                one_pm_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Afternoon.class).getTwo_pm().equals(TRUE))
                                two_pm_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                two_pm_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Afternoon.class).getThree_pm().equals(TRUE))
                                three_pm_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                three_pm_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Afternoon.class).getFour_pm().equals(TRUE))
                                four_pm_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                four_pm_btn.setBackgroundColor(Color.parseColor("#393939"));

                            twelve_pm_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Afternoon.class).getTwelve_pm().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("twelve_pm", FALSE);
                                                afternoonDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("twelve_pm", TRUE);
                                                afternoonDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                            one_pm_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Afternoon.class).getOne_pm().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("one_pm", FALSE);
                                                afternoonDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("one_pm", TRUE);
                                                afternoonDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                            two_pm_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Afternoon.class).getTwo_pm().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("two_pm", FALSE);
                                                afternoonDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("two_pm", TRUE);
                                                afternoonDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                            three_pm_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Afternoon.class).getThree_pm().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("three_pm", FALSE);
                                                afternoonDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("three_pm", TRUE);
                                                afternoonDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                            four_pm_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Afternoon.class).getFour_pm().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("four_pm", FALSE);
                                                afternoonDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("four_pm", TRUE);
                                                afternoonDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                    }
                });

        eveningDb.document(hisUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists())
                        {
                            five_pm_btn.setVisibility(View.VISIBLE);
                            six_pm_btn.setVisibility(View.VISIBLE);
                            seven_pm_btn.setVisibility(View.VISIBLE);
                            eight_pm_btn.setVisibility(View.VISIBLE);

                            if (documentSnapshot.toObject(Evening.class).getFive_pm().equals(TRUE))
                                five_pm_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                five_pm_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Evening.class).getSix_pm().equals(TRUE))
                                six_pm_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                six_pm_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Evening.class).getSeven_pm().equals(TRUE))
                                seven_pm_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                seven_pm_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Evening.class).getEight_pm().equals(TRUE))
                                eight_pm_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                eight_pm_btn.setBackgroundColor(Color.parseColor("#393939"));

                            five_pm_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Evening.class).getFive_pm().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("five_pm", FALSE);
                                                eveningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("five_pm", TRUE);
                                                eveningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                            six_pm_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Evening.class).getSix_pm().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("six_pm", FALSE);
                                                eveningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("six_pm", TRUE);
                                                eveningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                            seven_pm_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Evening.class).getSeven_pm().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("seven_pm", FALSE);
                                                eveningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("seven_pm", TRUE);
                                                eveningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                            eight_pm_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Evening.class).getEight_pm().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("eight_pm", FALSE);
                                                eveningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("eight_pm", TRUE);
                                                eveningDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            five_pm_btn.setVisibility(View.GONE);
                            six_pm_btn.setVisibility(View.GONE);
                            seven_pm_btn.setVisibility(View.GONE);
                            eight_pm_btn.setVisibility(View.GONE);
                        }
                    }
                });

        nightDb.document(hisUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists())
                        {
                            nine_pm_btn.setVisibility(View.VISIBLE);
                            ten_pm_btn.setVisibility(View.VISIBLE);
                            eleven_pm_btn.setVisibility(View.VISIBLE);

                            if (documentSnapshot.toObject(Night.class).getNine_pm().equals(TRUE))
                                nine_pm_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                nine_pm_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Night.class).getTen_pm().equals(TRUE))
                                ten_pm_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                ten_pm_btn.setBackgroundColor(Color.parseColor("#393939"));

                            if (documentSnapshot.toObject(Night.class).getEleven_pm().equals(TRUE))
                                eleven_pm_btn.setBackgroundColor(Color.parseColor("#1FA000"));
                            else
                                eleven_pm_btn.setBackgroundColor(Color.parseColor("#393939"));


                            nine_pm_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Night.class).getNine_pm().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("nine_pm", FALSE);
                                                nightDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("nine_pm", TRUE);
                                                nightDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                            ten_pm_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Night.class).getTen_pm().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("ten_pm", FALSE);
                                                nightDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("ten_pm", TRUE);
                                                nightDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });

                            eleven_pm_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (documentSnapshot.toObject(Night.class).getEleven_pm().equals(TRUE))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to FALSE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("eleven_pm", FALSE);
                                                nightDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Set to TRUE");
                                        builder.setMessage("Are you sure?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("eleven_pm", TRUE);
                                                nightDb.document(hisUID).set(hashMap, SetOptions.merge());
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            nine_pm_btn.setVisibility(View.GONE);
                            ten_pm_btn.setVisibility(View.GONE);
                            eleven_pm_btn.setVisibility(View.GONE);
                        }
                    }
                });


        addToEarlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                earlyDb.document(hisUID)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (!documentSnapshot.exists())
                                {
                                    Early early = new Early();
                                    early.setFive_am(FALSE);
                                    early.setSix_am(FALSE);
                                    early.setSlot("0");
                                    early.setUid(hisUID);

                                    earlyDb.document(hisUID).set(early);
                                }
                            }
                        });

            }
        });

        addToMorningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                morningDb.document(hisUID)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (!documentSnapshot.exists())
                                {
                                    Morning morning = new Morning();
                                    morning.setSeven_am(FALSE);
                                    morning.setEight_am(FALSE);
                                    morning.setNine_am(FALSE);
                                    morning.setTen_am(FALSE);
                                    morning.setEleven_am(FALSE);
                                    morning.setSlot("1");
                                    morning.setUid(hisUID);

                                    morningDb.document(hisUID).set(morning);
                                }
                            }
                        });
            }
        });

        addToAfternoonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                afternoonDb.document(hisUID)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (!documentSnapshot.exists())
                                {
                                    Afternoon afternoon = new Afternoon();
                                    afternoon.setTwelve_pm(FALSE);
                                    afternoon.setOne_pm(FALSE);
                                    afternoon.setTwo_pm(FALSE);
                                    afternoon.setThree_pm(FALSE);
                                    afternoon.setFour_pm(FALSE);
                                    afternoon.setSlot("2");
                                    afternoon.setUid(hisUID);

                                    afternoonDb.document(hisUID).set(afternoon);
                                }
                            }
                        });
            }
        });

        addToEveningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eveningDb.document(hisUID)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (!documentSnapshot.exists())
                                {
                                    Evening evening = new Evening();
                                    evening.setFive_pm(FALSE);
                                    evening.setSix_pm(FALSE);
                                    evening.setSeven_pm(FALSE);
                                    evening.setEight_pm(FALSE);
                                    evening.setSlot("3");
                                    evening.setUid(hisUID);

                                    eveningDb.document(hisUID).set(evening);
                                }
                            }
                        });
            }
        });

        addToNightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nightDb.document(hisUID)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (!documentSnapshot.exists())
                                {
                                    Night night = new Night();
                                    night.setNine_pm(FALSE);
                                    night.setTen_pm(FALSE);
                                    night.setEleven_pm(FALSE);
                                    night.setSlot("4");
                                    night.setUid(hisUID);

                                    nightDb.document(hisUID).set(night);
                                }
                            }
                        });
            }
        });

    }
}
