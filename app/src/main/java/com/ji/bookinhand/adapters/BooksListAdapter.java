package com.ji.bookinhand.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ji.bookinhand.R;
import com.ji.bookinhand.models.BooksList;
import com.ji.bookinhand.models.ImageLinks;
import com.ji.bookinhand.models.Item;

import java.util.List;

public class BooksListAdapter extends RecyclerView.Adapter<BooksListAdapter.BooksListAdapterViewHolder> {

    public Context mContext;
    public BooksList mBooksList;

    public BooksListAdapter(Context mContext, BooksList mBooksList) {
        this.mContext = mContext;
        this.mBooksList = mBooksList;
    }

    @Override
    public BooksListAdapter.BooksListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.book_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        BooksListAdapterViewHolder holder = new BooksListAdapterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BooksListAdapterViewHolder holder, int position) {
        if (position < 10) { //every page contains 10
            String title = mBooksList.getItems().get(position).getVolumeInfo().getTitle();
            List<String> author = mBooksList.getItems().get(position).getVolumeInfo().getAuthors();
            Double rating = mBooksList.getItems().get(position).getVolumeInfo().getAverageRating();
            ImageLinks image = mBooksList.getItems().get(position).getVolumeInfo().getImageLinks();
            holder.title.setText(title);
            if (author.size() == 1)
                holder.author.setText(author.get(0));
            else if (author.size() > 1)
                holder.author.setText(author.get(0) + "...");
            if (rating != null)
                holder.rating.setText("Rating " + rating);
            else holder.rating.setText("N/A");
            String imgUrl = image.getThumbnail();
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(holder.thumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return (mBooksList == null || mBooksList.getTotalItems() - 1 == 0) ? 0 : mBooksList.getTotalItems();
    }


    public class BooksListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, author, rating;
        ImageView thumbnail;

        public BooksListAdapterViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            rating = view.findViewById(R.id.rating);
            thumbnail = view.findViewById(R.id.thumbnail);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (mBooksList != null && position > -1) {
                Item libro = mBooksList.getItems().get(position);
                switch (view.getId()) {
                    case R.id.book_item:
                        //TODO: START THE DETAIL ACTIVITY WITH THE "libro" ITEM
                        break;
                }
            } else
                Log.d(mContext.getClass().getSimpleName(), "Position is -1!");
        }
    }
}