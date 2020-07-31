package com.example.android.skoob.utils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.example.android.skoob.R;
import com.example.android.skoob.model.Book;
import com.example.android.skoob.view.BookDetails;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class SkoobWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "WidgetProvider";
    private List<Book> mBooks = new ArrayList<>();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                List<Book> booksForSale, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.skoob_widget_provider);

        Intent svcIntent=new Intent(context, WidgetService.class);

        Bundle extraBundle = new Bundle();
        extraBundle.putSerializable(Constants.EXTRA_BOOKS_FOR_SALE, (Serializable) booksForSale);
        svcIntent.putExtra(Constants.BUNDLE_BOOKS_FOR_SALE, extraBundle);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.widget_skoob_list,
                svcIntent);

        Intent clickIntent=new Intent(context, SkoobWidgetProvider.class);
        clickIntent.setAction(Constants.ACTION_WIDGET_OPEN_BOOK_DETAIL);
        PendingIntent postPendingIntent = PendingIntent.getBroadcast(context,
                0, clickIntent, 0);
        views.setPendingIntentTemplate(R.id.widget_skoob_list, postPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Constants.ACTION_WIDGET_OPEN_BOOK_DETAIL.equals(intent.getAction())) {
            //Passed info from WidgetService.java
            Book book = (Book) intent.getSerializableExtra(Constants.EXTRA_BOOK_DETAILS);

            Bundle extras=new Bundle();
            extras.putSerializable(Constants.EXTRA_BOOK_DETAILS, book);
            extras.putString(Constants.EXTRA_USER_LOGIN_EMAIL, "noEMail");
            //Toast.makeText(context, "Touched view "+ book.getBookName(), Toast.LENGTH_SHORT).show();
            Intent intentPOST = new Intent(context,
                    BookDetails.class).putExtras(extras);
            intentPOST.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentPOST);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        // Firebase instance variables
        DatabaseReference mBooksForSaleDatabaseReference;

        mBooksForSaleDatabaseReference = FirebaseDatabase.getInstance().getReference().child("booksForSale");
        mBooksForSaleDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_skoob_list);
                Book bookForSale = dataSnapshot.getValue(Book.class); //the data of the POJO should match the EXACT name in of the key values in the database object
                mBooks.add(bookForSale);
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        });
        mBooksForSaleDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    if(dataSnapshot.getChildrenCount() == mBooks.size()){
                        updateSkoobRemoteWidgets(context, appWidgetManager, mBooks, appWidgetIds);
                    }
                }
            }

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DatabaseError: "+ databaseError.getMessage());
            }
        });
    }


    private static void updateSkoobRemoteWidgets(Context context, AppWidgetManager appWidgetManager,
                                                      List<Book> book, int[] appWidgetIds) { //you can add parameters to this to be used in Service
        //Log.d(TAG, "BookSize: " + book.size());
        // There may be multiple widgets active, so update all of them
        for (final int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, book, appWidgetId);
        }
    }

}

