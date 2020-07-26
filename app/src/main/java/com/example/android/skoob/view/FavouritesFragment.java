package com.example.android.skoob.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.android.skoob.R;
import com.example.android.skoob.model.Book;
import com.example.android.skoob.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavouritesFragment extends Fragment implements
        setBookListener, BooksAdapter.BooksAdapterOnClickHandler{

    // Firebase instance variables
    private DatabaseReference mBooksForSaleDatabaseReference;

    private List<String> mPushIds = new ArrayList<>();
    private List<Book> mBooks = new ArrayList<>();
    private final static String BASE_REF = "booksForSale/";
    private setBookListener mSetBookCallBack;
    private BooksAdapter mBookAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);

        mProgressBar = rootView.findViewById(R.id.tv_favourites_progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mSetBookCallBack = this;

        //TODO replace these values with values from room database
        mPushIds.add("-MCOIGaj4qLSotSwOK_M");
        mPushIds.add("-MCQ-tV0mz7YcNtrioea");
        mPushIds.add("-MC__Vf_NYWLUT6Ekeus");

        getBooksByPushID();

        // Set up the recycler view
        mRecyclerView = rootView.findViewById(R.id.tv_recyclerview_userFavourites);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBookAdapter = new BooksAdapter(null, "SET_ACCOUNT_ADAPTER", this);
        mRecyclerView.setAdapter(mBookAdapter);
        // End set up the recycler view

        return rootView;
    }

    private void getBooksByPushID(){

        for (int i =0 ; i < mPushIds.size(); i++) {
            // Initialize Firebase components
            mBooksForSaleDatabaseReference = FirebaseDatabase.getInstance().getReference(BASE_REF + mPushIds.get(i));
            mBooksForSaleDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mSetBookCallBack.setBookListener(snapshot.getValue(Book.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    public void setBookListener(Book book) {
        mBooks.add(book);
        if(mBooks.size() == mPushIds.size()){
            //System.out.println("Book Loaded size"+ mBooks.size());
            mBookAdapter.setData(mBooks);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(Book book) {
        MainActivity activity = (MainActivity) getActivity();
        String userEmail = activity.getUserEmail();
        final Intent intent = new Intent(getContext(), BookDetails.class);
        intent.putExtra(Constants.EXTRA_BOOK_DETAILS, book);
        intent.putExtra(Constants.EXTRA_USER_LOGIN_EMAIL, userEmail);
        startActivity(intent);
    }
}
