package com.ji.bookinhand.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.ji.bookinhand.R;
import com.ji.bookinhand.adapters.BooksListAdapter;
import com.ji.bookinhand.api.models.ImageLinks;
import com.ji.bookinhand.api.models.Item;
import com.ji.bookinhand.api.models.VolumeInfo;
import com.ji.bookinhand.ui.OcrCaptureActivity;
import com.ji.bookinhand.ui.ResultsActivity;

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

public class HomeFragment extends Fragment {

    FloatingActionButton takePhoto;
    private String TAG = this.getClass().getSimpleName();
    private static final int RC_OCR_CAPTURE = 9003;
    RecyclerView mRecyclerView;
    BooksListAdapter adapter;
    ArrayList<Item> mFavList;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                        Intent intent = new Intent(getContext(), OcrCaptureActivity.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("You selected \"" + text + "\". Are you sure?")
                .setTitle("Confirm your choice")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        startActivity(new Intent(getContext(), ResultsActivity.class)
                                .putExtra("result", text)
                                .putExtra("isCat", false)); //is a category search (here is false since it is not)
                    }
                })
                .setNegativeButton("Try again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Intent intent = new Intent(getContext(), OcrCaptureActivity.class);
                        intent.putExtra(OcrCaptureActivity.AutoFocus, true); //focus
                        intent.putExtra(OcrCaptureActivity.UseFlash, true); //flash

                        startActivityForResult(intent, RC_OCR_CAPTURE);

                    }
                });
        builder.create();
        builder.show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = v.findViewById(R.id.latestBook_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        loadFav();
        takePhoto = v.findViewById(R.id.takePhoto);
        takePhoto.setVisibility(View.VISIBLE);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, true); //focus
                intent.putExtra(OcrCaptureActivity.UseFlash, true); //flash

                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });
        return v;
    }

    private void loadFav() {
        mFavList = getFav();
        adapter = new BooksListAdapter(getContext(), mFavList);
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    public ArrayList<Item> getFav() {
        Cursor ingredientCursor = getActivity().getContentResolver()
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

}
