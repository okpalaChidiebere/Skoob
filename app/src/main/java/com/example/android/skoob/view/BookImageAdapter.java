package com.example.android.skoob.view;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.skoob.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookImageAdapter extends RecyclerView.Adapter<BookImageAdapter.BookImageViewHolder>{

    private List<Uri> mImageURIs;
    private Context mContext;
    private final BookImageAdapterOnLongClickHandler mClickHandler;

    public interface BookImageAdapterOnLongClickHandler {
        void onLongClicked(int position);
    }

    public BookImageAdapter(List<Uri> imageURLs, BookImageAdapterOnLongClickHandler clickHandler){
        this.mImageURIs = imageURLs;
        this.mClickHandler = clickHandler;
    }

    /**
     * BookImageViewHolder class for the recycler view item
     */
    class BookImageViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        ImageView mImageThumbnail;

        public BookImageViewHolder(View view){
            super(view);

            mImageThumbnail = view.findViewById(R.id.tv_thumbnail);

            view.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onLongClicked(adapterPosition);
            return true;
        }
    }

    @NonNull
    @Override
    public BookImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.books_image_list_item, parent, false);
        return new BookImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookImageViewHolder holder, int position) {

        Glide.with(mContext)
                .load(mImageURIs.get(position))
                .into(holder.mImageThumbnail);
    }

    @Override
    public int getItemCount() {
        if(mImageURIs == null) return 0;
        return mImageURIs.size();
    }

    /*This simply replace the current list of book images with a new one whenever new places are added or changed*/
    public void swapBookImageList(List<Uri> newImageUrlList) {
        mImageURIs = newImageUrlList;
        if (mImageURIs != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }
}
