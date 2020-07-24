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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListingsFragment extends Fragment implements BooksAdapter.BooksAdapterOnClickHandler{

    private List<Book> mBooks = new ArrayList<>();
    private BooksAdapter mBookAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    // Firebase instance variables
    private DatabaseReference mBooksForSaleDatabaseReference; //represent s a specific part of the Firebase database

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_listings, container, false);

        mProgressBar = rootView.findViewById(R.id.tv_listing_progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        MainActivity activity = (MainActivity) getActivity();
        final String userEmail = activity.getUserEmail();

        // Set up the recycler view
        mRecyclerView = rootView.findViewById(R.id.tv_recyclerview_userListings);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBookAdapter = new BooksAdapter(null, "SET_ACCOUNT_ADAPTER", this);
        mRecyclerView.setAdapter(mBookAdapter);
        // End set up the recycler view

        // Initialize Firebase components
        mBooksForSaleDatabaseReference = FirebaseDatabase.getInstance().getReference().child("booksForSale");

        Query query = mBooksForSaleDatabaseReference.orderByChild("emailOfSeller").equalTo(userEmail);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        mBooks.add(postSnapshot.getValue(Book.class));
                        mBookAdapter.setData(mBooks);
                    }
                    if(dataSnapshot.getChildrenCount() == mBooks.size()){
                        mProgressBar.setVisibility(View.GONE);
                    }
                }

            }

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DatabaseError: "+ databaseError.getMessage());
            }
        });

        return rootView;
    }

    @Override
    public void onClick(Book book) {
        final Intent intent = new Intent(getContext(), BookDetails.class);
        intent.putExtra(Constants.EXTRA_BOOK_DETAILS, book);
        startActivity(intent);
    }
}
