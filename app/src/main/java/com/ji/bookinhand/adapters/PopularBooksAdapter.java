package com.ji.bookinhand.adapters;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.ji.bookinhand.R;
import com.ji.bookinhand.api.BooksClient;
import com.ji.bookinhand.api.models.BooksList;
import com.ji.bookinhand.api.models.ImageLinks;
import com.ji.bookinhand.api.nytmodels.BookDetail;
import com.ji.bookinhand.api.nytmodels.NytBooksList;
import com.ji.bookinhand.api.nytmodels.Result;
import com.ji.bookinhand.ui.BookDetailActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ji.bookinhand.database.ItemsContract.BASE_CONTENT_URI;
import static com.ji.bookinhand.database.Room.Book.COLUMN_AUTHORS;
import static com.ji.bookinhand.database.Room.Book.COLUMN_AVERAGE_RATING;
import static com.ji.bookinhand.database.Room.Book.COLUMN_DESCRIPTION;
import static com.ji.bookinhand.database.Room.Book.COLUMN_IMAGE_LINKS;
import static com.ji.bookinhand.database.Room.Book.COLUMN_PAGE_COUNT;
import static com.ji.bookinhand.database.Room.Book.COLUMN_PUBLISHER;
import static com.ji.bookinhand.database.Room.Book.COLUMN_PUBLISH_DATE;
import static com.ji.bookinhand.database.Room.Book.COLUMN_TITLE;
import static com.ji.bookinhand.database.Room.BookContentProvider.URI_Book;

/**
 * WORKS ONLY IF A VERTICAL LINEAR LAYOUT MANAGER IS USED
 * CURRENTLY NOT USED
 */

public class PopularBooksAdapter extends RecyclerView.Adapter<PopularBooksAdapter.BooksListAdapterViewHolder> {
    private static final String TAG = PopularBooksAdapter.class.getSimpleName();
    public Context mContext;
    public NytBooksList mBooksList;
    public SparseArray<ImageLinks> imgs;

    public PopularBooksAdapter(Context mContext, NytBooksList mBooksList) {
        this.mContext = mContext;
        this.mBooksList = mBooksList;
        imgs = new SparseArray<>();
        imgs.clear();
    }

    public NytBooksList getmBooksList() {
        return mBooksList;
    }

    @Override
    public BooksListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.book_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        BooksListAdapterViewHolder holder = new BooksListAdapterViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(BooksListAdapterViewHolder holder, int position) {
        if (mBooksList != null) { //every page contains 10
            String title = "";
            if (mBooksList.getResults().get(position) != null && mBooksList.getResults().get(position).getBookDetails() != null && mBooksList.getResults().get(position).getBookDetails().get(0) != null)
                title = mBooksList.getResults().get(position).getBookDetails().get(0).getTitle();

            String author = mBooksList.getResults().get(position).getBookDetails().get(0).getAuthor();

            holder.title.setText(title);
            if (author != null)
                if (author != null)
                    holder.author.setText(author + mContext.getString(R.string.more_on));
                else holder.author.setVisibility(View.GONE);

            holder.rating.setVisibility(View.GONE);

          /*  if (imgs.size() > 0) {
                ImageLinks image = imgs.get(position);
                if (image != null)
                    if (image.getExtraLarge() != null)
                        Glide.with(mContext)
                                .load(image.getExtraLarge())
                                .into(holder.thumbnail);
                    else if (image.getLarge() != null)
                        Glide.with(mContext)
                                .load(image.getLarge())
                                .into(holder.thumbnail);
                    else if (image.getMedium() != null)
                        Glide.with(mContext)
                                .load(image.getMedium())
                                .into(holder.thumbnail);
                    else if (image.getThumbnail() != null)
                        Glide.with(mContext)
                                .load(image.getThumbnail())
                                .into(holder.thumbnail);

            } else
                */
            loadSingleImage(position, holder.thumbnail);
        }
    }


    void loadSingleImage(final int position, final ImageView imageView) {
        if (!((Activity) mContext).isFinishing()) {
            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.googleapis.com/books/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            BooksClient service = retrofit.create(BooksClient.class);
            Call<BooksList> data = service.getBookWithQuery(mBooksList.getResults().get(position).getBookDetails().get(0).getTitle());
            data.enqueue(new Callback<BooksList>() {
                @Override
                public void onResponse(Call<BooksList> call, Response<BooksList> response) {
                    BooksList data = response.body();
                    RequestOptions option = new RequestOptions()
                            .placeholder(R.drawable.ic_sync_black_24dp)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    if (data != null && data.getItems() != null && data.getItems().get(0) != null && data.getItems().get(0).getVolumeInfo() != null && data.getItems().get(0).getVolumeInfo().getImageLinks() != null) {
                        ImageLinks image = data.getItems().get(0).getVolumeInfo().getImageLinks();
                        if (image != null && !((Activity) mContext).isFinishing()) {
                            if (image.getExtraLarge() != null && mContext != null && !((Activity) mContext).isFinishing())
                                Glide.with(mContext)
                                        .load(image.getExtraLarge())
                                        .apply(option)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(imageView);
                            else if (image.getLarge() != null && !((Activity) mContext).isFinishing())
                                Glide.with(mContext)
                                        .load(image.getLarge())
                                        .apply(option)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(imageView);
                            else if (image.getMedium() != null && !((Activity) mContext).isFinishing())
                                Glide.with(mContext)
                                        .load(image.getMedium())
                                        .apply(option)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(imageView);
                            else if (image.getThumbnail() != null && mContext != null && !((Activity) mContext).isFinishing())
                                Glide.with(mContext)
                                        .load(image.getThumbnail())
                                        .apply(option)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(imageView);
                            imgs.put(position, image);
                        }
                    }

                }

                @Override
                public void onFailure(Call<BooksList> call, Throwable t) {
                    Log.e(TAG, "Error while loading Popular Book Posters" + t.getMessage(), t);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mBooksList != null && mBooksList.getNumResults() >= 20) return 20;
        else if (mBooksList == null || mBooksList.getNumResults() == 0)
            //    Toast.makeText(mContext, "Sorry, no results found.", Toast.LENGTH_SHORT).show();
            return (mBooksList == null) ? 0 : mBooksList.getNumResults();
        return mBooksList.getNumResults();
    }


    public class BooksListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, author, rating;
        ImageView thumbnail, menu;

        public BooksListAdapterViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            rating = view.findViewById(R.id.rating);
            thumbnail = view.findViewById(R.id.thumbnail);
            menu = view.findViewById(R.id.more);
            if (menu != null)
                menu.setOnClickListener(this);
            thumbnail.setOnClickListener(this);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(final View view) {
            final int position = getAdapterPosition();
            final PopupMenu popup = new PopupMenu(mContext, view);
            MenuInflater inflater = popup.getMenuInflater();

            Intent intent = new Intent(mContext, BookDetailActivity.class);
            // Get the transition name from the string
            String transitionName = mContext.getString(R.string.transition_string);
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext,
                            view,   // Starting view
                            transitionName    // The String
                    );

            switch (view.getId()) {
                case R.id.more:
                    inflater.inflate(R.menu.actions, popup.getMenu());
                    popup.show();

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            final String bookTitle = mBooksList.getResults().get(position).getBookDetails().get(0).getTitle();
                            //    Toast.makeText(mContext, "Position is " + position, Toast.LENGTH_SHORT).show();
                            if (!isFavourite(bookTitle)) {
                                addFavourite(position);
                                Snackbar.make(view, bookTitle + " " + mContext.getString(R.string.book_added_fav), Snackbar.LENGTH_LONG).setAction(mContext.getString(R.string.cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        removeFromFav(bookTitle);
                                    }
                                }).show();
                            } else
                                Snackbar.make(view, bookTitle + " " + mContext.getString(R.string.already_fav), Snackbar.LENGTH_LONG).setAction(mContext.getString(R.string.cancel), null).show();
                            return true;
                        }
                    });
                    break;
                case R.id.book_item:
                    intent.putExtra("volume", mBooksList.getResults().get(position).getBookDetails().get(0));
                    intent.putExtra("isFav", false);
                    intent.putExtra("imgs", imgs.get(position));
                    intent.putExtra("isNyt", true);
                    intent.putExtra("amazonUrl", mBooksList.getResults().get(position).getAmazonProductUrl());
                    ActivityCompat.startActivity(mContext, intent, options.toBundle());
                    break;
                case R.id.thumbnail:
                    intent.putExtra("volume", mBooksList.getResults().get(position).getBookDetails().get(0));
                    intent.putExtra("isFav", false);
                    intent.putExtra("isNyt", true);
                    intent.putExtra("amazonUrl", mBooksList.getResults().get(position).getAmazonProductUrl());
                    intent.putExtra("imgs", imgs.get(position));
                    ActivityCompat.startActivity(mContext, intent, options.toBundle());

                    break;
            }
        }


        void addFavourite(int position) {
            if (mBooksList != null && position > -1) {
                Result libro = mBooksList.getResults().get(position);
                BookDetail info = libro.getBookDetails().get(0);
                ContentValues values = new ContentValues();
                values.put(COLUMN_TITLE, info.getTitle());


                values.put(COLUMN_AUTHORS, libro.getBookDetails().get(0).getAuthor());
                values.put(COLUMN_PUBLISHER, info.getPublisher());
                values.put(COLUMN_PUBLISH_DATE, libro.getPublishedDate());
                //  values.put(COLUMN_INDUSTRY_IDENTIFIERS, info.getIndustryIdentifiers());
                //  values.put(COLUMN_READING_MODES, info.getReadingModes());
                values.put(COLUMN_PAGE_COUNT, 0);
                //   values.put(COLUMN_PRINT_TYPE, "null");
                //  values.put(COLUMN_CATEGORIES, );
                values.put(COLUMN_AVERAGE_RATING, libro.getRank());
                //  values.put(COLUMN_RATING_COUNT, info.getRatingsCount());
                //  values.put(COLUMN_MATURITY_RATING, info.getMaturityRating());
                if (imgs.get(position) != null)
                    if ((imgs.get(position)).getExtraLarge() != null)
                        values.put(COLUMN_IMAGE_LINKS, (imgs.get(position)).getExtraLarge());
                    else if ((imgs.get(position)).getLarge() != null)
                        values.put(COLUMN_IMAGE_LINKS, (imgs.get(position)).getLarge());
                    else if ((imgs.get(position)).getMedium() != null)
                        values.put(COLUMN_IMAGE_LINKS, (imgs.get(position)).getMedium());
                    else if ((imgs.get(position)).getThumbnail() != null)
                        values.put(COLUMN_IMAGE_LINKS, (imgs.get(position)).getThumbnail());

                //       values.put(COLUMN_LANGUAGE, info.getLanguage());
                //       values.put(COLUMN_PREVIEW_LINK, info.getPreviewLink());
                //      values.put(COLUMN_INFO_LINK, info.getInfoLink());
                //     values.put(COLUMN_CANONICAL_VOLUME_LINK, info.getCanonicalVolumeLink());
                values.put(COLUMN_DESCRIPTION, info.getDescription());
                //    values.put(COLUMN_SUBTITLE, info.getSubtitle());

                mContext.getContentResolver().insert(BASE_CONTENT_URI, values);

            } else
                Log.d(mContext.getClass().getSimpleName(), "Position is -1!");

        }

        void removeFromFav(String title) {
            mContext.getContentResolver().delete(
                    BASE_CONTENT_URI,
                    COLUMN_TITLE + " =? ",
                    new String[]{title}
            );
        }

        boolean isFavourite(String title) {
            String newName = title;
            String[] selections = {newName};
            Cursor c = mContext.getContentResolver().query(
                    BASE_CONTENT_URI,
                    null,
                    COLUMN_TITLE + " =? ",
                    selections,
                    null);
            if (c != null)
                return c.getCount() > 0;
            return false;
        }
    }


}