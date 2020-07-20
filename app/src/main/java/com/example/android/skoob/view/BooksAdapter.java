package com.example.android.skoob.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.skoob.R;
import com.example.android.skoob.model.Book;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BooksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Book> mBooksOnSale;
    private Context mContext;
    private final int HOME_FRAGMENT_ADAPTER = 1;
    private final int ACCOUNT_FRAGMENT_ADAPTER = 2;
    private static final String SET_HOME_ADAPTER = "SET_HOME_ADAPTER";
    private static final String SET_ACCOUNT_ADAPTER = "SET_ACCOUNT_ADAPTER";
    private String mAdapterToDisplay;

    public BooksAdapter(List<Book> books, String adapterToDisplay){
        this.mBooksOnSale = books;
        this.mAdapterToDisplay = adapterToDisplay;
    }

    class HomeBooksAdapterViewHolder extends RecyclerView.ViewHolder{
        ImageView mImageThumbnail;
        TextView mTextBookTitle, mTextBookPrice;

        public HomeBooksAdapterViewHolder(View view){
            super(view);

            mImageThumbnail = view.findViewById(R.id.tv_book_thumbnail);
            mTextBookTitle = view.findViewById(R.id.tv_bookName);
            mTextBookPrice = view.findViewById(R.id.tv_bookPrice);
        }
    }

    class AccountBooksAdapterViewHolder extends RecyclerView.ViewHolder{
        ImageView mAccountImageThumbnail;
        TextView mAccountTextBookTitle, mAccountTextBookPrice, mAccountTextBookPostDate, mAccountTextBookCity;

        public AccountBooksAdapterViewHolder(View view){
            super(view);

            mAccountImageThumbnail = view.findViewById(R.id.tv_bookImage);
            mAccountTextBookTitle = view.findViewById(R.id.tv_bookName);
            mAccountTextBookPostDate = view.findViewById(R.id.date);
            mAccountTextBookPrice = view.findViewById(R.id.tv_price);
            mAccountTextBookCity = view.findViewById(R.id.location);
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
                viewHolder2.mAccountTextBookPostDate.setText("1w");
                viewHolder2.mAccountTextBookPrice.setText("$" + mBooksOnSale.get(position).getPrice());
                viewHolder2.mAccountTextBookCity.setText(mBooksOnSale.get(position).getPlace());
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

}
