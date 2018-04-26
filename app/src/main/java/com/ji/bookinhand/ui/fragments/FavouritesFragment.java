package com.ji.bookinhand.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrusoft.sectionedrecyclerview.Section;
import com.ji.bookinhand.R;
import com.ji.bookinhand.adapters.BooksListAdapter;
import com.ji.bookinhand.api.models.ImageLinks;
import com.ji.bookinhand.api.models.Item;
import com.ji.bookinhand.api.models.VolumeInfo;

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

public class FavouritesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public FavouritesFragment() {
        // Required empty public constructor

        setHasOptionsMenu(true);
    }

    SwipeRefreshLayout refreshLayout;
    ArrayList<Item> mFavList = new ArrayList<>();
    List<SectionHeader> mSectionsDataList = new ArrayList<>();
    List<String> mSectionsList = new ArrayList<>();
    RecyclerView recyclerView;
    BooksListAdapter adapter;
   /* boolean isDashboard = true;
    SharedPreferences prefs;*/

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
        recyclerView = v.findViewById(R.id.ebookfictionRv);
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
        loadData();
        return v;
    }


    private void loadData() {
        mFavList.clear();
        mFavList = getFav();
        //    if (isDashboard) {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getNumberOfColumns()));
        if (adapter == null)
            adapter = new BooksListAdapter(getContext(), mFavList);
        recyclerView.setAdapter(adapter);
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

    void loadSections() {
        mSectionsList.clear();
        for (Item i : mFavList) {
            List<String> sections = Arrays.asList(i.getVolumeInfo().getCategories().get(0).split(","));
            for (String section : sections) {
                String sezione = section.replace(",", "");
                if (mSectionsList == null || !mSectionsList.contains(sezione)) {
                    if (sezione != null)
                        mSectionsList.add(sezione);
                }
            }
        }
    }

    void loadLists() {
        mSectionsDataList.clear();
        for (String section : mSectionsList) {
            List<Item> childList = new ArrayList<>();
            for (int i = 0; i < mFavList.size(); i++) {
                if (isItemInSectionAt(i, section)) {
                    childList.add(mFavList.get(i));
                }
            }
            SectionHeader sectionHeader = new SectionHeader(childList, section);
            mSectionsDataList.add(sectionHeader);
        }

    }

    boolean isItemInSectionAt(int itemPosition, String sectionName) {
        if (mFavList.get(itemPosition).getVolumeInfo().getCategories().get(0).contains(sectionName))
            return true;
        return false;
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
                            ArrayList<Item> data = getFav();
                            if (mFavList != null && data != null)
                                mFavList.addAll(data);
                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                        }
                        refreshLayout.setRefreshing(false);
                    }
                }, 3000
        );
    }

    public class SectionHeader implements Section<Item> {

        List<Item> childList;
        String sectionText;

        public SectionHeader(List<Item> childList, String sectionText) {
            this.childList = childList;
            this.sectionText = sectionText;
        }

        @Override
        public List<Item> getChildItems() {
            return childList;
        }

        public String getSectionText() {
            return sectionText;
        }
    }

    public int getNumberOfColumns() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int numberOfCs = (int) (dpWidth / 180);
        return numberOfCs;
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    /*  @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fav_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.change_sorting) {
            if (isDashboard) {
                isDashboard = !isDashboard;
                item.setIcon(getContext().getResources().getDrawable(R.drawable.ic_sort_black_24dp));
                loadData();
                prefs.edit().putString("layoutSetting", "list").commit();
            } else {
                isDashboard = !isDashboard;
                item.setIcon(getContext().getResources().getDrawable(R.drawable.ic_dashboard_black_24dp));
                loadData();
                prefs.edit().putString("layoutSetting", "dashboard").commit();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem cl = menu.getItem(2);
        if (isDashboard) {
            cl.setIcon(getContext().getResources().getDrawable(R.drawable.ic_dashboard_black_24dp));
        } else {
            cl.setIcon(getContext().getResources().getDrawable(R.drawable.ic_sort_black_24dp));
        }
    }*/
}
