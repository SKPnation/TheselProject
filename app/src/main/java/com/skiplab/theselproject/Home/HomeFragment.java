package com.skiplab.theselproject.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Adapter.AdapterPosts;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.LatenessReportActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Post;
import com.skiplab.theselproject.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] items;
    private String mActivityTitle;

    private ImageView drawerIconIv, optionsBtn;

    private TextView feedTitleTv, selCategoryHint;

    ListView listView;

    FirebaseAuth firebaseAuth;

    private AdView adView;

    RelativeLayout relLayout1;
    LinearLayout headerlayout;

    RecyclerView recyclerView;
    List<Post> postList;
    List<Object> mRecyclerViewItems = new ArrayList<>();
    List<User> consultantList;
    AdapterPosts adapterPosts;

    FirebaseDatabase db;
    DatabaseReference postDb;
    DatabaseReference userDb;

    String myUid;

    private ProgressBar mProgressBar;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        MobileAds.initialize(getActivity(), "ca-app-pub-4813843298673497~9934761507");

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance();
        postDb = db.getReference("posts");
        userDb = db.getReference("users");

        mProgressBar = view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.banner_ad_unit_id));

        MobileAds.initialize(getActivity(), getString(R.string.banner_ad_unit_id));
        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        relLayout1 = view.findViewById(R.id.relLayout1);

        recyclerView = view.findViewById(R.id.recycler_posts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //Show latest post first, for the load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //init post list
        postList = new ArrayList<>();
        consultantList = new ArrayList<>();


        drawerIconIv = view.findViewById(R.id.drawer_icon);
        optionsBtn = view.findViewById(R.id.optionsToolbar);
        feedTitleTv = view.findViewById(R.id.app_name);
        selCategoryHint = view.findViewById(R.id.selCategoryHint);
        mDrawerLayout = view.findViewById(R.id.drawer_layout);
        mActivityTitle = getActivity().getTitle().toString();
        listView = view.findViewById(R.id.navList);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        items = new String[]{"Relationship", "Addiction", "Depression", "Parenting", "Speaking Skills", "Child Abuse", "Low self-esteem",
                "Family", "Anxiety", "Pregnancy", "Business", "Weight Loss", "Fitness", "Helpful Tips", "#COVID19 NIGERIA"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), R.layout.list_item, R.id.listItem, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            //String selectedItem  = ((TextView)view).getText().toString();
            mDrawerLayout.closeDrawer(GravityCompat.START);
            DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            currentUserRef.child("selectedCategory").setValue(selectedItem);
            Intent intent = new Intent(getActivity(), DashboardActivity.class);
            startActivity(intent);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().finish();

        });

        if(Common.isConnectedToTheInternet(getContext())){
            Query querySelCategory = userDb.orderByKey().equalTo(firebaseAuth.getCurrentUser().getUid());
            querySelCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        User user = ds.getValue(User.class);
                        String selCategory = user.getSelectedCategory();

                        if (selCategory.isEmpty()){
                            mProgressBar.setVisibility(View.GONE);
                            ///..
                        }
                        else {
                            feedTitleTv.setText(selCategory);
                            feedTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //..
                }
            });

            loadPosts();

        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please check your internet connection");
            builder.show();
        }

        drawerIconIv.setOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.START));
        //addDrawersItem();
        setupDrawer();

        optionsBtn.setOnClickListener(v -> showMoreOptions(optionsBtn, myUid));

        return view;
    }


    private void showMoreOptions(ImageView optionsBtn, String myUid)
    {
        PopupMenu popupMenu = new PopupMenu(getActivity(), optionsBtn, Gravity.END);

        Query query = userDb.orderByKey().equalTo(myUid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.getValue(User.class).getIsStaff().equals("admin"))
                        popupMenu.getMenu().add(Menu.NONE,0,0, "Reports");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });

        popupMenu.getMenu().add(Menu.NONE,1,0, "Help Centre");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id==0){
                    startActivity(new Intent(getActivity(), LatenessReportActivity.class));
                }
                else if (id==1){
                    //...
                }
                return false;
            }
        });

        popupMenu.show();
    }


    private void loadPosts() {

        Query querySelCategory = userDb.orderByKey().equalTo(firebaseAuth.getCurrentUser().getUid());
        querySelCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    String selCategory = user.getSelectedCategory();

                    if (selCategory.isEmpty())
                    {
                        mProgressBar.setVisibility(View.GONE);
                        selCategoryHint.setVisibility(View.VISIBLE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("WELCOME TO THESEL");
                        builder.setMessage("Tap the Thesel icon in the top left corner of the screen to " +
                                "select a category.");

                        builder.show();
                    }
                    else {
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
                                    selCategoryHint.setVisibility(View.GONE);
                                    //add to list
                                    postList.add(post);

                                    adapterPosts = new AdapterPosts(getActivity(), postList);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}
