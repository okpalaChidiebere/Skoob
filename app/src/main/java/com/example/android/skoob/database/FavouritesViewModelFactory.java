package com.example.android.skoob.database;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

@SuppressWarnings("unchecked")
public class FavouritesViewModelFactory extends ViewModelProvider.NewInstanceFactory{

    private final String mbookFirebasePushID;
    private final Context mContext;

    public FavouritesViewModelFactory(Context context, String id){
        mContext = context;
        mbookFirebasePushID = id;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new FavouritesViewModel(mContext, mbookFirebasePushID);
    }
}