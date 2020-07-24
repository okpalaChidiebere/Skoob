package com.example.android.skoob.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.skoob.R;
import com.example.android.skoob.model.Book;
import com.example.android.skoob.utils.AdMobUtil;
import com.example.android.skoob.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookDetails extends AppCompatActivity {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private TextView mImagePositionSelected;

    private Book mBooks;

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

        mBooks = (Book) getIntent().getSerializableExtra(Constants.EXTRA_BOOK_DETAILS);

        ViewPager viewPager = findViewById(R.id.tv_image_details_viewPager);
        BookImageViewPagerAdapter adapter = new BookImageViewPagerAdapter(this, mBooks.getPhotoUrl());
        viewPager.setAdapter(adapter);

        mImagePositionSelected = findViewById(R.id.tv_image_ViewPage_position);
        if(adapter.getCount() > 0){
            mImagePositionSelected.setText("1/"+mBooks.getPhotoUrl().size());
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                int temp_position = ++position; //due to position starts to count from 0, i had to increment by one before i displau
                mImagePositionSelected.setText(temp_position+"/"+mBooks.getPhotoUrl().size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBookName.setText(mBooks.getBookName());
        mLocation.setText(mBooks.getPlaceAddress());
        mPrice.setText(getString(R.string.book_detail_price)+mBooks.getPrice());
        mDepartment.setText(getString(R.string.book_detail_department)+mBooks.getDepartment());
        mSubject.setText(getString(R.string.book_detail_subject)+mBooks.getSubject());
        mIsbn.setText(getString(R.string.book_detail_isbn)+mBooks.getIsbnNumber());
        String time = covertTimeToText(mBooks.getBookPostedTime());
        mDate.setText(getString(R.string.book_detail_time)+time);
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
                String map = "geo:0,0?q=" + mBooks.getPlaceAddress(); // only map app should handle this
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.email_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"+mBooks.getEmailOfSeller()+
                        "?subject="+getString(R.string.skoob_email_subject)+ mBooks.getBookName()+
                        "&body=" +getString(R.string.email_message))); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.skoob_email_subject)+ mBooks.getBookName());
                //intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_message));

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
                .setText(mBooks.getBookName() + Constants.SKOOB_SHARE_HASHTAG)
                .getIntent();
        return shareIntent;
    }

    private String covertTimeToText(String dataDate) {

        String convTime = null;
        String suffix = "Ago";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            Date pasTime = dateFormat.parse(dataDate);
            Date nowTime = new Date();

            long dateDiff = Math.abs(nowTime.getTime() - pasTime.getTime());

            int day = (int) (dateDiff / (1000*60*60*24));

            if (day >= 7) {
                if (day > 360) {
                    return (day / 30) + " Years " + suffix;
                } else if (day > 30) {
                    return (day / 360) + " Months " + suffix;
                } else {
                    return (day / 7) + " Week " + suffix;
                }
            }

            if (dateDiff < MINUTE_MILLIS) {
                return "just now";
            } else if (dateDiff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (dateDiff < 50 * MINUTE_MILLIS) {
                return dateDiff / MINUTE_MILLIS + " minutes ago";
            } else if (dateDiff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (dateDiff < 24 * HOUR_MILLIS) {
                return dateDiff / HOUR_MILLIS + " hours ago";
            } else if (dateDiff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return dateDiff / DAY_MILLIS + " days ago";
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }
}
