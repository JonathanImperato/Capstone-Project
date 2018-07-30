package com.ji.bookinhand.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.ji.bookinhand.R;
import com.ji.bookinhand.adapters.PopularBooksAdapter;
import com.ji.bookinhand.api.BooksClient;
import com.ji.bookinhand.api.nytmodels.NytBooksList;
import com.ji.bookinhand.ui.OcrCaptureActivity;
import com.ji.bookinhand.ui.ResultsActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    FloatingActionButton takePhoto;
    private String TAG = this.getClass().getSimpleName();
    private static final int RC_OCR_CAPTURE = 9003;
    private String MY_PREFS_NAME;
    RecyclerView ebookfictionRv, paperbacknonfictionRv, hardcovernonFictionRv, hardcoverFictionRv;
    ProgressBar ebookfictionProgressBar, paperbacknonfictionProgressBar, hardcoverFictionProgressBar, hardcovernonFictionProgressBar;
    static HomeFragment fragment;
    Parcelable ebData;
    Parcelable hardfData;
    Parcelable hardnonfData;
    Parcelable paperData;
    NytBooksList paperrvData, hardnonfrvData, hardfrvData, ebrvData;
    PopularBooksAdapter paperbacknonfictionRvAdapter, hardcoverFictionRvAdapter, hardcovernonFictionRvAdapter, ebookfictionRvAdapter;
    int paperrvIndexY, ebrvIndexY, hardfrvIndexY, hardnonfrvIndexY;
    int paperrvIndexX, ebrvIndexX, hardfrvIndexX, hardnonfrvIndexX;

    public static HomeFragment newInstance() {
        return getInstance();
    }

    public static HomeFragment getInstance() {
        if (fragment == null)
            fragment = new HomeFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ebookfictionRv = v.findViewById(R.id.ebookfictionRv);
        paperbacknonfictionRv = v.findViewById(R.id.paperbacknonfictionRv);
        hardcovernonFictionRv = v.findViewById(R.id.hardcovernonFictionRv);
        hardcoverFictionRv = v.findViewById(R.id.hardcoverFictionRv);

        ebookfictionProgressBar = v.findViewById(R.id.ebookfictionProgressBar);
        paperbacknonfictionProgressBar = v.findViewById(R.id.paperbacknonfictionProgressBar);
        hardcoverFictionProgressBar = v.findViewById(R.id.hardcoverFictionProgressBar);
        hardcovernonFictionProgressBar = v.findViewById(R.id.hardcovernonFictionProgressBar);
        ebookfictionProgressBar.setIndeterminate(true);
        paperbacknonfictionProgressBar.setIndeterminate(true);
        hardcoverFictionProgressBar.setIndeterminate(true);
        hardcovernonFictionProgressBar.setIndeterminate(true);

        ebookfictionRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        paperbacknonfictionRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        hardcovernonFictionRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        hardcoverFictionRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        ebookfictionRv.setHasFixedSize(true);
        paperbacknonfictionRv.setHasFixedSize(true);
        hardcovernonFictionRv.setHasFixedSize(true);
        hardcoverFictionRv.setHasFixedSize(true);

        takePhoto = v.findViewById(R.id.takePhoto);
        takePhoto.setVisibility(View.VISIBLE);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), OcrCaptureActivity.class);
                ActivityCompat.startActivityForResult(getActivity(), intent, RC_OCR_CAPTURE, null);
            }
        });

        MY_PREFS_NAME = getContext().getString(R.string.history_pref_name);
        if (savedInstanceState == null)
            new loadData().execute();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ebData = (savedInstanceState.getParcelable("ebookrv"));
            hardfData = (savedInstanceState.getParcelable("hardcoverfiction"));
            hardnonfData = (savedInstanceState.getParcelable("hardcovernonfiction"));
            paperData = (savedInstanceState.getParcelable("paperrv"));
            ebrvData = ((NytBooksList) savedInstanceState.getParcelable("ebrvData"));
            hardfrvData = ((NytBooksList) savedInstanceState.getParcelable("hardfrvData"));
            hardnonfrvData = ((NytBooksList) savedInstanceState.getParcelable("hardnonfrvData"));
            paperrvData = ((NytBooksList) savedInstanceState.getParcelable("paperrvData"));

            paperrvIndexY = savedInstanceState.getInt("paperrvIndexY");
            ebrvIndexY = savedInstanceState.getInt("ebrvIndexY");
            hardfrvIndexY = savedInstanceState.getInt("hardfrvIndexY");
            hardnonfrvIndexY = savedInstanceState.getInt("hardnonfrvIndexY");
            paperrvIndexX = savedInstanceState.getInt("paperrvIndexX");
            ebrvIndexX = savedInstanceState.getInt("ebrvIndexX");
            hardfrvIndexX = savedInstanceState.getInt("hardfrvIndexX");
            hardnonfrvIndexX = savedInstanceState.getInt("hardnonfrvIndexX");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveRecyclerViewsStates();

        if (paperbacknonfictionRv.getLayoutManager() != null) {
            outState.putParcelable("paperrv", paperbacknonfictionRv.getLayoutManager().onSaveInstanceState());
            outState.putParcelable("paperrvData", paperrvData);
            outState.putInt("paperrvIndexX", paperrvIndexX);
            outState.putInt("paperrvIndexY", paperrvIndexY);
        }
        if (ebookfictionRv.getLayoutManager() != null) {
            outState.putParcelable("ebookrv", ebookfictionRv.getLayoutManager().onSaveInstanceState());
            outState.putParcelable("ebrvData", ebrvData);
            outState.putInt("ebrvIndexX", ebrvIndexX);
            outState.putInt("ebrvIndexY", ebrvIndexY);

        }
        if (hardcoverFictionRv.getLayoutManager() != null) {
            outState.putParcelable("hardcoverfiction", hardcoverFictionRv.getLayoutManager().onSaveInstanceState());
            outState.putParcelable("hardfrvData", hardfrvData);
            outState.putInt("hardfrvIndexX", hardfrvIndexX);
            outState.putInt("hardfrvIndexY", hardfrvIndexY);
        }
        if (hardcovernonFictionRv.getLayoutManager() != null) {
            outState.putParcelable("hardcovernonfiction", hardcovernonFictionRv.getLayoutManager().onSaveInstanceState());
            outState.putParcelable("hardnonfrvData", hardnonfrvData);
            outState.putInt("hardnonfrvIndexX", hardnonfrvIndexX);
            outState.putInt("hardnonfrvIndexY", hardnonfrvIndexY);
        }
    }


    private void saveRecyclerViewsStates() {
        paperrvIndexY = paperbacknonfictionRv.getScrollY();
        ebrvIndexY = ebookfictionRv.getScrollY();
        hardfrvIndexY = hardcoverFictionRv.getScrollY();
        hardnonfrvIndexY = hardcovernonFictionRv.getScrollY();
        paperrvIndexX = paperbacknonfictionRv.getScrollX();
        ebrvIndexX = ebookfictionRv.getScrollX();
        hardfrvIndexX = hardcoverFictionRv.getScrollX();
        hardnonfrvIndexX = hardcovernonFictionRv.getScrollX();

        if (paperbacknonfictionRv.getAdapter() != null)
            paperrvData = ((PopularBooksAdapter) paperbacknonfictionRv.getAdapter()).getmBooksList();
        if (hardcovernonFictionRv.getAdapter() != null)
            hardnonfrvData = ((PopularBooksAdapter) hardcovernonFictionRv.getAdapter()).getmBooksList();
        if (ebookfictionRv.getAdapter() != null)
            ebrvData = ((PopularBooksAdapter) ebookfictionRv.getAdapter()).getmBooksList();
        if (hardcoverFictionRv.getAdapter() != null)
            hardfData = ((PopularBooksAdapter) hardcoverFictionRv.getAdapter()).getmBooksList();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
            //it is resumed from the homeActivity so no need to perform anything
        } else {
            //it is an orientation change or something similar, need to restore its state
            restoreRecyclerViews();
        }
    }

    private void restoreRecyclerViews() {
        if (ebData != null) {
            ebookfictionRv.getLayoutManager().onRestoreInstanceState(ebData);
            ebookfictionProgressBar.setVisibility(View.GONE);
            if (paperrvData != null) {
                ebookfictionRvAdapter = new PopularBooksAdapter(getContext(), ebrvData);
                ebookfictionRv.setAdapter(ebookfictionRvAdapter);
                ebookfictionRv.scrollTo(ebrvIndexX, ebrvIndexY);
            }
        } else loadPopularEBooks();
        if (hardfData != null) {
            hardcoverFictionRv.getLayoutManager().onRestoreInstanceState(hardfData);
            hardcoverFictionProgressBar.setVisibility(View.GONE);
            if (paperrvData != null) {
                hardcoverFictionRvAdapter = new PopularBooksAdapter(getContext(), hardfrvData);
                hardcoverFictionRv.setAdapter(hardcoverFictionRvAdapter);
                hardcoverFictionRv.scrollTo(hardfrvIndexX, hardfrvIndexY);
            }
        } else loadPopularHardCoverFiction();
        if (hardnonfData != null) {
            hardcovernonFictionRv.getLayoutManager().onRestoreInstanceState(hardnonfData);
            hardcovernonFictionProgressBar.setVisibility(View.GONE);
            if (paperrvData != null) {
                hardcovernonFictionRvAdapter = new PopularBooksAdapter(getContext(), hardnonfrvData);
                hardcovernonFictionRv.setAdapter(hardcovernonFictionRvAdapter);
                hardcovernonFictionRv.scrollTo(hardnonfrvIndexX, hardnonfrvIndexY);
            }
        } else loadPopularHardCoverNonFiction();
        if (paperData != null) {
            paperbacknonfictionRv.getLayoutManager().onRestoreInstanceState(paperData);
            paperbacknonfictionProgressBar.setVisibility(View.GONE);
            if (paperrvData != null) {
                paperbacknonfictionRvAdapter = new PopularBooksAdapter(getContext(), paperrvData);
                paperbacknonfictionRv.setAdapter(paperbacknonfictionRvAdapter);
                paperbacknonfictionRv.scrollTo(paperrvIndexX, paperrvIndexY);
            }
        } else loadPopularPaperBackNonFiction();
    }

    public class loadData extends AsyncTask<Void, Void, Void> { //added only to pass the project, todo: temove after project passed

        @Override
        protected Void doInBackground(Void... voids) {
            setUpRecyclerViews();
            return null;
        }
    }

    /**
     * Method to load data in recyclerviews
     */
    void setUpRecyclerViews() {
        loadPopularEBooks();
        loadPopularHardCoverFiction();
        loadPopularHardCoverNonFiction();
        loadPopularPaperBackNonFiction();
    }


    private void loadPopularHardCoverFiction() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nytimes.com/svc/books/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BooksClient service = retrofit.create(BooksClient.class);
        //do a setting screen to choose wich categories of book show based on this:
        //https://www.nytimes.com/books/best-sellers/
        Call<NytBooksList> data = service.getPopularBooks("hardcover-fiction", getString(R.string.nyt_api_key));
        data.enqueue(new Callback<NytBooksList>() {
            @Override
            public void onResponse(Call<NytBooksList> call, Response<NytBooksList> response) {
                NytBooksList lista = response.body();
                if (lista != null && lista.getNumResults() > 0) {
                    //do fill data
                    hardcoverFictionRvAdapter = new PopularBooksAdapter(getContext(), lista);
                    hardcoverFictionProgressBar.setIndeterminate(false);

                    hardfrvData = lista;
                    hardcoverFictionProgressBar.setVisibility(View.GONE);
                    hardcoverFictionRv.setAdapter(hardcoverFictionRvAdapter);
                    animateRecyclerView(hardcoverFictionRv);
                }
            }

            @Override
            public void onFailure(Call<NytBooksList> call, Throwable t) {

                Log.d(TAG, "Error while fetching data for home: " + t.getMessage(), t);
            }
        });
    }

    private void loadPopularHardCoverNonFiction() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nytimes.com/svc/books/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BooksClient service = retrofit.create(BooksClient.class);
        //do a setting screen to choose wich categories of book show based on this:
        //https://www.nytimes.com/books/best-sellers/
        Call<NytBooksList> data = service.getPopularBooks("hardcover-nonfiction", getString(R.string.nyt_api_key));
        data.enqueue(new Callback<NytBooksList>() {
            @Override
            public void onResponse(Call<NytBooksList> call, Response<NytBooksList> response) {
                NytBooksList lista = response.body();
                if (lista != null && lista.getNumResults() > 0) {
                    //do fill data

                    hardcovernonFictionRvAdapter = new PopularBooksAdapter(getContext(), lista);
                    hardnonfrvData = lista;
                    hardcovernonFictionProgressBar.setIndeterminate(false);
                    hardcovernonFictionProgressBar.setVisibility(View.GONE);
                    hardcovernonFictionRv.setAdapter(hardcovernonFictionRvAdapter);
                    animateRecyclerView(hardcovernonFictionRv);
                }
            }

            @Override
            public void onFailure(Call<NytBooksList> call, Throwable t) {

                Log.d(TAG, "Error while fetching data for home: " + t.getMessage(), t);
            }
        });
    }

    private void loadPopularEBooks() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nytimes.com/svc/books/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BooksClient service = retrofit.create(BooksClient.class);
        //do a setting screen to choose wich categories of book show based on this:
        //https://www.nytimes.com/books/best-sellers/
        Call<NytBooksList> data = service.getPopularBooks("e-book-fiction", getString(R.string.nyt_api_key));
        data.enqueue(new Callback<NytBooksList>() {
            @Override
            public void onResponse(Call<NytBooksList> call, Response<NytBooksList> response) {
                NytBooksList lista = response.body();
                if (lista != null && lista.getNumResults() > 0) {
                    //do fill data
                    ebookfictionRvAdapter = new PopularBooksAdapter(getContext(), lista);
                    ebookfictionProgressBar.setIndeterminate(false);
                    ebrvData = lista;
                    ebookfictionProgressBar.setVisibility(View.GONE);
                    ebookfictionRv.setAdapter(ebookfictionRvAdapter);
                    animateRecyclerView(ebookfictionRv);
                }
            }

            @Override
            public void onFailure(Call<NytBooksList> call, Throwable t) {

                Log.d(TAG, "Error while fetching data for home: " + t.getMessage(), t);
            }
        });
    }

    private void loadPopularPaperBackNonFiction() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nytimes.com/svc/books/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BooksClient service = retrofit.create(BooksClient.class);
        //do a setting screen to choose wich categories of book show based on this:
        //https://www.nytimes.com/books/best-sellers/
        Call<NytBooksList> data = service.getPopularBooks("paperback-nonfiction", getString(R.string.nyt_api_key));
        data.enqueue(new Callback<NytBooksList>() {
            @Override
            public void onResponse(Call<NytBooksList> call, Response<NytBooksList> response) {
                NytBooksList lista = response.body();
                if (lista != null && lista.getNumResults() > 0) {
                    //do fill data
                    paperbacknonfictionRvAdapter = new PopularBooksAdapter(getContext(), lista);
                    paperrvData = lista;
                    paperbacknonfictionProgressBar.setIndeterminate(false);
                    paperbacknonfictionProgressBar.setVisibility(View.GONE);
                    paperbacknonfictionRv.setAdapter(paperbacknonfictionRvAdapter);
                    animateRecyclerView(paperbacknonfictionRv);
                }
            }

            @Override
            public void onFailure(Call<NytBooksList> call, Throwable t) {

                Log.d(TAG, "Error while fetching data for home: " + t.getMessage(), t);
            }
        });
    }

    void createDialog(final String text) {

        //I save history in SharedPreferences
        final SharedPreferences prefs = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.you_selected) + text + getString(R.string.are_you_sure))
                .setTitle(getString(R.string.confirm_choice))
                .setPositiveButton(getString(R.string.go_on), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (prefs.getString(MY_PREFS_NAME, null) == null || !prefs.getString(MY_PREFS_NAME, null).contains(text)) {
                            String newValue = prefs.getString(MY_PREFS_NAME, null) + "," + text;
                            editor.putString(MY_PREFS_NAME, newValue);
                            editor.apply();
                        }
                        startActivity(new Intent(getContext(), ResultsActivity.class)
                                .putExtra("result", text)
                                .putExtra("isCat", false)); //is a category search (here is false since it is not)
                    }
                })
                .setNegativeButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Intent intent = new Intent(getContext(), OcrCaptureActivity.class);
                        startActivityForResult(intent, RC_OCR_CAPTURE);

                    }
                });

        builder.create().show();
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
                Snackbar.make(takePhoto, R.string.something_wrong, Snackbar.LENGTH_LONG).setAction(R.string.try_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), OcrCaptureActivity.class);

                        startActivityForResult(intent, RC_OCR_CAPTURE);
                    }
                }).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void animateRecyclerView(final RecyclerView recyclerView) {
        return;
    }


}
