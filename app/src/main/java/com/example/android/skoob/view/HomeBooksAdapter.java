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

public class HomeBooksAdapter extends RecyclerView.Adapter<HomeBooksAdapter.HomeBooksAdapterViewHolder>{

    private List<Book> mBooksOnSale;
    private Context mContext;

    public HomeBooksAdapter(List<Book> books){
        this.mBooksOnSale = books;
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

    @NonNull
    @Override
    public HomeBooksAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.home_books_list_item, parent, false);
        return new HomeBooksAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeBooksAdapterViewHolder holder, int position) {

        Glide.with(mContext)
                .load(mBooksOnSale.get(position).getPhotoUrl().get(0))
                .into(holder.mImageThumbnail);
        holder.mTextBookTitle.setText(mBooksOnSale.get(position).getBookName());
        holder.mTextBookPrice.setText("$" + mBooksOnSale.get(position).getPrice());

    }

    @Override
    public int getItemCount() {
        if(mBooksOnSale == null) return 0;
        return mBooksOnSale.size();
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
