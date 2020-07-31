package com.example.android.skoob.utils;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return(new WidgetBooksForSaleListFactory(this.getApplicationContext(),
                intent));
    }

}
