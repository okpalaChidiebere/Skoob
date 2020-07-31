package com.example.android.skoob.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.example.android.skoob.R;
import com.example.android.skoob.model.Book;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WidgetBooksForSaleListFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private Context ctxt=null;
    private List<Book> list;

    public WidgetBooksForSaleListFactory(Context ctxt, Intent intent) {
        this.ctxt=ctxt;

        Bundle bundle = intent.getExtras();
        bundle = bundle.getBundle(Constants.BUNDLE_BOOKS_FOR_SALE);
        list = (List<Book>) bundle.getSerializable(Constants.EXTRA_BOOKS_FOR_SALE);
        //Log.d("WidgetBooksListFactory", "BookName: " + list.get(3).getBookName());
    }

    @Override
    public void onCreate() {
        // no-op
    }

    @Override
    public void onDestroy() {
        // no-op
    }

    @Override
    public int getCount() {
        return(list.size());
    }

    @Override
    public RemoteViews getViewAt(int position) {

        //Log.d("WidgetBooksListFactory", "BookName: " + list.get(position).getPhotoUrl().get(0));
        RemoteViews row=new RemoteViews(ctxt.getPackageName(),
                R.layout.widget_book_list_row);

        try {
            Bitmap bitmap = Glide.with(ctxt)
                    .asBitmap()
                    .load(list.get(position).getPhotoUrl().get(0))
                    .submit(110, 120)
                    .get();

            row.setImageViewBitmap(R.id.tv_widget_bookImage, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        row.setTextViewText(R.id.tv_widget_bookName, list.get(position).getBookName());
        row.setTextViewText(R.id.tv_widget_price, "$" + list.get(position).getPrice());
        String convertTime = covertTimeToText(list.get(position).getBookPostedTime());
        row.setTextViewText(R.id.tv_widget_news_posted_time, convertTime);
        String decryptedPlaceCity = AES.decrypt(list.get(position).getPlace(), Constants.SECRET_AES_KEY);
        row.setTextViewText(R.id.tv_widget_place, decryptedPlaceCity);

        Bundle extras=new Bundle();
        extras.putSerializable(Constants.EXTRA_BOOK_DETAILS, list.get(position));
        Intent fillInIntent=new Intent();
        fillInIntent.putExtras(extras);
        row.setOnClickFillInIntent(R.id.tv_widget_list_row_cover, fillInIntent);

        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return(null);
    }

    @Override
    public int getViewTypeCount() {
        return(1);
    }

    @Override
    public long getItemId(int position) {
        return(position);
    }

    @Override
    public boolean hasStableIds() {
        return(true);
    }

    @Override
    public void onDataSetChanged() {
        // no-op
    }

    private String covertTimeToText(String dataDate) {

        String convTime = null;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            Date pasTime = dateFormat.parse(dataDate);
            Date nowTime = new Date();

            long dateDiff = Math.abs(nowTime.getTime() - pasTime.getTime());

            int day = (int) (dateDiff / (1000*60*60*24));

            if (day >= 7) {
                if (day > 360) {
                    return (day / 30) + "y";
                } else if (day > 30) {
                    return (day / 360) + "m";
                } else {
                    return (day / 7) + "w";
                }
            }

            if (dateDiff < MINUTE_MILLIS) {
                return "just now";
            } else if (dateDiff < 2 * MINUTE_MILLIS) {
                return "1m";
            } else if (dateDiff < 50 * MINUTE_MILLIS) {
                return dateDiff / MINUTE_MILLIS + "m";
            } else if (dateDiff < 90 * MINUTE_MILLIS) {
                return "1h";
            } else if (dateDiff < 24 * HOUR_MILLIS) {
                return dateDiff / HOUR_MILLIS + "h";
            } else if (dateDiff < 48 * HOUR_MILLIS) {
                return "1d";
            } else {
                return dateDiff / DAY_MILLIS + "d";
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }
}
