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
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ji.bookinhand.R;
import com.ji.bookinhand.api.models.BooksList;
import com.ji.bookinhand.api.models.ImageLinks;
import com.ji.bookinhand.api.models.Item;
import com.ji.bookinhand.api.models.VolumeInfo;
import com.ji.bookinhand.ui.BookDetailActivity;

import java.util.ArrayList;
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

    public BooksListAdapter(Context mContext, BooksList mBooksList) {
        this.mContext = mContext;
        this.mBooksList = mBooksList;
        isFav = false;
    }

    public BooksListAdapter(Context mContext, ArrayList<Item> mBooksList) {
        this.mContext = mContext;
        this.mFavList = mBooksList;
        isFav = true;
    }

    @Override
    public BooksListAdapter.BooksListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.book_item;
        if (isFav)
            layoutId = R.layout.fav_book_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        BooksListAdapterViewHolder holder = new BooksListAdapterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BooksListAdapterViewHolder holder, int position) {
        if (isFav) {
            if (position < 10 && mFavList != null) { //every page contains 10
                String title = mFavList.get(position).getVolumeInfo().getTitle();
                List<String> author = mFavList.get(position).getVolumeInfo().getAuthors();
                Double rating = mFavList.get(position).getVolumeInfo().getAverageRating();
                ImageLinks image = mFavList.get(position).getVolumeInfo().getImageLinks();
                holder.title.setText(title);
                if (author != null)
                    if (author.size() == 1)
                        holder.author.setText(author.get(0));
                    else if (author.size() > 1)
                        holder.author.setText(author.get(0) + "...");
                if (rating != null && rating != 0.0)
                    holder.rating.setText("Rating " + rating);
                else holder.rating.setText("N/A");
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

            }
        } else

        {
            if (position < 10 && mBooksList != null) { //every page contains 10
                String title = mBooksList.getItems().get(position).getVolumeInfo().getTitle();
                List<String> author = mBooksList.getItems().get(position).getVolumeInfo().getAuthors();
                Double rating = mBooksList.getItems().get(position).getVolumeInfo().getAverageRating();
                ImageLinks image = mBooksList.getItems().get(position).getVolumeInfo().getImageLinks();
                holder.title.setText(title);
                if (author != null)
                    if (author.size() == 1)
                        holder.author.setText(author.get(0));
                    else if (author.size() > 1)
                        holder.author.setText(author.get(0) + "...");
                if (rating != null)
                    holder.rating.setText("Rating " + rating);
                else holder.rating.setText("N/A");
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
            }
        }
    }

    @Override
    public int getItemCount() {
        if (isFav) {
            if (mFavList != null && mFavList.size() >= 10) return 10;
            return (mFavList == null) ? 0 : mFavList.size();
        } else {
            if (mBooksList != null && mBooksList.getTotalItems() >= 10) return 10;
            else if (mBooksList == null || mBooksList.getTotalItems() == 0)
                Toast.makeText(mContext, "Sorry, no results found.", Toast.LENGTH_SHORT).show();
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
            else
                menu = view.findViewById(R.id.more);

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
                                Snackbar.make(view, bookTitle + " has been added to favourite", Snackbar.LENGTH_LONG).setAction("Cancel", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        removeFromFav(bookTitle);
                                    }
                                }).show();
                            } else
                                Snackbar.make(view, bookTitle + " is already a favourite", Snackbar.LENGTH_LONG).setAction("Cancel", null).show();
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
                            Snackbar snack = Snackbar.make(view, bookTitle + " has been removed from favourites", Snackbar.LENGTH_LONG).setAction("Cancel", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (position != -1) {
                                        addFavourite(position);
                                        mFavList.add(position, justRemovedItem);
                                        //        notifyItemRangeInserted(position, mFavList.size());
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
                values.put(COLUMN_TITLE, info.getTitle().replace("_", " "));
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

                String categories = "";
                if (info.getCategories() != null)
                    for (String cat : info.getCategories()) {
                        if (!categories.contains(cat))
                            authorsList.append(cat).append(","); //i separate every author using a ,
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
                    new String[]{title.replace("_", " ")}
            );
        }

        boolean isFavourite(String title) {
            String newName = title.replace("_", " ");
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
/*
    class MySection extends StatelessSection {

        ArrayList<String> totalCategories = new ArrayList<>();

        public MySection() {
            // call constructor with layout resources for this Section header and items
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.section_item)
                    .headerResourceId(R.layout.section_header)
                    .build());
        }

        void generateSections() {
            for (Item item : mFavList) {getCategories();
            }
        }

        void getCategories(String title) {
            String newName = title.replace("_", " ");
            String[] selections = {newName};
            ArrayList<String> categories = new ArrayList<>();

            Cursor cursor = mContext.getContentResolver().query(
                    BASE_CONTENT_URI,
                    new String[]{COLUMN_CATEGORIES},
                    COLUMN_TITLE + " =? ",
                    selections,
                    null);
            if (cursor != null) {
                cursor.moveToFirst();
                String category;
                for (int i = 0; i < cursor.getCount(); i++) {
                    category = cursor.getString(cursor
                            .getColumnIndexOrThrow(COLUMN_CATEGORIES));
                    categories.add(category);
                    cursor.moveToNext();
                }
                // always close the cursor
                cursor.close();
            }

            totalCategories = categories;
        }

        @Override
        public int getContentItemsTotal() {
            return itemList.size(); // number of items of this section
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            // return a custom instance of ViewHolder for the items of this section
            return new MyItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyItemViewHolder itemHolder = (MyItemViewHolder) holder;

            // bind your view here
            itemHolder.tvItem.setText(itemList.get(position));
        }
    } */
}
