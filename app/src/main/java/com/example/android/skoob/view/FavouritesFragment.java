package com.example.android.skoob.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.skoob.R;
import com.example.android.skoob.database.FavouritesViewModel;
import com.example.android.skoob.model.Book;
import com.example.android.skoob.model.Favourites;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
    private FavouritesViewModel mFavouritesViewModel;
    private TextView mDefaultMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);

        mProgressBar = rootView.findViewById(R.id.tv_favourites_progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        mDefaultMessage = rootView.findViewById(R.id.tv_default_movies_message);

        mSetBookCallBack = this;
        mFavouritesViewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);
        mFavouritesViewModel.getFavourites(getContext()).observe(getViewLifecycleOwner(), new Observer<List<Favourites>>() {
            @Override
            public void onChanged(List<Favourites> favourites) {
                mPushIds.clear(); //clear old data in list if it exist so we can have updated list with new data
                mBooks.clear();  //clear old data in list if it exist so we can have updated list with new data
                for(int i = 0; i < favourites.size(); i++){
                    mPushIds.add(favourites.get(i).getBookPush_id());
                }

                if(favourites.size() != 0){
                    getBooksByPushID();
                }else{
                    noBookFavouriteList();
                }
            }
        });
        //getBooksByPushID();

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
            mBookAdapter.setData(mBooks);
            mProgressBar.setVisibility(View.GONE);
            favouriteBookListExists();
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

    private void favouriteBookListExists() {
        mDefaultMessage.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void noBookFavouriteList(){
        mDefaultMessage.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }
}
