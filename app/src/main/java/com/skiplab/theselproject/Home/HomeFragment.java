package com.skiplab.theselproject.Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.skiplab.theselproject.Activity.NotificationsActivity;
import com.skiplab.theselproject.Adapter.AdapterConsultant;
import com.skiplab.theselproject.Adapter.AdapterPosts;
import com.skiplab.theselproject.AddPost.SelectMood;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Consultation.ChatRoomsActivity;
import com.skiplab.theselproject.Consultation.WalletActivity;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Settings.AccountSettingsActivity;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.Post;
import com.skiplab.theselproject.models.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private NestedScrollView nestedScrollView;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] items;
    private FloatingActionButton fab;
    private FrameLayout frameLayout;

    private ImageView optionsBtn, mAvaterIv, walletBtn, videosBtn, addPostBtn;
    private EditText share_post_et;

    private TextView feedTitleTv, txt_notification_badge;

    ListView listView;

    FirebaseAuth firebaseAuth;

    private AdView adView;

    private RelativeLayout relLayout1;
    private SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView, cRecyclerView;
    List<Post> postList;
    List<User> consultantList;
    AdapterPosts adapterPosts;
    AdapterConsultant adapterConsultant;

    FirebaseDatabase db;
    DatabaseReference postDb;
    DatabaseReference userDb;
    CollectionReference myNotificationDb;

    String myUid, myName;
    String selCategory;

    private ProgressBar mProgressBar;

    boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;

    public static final int ITEM_PER_AD = 4;

    private int i=0;
    private int count;

    private BottomAppBar bottomAppBar;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance();
        postDb = db.getReference("posts");
        userDb = db.getReference("users");
        myNotificationDb = FirebaseFirestore.getInstance().collection("myNotifications");

        postDb.keepSynced(true);

        relLayout1 = view.findViewById(R.id.relLayout1);

        mDrawerLayout = view.findViewById(R.id.drawer_layout);
        listView = view.findViewById(R.id.navList);
        bottomAppBar = view.findViewById(R.id.bottomAppBar);
        mProgressBar = view.findViewById(R.id.progressBar);
        nestedScrollView = view.findViewById(R.id.nsv);
        frameLayout = view.findViewById(R.id.consult_notify_layout);

        fab = view.findViewById(R.id.fab);
        walletBtn = view.findViewById(R.id.walletBtn);
        //videosBtn = view.findViewById(R.id.videosBtn);
        share_post_et = view.findViewById(R.id.share_post_et);
        //addPostBtn = view.findViewById(R.id.add_post);
        mAvaterIv = view.findViewById(R.id.avatarIv);
        optionsBtn = view.findViewById(R.id.optionsToolbar);
        feedTitleTv = view.findViewById(R.id.app_name);
        txt_notification_badge = view.findViewById(R.id.txt_notification_badge);

        adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.banner_ad_unit_id));

        MobileAds.initialize(getActivity(), getString(R.string.banner_ad_unit_id));
        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        postList = new ArrayList<>();
        consultantList = new ArrayList<>();

        cRecyclerView = view.findViewById(R.id.recycler_consultants);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        cRecyclerView.setLayoutManager(linearLayoutManager1);

        recyclerView = view.findViewById(R.id.recycler_posts);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //Show latest post first, for the load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration  dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapterPosts = new AdapterPosts(getActivity(), postList);


        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        items = new String[]{"Relationship", "Addiction", "Depression", "Parenting", "Career", "Self-Esteem", "Disabilities", "Hopes",
                "Family", "Anxiety", "Pregnancy", "Religion", "Business", "Weight Loss", "Fitness", "Grief", "Bullying", "Education", "Music", "Helpful Tips"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), R.layout.list_item, R.id.listItem, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            mDrawerLayout.closeDrawer(GravityCompat.START);
            DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            if (selectedItem.equals(selCategory)){
                //..
            }
            else {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage("This application is under maintenance.")
                        .create();
                alertDialog.show();
                /*currentUserRef.child("selectedCategory").setValue(selectedItem);
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().finish();*/
            }

        });


        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(optionsBtn);
            }
        });

        share_post_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage("This application is under maintenance.")
                        .create();
                alertDialog.show();
                //startActivity(new Intent(getActivity(), SelectMood.class));
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                //..
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

        });

        share_post_et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage("This application is under maintenance.")
                        .create();
                alertDialog.show();
                //startActivity(new Intent(getActivity(), SelectMood.class));
                return false;
            }
        });

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_menu:
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setMessage("This application is under maintenance.")
                                .create();
                        alertDialog.show();
                        //mDrawerLayout.openDrawer(GravityCompat.START);

                        break;
                    case R.id.nav_notifications:
                        i++;

                        Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (i == 1){
                                    AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
                                    builder.setMessage("This application is under maintenance.");
                                    builder.show();
                                    //startActivity(new Intent(getActivity(), NotificationsActivity.class));

                                } else if (i == 2){
                                    Log.d(TAG, "IconDoubleClick: Double tap");
                                }
                                i=0;
                            }
                        }, 200);
                        break;

                    case R.id.nav_settings:
                        i++;

                        Handler handler4 = new Handler();
                        handler4.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (i == 1){
                                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                            .setMessage("This application is under maintenance.")
                                            .create();
                                    alertDialog.show();
                                    //startActivity(new Intent(getActivity(), AccountSettingsActivity.class));

                                } else if (i == 2){
                                    Log.d(TAG, "IconDoubleClick: Double tap");
                                }
                                i=0;
                            }
                        }, 200);
                        break;
                }
                return false;
            }
        });

        userDb.orderByKey().equalTo(firebaseAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            User user = ds.getValue(User.class);
                            selCategory = user.getSelectedCategory();

                            if (user.getIsStaff().equals("true"))
                            {
                                frameLayout.setVisibility(View.VISIBLE);
                                walletBtn.setVisibility(View.GONE);
                                initNotificationUpdate();
                            }
                            else if (user.getIsStaff().equals("admin"))
                            {
                                walletBtn.setVisibility(View.GONE);
                                frameLayout.setVisibility(View.GONE);
                            }
                            else
                            {
                                frameLayout.setVisibility(View.GONE);
                            }


                            if (selCategory.isEmpty())
                            {
                                mProgressBar.setVisibility(View.GONE);
                                i++;

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDrawerLayout.openDrawer(GravityCompat.START);
                                    }
                                }, 1000);
                            }
                            else if (selCategory.equals("#COVID19 NIGERIA") || selCategory.equals("Low self-esteem"))
                            {
                                mProgressBar.setVisibility(View.GONE);
                                i++;

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDrawerLayout.openDrawer(GravityCompat.START);
                                    }
                                }, 1000);
                            }
                            else
                            {
                                feedTitleTv.setText(selCategory);
                                feedTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);

                                loadPosts();
                            }

                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Common.isConnectedToTheInternet(getContext()))
                                    {
                                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                                .setMessage("This application is under maintenance.")
                                                .create();
                                        alertDialog.show();
                                        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                                        {
                                            if (user.getIsStaff().equals("false"))
                                            {
                                            *//*AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                                    .setMessage("The private consultation feature is currently undergoing an upgrade")
                                                    .create();
                                            alertDialog.show();*//*
                                                if (!ds.hasChild("wallet")){
                                                    DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    currentUserRef.child("wallet").setValue(0);

                                                    Intent intent = new Intent(getActivity(), SelectCategory.class);
                                                    startActivity(intent);
                                                }
                                                else{
                                                    Intent intent = new Intent(getActivity(), SelectCategory.class);
                                                    startActivity(intent);

                                                }
                                            }
                                            else if (user.getIsStaff().equals("true"))
                                            {
                                                startActivity(new Intent(getActivity(), ChatRoomsActivity.class));
                                            *//*AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                                    .setMessage("The private consultation feature is currently undergoing an upgrade")
                                                    .create();
                                            alertDialog.show();*//*
                                            }
                                            else
                                            {
                                                Intent intent = new Intent(getActivity(), SelectCategory.class);
                                                startActivity(intent);
                                            }
                                        }
                                        else
                                        {
                                            AlertDialog alertDialog =new AlertDialog.Builder(getActivity())
                                                    .setTitle("Upgrade your OS")
                                                    .setMessage("To use this feature, your Android OS must be 8.0 and above!")
                                                    .create();
                                            alertDialog.show();
                                        }*/
                                    }
                                    else
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(getActivity())
                                                .setMessage("Please check your internet connection!")
                                                .create();
                                        alertDialog.show();
                                    }

                                }
                            });


                            walletBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Common.isConnectedToTheInternet(getContext()))
                                    {
                                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                                .setMessage("This application is under maintenance.")
                                                .create();
                                        alertDialog.show();
                                        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                                        {
                                            if (user.getIsStaff().equals("false"))
                                            {
                                            *//*AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                                    .setMessage("The private consultation feature is currently undergoing an upgrade")
                                                    .create();
                                            alertDialog.show();*//*
                                                if (!ds.hasChild("wallet")){
                                                    DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    currentUserRef.child("wallet").setValue(0);

                                                    Intent intent = new Intent(getActivity(), WalletActivity.class);
                                                    startActivity(intent);
                                                }
                                                else{
                                                    Intent intent = new Intent(getActivity(), WalletActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                            else
                                            {
                                                //..
                                            }
                                        }
                                        else
                                        {
                                            AlertDialog alertDialog =new AlertDialog.Builder(getActivity())
                                                    .setTitle("Upgrade your OS")
                                                    .setMessage("To use this feature, your Android OS must be 8.0 and above!")
                                                    .create();
                                            alertDialog.show();
                                        }*/
                                    }
                                    else
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(getActivity())
                                                .setMessage("Please check your internet connection!")
                                                .create();
                                        alertDialog.show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }
                });

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage("This application is under maintenance.")
                        .create();
                alertDialog.show();
                //startActivity(new Intent(getActivity(),ConsultationNotificationActivity.class));
            }
        });

        loadConsultants();
        setupDrawer();

        return view;
    }

    private void showMoreOptions(ImageView optionsBtn)
    {
        PopupMenu popupMenu = new PopupMenu(getActivity(), optionsBtn, Gravity.END);

        popupMenu.getMenu().add(Menu.NONE,0,0, "Video library");
        popupMenu.getMenu().add(Menu.NONE,1,0, "Thesel gallery");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id==0){
                    startActivity(new Intent(getActivity(), VideoGallery.class));
                }
                else if (id==1) {
                    startActivity(new Intent(getActivity(), PhotoGalleryActivity.class));
                }
                return false;
            }
        });
        //show menu
        popupMenu.show();
    }

    private void initNotificationUpdate()
    {
        myNotificationDb
                .whereEqualTo("counsellor_id",myUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if (task.getResult().size() > 0)
                            {
                                task.getResult().getQuery().whereEqualTo("read",false)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                count = documentSnapshots.size();

                                                if (count == 0)
                                                    txt_notification_badge.setVisibility(View.GONE);
                                                else
                                                {
                                                    txt_notification_badge.setVisibility(View.VISIBLE);
                                                    if (count<=9)
                                                        txt_notification_badge.setText(String.valueOf(count));
                                                    else
                                                        txt_notification_badge.setText("9+");
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void loadPosts()
    {
        Query queryPosts = postDb.orderByChild("pCategory").equalTo(selCategory);
        //get all data from this reference
        queryPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Post post = ds.getValue(Post.class);

                    //pd.dismiss();
                    mProgressBar.setVisibility(View.GONE);
                    //add to list
                    postList.add(post);

                    adapterPosts.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..

            }
        });
    }



    private void loadConsultants()
    {
        Query querySelCategory = userDb.orderByKey().equalTo(firebaseAuth.getCurrentUser().getUid());
        querySelCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    String selCategory = user.getSelectedCategory();

                    try{
                        UniversalImageLoader.setImage(user.getProfile_photo(), mAvaterIv, null, "");
                    }
                    catch (Exception e){
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (user.getIsStaff().equals("true") || selCategory.equals("Helpful Tips") || selCategory.equals("#COVID19 NIGERIA")){
                        relLayout1.setVisibility(View.GONE);
                    }
                    else
                    {
                        Query queryUsers = userDb.orderByChild("isStaff").equalTo("true");

                        queryUsers.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                consultantList.clear();
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    User user = ds.getValue(User.class);

                                    consultantList.add(user);

                                    adapterConsultant = new AdapterConsultant(getActivity(), consultantList);
                                    adapterConsultant.notifyDataSetChanged();
                                    cRecyclerView.setAdapter(adapterConsultant);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //pd.dismiss();
                                //Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

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

    private void setupDrawer()
    {
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),mDrawerLayout,R.string.open,R.string.close)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onResume() {
        super.onResume();
        initNotificationUpdate();
    }
}