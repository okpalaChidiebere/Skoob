package com.example.android.skoob.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.skoob.R;
import com.example.android.skoob.model.Book;
import com.example.android.skoob.utils.AES;
import com.example.android.skoob.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BooksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Book> mBooksOnSale;
    private Context mContext;
    private final int HOME_FRAGMENT_ADAPTER = 1;
    private final int ACCOUNT_FRAGMENT_ADAPTER = 2;
    private static final String SET_HOME_ADAPTER = "SET_HOME_ADAPTER";
    private static final String SET_ACCOUNT_ADAPTER = "SET_ACCOUNT_ADAPTER";

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private String mAdapterToDisplay;

    private final BooksAdapterOnClickHandler mClickHandler;

    public interface BooksAdapterOnClickHandler {
        void onClick(Book book);
    }

    public BooksAdapter(List<Book> books, String adapterToDisplay, BooksAdapterOnClickHandler clickHandler){
        this.mBooksOnSale = books;
        this.mAdapterToDisplay = adapterToDisplay;
        this.mClickHandler = clickHandler;
    }

    class HomeBooksAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImageThumbnail;
        TextView mTextBookTitle, mTextBookPrice;

        public HomeBooksAdapterViewHolder(View view){
            super(view);

            mImageThumbnail = view.findViewById(R.id.tv_book_thumbnail);
            mTextBookTitle = view.findViewById(R.id.tv_bookName);
            mTextBookPrice = view.findViewById(R.id.tv_bookPrice);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Book book = mBooksOnSale.get(adapterPosition);
            mClickHandler.onClick(book);
        }
    }

    class AccountBooksAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mAccountImageThumbnail;
        TextView mAccountTextBookTitle, mAccountTextBookPrice, mAccountTextBookPostDate, mAccountTextBookCity;

        public AccountBooksAdapterViewHolder(View view){
            super(view);

            mAccountImageThumbnail = view.findViewById(R.id.tv_bookImage);
            mAccountTextBookTitle = view.findViewById(R.id.tv_bookName);
            mAccountTextBookPostDate = view.findViewById(R.id.date);
            mAccountTextBookPrice = view.findViewById(R.id.tv_price);
            mAccountTextBookCity = view.findViewById(R.id.location);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Book book = mBooksOnSale.get(adapterPosition);
            mClickHandler.onClick(book);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        RecyclerView.ViewHolder holder = null;

        switch (viewType) {
            case HOME_FRAGMENT_ADAPTER:
                view = inflater.inflate(R.layout.home_books_list_item, parent, false);
                holder = new HomeBooksAdapterViewHolder(view);
                break;
            case ACCOUNT_FRAGMENT_ADAPTER:
                view = inflater.inflate(R.layout.temp_books_list_item, parent, false);
                holder = new AccountBooksAdapterViewHolder(view);
                break;
            default:
                /*view = inflater.inflate(R.layout.horizontal, parent, false);
                holder = new HorizontalViewHolder(view);*/
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case HOME_FRAGMENT_ADAPTER:
                HomeBooksAdapterViewHolder viewHolder0 = (HomeBooksAdapterViewHolder)holder;
                Glide.with(mContext)
                        .load(mBooksOnSale.get(position).getPhotoUrl().get(0))
                        .into(viewHolder0.mImageThumbnail);
                viewHolder0.mTextBookTitle.setText(mBooksOnSale.get(position).getBookName());
                viewHolder0.mTextBookPrice.setText("$" + mBooksOnSale.get(position).getPrice());
                break;

            case ACCOUNT_FRAGMENT_ADAPTER:
                AccountBooksAdapterViewHolder viewHolder2 = (AccountBooksAdapterViewHolder)holder;
                Glide.with(mContext)
                        .load(mBooksOnSale.get(position).getPhotoUrl().get(0))
                        .into(viewHolder2.mAccountImageThumbnail);
                viewHolder2.mAccountTextBookTitle.setText(mBooksOnSale.get(position).getBookName());

                String convertTime = covertTimeToText(mBooksOnSale.get(position).getBookPostedTime());
                viewHolder2.mAccountTextBookPostDate.setText(convertTime);
                viewHolder2.mAccountTextBookPrice.setText("$" + mBooksOnSale.get(position).getPrice());
                String decryptedPlaceCity = AES.decrypt(mBooksOnSale.get(position).getPlace(), Constants.SECRET_AES_KEY);
                viewHolder2.mAccountTextBookCity.setText(decryptedPlaceCity);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(mBooksOnSale == null) return 0;
        return mBooksOnSale.size();
    }


    //this returns viewType to the onCreateViewHolder to use
    @Override
    public int getItemViewType(int position) {
        if (mAdapterToDisplay.equals(SET_HOME_ADAPTER))
            return HOME_FRAGMENT_ADAPTER;
        if (mAdapterToDisplay.equals(SET_ACCOUNT_ADAPTER))
            return ACCOUNT_FRAGMENT_ADAPTER;
        return -1;
    }

    public void setData(List<Book> data) {
        this.mBooksOnSale=data;
        if (mBooksOnSale != null)
            this.notifyDataSetChanged();
    }

    public void clear() {
        int size = mBooksOnSale.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mBooksOnSale.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }

    private String covertTimeToText(String dataDate) {

        String convTime = null;

        //String prefix = "";
        //String suffix = "Ago";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            Date pasTime = dateFormat.parse(dataDate);
            Date nowTime = new Date();

            long dateDiff = Math.abs(nowTime.getTime() - pasTime.getTime());

            /*int second = (int) (dateDiff / 1000) % 60 ;
            int minute = (int) ((dateDiff / (1000*60)) % 60);
            int hour   = (int) ((dateDiff / (1000*60*60)) % 24);*/
            int day = (int) (dateDiff / (1000*60*60*24));

            if (day >= 7) {
                if (day > 360) {
                    return (day / 30) + "y";
                } else if (day > 30) {
                    return (day / 360) + "m";
                } else {
                    return (day / 7) + "w";
                }
            }

            if (dateDiff < MINUTE_MILLIS) {
                return "just now";
            } else if (dateDiff < 2 * MINUTE_MILLIS) {
                return "1m";
            } else if (dateDiff < 50 * MINUTE_MILLIS) {
                return dateDiff / MINUTE_MILLIS + "m";
            } else if (dateDiff < 90 * MINUTE_MILLIS) {
                return "1h";
            } else if (dateDiff < 24 * HOUR_MILLIS) {
                return dateDiff / HOUR_MILLIS + "h";
            } else if (dateDiff < 48 * HOUR_MILLIS) {
                return "1d";
            } else {
                return dateDiff / DAY_MILLIS + "d";
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }

}
