package com.ji.bookinhand.ui;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ji.bookinhand.BuildConfig;
import com.ji.bookinhand.R;
import com.ji.bookinhand.adapters.BooksListAdapter;
import com.ji.bookinhand.adapters.CategoriesAdapter;
import com.ji.bookinhand.adapters.ReviewsAdapter;
import com.ji.bookinhand.api.BooksClient;
import com.ji.bookinhand.api.models.BooksList;
import com.ji.bookinhand.api.models.ImageLinks;
import com.ji.bookinhand.api.models.Review;
import com.ji.bookinhand.api.models.VolumeInfo;
import com.ji.bookinhand.api.nytmodels.BookDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static com.ji.bookinhand.R.color;
import static com.ji.bookinhand.R.drawable;
import static com.ji.bookinhand.R.id;
import static com.ji.bookinhand.R.layout;
import static com.ji.bookinhand.R.transition;
import static com.ji.bookinhand.database.Book.COLUMN_AUTHORS;
import static com.ji.bookinhand.database.Book.COLUMN_AVERAGE_RATING;
import static com.ji.bookinhand.database.Book.COLUMN_CANONICAL_VOLUME_LINK;
import static com.ji.bookinhand.database.Book.COLUMN_CATEGORIES;
import static com.ji.bookinhand.database.Book.COLUMN_DESCRIPTION;
import static com.ji.bookinhand.database.Book.COLUMN_IMAGE_LINKS;
import static com.ji.bookinhand.database.Book.COLUMN_INFO_LINK;
import static com.ji.bookinhand.database.Book.COLUMN_LANGUAGE;
import static com.ji.bookinhand.database.Book.COLUMN_MATURITY_RATING;
import static com.ji.bookinhand.database.Book.COLUMN_PAGE_COUNT;
import static com.ji.bookinhand.database.Book.COLUMN_PREVIEW_LINK;
import static com.ji.bookinhand.database.Book.COLUMN_PRINT_TYPE;
import static com.ji.bookinhand.database.Book.COLUMN_PUBLISHER;
import static com.ji.bookinhand.database.Book.COLUMN_PUBLISH_DATE;
import static com.ji.bookinhand.database.Book.COLUMN_RATING_COUNT;
import static com.ji.bookinhand.database.Book.COLUMN_SUBTITLE;
import static com.ji.bookinhand.database.Book.COLUMN_TITLE;
import static com.ji.bookinhand.database.BookContentProvider.URI_Book;

public class BookDetailActivity extends AppCompatActivity {
    TextView titleTextView, authorTextView, pubblishdateTextView, addBookMark, descriptionBook, ratingTextView, revs_title;
    List<String> authors;
    ImageView bookmark;
    Boolean isFav = false, isNyt;
    String date, isbn = "";
    ReviewsAdapter reviewsAdapter;
    VolumeInfo item;
    RecyclerView catRecyclerView, moreRecyclerView, reviewsRecyclerView;
    CategoriesAdapter adapter;
    ImageLinks imgs;
    BookDetail libro;
    String TAG = this.getClass().getSimpleName();
    ImageView img;
    RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_book_detail);
        Toolbar toolbar = findViewById(id.dtoolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } catch (NullPointerException ex) {
            Toast.makeText(this, R.string.exception, Toast.LENGTH_SHORT).show();
        }

        bindViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                    .inflateTransition(transition.curve));
        }

        if (getIntent().getExtras() != null) {
            isNyt = getIntent().getExtras().getBoolean("isNyt");
            if (!isNyt) {
                item = getIntent().getExtras().getParcelable("volume");
                new isFavouriteAs().execute(item.getTitle());

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

                if (rating != null && !rating.isNaN() && rating > -1 && rating.floatValue() != 0.0) {
                    ratingTextView.setText(String.valueOf(rating));
                    if (rating.floatValue() <= 5)
                        ratingBar.setRating(rating.floatValue()); //5 mark vote
                    else if (rating.floatValue() <= 10)
                        ratingBar.setRating(rating.floatValue() / 2); //10 mark vote
                    else
                        ratingBar.setRating(rating.floatValue() / 3); //15 mark vote

                } else {
                    ratingBar.setVisibility(GONE);
                    ratingTextView.setVisibility(GONE);
                }
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
                                authorTextView.setText(author + getString(R.string.comma));
                        } else if (i < authors.size() - 1)
                            authorTextView.setText(authorTextView.getText() + " " + author + getString(R.string.comma));
                        else
                            authorTextView.setText(authorTextView.getText() + " " + author);

                    }
                }

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

                setCategoriesRecyclerView(cats);
            } else {
                libro = getIntent().getExtras().getParcelable("volume");

                new isFavouriteAs().execute(libro.getTitle());
                imgs = getIntent().getExtras().getParcelable("imgs");
                String title_book = libro.getTitle();
                String description = libro.getDescription();

                if (!isFav) {
                    authors = Collections.singletonList(libro.getAuthor());
                } else {
                    bookmark.setImageResource(drawable.ic_bookmark_black_24dp);
                    addBookMark.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(drawable.ic_bookmark_black_24dp), null, null);
                    if (libro.getAuthor() != null)
                        authors = Collections.singletonList(libro.getAuthor());
                }

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

                ratingBar.setVisibility(GONE);
                ratingTextView.setVisibility(GONE);

                toolbar.setTitle(title_book);

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

                String url = getIntent().getExtras().getString("amazonUrl");
                Button prvBtn = findViewById(id.previewBtn);
                if (url != null)
                    prvBtn.setText(R.string.buy_on_amazon);
            }

            setMoreFromAuthorRecyclerView();
            setReviewsRecyclerView();
            if (!isOnline()) {
                revs_title.setVisibility(GONE);
                CardView cardView = findViewById(R.id.cardMore);
                cardView.setVisibility(GONE);
            }
        }


    }

    void setCategoriesRecyclerView(List<String> cats) {
        adapter = new CategoriesAdapter(this, cats, item.getAverageRating());
        catRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void bindViews() {
        titleTextView = findViewById(id.book_title);
        authorTextView = findViewById(id.author);
        pubblishdateTextView = findViewById(id.pubblish_date);
        ratingTextView = findViewById(id.rating_text);
        addBookMark = findViewById(id.addToFav2);
        descriptionBook = findViewById(id.description);
        bookmark = findViewById(id.addToFav);
        revs_title = findViewById(id.review_title);
        ratingBar = findViewById(id.ratingBar);

        img = findViewById(id.img);
        catRecyclerView = findViewById(id.categoriesRecyclerView);

        catRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        reviewsRecyclerView = findViewById(id.reviewsRecyclerView);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        moreRecyclerView = findViewById(id.moreRecyclerView);
        moreRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    List<String> getFavCategories() {
        List<String> lista = new ArrayList<>();

        String[] projection = new String[]{COLUMN_CATEGORIES};
        String selection = COLUMN_TITLE + " =? ";
        String newName = item.getTitle();
        String[] selectionArgs = {newName};
        String sortOrder = null;
        Cursor cursor = getContentResolver().query(URI_Book, projection, selection, selectionArgs,
                sortOrder);

        if (cursor != null) {
            cursor.moveToFirst();
            String category = "";
            for (int i = 0; i < cursor.getCount(); i++) {
                category = cursor.getString(cursor
                        .getColumnIndexOrThrow(COLUMN_CATEGORIES));
                if (category != null) {
                    List<String> cat = Arrays.asList(category.split(","));
                    lista.addAll(cat);
                }
                cursor.moveToNext();
            }

            cursor.close();
        }

        return lista;
    }

    public void onAddBookmark(View view) {
        if (BuildConfig.FLAVOR.equals("free")) {
            createUpgradeDialog();
        } else {
            if (!isNyt) {
                final String bookTitle = item.getTitle();
                if (isFav) { //remove fav
                    animateVectorDrawable(true);
                    Snackbar.make(view, bookTitle + " " + getString(R.string.removed_from_favs), Snackbar.LENGTH_LONG).show();
                    removeFromFav();
                } else { //add fav
                    animateVectorDrawable(false);
                    addToFavourite();
                    Snackbar.make(view, bookTitle + " " + getString(R.string.added_to_favs), Snackbar.LENGTH_LONG).show();
                }
            } else {
                final String bookTitle = libro.getTitle();
                if (isFav) { //remove fav
                    animateVectorDrawable(true);
                    Snackbar.make(view, bookTitle + " " + getString(R.string.removed_from_favs), Snackbar.LENGTH_LONG).show();
                    removeFromFav();
                } else { //add fav
                    animateVectorDrawable(false);
                    addToFavourite();
                    Snackbar.make(view, bookTitle + " " + getString(R.string.added_to_favs), Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private void createUpgradeDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.paid_feature))
                .setTitle(getString(R.string.paid_feature_title))
                .setPositiveButton(getString(R.string.learn_more), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String appPackageName = "com.ji.bookinhand.paid";
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (ActivityNotFoundException ex) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .setNegativeButton(R.string.no_thanks, null);

        builder.create().show();
    }

    void animateVectorDrawable(boolean isBooked) {
  /*       if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // must have a similar path, same length
            AnimatedVectorDrawable drawable = null;
            if (isBooked) {
                drawable = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_bookmark_full_to_border);
                bookmark.setImageDrawable(drawable);
                addBookMark.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                drawable.start();
            } else {
                drawable = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_bookmark_border_to_full);
                bookmark.setImageDrawable(drawable);
                addBookMark.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                drawable.start();
            }
        } else
*/
        {
            if (isBooked) {
                bookmark.setImageResource(drawable.ic_bookmark_border_black_24dp);
                addBookMark.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(drawable.ic_bookmark_border_black_24dp), null, null);
            } else {
                bookmark.setImageResource(drawable.ic_bookmark_black_24dp);
                addBookMark.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(drawable.ic_bookmark_black_24dp), null, null);
            }
        }
    }

    public class isFavouriteAs extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            if (BuildConfig.FLAVOR.equals("paid")) {
                String newName = strings[0];
                String[] selections = {newName};
                Cursor c = getApplicationContext().getContentResolver().query(
                        URI_Book,
                        null,
                        COLUMN_TITLE + " =? ",
                        selections,
                        null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return Objects.requireNonNull(c.getCount() > 0);
                } else {
                    if (c != null)
                        return c.getCount() > 0;
                }
            }
            return false; //always false for free flavour
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            isFav = aBoolean;
            if (isFav == true) {
                bookmark.setImageResource(drawable.ic_bookmark_black_24dp);
                addBookMark.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(drawable.ic_bookmark_black_24dp), null, null);
            }
        }

    }

    /*
        boolean isFavourite(String title) {
            if (BuildConfig.FLAVOR.equals("paid")) {
                String newName = title;
                String[] selections = {newName};
                Cursor c = this.getContentResolver().query(
                        URI_Book,
                        null,
                        COLUMN_TITLE + " =? ",
                        selections,
                        null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return Objects.requireNonNull(c.getCount() > 0);
                } else {
                    if (c != null)
                        return c.getCount() > 0;
                }
            } else return false; //always false for free flavour
            return false;
        }
    */
    void addToFavourite() {
        String bookTitle = "";
        if (!isNyt) bookTitle = item.getTitle();
        else bookTitle = libro.getTitle();
        ContentValues values;
        if (!isNyt) {
            values = new ContentValues();
            VolumeInfo info = item;
            values.put(COLUMN_TITLE, bookTitle);
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
                    if (cat != null && !categories.toString().contains(cat))
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
        } else {

            values = new ContentValues();
            values.put(COLUMN_AUTHORS, libro.getAuthor());
            values.put(COLUMN_PAGE_COUNT, 0);
            values.put(COLUMN_AUTHORS, libro.getAuthor());
            values.put(COLUMN_TITLE, bookTitle);
            if (imgs != null)
                if (imgs.getExtraLarge() != null)
                    values.put(COLUMN_IMAGE_LINKS, imgs.getExtraLarge());
                else if (imgs.getLarge() != null)
                    values.put(COLUMN_IMAGE_LINKS, imgs.getLarge());
                else if (imgs.getMedium() != null)
                    values.put(COLUMN_IMAGE_LINKS, imgs.getMedium());
                else if (imgs.getThumbnail() != null)
                    values.put(COLUMN_IMAGE_LINKS, imgs.getThumbnail());

            values.put(COLUMN_DESCRIPTION, libro.getDescription());
        }
        new addToFav().execute(values);
    }

    public class addToFav extends AsyncTask<ContentValues, Void, Void> {


        @Override
        protected Void doInBackground(ContentValues... contentValues) {

            getContentResolver().insert(URI_Book, contentValues[0]);
            return null;
        }
    }

    public class removeFromFav extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            getContentResolver().delete(
                    URI_Book,
                    COLUMN_TITLE + " =? ",
                    new String[]{strings[0].replace("_", " ")}
            );
            return null;
        }
    }

    void removeFromFav() {
        String bookTitle = null;
        if (!isNyt) bookTitle = item.getTitle();
        else bookTitle = libro.getTitle();
        new removeFromFav().execute(bookTitle);
    }

    private void setMoreFromAuthorRecyclerView() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(moreRecyclerView);

    /*    moreRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.computeHorizontalScrollOffset() != 0) {
                    TranslateAnimation anim = new TranslateAnimation(0.25f * (0 - recyclerView.computeHorizontalScrollOffset()),
                            0.25f * (0 - recyclerView.computeHorizontalScrollOffset()), 0f, 0f);
                    anim.setFillAfter(true);
                    anim.setDuration(0);
                    moreFromAuthor.startAnimation(anim);
                    moreFromAuthor.setAlpha(recyclerView.computeHorizontalScrollOffset() * (-1.4f / recyclerView.computeHorizontalScrollExtent()) + 1f);
                } else {
                    moreFromAuthor.setAlpha(1f);
                }
            }
        });
        */

        BooksClient service = retrofit.create(BooksClient.class);
        if (isNyt && libro.getAuthor() != null || !isNyt && item.getAuthors() != null) {
            Call<BooksList> books;
            if (!isNyt)
                books = service.getBookFromAuthor("inauthor:" + item.getAuthors().get(0));
            else
                books = service.getBookFromAuthor("inauthor:" + libro.getAuthor());

            books.enqueue(new Callback<BooksList>() {
                @Override
                public void onResponse(Call<BooksList> call, Response<BooksList> response) {
                    BooksList result = response.body();
                    BooksListAdapter adapter = new BooksListAdapter(BookDetailActivity.this, result, true);
                    moreRecyclerView.setAdapter(adapter);
                    if (result == null || result.getTotalItems() == 0) {
                        CardView cardView = findViewById(id.cardMore);
                        cardView.setVisibility(GONE);
                    }
                }

                @Override
                public void onFailure(Call<BooksList> call, Throwable t) {

                    Log.d(TAG, t.getMessage(), t);
                }
            });
        } else {
            CardView rootCard = (CardView) findViewById(id.cardMore);
            rootCard.setVisibility(GONE);
        }
    }

    private void setReviewsRecyclerView() {
        if (isOnline()) {
            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.nytimes.com/svc/books/v3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            BooksClient service = retrofit.create(BooksClient.class);
            Call<Review> books;
            if (!isNyt)
                books = service.getBookReviewsByTitle(item.getTitle(), getString(R.string.nyt_api_key));
            else
                books = service.getBookReviewsByTitle(libro.getTitle(), getString(R.string.nyt_api_key));

            books.enqueue(new Callback<Review>() {
                @Override
                public void onResponse(Call<Review> call, Response<Review> response) {
                    Review result = response.body();

                    reviewsAdapter = new ReviewsAdapter(BookDetailActivity.this, result);
                    reviewsRecyclerView.setAdapter(reviewsAdapter);
                    if (reviewsAdapter == null || reviewsAdapter.getItemCount() == 0) {
                        revs_title.setVisibility(GONE);
                    } //else reviewsRecyclerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<Review> call, Throwable t) {

                    Log.d(TAG, t.getMessage(), t);
                }
            });
        } else {
            Snackbar.make(authorTextView, getString(R.string.no_reviews_offline), Snackbar.LENGTH_LONG).show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void onPreview(View view) {
        if (!isNyt && item != null) {
            String previewLink = item.getPreviewLink();
            if (previewLink != null) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                builder.setToolbarColor(getResources().getColor(color.colorPrimary));
                customTabsIntent.launchUrl(this, Uri.parse(previewLink));
            } else
                Snackbar.make(view, R.string.no_preview, Snackbar.LENGTH_LONG).show();
        } else {
            String url = getIntent().getExtras().getString("amazonUrl");
            if (url != null) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                builder.setToolbarColor(getResources().getColor(color.colorPrimary));
                customTabsIntent.launchUrl(this, Uri.parse(url));
            } else
                Snackbar.make(view, R.string.no_preview, Snackbar.LENGTH_LONG).show();
        }
    }

    public void onShare(View view) {
        if (item != null) {
            String url = item.getInfoLink();
            String title = item.getTitle();
            if (url != null) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.sharing) + title);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.sharing_via)));
            }
        } else Snackbar.make(view, R.string.couldnt_shre, Snackbar.LENGTH_LONG).show();
    }

    public void onMoreInfo(View view) {
        Intent intent = new Intent(this, MoreInfoActivity.class);
        if (isNyt)
            intent.putExtra("libro", libro);
        else
            intent.putExtra("item", item);
        intent.putExtra("isbn", isbn);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onMoreFromAuthor(View view) {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("isCat", false);
        if (item != null && item.getAuthors() != null && item.getAuthors().get(0) != null) {
            intent.putExtra("result", "inauthor:" + item.getAuthors().get(0));
        } else if (libro != null && libro.getAuthor() != null) {
            intent.putExtra("result", "inauthor:" + libro.getAuthor());
        }
        startActivity(intent);
    }


}
