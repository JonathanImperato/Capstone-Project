package com.ji.bookinhand.adapters;

import android.annotation.SuppressLint;
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
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.ji.bookinhand.R;
import com.ji.bookinhand.api.models.BooksList;
import com.ji.bookinhand.api.models.ImageLinks;
import com.ji.bookinhand.api.models.Item;
import com.ji.bookinhand.api.models.VolumeInfo;
import com.ji.bookinhand.ui.BookDetailActivity;

import java.util.ArrayList;
import java.util.Arrays;
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
    public ArrayList<Item> mFavList;
    boolean isFav;
    boolean isMoreRecyclerview = false;
    private Cursor mCursor;

    public BooksListAdapter(Context mContext, BooksList mBooksList) {
        this.mContext = mContext;
        this.mBooksList = mBooksList;
        isFav = false;
    }

    public BooksListAdapter(Context mContext) {
        this.mContext = mContext;
        this.mFavList = new ArrayList<>();
        isFav = true;
    }

    public BooksListAdapter(Context mContext, BooksList mBooksList, boolean isMoreRecyclerview) {
        this.mContext = mContext;
        this.mBooksList = mBooksList;
        isFav = false;
        this.isMoreRecyclerview = isMoreRecyclerview;
    }

    @Override
    public BooksListAdapter.BooksListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.book_item;
        if (isFav) layoutId = R.layout.fav_book_item;
        else if (!isFav && isMoreRecyclerview) layoutId = R.layout.more_book_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        BooksListAdapterViewHolder holder = new BooksListAdapterViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(BooksListAdapterViewHolder holder, int position) {
        if (isFav) {
            if (mFavList != null && mFavList.size() > position && mFavList.get(position) != null) { //every page contains 10
                String title = mFavList.get(position).getVolumeInfo().getTitle();
                List<String> author = mFavList.get(position).getVolumeInfo().getAuthors();
                Double rating = mFavList.get(position).getVolumeInfo().getAverageRating();
                ImageLinks image = mFavList.get(position).getVolumeInfo().getImageLinks();
                holder.title.setText(title);
                if (author != null)
                    if (author.size() == 1)
                        holder.author.setText(author.get(0));
                    else if (author.size() > 1)
                        holder.author.setText(author.get(0) + mContext.getString(R.string.more_on));
                if (rating != null && rating != 0.0)
                    holder.rating.setText(mContext.getString(R.string.rating) + " " + rating);
                else holder.rating.setText(mContext.getString(R.string.not_applicable));

                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                if (image != null && !((Activity) mContext).isFinishing())
                    if (image.getExtraLarge() != null)
                        Glide.with(mContext)
                                .load(image.getExtraLarge())
                                .apply(options)
                                .into(holder.thumbnail);
                    else if (image.getLarge() != null)
                        Glide.with(mContext)
                                .load(image.getLarge())
                                .apply(options)
                                .into(holder.thumbnail);
                    else if (image.getMedium() != null)
                        Glide.with(mContext)
                                .load(image.getMedium())
                                .apply(options)
                                .into(holder.thumbnail);
                    else if (image.getThumbnail() != null)
                        Glide.with(mContext)
                                .load(image.getThumbnail())
                                .apply(options)
                                .into(holder.thumbnail);

            }
        } else if (!isFav && isMoreRecyclerview && position < 10) {
            if (mBooksList != null) {
                String title = mBooksList.getItems().get(position).getVolumeInfo().getTitle();
                ImageLinks image = mBooksList.getItems().get(position).getVolumeInfo().getImageLinks();
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.ic_sync_black_24dp)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                holder.title.setText(title);
                if (image != null)
                    if (image.getExtraLarge() != null)
                        Glide.with(mContext)
                                .load(image.getExtraLarge())
                                .apply(options)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(holder.thumbnail);
                    else if (image.getLarge() != null)
                        Glide.with(mContext)
                                .load(image.getLarge())
                                .apply(options)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(holder.thumbnail);
                    else if (image.getMedium() != null)
                        Glide.with(mContext)
                                .load(image.getMedium())
                                .apply(options)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(holder.thumbnail);
                    else if (image.getThumbnail() != null)
                        Glide.with(mContext)
                                .load(image.getThumbnail())
                                .apply(options)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(holder.thumbnail);

            }

        } else {
            if (mBooksList != null) { //every page contains 10
                String title = mBooksList.getItems().get(position).getVolumeInfo().getTitle();
                List<String> author = mBooksList.getItems().get(position).getVolumeInfo().getAuthors();
                Double rating = mBooksList.getItems().get(position).getVolumeInfo().getAverageRating();
                ImageLinks image = mBooksList.getItems().get(position).getVolumeInfo().getImageLinks();
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.ic_sync_black_24dp)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                holder.title.setText(title);
                if (author != null)
                    if (author.size() == 1)
                        holder.author.setText(author.get(0));
                    else if (author.size() > 1)
                        holder.author.setText(author.get(0) + mContext.getString(R.string.more_on));
                if (rating != null)
                    holder.rating.setText(mContext.getString(R.string.rating) + " " + rating);
                else holder.rating.setText(mContext.getString(R.string.not_applicable));
                if (image != null)
                    if (image.getExtraLarge() != null)
                        Glide.with(mContext)
                                .load(image.getExtraLarge())
                                .apply(options)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(holder.thumbnail);
                    else if (image.getLarge() != null)
                        Glide.with(mContext)
                                .load(image.getLarge())
                                .apply(options)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(holder.thumbnail);
                    else if (image.getMedium() != null)
                        Glide.with(mContext)
                                .load(image.getMedium())
                                .apply(options)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(holder.thumbnail);
                    else if (image.getThumbnail() != null)
                        Glide.with(mContext)
                                .load(image.getThumbnail())
                                .apply(options)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(holder.thumbnail);
            }
        }

    }

    public ArrayList<Item> getmFavList() {
        return mFavList;
    }

    public void setmFavList(ArrayList<Item> mFavList) {
        this.mFavList = mFavList;
        notifyDataSetChanged();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        reloadCursorDataForFavourites();
    }

    void reloadCursorDataForFavourites() {
        mFavList.clear();
        Cursor itemCurso = mCursor;
        itemCurso.moveToFirst();
        while (mCursor.moveToNext()) {
            Item item = new Item();

            VolumeInfo volumeInfo = new VolumeInfo();

            volumeInfo.setTitle(
                    itemCurso.getString(itemCurso
                            .getColumnIndex(COLUMN_TITLE)));
            volumeInfo.setAuthors(Arrays.asList(new String[]{
                    itemCurso.getString(itemCurso
                            .getColumnIndex(COLUMN_AUTHORS))}));
            volumeInfo.setAverageRating(
                    itemCurso.getDouble(itemCurso
                            .getColumnIndex(COLUMN_AVERAGE_RATING)));
            volumeInfo.setCanonicalVolumeLink(
                    itemCurso.getString(itemCurso
                            .getColumnIndex(COLUMN_CANONICAL_VOLUME_LINK)));
            volumeInfo.setCategories(Arrays.asList(new String[]{
                    itemCurso.getString(itemCurso
                            .getColumnIndex(COLUMN_CATEGORIES))}));
            volumeInfo.setDescription(
                    itemCurso.getString(itemCurso
                            .getColumnIndex(COLUMN_DESCRIPTION)));
            ImageLinks img = new ImageLinks();
            img.setThumbnail(itemCurso.getString(itemCurso
                    .getColumnIndex(COLUMN_IMAGE_LINKS)));
            volumeInfo.setImageLinks(img);
            volumeInfo.setInfoLink(
                    itemCurso.getString(itemCurso
                            .getColumnIndex(COLUMN_INFO_LINK)));
            volumeInfo.setLanguage(
                    itemCurso.getString(itemCurso
                            .getColumnIndex(COLUMN_LANGUAGE)));
            volumeInfo.setMaturityRating(
                    itemCurso.getString(itemCurso
                            .getColumnIndex(COLUMN_MATURITY_RATING)));
            volumeInfo.setPageCount(
                    itemCurso.getInt(itemCurso
                            .getColumnIndex(COLUMN_PAGE_COUNT)));
            volumeInfo.setPreviewLink(
                    itemCurso.getString(itemCurso
                            .getColumnIndex(COLUMN_PREVIEW_LINK)));
            volumeInfo.setPrintType(
                    itemCurso.getString(itemCurso
                            .getColumnIndex(COLUMN_PRINT_TYPE)));
            volumeInfo.setPublishedDate(
                    itemCurso.getString(itemCurso
                            .getColumnIndex(COLUMN_PUBLISH_DATE)));
            volumeInfo.setPublisher(
                    itemCurso.getString(itemCurso
                            .getColumnIndex(COLUMN_PUBLISHER)));
            volumeInfo.setRatingsCount(
                    itemCurso.getInt(itemCurso
                            .getColumnIndex(COLUMN_RATING_COUNT)));
            volumeInfo.setSubtitle(
                    itemCurso.getString(itemCurso
                            .getColumnIndex(COLUMN_SUBTITLE)));

            item.setVolumeInfo(volumeInfo);

            item.setVolumeInfo(volumeInfo);
            mFavList.add(item);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (isFav) {
            return (mFavList == null) ? 0 : mFavList.size();
        } else if (!isFav && isMoreRecyclerview) {
            if (mBooksList != null && mBooksList.getTotalItems() >= 10) return 10;
            return (mBooksList == null) ? 0 : mBooksList.getTotalItems();
        } else {
            if (mBooksList != null && mBooksList.getTotalItems() >= 20) return 20;
            else if (mBooksList == null || mBooksList.getTotalItems() == 0)
                Toast.makeText(mContext, R.string.no_results_found, Toast.LENGTH_SHORT).show();
            return (mBooksList == null) ? 0 : mBooksList.getTotalItems();
        }
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
            if (isFav)
                menu = view.findViewById(R.id.more_fav);
            else if (!isFav && isMoreRecyclerview)
                menu = null;
            else
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
                            final String bookTitle = mBooksList.getItems().get(position).getVolumeInfo().getTitle();
                            //    Toast.makeText(mContext, "Position is " + position, Toast.LENGTH_SHORT).show();
                            if (!isFavourite(bookTitle)) {
                                addFavourite(position);
                                Snackbar.make(view, bookTitle + " " + mContext.getString(R.string.added_fav), Snackbar.LENGTH_LONG).setAction("Cancel", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        removeFromFav(bookTitle);
                                    }
                                }).show();
                            } else
                                Snackbar.make(view, bookTitle + " " + mContext.getString(R.string.already_fav), Snackbar.LENGTH_LONG).setAction("Cancel", null).show();
                            return true;
                        }
                    });
                    break;
                case R.id.more_fav:
                    inflater.inflate(R.menu.actions_fav, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            final String bookTitle = mFavList.get(position).getVolumeInfo().getTitle();
                            final boolean[] hasRestored = new boolean[1];
                            notifyItemRemoved(position);
                            final Item justRemovedItem = mFavList.get(position);
                            mFavList.remove(position);
                            Snackbar snack = Snackbar.make(view, bookTitle + " " + mContext.getString(R.string.removed_fav), Snackbar.LENGTH_LONG).setAction("Cancel", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (position != -1) {
                                        mFavList.add(position, justRemovedItem);
                                        notifyItemInserted(position);
                                        hasRestored[0] = true;
                                    }
                                }
                            });
                            snack.show();
                            snack.addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    if (!snackbar.isShown() && !hasRestored[0])
                                        removeFromFav(bookTitle);

                                }
                            });
                            return true;
                        }
                    });
                    break;
                case R.id.book_item:
                    if (isFav) {
                        intent.putExtra("volume", mFavList.get(position).getVolumeInfo());
                        intent.putExtra("isFav", true);
                        intent.putExtra("imgs", mFavList.get(position).getVolumeInfo().getImageLinks());
                        ActivityCompat.startActivity(mContext, intent, options.toBundle());
                    } else {
                        intent.putExtra("volume", mBooksList.getItems().get(position).getVolumeInfo());
                        intent.putExtra("isFav", false);
                        intent.putExtra("isbn", mBooksList.getItems().get(position).getVolumeInfo().getIndustryIdentifiers().get(0).getIdentifier());
                        intent.putExtra("imgs", mBooksList.getItems().get(position).getVolumeInfo().getImageLinks());
                        ActivityCompat.startActivity(mContext, intent, options.toBundle());
                    }
                    break;
                case R.id.thumbnail:
                    if (isFav) {
                        intent.putExtra("volume", mFavList.get(position).getVolumeInfo());
                        intent.putExtra("isFav", true);
                        intent.putExtra("imgs", mFavList.get(position).getVolumeInfo().getImageLinks());
                        ActivityCompat.startActivity(mContext, intent, options.toBundle());
                    } else {
                        intent.putExtra("volume", mBooksList.getItems().get(position).getVolumeInfo());
                        intent.putExtra("isFav", false);
                        if (mBooksList.getItems().get(position) != null && mBooksList.getItems().get(position).getVolumeInfo().getIndustryIdentifiers() != null && mBooksList.getItems().get(position).getVolumeInfo().getIndustryIdentifiers().get(0) != null)
                            intent.putExtra("isbn", mBooksList.getItems().get(position).getVolumeInfo().getIndustryIdentifiers().get(0).getIdentifier());
                        intent.putExtra("imgs", mBooksList.getItems().get(position).getVolumeInfo().getImageLinks());
                        ActivityCompat.startActivity(mContext, intent, options.toBundle());
                    }
                    break;
            }
        }

        void addFavourite(int position) {
            if (mBooksList != null && position > -1) {
                Item libro = mBooksList.getItems().get(position);
                VolumeInfo info = libro.getVolumeInfo();
                ContentValues values = new ContentValues();
                values.put(COLUMN_TITLE, info.getTitle());
                StringBuilder authorsList = new StringBuilder();
                if (info.getAuthors() != null)
                    for (String author : info.getAuthors()) {
                        if (authorsList != null)
                            if (info.getAuthors().size() == 1)
                                authorsList.append(author);
                            else {
                                if (!authorsList.toString().contains(author))
                                    authorsList.append(author).append(","); //i separate every author using a ,
                                else
                                    authorsList = new StringBuilder(author + ","); //i separate every author using a ,
                            }
                    }
                values.put(COLUMN_AUTHORS, authorsList.toString());
                values.put(COLUMN_PUBLISHER, info.getPublisher());
                values.put(COLUMN_PUBLISH_DATE, info.getPublishedDate());
                //  values.put(COLUMN_INDUSTRY_IDENTIFIERS, info.getIndustryIdentifiers());
                //  values.put(COLUMN_READING_MODES, info.getReadingModes());
                values.put(COLUMN_PAGE_COUNT, info.getPageCount());
                values.put(COLUMN_PRINT_TYPE, info.getPrintType());

                StringBuilder categories = new StringBuilder();
                if (info.getCategories() != null)
                    for (String cat : info.getCategories()) {
                        if (!categories.toString().contains(cat))
                            categories.append(cat).append(","); //i separate every author using a ,
                    }
                values.put(COLUMN_CATEGORIES, categories.toString());
                values.put(COLUMN_AVERAGE_RATING, info.getAverageRating());
                values.put(COLUMN_RATING_COUNT, info.getRatingsCount());
                values.put(COLUMN_MATURITY_RATING, info.getMaturityRating());
                if (info.getImageLinks().getExtraLarge() != null)
                    values.put(COLUMN_IMAGE_LINKS, info.getImageLinks().getExtraLarge());
                else if (info.getImageLinks().getLarge() != null)
                    values.put(COLUMN_IMAGE_LINKS, info.getImageLinks().getLarge());
                else if (info.getImageLinks().getMedium() != null)
                    values.put(COLUMN_IMAGE_LINKS, info.getImageLinks().getMedium());
                else if (info.getImageLinks().getThumbnail() != null)
                    values.put(COLUMN_IMAGE_LINKS, info.getImageLinks().getThumbnail());
                values.put(COLUMN_LANGUAGE, info.getLanguage());
                values.put(COLUMN_PREVIEW_LINK, info.getPreviewLink());
                values.put(COLUMN_INFO_LINK, info.getInfoLink());
                values.put(COLUMN_CANONICAL_VOLUME_LINK, info.getCanonicalVolumeLink());
                values.put(COLUMN_DESCRIPTION, info.getDescription());
                values.put(COLUMN_SUBTITLE, info.getSubtitle());

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

            return c.getCount() > 0;
        }
    }

}
