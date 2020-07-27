package com.example.android.skoob.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourites")
public class Favourites {

    @PrimaryKey(autoGenerate = true)
    private int favourite_id;
    private String bookPush_id;

    /**
     * No args constructor for use in serialization
     */
    @Ignore
    public Favourites() {
    }

    public Favourites(int favourite_id, String bookPush_id) {
        this.favourite_id = favourite_id;
        this.bookPush_id = bookPush_id;
    }

    @Ignore
    public Favourites(String bookPush_id){
        this.bookPush_id = bookPush_id;
    }

    /* getters and setters are ignored for brevity but they are required for Room to work.*/
    public int getFavourite_id() {
        return favourite_id;
    }

    public void setFavourite_id(int favourite_id) {
        this.favourite_id = favourite_id;
    }

    public String getBookPush_id() {
        return bookPush_id;
    }

    public void setBookPush_id(String bookPush_id) {
        this.bookPush_id = bookPush_id;
    }
}
