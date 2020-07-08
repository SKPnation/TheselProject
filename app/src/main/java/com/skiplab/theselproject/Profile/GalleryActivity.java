package com.skiplab.theselproject.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.skiplab.theselproject.Adapter.GridImageAdapter;
import com.skiplab.theselproject.R;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    //private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 2;

    private Context mContext = GalleryActivity.this;

    private ImageView backBtn;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        setupToolbar();

        backBtn = findViewById(R.id.backArrow);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupActivityWidgets();
        tempGridSetup();
    }

    private void tempGridSetup(){
        ArrayList<String> imgURLs = new ArrayList<>();
        imgURLs.add("https://www.healthyplace.com/sites/default/files/positive-inspirational-quotes-1.jpg");
        imgURLs.add("https://www.yourtango.com/sites/default/files/styles/body_image_default/public/image_list/depressionquotes1.jpg?itok=HQh_pLTu");
        imgURLs.add("https://motivationquotes.org/wp-content/uploads/2019/04/quotes-against-depression.jpg");
        imgURLs.add("https://images.squarespace-cdn.com/content/v1/564fb8a6e4b083936d41b409/1467230085245-JXQCXCF1ZB0VJA2WUU9N/ke17ZwdGBToddI8pDm48kGeVPyNDvAZovujTC_sH5FdZw-zPPgdn4jUwVcJE1ZvWhcwhEtWJXoshNdA9f1qD7Xj1nVWs2aaTtWBneO2WM-s5J7K1VRsUUwqw_xASZN3d9Yn4QCLxID_k9K1IRmDbEQ/darkest+night+sun+rise.jpg");
        imgURLs.add("https://i.pinimg.com/564x/2a/e6/fd/2ae6fd53358fc33193fdc71fd37f3ae4.jpg");
        imgURLs.add("https://i.pinimg.com/474x/73/e1/f2/73e1f2c46cc1c17ae70c3e42d9aca709.jpg");
        imgURLs.add("https://i.pinimg.com/originals/df/83/94/df839426c9c83a230627aa8fb1db7cda.jpg");
        imgURLs.add("https://i.pinimg.com/474x/73/e1/f2/73e1f2c46cc1c17ae70c3e42d9aca709.jpg");


        setupImageGrid(imgURLs);
    }

    private void setupImageGrid(ArrayList<String> imgURLs){
        GridView gridView = findViewById(R.id.gridView);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgURLs);
        gridView.setAdapter(adapter);
    }

    private void setupActivityWidgets(){
        mProgressBar = (ProgressBar) findViewById(R.id.gridImageProgressbar);
        mProgressBar.setVisibility(View.GONE);

    }

    /**
     * Responsible for setting up the profile toolbar
     */
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.galleryToolbar);
        setSupportActionBar(toolbar);
    }
}
