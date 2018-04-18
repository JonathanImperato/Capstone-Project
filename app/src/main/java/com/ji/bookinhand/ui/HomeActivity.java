package com.ji.bookinhand.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.ji.bookinhand.R;
import com.ji.bookinhand.adapters.BooksListAdapter;
import com.ji.bookinhand.api.models.ImageLinks;
import com.ji.bookinhand.api.models.Item;
import com.ji.bookinhand.api.models.VolumeInfo;

import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.SearchItem;

import java.util.ArrayList;
import java.util.Arrays;

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

public class HomeActivity extends AppCompatActivity implements PersistentSearchView.SearchListener {

    GoogleSignInAccount account;
    FloatingActionButton takePhoto;
    private String TAG = this.getClass().getSimpleName();
    private static final int RC_OCR_CAPTURE = 9003;
    RecyclerView favRecyclerview;
    BooksListAdapter adapter;
    ArrayList<Item> mFavList;
    PersistentSearchView mSearchView;
    MenuItem mSearchMenuItem;
    MenuItem mProfileMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");
        CollapsingToolbarLayout ct = findViewById(R.id.collapstoolbar);
        ct.setTitle("Home");

        if (getIntent() != null && getIntent().getExtras() != null) {
            String name = getIntent().getExtras().getString("name");
            Snackbar.make(toolbar, "Welcome " + name + "!", Snackbar.LENGTH_LONG).setAction("action", null).show();
        } else if (account != null) {
            Snackbar.make(toolbar, "Welcome back " + account.getDisplayName() + "!", Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(toolbar, "Welcome! You are not authenticated.", Snackbar.LENGTH_LONG).setAction("Login", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            }).show();
        }
        mSearchView = (PersistentSearchView) findViewById(R.id.searchview);
        mSearchView.setSearchListener(this);

      /*  RoundedImageView profilePic = findViewById(R.id.profilePic);
        if (account != null)
            Glide.with(this).load(account.getPhotoUrl()).into(profilePic);
        else
            Glide.with(this).load("https://lh3.googleusercontent.com/-KpBZmzRBm4A/AAAAAAAAAAI/AAAAAAAAM0k/qVSHIMlvopQ/s60-p-rw-no/photo.jpg").into(profilePic);

       */
        favRecyclerview = findViewById(R.id.favRecyclerview);
        favRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loadFav();
        adapter.notifyDataSetChanged();
        takePhoto = findViewById(R.id.takePhoto);
        takePhoto.setVisibility(View.VISIBLE);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, true); //focus
                intent.putExtra(OcrCaptureActivity.UseFlash, true); //flash

                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });
        if (mProfileMenuItem != null && account != null) {
            Glide.with(this)
                    .load(account.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            mProfileMenuItem.setIcon(resource);
                        }
                    });
        } else if (mProfileMenuItem != null && account == null) {
            Glide.with(this)
                    .load("https://lh3.googleusercontent.com/-KpBZmzRBm4A/AAAAAAAAAAI/AAAAAAAAM0k/qVSHIMlvopQ/s60-p-rw-no/photo.jpg")
                    .apply(RequestOptions.circleCropTransform())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            mProfileMenuItem.setIcon(resource);
                        }
                    });

        }
    }

    public void openSearch() {
        View menuItemView = findViewById(R.id.action_search);
        mSearchView.setStartPositionFromMenuItem(menuItemView);
        mSearchView.openSearch();
    }

    private void loadFav() {
        mFavList = getFav();
        adapter = new BooksListAdapter(this, mFavList);
        adapter.notifyDataSetChanged();
        favRecyclerview.setAdapter(adapter);
    }

    void updateRV() {
        mFavList.clear();
        adapter.notifyDataSetChanged();
        mFavList = getFav();
        adapter = new BooksListAdapter(this, mFavList);
        adapter.notifyDataSetChanged();
        favRecyclerview.setAdapter(adapter);
    }

    public ArrayList<Item> getFav() {
        Cursor ingredientCursor = this.getContentResolver()
                .query(BASE_CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        ArrayList<Item> books = new ArrayList<>();
        if (ingredientCursor != null) {
            while (ingredientCursor.moveToNext()) {
                Item ingredient = getDataFromCursor(ingredientCursor);
                books.add(ingredient);
            }
            ingredientCursor.close();
        }

        return books;
    }

    private Item getDataFromCursor(Cursor ingredientCursor) {
        Item ingredient = new Item();
        VolumeInfo volumeInfo = new VolumeInfo();
        volumeInfo.setTitle(
                ingredientCursor.getString(ingredientCursor
                        .getColumnIndex(COLUMN_TITLE)));
        volumeInfo.setAuthors(Arrays.asList(new String[]{
                ingredientCursor.getString(ingredientCursor
                        .getColumnIndex(COLUMN_AUTHORS))}));
        volumeInfo.setAverageRating(
                ingredientCursor.getDouble(ingredientCursor
                        .getColumnIndex(COLUMN_AVERAGE_RATING)));
        volumeInfo.setCanonicalVolumeLink(
                ingredientCursor.getString(ingredientCursor
                        .getColumnIndex(COLUMN_CANONICAL_VOLUME_LINK)));
        volumeInfo.setCategories(Arrays.asList(new String[]{
                ingredientCursor.getString(ingredientCursor
                        .getColumnIndex(COLUMN_CATEGORIES))}));
        volumeInfo.setDescription(
                ingredientCursor.getString(ingredientCursor
                        .getColumnIndex(COLUMN_DESCRIPTION)));
        ImageLinks img = new ImageLinks();
        img.setThumbnail(ingredientCursor.getString(ingredientCursor
                .getColumnIndex(COLUMN_IMAGE_LINKS)));
        volumeInfo.setImageLinks(img);
        volumeInfo.setInfoLink(
                ingredientCursor.getString(ingredientCursor
                        .getColumnIndex(COLUMN_INFO_LINK)));
        volumeInfo.setLanguage(
                ingredientCursor.getString(ingredientCursor
                        .getColumnIndex(COLUMN_LANGUAGE)));
        volumeInfo.setMaturityRating(
                ingredientCursor.getString(ingredientCursor
                        .getColumnIndex(COLUMN_MATURITY_RATING)));
        volumeInfo.setPageCount(
                ingredientCursor.getInt(ingredientCursor
                        .getColumnIndex(COLUMN_PAGE_COUNT)));
        volumeInfo.setPreviewLink(
                ingredientCursor.getString(ingredientCursor
                        .getColumnIndex(COLUMN_PREVIEW_LINK)));
        volumeInfo.setPrintType(
                ingredientCursor.getString(ingredientCursor
                        .getColumnIndex(COLUMN_PRINT_TYPE)));
        volumeInfo.setPublishedDate(
                ingredientCursor.getString(ingredientCursor
                        .getColumnIndex(COLUMN_PUBLISH_DATE)));
        volumeInfo.setPublisher(
                ingredientCursor.getString(ingredientCursor
                        .getColumnIndex(COLUMN_PUBLISHER)));
        volumeInfo.setRatingsCount(
                ingredientCursor.getInt(ingredientCursor
                        .getColumnIndex(COLUMN_RATING_COUNT)));
        volumeInfo.setSubtitle(
                ingredientCursor.getString(ingredientCursor
                        .getColumnIndex(COLUMN_SUBTITLE)));

        ingredient.setVolumeInfo(volumeInfo);

        return ingredient;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    // statusMessage.setText(R.string.ocr_success);
                    // textValue.setText(text);
                    createDialog(text);
                    Log.d(TAG, "Text read: " + text);
                } else {
                    //  statusMessage.setText(R.string.ocr_failure);
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                /* statusMessage.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
                */
                Snackbar.make(takePhoto, "Something went wrong.", Snackbar.LENGTH_LONG).setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, OcrCaptureActivity.class);
                        intent.putExtra(OcrCaptureActivity.AutoFocus, true); //focus
                        intent.putExtra(OcrCaptureActivity.UseFlash, true); //flash

                        startActivityForResult(intent, RC_OCR_CAPTURE);
                    }
                }).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void createDialog(final String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You selected \"" + text + "\". Are you sure?")
                .setTitle("Confirm your choice")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        startActivity(new Intent(HomeActivity.this, ResultsActivity.class)
                                .putExtra("result", text)
                                .putExtra("isCat", false)); //is a category search (here is false since it is not)
                    }
                })
                .setNegativeButton("Try again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Intent intent = new Intent(HomeActivity.this, OcrCaptureActivity.class);
                        intent.putExtra(OcrCaptureActivity.AutoFocus, true); //focus
                        intent.putExtra(OcrCaptureActivity.UseFlash, true); //flash

                        startActivityForResult(intent, RC_OCR_CAPTURE);

                    }
                });
        builder.create();
        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRV();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mSearchView.isSearching()) {
            mSearchView.closeSearch();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_searchview, menu);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mProfileMenuItem = menu.findItem(R.id.action_profile);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_profile:
                if (account != null) {
                    //settings activity?
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case R.id.action_search:
                if (mSearchMenuItem != null) {
                    openSearch();
                    return true;
                } else {
                    return false;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSuggestion(SearchItem searchItem) {
        return false;
    }

    @Override
    public void onSearchCleared() {

    }

    @Override
    public void onSearchTermChanged(String term) {

    }

    @Override
    public void onSearch(String query) {
        mSearchView.closeSearch();
        startActivity(new Intent(HomeActivity.this, ResultsActivity.class)
                .putExtra("result", query)
                .putExtra("isCat", false)); //is a category search (here is false since it is not)
    }

    @Override
    public void onSearchEditOpened() {

    }

    @Override
    public void onSearchEditClosed() {

    }

    @Override
    public boolean onSearchEditBackPressed() {
        return false;
    }

    @Override
    public void onSearchExit() {

    }
}
