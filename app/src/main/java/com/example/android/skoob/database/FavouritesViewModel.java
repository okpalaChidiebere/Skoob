package com.example.android.skoob.database;

import android.content.Context;

import com.example.android.skoob.model.Favourites;
import com.example.android.skoob.repository.BookRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class FavouritesViewModel extends ViewModel {

    // Constant for logging
    private static final String LOG_TAG = FavouritesViewModel.class.getSimpleName();
    private BookRepository mBookRepository;
    private LiveData<List<Favourites>> favourites;
    private LiveData<Integer> rowsReturnedFromCheck;

    public FavouritesViewModel(){

    }

    public FavouritesViewModel(Context context, String bookPushID){
        initializeRepository(context);
        //mFirebasePushID = bookPushID;
        rowsReturnedFromCheck = checkForFavourite(bookPushID);
    }

    public void initializeRepository(Context context){
        mBookRepository =  new BookRepository(context);
    }

    public LiveData<List<Favourites>> getFavourites(Context context){
        if (favourites == null) {
            initializeRepository(context);
            favourites = mBookRepository.getAllFavourites();
        }
        return favourites;
    }

    public void insertFavourite(Favourites favourites){
        mBookRepository.insertFavourites(favourites);
    }

    public void deleteFavourite(String bookFirebasePushID){
        mBookRepository.deleteFavourite(bookFirebasePushID);
    }

    public LiveData<Integer> checkForFavourite(String id){
        return mBookRepository.checkBookFavourites(id);
    }

    public LiveData<Integer> getCheckFavouriteReturnRow(){
        return rowsReturnedFromCheck;
    }

}
