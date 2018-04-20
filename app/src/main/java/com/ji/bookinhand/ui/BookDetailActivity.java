package com.ji.bookinhand.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ji.bookinhand.R;
import com.ji.bookinhand.adapters.BooksListAdapter;
import com.ji.bookinhand.adapters.CategoriesAdapter;
import com.ji.bookinhand.api.BooksClient;
import com.ji.bookinhand.api.models.BooksList;
import com.ji.bookinhand.api.models.ImageLinks;
import com.ji.bookinhand.api.models.VolumeInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static com.ji.bookinhand.R.drawable;
import static com.ji.bookinhand.R.id;
import static com.ji.bookinhand.R.layout;
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

public class BookDetailActivity extends AppCompatActivity {
    TextView titleTextView, authorTextView, pubblishdateTextView, addBookMark, descriptionBook, ratingTextView;
    List<String> authors;
    ImageView bookmark;
    Boolean isFav, isMore;
    String date, isbn = "";
    VolumeInfo item;
    RecyclerView catRecyclerView, moreRecyclerView;
    CategoriesAdapter adapter;
    ImageLinks imgs;
    String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_book_detail);
        Toolbar toolbar = findViewById(id.dtoolbar);
        setSupportActionBar(toolbar);
        titleTextView = findViewById(id.book_title);
        authorTextView = findViewById(id.author);
        pubblishdateTextView = findViewById(id.pubblish_date);
        ratingTextView = findViewById(id.rating_text);
        addBookMark = findViewById(id.addToFav2);
        catRecyclerView = findViewById(id.categories);
        moreRecyclerView = findViewById(id.moreRecyclerView);
        descriptionBook = findViewById(id.description);
        catRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        moreRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bookmark = findViewById(id.addToFav);

        if (getIntent().getExtras() != null) {
            item = getIntent().getExtras().getParcelable("volume");
            isFav = isFavourite(item.getTitle());
            isMore = getIntent().getExtras().getBoolean("isMore");
            isbn = getIntent().getExtras().getString("isbn");
            imgs = getIntent().getExtras().getParcelable("imgs");

            String title_book = item.getTitle();
            String description = item.getDescription();
            Double rating = item.getAverageRating();
            if (item.getPublishedDate() != null)
                if (item.getPublishedDate().length() == 25)
                    date = item.getPublishedDate().substring(0, item.getPublishedDate().length() - 15);
                else date = item.getPublishedDate();
            if (!isFav) {
                authors = item.getAuthors();
            } else {
                bookmark.setImageResource(drawable.ic_bookmark_black_24dp);
                addBookMark.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(drawable.ic_bookmark_black_24dp), null, null);
                if (item.getAuthors() != null)
                    authors = Arrays.asList(item.getAuthors().get(0).split(","));
            }

            ratingTextView.setText("Rating " + rating);
            titleTextView.setText(title_book);
            if (description != null && description.length() > 15)
                descriptionBook.setText(description);
            else {
                descriptionBook.setVisibility(GONE);
                View divider = findViewById(id.divider3);
                divider.setVisibility(GONE);
                Button moreInfo = findViewById(id.moreBtn);
                moreInfo.setVisibility(GONE);
            }
            toolbar.setTitle(title_book);
            if (date != null)
                pubblishdateTextView.setText(date);

            if (authors != null) {
                for (int i = 0; i < authors.size(); i++) {
                    String author = authors.get(i);
                    if (i == 0) {
                        if (authors.size() == 1)
                            authorTextView.setText(author);
                        else
                            authorTextView.setText(author + ",");
                    } else if (i < authors.size() - 1)
                        authorTextView.setText(authorTextView.getText() + " " + author + ",");
                    else
                        authorTextView.setText(authorTextView.getText() + " " + author);

                }
            }

            ImageView img = findViewById(id.img);
            if (imgs != null) {
                if (imgs.getExtraLarge() != null)
                    Glide.with(this).load(imgs.getExtraLarge()).into(img);
                else if (imgs.getLarge() != null)
                    Glide.with(this).load(imgs.getLarge()).into(img);
                else if (imgs.getMedium() != null)
                    Glide.with(this).load(imgs.getMedium()).into(img);
                else if (imgs.getThumbnail() != null)
                    Glide.with(this).load(imgs.getThumbnail()).into(img);

            }
            List<String> cats = null;
            if (item.getCategories() != null)
                if (isFav) {
                    cats = getFavCategories();//Arrays.asList(item.getCategories().get(i).split(","));
                } else {
                    cats = item.getCategories();
                }
            adapter = new CategoriesAdapter(this, cats, item.getAverageRating());
            catRecyclerView.setAdapter(adapter);
            setMoreFromAuthorRecyclerView();
        }
    }

    List<String> getFavCategories() {
        List<String> lista = new ArrayList<>();

        String[] projection = new String[]{COLUMN_CATEGORIES};
        String selection = COLUMN_TITLE + " =? ";
        String newName = item.getTitle();
        String[] selectionArgs = {newName};
        String sortOrder = null;
        Cursor cursor = getContentResolver().query(BASE_CONTENT_URI, projection, selection, selectionArgs,
                sortOrder);

        if (cursor != null) {
            cursor.moveToFirst();
            String category;
            for (int i = 0; i < cursor.getCount(); i++) {
                category = cursor.getString(cursor
                        .getColumnIndexOrThrow(COLUMN_CATEGORIES));
                List<String> cat = Arrays.asList(category.split(","));
                lista.addAll(cat);
                cursor.moveToNext();
            }

            cursor.close();
        }

        return lista;
    }

    public void onAddBookmark(View view) {
        if (isFavourite(item.getTitle())) { //remove fav
            bookmark.setImageResource(drawable.ic_bookmark_border_black_24dp);
            addBookMark.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(drawable.ic_bookmark_border_black_24dp), null, null);
            final String bookTitle = item.getTitle();
            Snackbar.make(view, bookTitle + " has been removed from favourites", Snackbar.LENGTH_LONG).show();
            removeFromFav();
        } else { //add fav
            bookmark.setImageResource(drawable.ic_bookmark_black_24dp);
            addBookMark.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(drawable.ic_bookmark_black_24dp), null, null);
            addToFavourite();
            Snackbar.make(view, item.getTitle() + " has been added to favourites.", Snackbar.LENGTH_LONG).show();
        }
    }

    boolean isFavourite(String title) {
        String newName = title;
        String[] selections = {newName};
        Cursor c = this.getContentResolver().query(
                BASE_CONTENT_URI,
                null,
                COLUMN_TITLE + " =? ",
                selections,
                null);

        return c.getCount() > 0;
    }

    void addToFavourite() {
        VolumeInfo info = item;
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, info.getTitle());
        StringBuilder authorsList = new StringBuilder();
        if (info.getAuthors() != null)
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
        if (imgs != null)
            if (imgs.getExtraLarge() != null)
                values.put(COLUMN_IMAGE_LINKS, imgs.getExtraLarge());
            else if (imgs.getLarge() != null)
                values.put(COLUMN_IMAGE_LINKS, imgs.getLarge());
            else if (imgs.getMedium() != null)
                values.put(COLUMN_IMAGE_LINKS, imgs.getMedium());
            else if (imgs.getThumbnail() != null)
                values.put(COLUMN_IMAGE_LINKS, imgs.getThumbnail());
        values.put(COLUMN_LANGUAGE, info.getLanguage());
        values.put(COLUMN_PREVIEW_LINK, info.getPreviewLink());
        values.put(COLUMN_INFO_LINK, info.getInfoLink());
        values.put(COLUMN_CANONICAL_VOLUME_LINK, info.getCanonicalVolumeLink());
        values.put(COLUMN_DESCRIPTION, info.getDescription());
        values.put(COLUMN_SUBTITLE, info.getSubtitle());
        this.getContentResolver().insert(BASE_CONTENT_URI, values);
    }

    void removeFromFav() {
        this.getContentResolver().delete(
                BASE_CONTENT_URI,
                COLUMN_TITLE + " =? ",
                new String[]{item.getTitle().replace("_", " ")}
        );
    }

    private void setMoreFromAuthorRecyclerView() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BooksClient service = retrofit.create(BooksClient.class);
        Call<BooksList> books = service.getBookFromAuthor("inauthor:" + item.getAuthors().get(0));
        books.enqueue(new Callback<BooksList>() {
            @Override
            public void onResponse(Call<BooksList> call, Response<BooksList> response) {
                BooksList result = response.body();
                Log.d(TAG, "Numero di libri da autore: " + result.getTotalItems());
                BooksListAdapter adapter = new BooksListAdapter(BookDetailActivity.this, result, true);
                moreRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<BooksList> call, Throwable t) {
                Toast.makeText(BookDetailActivity.this, "Error while fetching data :(", Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage(), t);
            }
        });

    }

    public void onPreview(View view) {
        if (item != null) {
            String previewLink = item.getPreviewLink();
            if (previewLink != null) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                customTabsIntent.launchUrl(this, Uri.parse(previewLink));
            } else
                Snackbar.make(view, R.string.no_preview, Snackbar.LENGTH_LONG).show();
        }
    }

    public void onShare(View view) {
        String url = item.getInfoLink();
        String title = item.getTitle();
        if (url != null) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sharing " + title);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else Snackbar.make(view, "Sorry, could not share", Snackbar.LENGTH_LONG).show();
    }

    public void onMoreInfo(View view) {
        Intent intent = new Intent(this, MoreInfoActivity.class);
        intent.putExtra("item", item);
        intent.putExtra("isbn", isbn);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onMoreFromAuthor(View view) {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("isCat", false);
        intent.putExtra("result", "inauthor:" + item.getAuthors().get(0));
        startActivity(intent);
    }
}
