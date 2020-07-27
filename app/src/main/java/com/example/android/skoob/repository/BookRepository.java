package com.example.android.skoob.repository;

import android.content.Context;
import android.os.AsyncTask;

import com.example.android.skoob.database.AppDatabase;
import com.example.android.skoob.database.FavouritesDao;
import com.example.android.skoob.model.Favourites;

import java.util.List;

import androidx.lifecycle.LiveData;

public class BookRepository {

    private static final String LOG_TAG = BookRepository.class.getSimpleName();

    private FavouritesDao mFavouritesDao;

    public BookRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        mFavouritesDao = db.favouritesDao();
    }

    public LiveData<Integer> checkBookFavourites(String movieID) {
        return mFavouritesDao.getFavouritesById(movieID);
    }

    public LiveData<List<Favourites>> getAllFavourites() {
        return mFavouritesDao.getAllFavourites();
    }

    public void insertFavourites(Favourites favourites){
        new FavouriteAsyncTask(mFavouritesDao, favourites, true).execute();
    }

    public void deleteFavourite(String FirebasePushID){
        new FavouriteAsyncTask(mFavouritesDao, FirebasePushID, false).execute();
    }


    /*Room database insert and delete will be done on background thread, while query for data will be done with ViewModel*/
    private static class FavouriteAsyncTask extends AsyncTask<Void, Void, Void> {
        private FavouritesDao mFavouritesDao;
        private Favourites mFavourites;
        private boolean mIfAdd;
        private String mPushIdToDelete;

        FavouriteAsyncTask(FavouritesDao dao, Favourites favourites, boolean ifAdd){
            mFavouritesDao = dao;
            mFavourites = favourites;
            mIfAdd = ifAdd;
        }

        FavouriteAsyncTask(FavouritesDao dao, String pushID, boolean ifAdd){
            mFavouritesDao = dao;
            mPushIdToDelete = pushID;
            mIfAdd = ifAdd;
        }

        @Override
        protected Void doInBackground(final Void... voids) {

            if(mIfAdd) {
                mFavouritesDao.insertFavourites(mFavourites);
            }else{
                mFavouritesDao.deleteFavouritesByBookPushId(mPushIdToDelete);
            }
            return null;
        }
    }
}
