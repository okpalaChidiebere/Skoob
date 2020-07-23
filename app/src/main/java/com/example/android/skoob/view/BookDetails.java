package com.example.android.skoob.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.skoob.R;
import com.example.android.skoob.utils.AdMobUtil;
import com.example.android.skoob.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BookDetails extends AppCompatActivity {

    private TextView mImagePositionSelected;

    private List<String> mImageUrls = new ArrayList<>();

    private TextView mBookName, mLocation, mPrice, mDepartment, mSubject, mIsbn, mDate, mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mBookName = findViewById(R.id.tv_detail_bookName);
        mLocation = findViewById(R.id.tv_detail_location);
        mPrice = findViewById(R.id.tv_detail_bookPrice);
        mDepartment = findViewById(R.id.tv_detail_department);
        mSubject = findViewById(R.id.tv_detail_subject);
        mIsbn = findViewById(R.id.tv_detail_isbnNumber);
        mDate = findViewById(R.id.tv_detail_postedTime);
        mStatus = findViewById(R.id.tv_detail_bookStatus);

        mImageUrls.add("https://cdn.pixabay.com/photo/2016/11/11/23/34/cat-1817970_960_720.jpg");
        mImageUrls.add("https://cdn.pixabay.com/photo/2017/12/21/12/26/glowworm-3031704_960_720.jpg");
        mImageUrls.add("https://cdn.pixabay.com/photo/2017/11/07/00/07/fantasy-2925250_960_720.jpg");

        ViewPager viewPager = findViewById(R.id.tv_image_details_viewPager);
        BookImageViewPagerAdapter adapter = new BookImageViewPagerAdapter(this, mImageUrls);
        viewPager.setAdapter(adapter);

        mImagePositionSelected = findViewById(R.id.tv_image_ViewPage_position);
        if(adapter.getCount() > 0){
            mImagePositionSelected.setText("1/"+mImageUrls.size());
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                int temp_position = ++position; //due to position starts to count from 0, i had to increment by one before i displau
                mImagePositionSelected.setText(temp_position+"/"+mImageUrls.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBookName.setText(getString(R.string.book_detail_name));
        mLocation.setText(getString(R.string.book_detail_location));
        mPrice.setText(getString(R.string.book_detail_price));
        mDepartment.setText(getString(R.string.book_detail_department));
        mSubject.setText(getString(R.string.book_detail_subject));
        mIsbn.setText(getString(R.string.book_detail_isbn));
        mDate.setText(getString(R.string.book_detail_time));
        mStatus.setText(getString(R.string.book_detail_status));

        //Banner AdMob set up
        AdView mAdView = findViewById(R.id.adView);
        mAdView.setMinimumHeight(AdMobUtil.getAdViewHeightInDP(BookDetails.this));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //end banner ad set up

        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String map = "geo:0,0?q=" + getString(R.string.book_detail_location); // only map app should handle this
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.email_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:okpalacollins4@gmail.com")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.skoob_email_subject)+ getString(R.string.book_detail_name));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_message));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
        return true;
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mBookName.getText() + Constants.SKOOB_SHARE_HASHTAG)
                .getIntent();
        return shareIntent;
    }

}
