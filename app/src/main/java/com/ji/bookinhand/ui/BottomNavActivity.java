package com.ji.bookinhand.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.ji.bookinhand.R;
import com.ji.bookinhand.ui.fragments.FavouritesFragment;
import com.ji.bookinhand.ui.fragments.HomeFragment;

import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.SearchItem;

public class BottomNavActivity extends AppCompatActivity implements PersistentSearchView.SearchListener {

    GoogleSignInAccount account;
    FloatingActionButton takePhoto;
    private String TAG = this.getClass().getSimpleName();
    private static final int RC_OCR_CAPTURE = 9003;
    PersistentSearchView mSearchView;
    MenuItem mSearchMenuItem;
    MenuItem mProfileMenuItem;
    FragmentManager fragmentManager;
    FavouritesFragment favouritesFragment;
    HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");

        if (getIntent() != null && getIntent().getExtras() != null) {
            String name = getIntent().getExtras().getString("name");
            Snackbar.make(toolbar, "Welcome " + name + "!", Snackbar.LENGTH_LONG).setAction("action", null).show();
        } else if (account != null) {
            Snackbar.make(toolbar, "Welcome back " + account.getDisplayName() + "!", Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(toolbar, "Welcome! You are not authenticated.", Snackbar.LENGTH_LONG).setAction("Login", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(BottomNavActivity.this, LoginActivity.class));
                }
            }).show();
        }
        mSearchView = (PersistentSearchView) findViewById(R.id.searchview);
        mSearchView.setSearchListener(this);

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
        homeFragment = new HomeFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, homeFragment)
                .commit();
    }

    public void openSearch() {
        View menuItemView = findViewById(R.id.action_search);
        mSearchView.setStartPositionFromMenuItem(menuItemView);
        mSearchView.openSearch();
    }

    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
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
        startActivity(new Intent(this, ResultsActivity.class)
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (homeFragment == null)
                        homeFragment = new HomeFragment();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                    return true;
                case R.id.navigation_favourites:
                    if (favouritesFragment == null)
                        favouritesFragment = new FavouritesFragment();
                    favouritesFragment = new FavouritesFragment();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, favouritesFragment).commit();
                    return true;
                case R.id.navigation_settings:
                    //settings page for data sync and account management
                    return true;
            }
            return false;
        }
    };
}

/*extends AppCompatActivity implements PersistentSearchView.SearchListener {

    FragmentManager fragmentManager;

    GoogleSignInAccount account;
    FloatingActionButton takePhoto;
    private String TAG = this.getClass().getSimpleName();
    private static final int RC_OCR_CAPTURE = 9003;
    PersistentSearchView mSearchView;
    MenuItem mSearchMenuItem;
    MenuItem mProfileMenuItem;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:

                    return true;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (getIntent() != null && getIntent().getExtras() != null) {
            String name = getIntent().getExtras().getString("name");
            Snackbar.make(navigation, "Welcome " + name + "!", Snackbar.LENGTH_LONG).setAction("action", null).show();
        } else if (account != null) {
            Snackbar.make(navigation, "Welcome back " + account.getDisplayName() + "!", Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(navigation, "Welcome! You are not authenticated.", Snackbar.LENGTH_LONG).setAction("Login", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(BottomNavActivity.this, LoginActivity.class));
                }
            }).show();
        }


        fragmentManager = getSupportFragmentManager();

        HomeFragment homeFragment = new HomeFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, homeFragment)
                .addToBackStack("backStack")
                .commit();
        mSearchView = findViewById(R.id.searchview);

        mSearchView = (PersistentSearchView) findViewById(R.id.searchview);
        mSearchView.setSearchListener(this);

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

    @Override
    public void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
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
 /*               Snackbar.make(takePhoto, "Something went wrong.", Snackbar.LENGTH_LONG).setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(BottomNavActivity.this, OcrCaptureActivity.class);
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
                        startActivity(new Intent(BottomNavActivity.this, ResultsActivity.class)
                                .putExtra("result", text)
                                .putExtra("isCat", false)); //is a category search (here is false since it is not)
                    }
                })
                .setNegativeButton("Try again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Intent intent = new Intent(BottomNavActivity.this, OcrCaptureActivity.class);
                        intent.putExtra(OcrCaptureActivity.AutoFocus, true); //focus
                        intent.putExtra(OcrCaptureActivity.UseFlash, true); //flash

                        startActivityForResult(intent, RC_OCR_CAPTURE);

                    }
                });
        builder.create();
        builder.show();
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


    public void openSearch() {
        View menuItemView = findViewById(R.id.action_search);
        mSearchView.setStartPositionFromMenuItem(menuItemView);
        mSearchView.openSearch();
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
        startActivity(new Intent(this, ResultsActivity.class)
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
*/