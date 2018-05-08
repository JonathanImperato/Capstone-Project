package com.ji.bookinhand.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ji.bookinhand.BuildConfig;
import com.ji.bookinhand.R;
import com.ji.bookinhand.ui.fragments.FavouritesFragment;
import com.ji.bookinhand.ui.fragments.HomeFragment;
import com.ji.bookinhand.ui.fragments.SettingsFragment;

import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.SearchItem;


public class HomeActivity extends AppCompatActivity implements PersistentSearchView.SearchListener {

    GoogleSignInAccount account;
    FloatingActionButton takePhoto;
    private String TAG = this.getClass().getSimpleName();
    private static final int RC_OCR_CAPTURE = 9003;
    PersistentSearchView mSearchView;
    private String MY_PREFS_NAME;
    MenuItem mSearchMenuItem;
    MenuItem mProfileMenuItem;
    FragmentManager fragmentManager;
    FavouritesFragment favouritesFragment;
    HomeFragment homeFragment;
    SettingsFragment settingsFragment;
    ImageView noConnectionImg;
    FrameLayout frameLayout;
    InterstitialAd mInterstitialAd;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    BottomNavigationView navigation;
    String TEST_DEVICE = "69A744B1C87D5CA7268C31E20AC93CCA";
    Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");
        MY_PREFS_NAME = getResources().getString(R.string.history_pref_name);
        frameLayout = findViewById(R.id.fragment_container);
        noConnectionImg = findViewById(R.id.imageNoCon);
        prefs = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (BuildConfig.FLAVOR.equals("free")) {
            // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
            MobileAds.initialize(this,
                    getString(R.string.admob_app_id));
            mInterstitialAd = new InterstitialAd(this);
            //TODO: REMOVE TEST DEVICE BEFORE PUBLISHING
            //TODO: AFTER PUBLISHED, ADD THE APP TO THE ADMOB CONSOLE
            final AdRequest request = new AdRequest.Builder()
                    .addTestDevice(new String(AdRequest.DEVICE_ID_EMULATOR))  // An example device ID
                    .addTestDevice(TEST_DEVICE)
                    .build();

            mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
            mInterstitialAd.loadAd(request);//new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    mInterstitialAd.loadAd(request);//new AdRequest.Builder().build());
                }

            });
        }

        if (account != null) {
            Toast.makeText(this, "Welcome " + account.getDisplayName() + "!", Toast.LENGTH_SHORT).show();
        }

        mSearchView = (PersistentSearchView) findViewById(R.id.searchview);
        mSearchView.setSearchListener(this);
        SuggestionBuilder mSuggestionBuilder = new SuggestionBuilder(this);
        mSearchView.setSuggestionBuilder(mSuggestionBuilder);

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
        }
        fragmentManager = getSupportFragmentManager();

        /*switch (navigation.getSelectedItemId()) {
            case R.id.navigation_settings:
                fragment = selectedFragment = new SettingsFragment();
                break;
            case R.id.navigation_home:
                fragment = selectedFragment = new HomeFragment();
                break;
            default:
                fragment = selectedFragment = new FavouritesFragment();
                break;
        }*/
        if (savedInstanceState == null) {
            navigation.setSelectedItemId(R.id.navigation_home);
        /*    selectedFragment = new HomeFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, selectedFragment)
                    .commit();*/
        }

        if (isOnline()) {
            frameLayout.setVisibility(View.VISIBLE);
            noConnectionImg.setVisibility(View.GONE);

        } else {
            animateNoConnection();
            frameLayout.setVisibility(View.GONE);
            noConnectionImg.setVisibility(View.VISIBLE);
        }

    }


    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    noConnectionImg.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onLost(Network network) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    noConnectionImg.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.GONE);
                    animateNoConnection();
                }
            });
        }
    };

    void animateNoConnection() {
        AnimatedVectorDrawable failed = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            failed = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_no_connection);
            noConnectionImg.setImageDrawable(failed);
            failed.start();
        }
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
    protected void onResume() {
        super.onResume();
        SuggestionBuilder mSuggestionBuilder = new SuggestionBuilder(this);
        mSearchView.setSuggestionBuilder(mSuggestionBuilder);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mSearchView.isSearching()) {
            mSearchView.closeSearch();
        }
    }

    @Override
    protected void onPause() {
        Log.d("Home", "onPause called");
        super.onPause();
        if (BuildConfig.FLAVOR.equals("free") && mInterstitialAd != null) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("HomeActivity", "The interstitial wasn't loaded yet.");
            }
        }
        Log.d("Home", "onPause finished");
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
                    navigation.setSelectedItemId(R.id.navigation_settings);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        //return super.onPrepareOptionsMenu(menu);
        mProfileMenuItem = menu.getItem(1);
        if (account != null)
            Glide.with(this)
                    .load(account.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            mProfileMenuItem.setIcon(resource);
                        }
                    });
        return true;
    }

    @Override
    public boolean onSuggestion(SearchItem searchItem) {
        /*
        String query = searchItem.getValue();
        startActivity(new Intent(this, ResultsActivity.class).putExtra("result", query))
        */
        return true;
    }

    @Override
    public void onSearchCleared() {

    }

    @Override
    public void onSearchTermChanged(String term) {

    }

    @Override
    public void onSearch(String query) {
        addToHistory(query);
        mSearchView.closeSearch();
        startActivity(new Intent(this, ResultsActivity.class)
                .putExtra("result", query));
    }

    void addToHistory(String value) {
        //I save history in SharedPreferences
        if (prefs.getString(MY_PREFS_NAME, null) == null || !prefs.getString(MY_PREFS_NAME, null).contains(value)) {
            String newValue = prefs.getString(MY_PREFS_NAME, null) + "," + value;
            editor.putString(MY_PREFS_NAME, newValue);
            editor.apply();
        }
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
            if (item.getItemId() == R.id.navigation_home) {

                if (homeFragment == null)
                    homeFragment = HomeFragment.newInstance();
                loadFragment(homeFragment);


            } else if (item.getItemId() == R.id.navigation_settings) {

                if (settingsFragment == null)
                    settingsFragment = SettingsFragment.newInstance();
                loadFragment(settingsFragment);


            } else {

                if (favouritesFragment == null)
                    favouritesFragment = FavouritesFragment.newInstance();
                loadFragment(favouritesFragment);


            }
            if (!isOnline()) {
                frameLayout.setVisibility(View.VISIBLE);
                noConnectionImg.setVisibility(View.GONE);
            }
            return true; //this highlight the selected item with primary color
        }

    };

    void loadFragment(Fragment fragment) {

        for (int i = 0; i < fragmentManager.getFragments().size(); i++) {
            fragmentManager
                    .beginTransaction()
                    .hide(fragmentManager.getFragments().get(i))
                    .commit();
        }
        if (fragmentManager.getFragments().contains(fragment)) {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .show(fragment)
                    .commit();
        } else {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .addToBackStack("backstack")
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
        selectedFragment = fragment;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cm.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
                    networkCallback);
        }

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}