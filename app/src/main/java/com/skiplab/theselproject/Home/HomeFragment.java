package com.skiplab.theselproject.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Activity.NotificationsActivity;
import com.skiplab.theselproject.Adapter.AdapterConsultant;
import com.skiplab.theselproject.Adapter.AdapterPosts;
import com.skiplab.theselproject.AddPost.SelectMood;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.Settings.AccountSettingsActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.Post;
import com.skiplab.theselproject.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private NestedScrollView nestedScrollView;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] items;
    private String mActivityTitle;
    private FloatingActionButton fab;

    private ImageView optionsBtn, mAvaterIv, wklyVideosBtn;
    private EditText share_post_et;

    private TextView feedTitleTv;

    ListView listView;

    FirebaseAuth firebaseAuth;

    private AdView adView;

    private RelativeLayout relLayout1;
    private SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView, cRecyclerView;
    List<Post> postList;
    List<Object> mRecyclerViewItems = new ArrayList<>();
    List<User> consultantList;
    AdapterPosts adapterPosts;
    AdapterConsultant adapterConsultant;

    FirebaseDatabase db;
    DatabaseReference postDb;
    DatabaseReference userDb;

    String myUid, myName;
    String selCategory;

    private ProgressBar mProgressBar;

    private Timer timer=new Timer();
    private final long DELAY = 1000; // milliseconds

    private final int ITEM_LOAD_COUNT = 4;
    private int total_item = 0, last_visible_item;
    boolean isLoading=false, isMaxData=false;

    String last_node="", last_key="";

    private int i=0;

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
        postDb.keepSynced(true);

        relLayout1 = view.findViewById(R.id.relLayout1);

        mDrawerLayout = view.findViewById(R.id.drawer_layout);
        listView = view.findViewById(R.id.navList);
        bottomAppBar = view.findViewById(R.id.bottomAppBar);
        mProgressBar = view.findViewById(R.id.progressBar);
        nestedScrollView = view.findViewById(R.id.nsv);

        fab = view.findViewById(R.id.fab);
        wklyVideosBtn = view.findViewById(R.id.weekly_videos);
        share_post_et = view.findViewById(R.id.share_post_et);
        mAvaterIv = view.findViewById(R.id.avatarIv);
        optionsBtn = view.findViewById(R.id.optionsToolbar);
        feedTitleTv = view.findViewById(R.id.app_name);
        mActivityTitle = getActivity().getTitle().toString();

        adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.banner_ad_unit_id));

        MobileAds.initialize(getActivity(), getString(R.string.banner_ad_unit_id));
        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

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

        adapterPosts = new AdapterPosts(getActivity());
        recyclerView.setAdapter(adapterPosts);

        //init post list
        postList = new ArrayList<>();
        consultantList = new ArrayList<>();

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        items = new String[]{"Relationship", "Addiction", "Depression", "Parenting", "Career", "Low self-esteem",
                "Family", "Anxiety", "Pregnancy", "Business", "Weight Loss", "Fitness", "Helpful Tips", "#COVID19 NIGERIA"};
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
                currentUserRef.child("selectedCategory").setValue(selectedItem);
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().finish();
            }

        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SelectCategory.class));
            }
        });

        share_post_et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startActivity(new Intent(getActivity(), SelectMood.class));
                return false;
            }
        });

        wklyVideosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), VideoGallery.class));
            }
        });

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_menu:
                        mDrawerLayout.openDrawer(GravityCompat.START);

                        break;
                    case R.id.nav_notifications:
                        i++;

                        Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (i == 1){

                                    startActivity(new Intent(getActivity(), NotificationsActivity.class));

                                } else if (i == 2){
                                    Log.d(TAG, "IconDoubleClick: Double tap");
                                }
                                i=0;
                            }
                        }, 500);
                        break;

                    case R.id.nav_post:
                        i++;

                        Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (i == 1){

                                    startActivity(new Intent(getActivity(), SelectMood.class));

                                } else if (i == 2){
                                    Log.d(TAG, "IconDoubleClick: Double tap");
                                }
                                i=0;
                            }
                        }, 500);
                        break;

                    case R.id.nav_settings:
                        i++;

                        Handler handler4 = new Handler();
                        handler4.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (i == 1){

                                    startActivity(new Intent(getActivity(), AccountSettingsActivity.class));

                                } else if (i == 2){
                                    Log.d(TAG, "IconDoubleClick: Double tap");
                                }
                                i=0;
                            }
                        }, 500);
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

                            if (selCategory.isEmpty())
                            {
                                mProgressBar.setVisibility(View.GONE);
                                i++;

                                Handler handler1 = new Handler();
                                handler1.postDelayed(new Runnable() {
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

                                getLastKeyFromFirebase();
                                getPosts();
                                //loadPosts();


                                nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                                    if(v.getChildAt(v.getChildCount() - 1) != null) {
                                        if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                                                scrollY > oldScrollY) {

                                            try {
                                                //code to fetch more data for endless scrolling
                                                total_item = linearLayoutManager.getItemCount();
                                                last_visible_item = linearLayoutManager.findLastVisibleItemPosition();

                                                if (!isLoading && total_item <= ((last_visible_item + ITEM_LOAD_COUNT))){
                                                    getPosts();
                                                    isLoading = true;
                                                }
                                            }
                                            catch (Exception e){
                                                Toast.makeText(getActivity(), ""+e, Toast.LENGTH_LONG).show();

                                            }
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




        loadConsultants();
        setupDrawer();

        return view;
    }

    private void getPosts() {
        if (!isMaxData)
        {
            try {
                Query query;
                if (TextUtils.isEmpty(last_node))
                    query = postDb
                            .orderByChild("pCategory")
                            .equalTo(selCategory)
                            .limitToLast(ITEM_LOAD_COUNT);
                else
                    query = postDb
                            .orderByChild("pCategory")
                            .equalTo(selCategory)
                            .startAt(last_node)
                            .limitToLast(ITEM_LOAD_COUNT);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren())
                        {
                            List<Post> newPosts = new ArrayList<>();
                            for (DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                newPosts.add(ds.getValue(Post.class));
                            }
                            last_node = newPosts.get(newPosts.size()-1).getpId();

                            if (!last_node.equals(last_key))
                                newPosts.remove(newPosts.size()-1);
                            else
                                last_node = "end";

                            adapterPosts.addAll(newPosts);
                            isLoading = false;
                        }
                        else
                        {
                            isLoading = false;
                            isMaxData = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        isLoading = false;
                    }
                });
            }
            catch (Exception e){
                Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLastKeyFromFirebase() {
        Query getLastKey = postDb
                .orderByChild("pCategory")
                .equalTo(selCategory)
                .limitToLast(1);

        getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot lastKey: dataSnapshot.getChildren()){
                    last_key = lastKey.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Cannot get last key",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadPosts() {
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
                    swipeRefreshLayout.setRefreshing( false );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..

            }
        });
    }

    private void loadConsultants() {
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

    private void setupDrawer() {

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),mDrawerLayout,R.string.open,R.string.close)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Thesel");
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Feed");
                getActivity().invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    /*@Override
    public void onResume() {
        super.onResume();
        getLastKeyFromFirebase();
        getPosts();
    }*/
}


