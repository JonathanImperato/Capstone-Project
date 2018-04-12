package com.ji.bookinhand.adapters;

import android.content.ContentValues;
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
import com.ji.bookinhand.api.models.BooksList;
import com.ji.bookinhand.api.models.ImageLinks;
import com.ji.bookinhand.api.models.Item;
import com.ji.bookinhand.api.models.VolumeInfo;

import java.util.List;

import static com.ji.bookinhand.database.ItemsContract.BASE_CONTENT_URI;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_AUTHORS;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_AVERAGE_RATING;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_CANONICAL_VOLUME_LINK;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_CATEGORIES;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_DESCRIPTION;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_IMAGE_LINKS;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_INFO_LINK;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_LANGUAGE;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_MATURITY_RATING;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_PAGE_COUNT;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_PREVIEW_LINK;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_PRINT_TYPE;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_PUBLISHER;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_PUBLISH_DATE;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_RATING_COUNT;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_SUBTITLE;
import static com.ji.bookinhand.database.ItemsContract.BookEntry.COLUMN_TITLE;

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
                VolumeInfo info = libro.getVolumeInfo();
                switch (view.getId()) {
                    case R.id.book_item:

                        ContentValues values = new ContentValues();
                        values.put(COLUMN_TITLE, info.getTitle());
                        StringBuilder authorsList = new StringBuilder();
                        for (String author : info.getAuthors()) {
                            if (authorsList != null)
                                if (!authorsList.toString().contains(author))
                                    authorsList.append(author).append(","); //i separate every author using a ,
                                else
                                    authorsList = new StringBuilder(author + ","); //i separate every author using a ,
                        }
                        values.put(COLUMN_AUTHORS, authorsList.toString());
                        values.put(COLUMN_PUBLISHER, info.getPublisher());
                        values.put(COLUMN_PUBLISH_DATE, info.getPublishedDate());
                        //  values.put(COLUMN_INDUSTRY_IDENTIFIERS, info.getIndustryIdentifiers());
                        //  values.put(COLUMN_READING_MODES, info.getReadingModes());
                        values.put(COLUMN_PAGE_COUNT, info.getPageCount());
                        values.put(COLUMN_PRINT_TYPE, info.getPrintType());

                        String categories = "";
                        for (String cat : info.getCategories()) {
                            if (!categories.contains(cat))
                                authorsList.append(cat).append(","); //i separate every author using a ,
                        }
                        values.put(COLUMN_CATEGORIES, categories);
                        values.put(COLUMN_AVERAGE_RATING, info.getAverageRating());
                        values.put(COLUMN_RATING_COUNT, info.getRatingsCount());
                        values.put(COLUMN_MATURITY_RATING, info.getMaturityRating());
                        values.put(COLUMN_IMAGE_LINKS, info.getImageLinks().getThumbnail());
                        values.put(COLUMN_LANGUAGE, info.getLanguage());
                        values.put(COLUMN_PREVIEW_LINK, info.getPreviewLink());
                        values.put(COLUMN_INFO_LINK, info.getInfoLink());
                        values.put(COLUMN_CANONICAL_VOLUME_LINK, info.getCanonicalVolumeLink());
                        values.put(COLUMN_DESCRIPTION, info.getDescription());
                        values.put(COLUMN_SUBTITLE, info.getSubtitle());

                        mContext.getContentResolver().insert(BASE_CONTENT_URI, values);
                        break;
                }
            } else
                Log.d(mContext.getClass().getSimpleName(), "Position is -1!");
        }
    }
}