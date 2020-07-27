package com.example.android.skoob.database;

import com.example.android.skoob.model.Favourites;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface FavouritesDao {
    @Query("SELECT * FROM favourites")
    LiveData<List<Favourites>> getAllFavourites();

    @Insert
    void insertFavourites(Favourites favourites);

    @Query("DELETE FROM favourites WHERE bookPush_id = :id")
    void deleteFavouritesByBookPushId(String id);

    @Query("SELECT COUNT(bookPush_id) FROM favourites WHERE bookPush_id = :id LIMIT 1")
    LiveData<Integer> getFavouritesById(String id);
}
