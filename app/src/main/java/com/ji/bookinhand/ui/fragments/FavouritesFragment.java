package com.ji.bookinhand.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ji.bookinhand.R;
import com.ji.bookinhand.adapters.BooksListAdapter;
import com.ji.bookinhand.api.models.Item;

import java.util.ArrayList;

import static com.ji.bookinhand.database.ItemsContract.BASE_CONTENT_URI;

public class FavouritesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnLayoutChangeListener, LoaderManager.LoaderCallbacks<Cursor> {


    ImageView heart;
    private int ID_FAVOURITE_LOADER = 32;
    ConstraintLayout noFavLayout;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    BooksListAdapter mAdapter;
    ArrayList<Item> list;
    static FavouritesFragment fragment;
    private int milliseconds = 1000;
    private int numberOfSeconds = 3;
    private int seconds = milliseconds * numberOfSeconds;


    public FavouritesFragment() {
        // Required empty public constructor
    }

    public static FavouritesFragment newInstance() {
        return getInstance();
    }

    public static FavouritesFragment getInstance() {
        if (fragment == null)
            fragment = new FavouritesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_favourites, container, false);
        noFavLayout = v.findViewById(R.id.no_fav);
        heart = v.findViewById(R.id.heart);
        refreshLayout = v.findViewById(R.id.recyclerview_swipe);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(
                R.color.refresh_progress_3,
                R.color.refresh_progress_2,
                R.color.refresh_progress_1);

        mAdapter = new BooksListAdapter(getContext());
        recyclerView = v.findViewById(R.id.ebookfictionRv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getNumberOfColumns()));
        recyclerView.addOnLayoutChangeListener(this);
        recyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(ID_FAVOURITE_LOADER, null, this);

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {

            //restore recyclerview's data
        } else
            updateData();


    }

/*
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            list = new ArrayList<>();
            ArrayList<Item> items = savedInstanceState.getParcelableArrayList("data");
            list.addAll(items);
        }
    }
*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("data", ((BooksListAdapter) recyclerView.getAdapter()).getmFavList());

    }

    @Override
    public void onRefresh() {
        if (!refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(true);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter != null) {
                            updateData();
                        }
                        refreshLayout.setRefreshing(false);
                    }
                }, seconds
        );

    }

    void updateData() {
        getLoaderManager().restartLoader(ID_FAVOURITE_LOADER, null, this);
        recyclerView.setAdapter(mAdapter);
    }


    public int getNumberOfColumns() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int numberOfCs = (int) (dpWidth / 120);
        return numberOfCs;
    }

    /**
     * Method to display or hide the "no favourites" layout depending on the RecyclerView items
     */
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
      /*  if (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() == 0 && !getLoaderManager().hasRunningLoaders()) {
            noFavLayout.setVisibility(View.VISIBLE);
            Animation pulse = AnimationUtils.loadAnimation(getContext(), R.anim.pulse);
            heart.startAnimation(pulse);
        } else
            noFavLayout.setVisibility(View.GONE);*/
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getContext(),
                BASE_CONTENT_URI,
                null,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (recyclerView != null) {
            mAdapter = new BooksListAdapter(getContext());

            mAdapter.swapCursor(data);
            if (list != null)
                Log.d("LIST SIZE", "IS = " + list.size());
            recyclerView.setAdapter(mAdapter);
            int count = data.getCount();
            if (count != 0)
                showData();
        }
    }

    private void showData() {
        noFavLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.addOnLayoutChangeListener(this);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}
