package com.ji.bookinhand.ui;

import android.content.Context;
import android.content.SharedPreferences;

import com.ji.bookinhand.R;

import org.cryse.widget.persistentsearch.SearchItem;
import org.cryse.widget.persistentsearch.SearchSuggestionsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SuggestionBuilder implements SearchSuggestionsBuilder {
    private Context mContext;
    private String MY_PREFS_NAME;
    private List<SearchItem> mHistorySuggestions = new ArrayList<SearchItem>();


    public SuggestionBuilder(Context context) {
        this.mContext = context;
        MY_PREFS_NAME = context.getResources().getString(R.string.history_pref_name);
        createHistorys();
    }

    private void createHistorys() {
        //I save history in SharedPreferences
        SharedPreferences prefs = mContext.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString(MY_PREFS_NAME, null);
        if (restoredText != null && restoredText.length() > 0)
            for (String value : restoredText.split(",")) {
                if (value.length() > 1 && !value.equals("null")) {
                    SearchItem item = new SearchItem(
                            value,
                            value,
                            SearchItem.TYPE_SEARCH_ITEM_HISTORY
                    );
                    mHistorySuggestions.add(item);
                }
            }
    }

    @Override
    public Collection<SearchItem> buildEmptySearchSuggestion(int maxCount) {
        List<SearchItem> items = new ArrayList<SearchItem>();
        items.addAll(mHistorySuggestions);
        return items;
    }

    @Override
    public Collection<SearchItem> buildSearchSuggestion(int maxCount, String query) {
        List<SearchItem> items = new ArrayList<SearchItem>();
        for (SearchItem item : mHistorySuggestions) {
            if (item.getValue().startsWith(query) || item.getValue().contains(query) || item.getValue().endsWith(query)) {
                items.add(item);
            }
        }
        return items;
    }
}