package com.ji.bookinhand.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ji.bookinhand.R;
import com.ji.bookinhand.adapters.BooksListAdapter;
import com.ji.bookinhand.api.models.ImageLinks;
import com.ji.bookinhand.api.models.Item;
import com.ji.bookinhand.api.models.VolumeInfo;

import java.util.ArrayList;
import java.util.Arrays;

import static com.ji.bookinhand.database.ItemsContract.BASE_CONTENT_URI;
import static com.ji.bookinhand.database.Room.Book.COLUMN_AUTHORS;
import static com.ji.bookinhand.database.Room.Book.COLUMN_AVERAGE_RATING;
import static com.ji.bookinhand.database.Room.Book.COLUMN_CANONICAL_VOLUME_LINK;
import static com.ji.bookinhand.database.Room.Book.COLUMN_CATEGORIES;
import static com.ji.bookinhand.database.Room.Book.COLUMN_DESCRIPTION;
import static com.ji.bookinhand.database.Room.Book.COLUMN_IMAGE_LINKS;
import static com.ji.bookinhand.database.Room.Book.COLUMN_INFO_LINK;
import static com.ji.bookinhand.database.Room.Book.COLUMN_LANGUAGE;
import static com.ji.bookinhand.database.Room.Book.COLUMN_MATURITY_RATING;
import static com.ji.bookinhand.database.Room.Book.COLUMN_PAGE_COUNT;
import static com.ji.bookinhand.database.Room.Book.COLUMN_PREVIEW_LINK;
import static com.ji.bookinhand.database.Room.Book.COLUMN_PRINT_TYPE;
import static com.ji.bookinhand.database.Room.Book.COLUMN_PUBLISHER;
import static com.ji.bookinhand.database.Room.Book.COLUMN_PUBLISH_DATE;
import static com.ji.bookinhand.database.Room.Book.COLUMN_RATING_COUNT;
import static com.ji.bookinhand.database.Room.Book.COLUMN_SUBTITLE;
import static com.ji.bookinhand.database.Room.Book.COLUMN_TITLE;


public class FavouritesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnLayoutChangeListener {

    private static final int LOADER_BOOKS = 1;

    public FavouritesFragment() {
        // Required empty public constructor

        setHasOptionsMenu(true);
    }

    ImageView heart;
    ConstraintLayout noFavLayout;
    SwipeRefreshLayout refreshLayout;
    ArrayList<Item> mFavList = new ArrayList<>();
    RecyclerView recyclerView;
    BooksListAdapter adapter;
    String listKey = "recyclerViewData";


    public static FavouritesFragment newInstance() {
        FavouritesFragment fragment = new FavouritesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_favourites, container, false);
        noFavLayout = v.findViewById(R.id.no_fav);
        heart = v.findViewById(R.id.heart);
        recyclerView = v.findViewById(R.id.ebookfictionRv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getNumberOfColumns()));
        refreshLayout = v.findViewById(R.id.recyclerview_swipe);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(
                R.color.refresh_progress_3,
                R.color.refresh_progress_2,
                R.color.refresh_progress_1);
/*
        prefs = getContext().getSharedPreferences("general_settings", Context.MODE_PRIVATE);
        String layoutSetting = prefs.getString("layoutSetting", "");
        if (layoutSetting == null)
            isDashboard = true;
        else if (layoutSetting.equals("list"))
            isDashboard = false;
        else isDashboard = true;
        */
        if (mFavList.size() == 0 || mFavList == null) //has not been restored
            loadData();
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
            //it is resumed from the homeActivity so no need to perform anything
        } else {
            //it is an orientation change or something similar, need to restore its state
            if (mFavList != null) {
                adapter = new BooksListAdapter(getContext(), mFavList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getNumberOfColumns()));
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mFavList = savedInstanceState.getParcelableArrayList(listKey);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mFavList = adapter.getmFavList();
        outState.putParcelableArrayList(listKey, mFavList);
    }

    private void loadData() {
        mFavList.clear();
        //mFavList = getFav();


        // getLoaderManager().initLoader(LOADER_BOOKS, null, mLoaderCallbacks);

        //    if (isDashboard) {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getNumberOfColumns()));
        mFavList.addAll(getFav());
        adapter = new BooksListAdapter(getContext(), mFavList);
        recyclerView.addOnLayoutChangeListener(this);
        recyclerView.setAdapter(adapter);
        if (mFavList.size() < 1)
            noFavLayout.setVisibility(View.VISIBLE);

  /*      } else {
            loadSections();
            loadLists();
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(new FavBooksAdapter(getContext(), mSectionsDataList, mFavList));
        }*/
    }

    public ArrayList<Item> getFav() {
        if (getActivity() != null) {
            Cursor ingredientCursor = getActivity().getApplicationContext().getContentResolver()
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
        return null;
    }

    /*
      private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks =
              new LoaderManager.LoaderCallbacks<Cursor>() {

                  @Override
                  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                      switch (id) {
                          case LOADER_BOOKS:
                              return new CursorLoader(getContext().getApplicationContext(),
                                      BookContentProvider.URI_Book,
                                      new String[]{Book.COLUMN_TITLE},
                                      null, null, null);
                          default:
                              throw new IllegalArgumentException();
                      }
                  }

                  @Override
                  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                      switch (loader.getId()) {
                          case LOADER_BOOKS:
                              ArrayList<Item> books = new ArrayList<>();
                              if (data != null) {
                                  while (data.moveToNext()) {
                                      Item ingredient = getDataFromCursor(data);
                                      books.add(ingredient);
                                  }
                                  data.close();
                              }
                              adapter.setmFavList(books);
                              break;
                      }
                  }

                  @Override
                  public void onLoaderReset(Loader<Cursor> loader) {
                      switch (loader.getId()) {
                          case LOADER_BOOKS:
                              adapter.setmFavList(null);
                              break;
                      }
                  }

              };
  */
    private Item getDataFromCursor(Cursor itemCurso) {
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

        return item;
    }

    @Override
    public void onRefresh() {
        if (!refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(true);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (adapter != null) {
                            if (mFavList == null)
                                mFavList = new ArrayList<>();
                            mFavList.clear();
                            mFavList.addAll(getFav());
                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                        }
                        refreshLayout.setRefreshing(false);
                    }
                }, 3000
        );
    }

    public int getNumberOfColumns() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int numberOfCs = (int) (dpWidth / 120);
        return numberOfCs;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (recyclerView.getAdapter().getItemCount() == 0) {
            noFavLayout.setVisibility(View.VISIBLE);

            Animation pulse = AnimationUtils.loadAnimation(getContext(), R.anim.pulse);
            heart.startAnimation(pulse);


        } else noFavLayout.setVisibility(View.GONE);
    }

}