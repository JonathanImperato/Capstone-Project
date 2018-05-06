package com.ji.bookinhand.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

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

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });

        MY_PREFS_NAME = getContext().getString(R.string.history_pref_name);
        if (savedInstanceState == null)
            setUpRecyclerViews();
        return v;
    }

    Parcelable ebData;
    Parcelable hardfData;
    Parcelable hardnonfData;
    Parcelable paperData;

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

            restoreRecyclerViews(); //todo: check it works
        }
    }

    NytBooksList paperrvData, hardnonfrvData, hardfrvData, ebrvData;
    PopularBooksAdapter paperbacknonfictionRvAdapter, hardcoverFictionRvAdapter, hardcovernonFictionRvAdapter, ebookfictionRvAdapter;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (paperbacknonfictionRv.getLayoutManager() != null) {
            outState.putParcelable("paperrv", paperbacknonfictionRv.getLayoutManager().onSaveInstanceState());
            outState.putParcelable("paperrvData", paperrvData);
        }
        if (ebookfictionRv.getLayoutManager() != null) {
            outState.putParcelable("ebookrv", ebookfictionRv.getLayoutManager().onSaveInstanceState());
            outState.putParcelable("ebrvData", ebrvData);
        }
        if (hardcoverFictionRv.getLayoutManager() != null) {
            outState.putParcelable("hardcoverfiction", hardcoverFictionRv.getLayoutManager().onSaveInstanceState());
            outState.putParcelable("hardfrvData", hardfrvData);
        }
        if (hardcovernonFictionRv.getLayoutManager() != null) {
            outState.putParcelable("hardcovernonfiction", hardcovernonFictionRv.getLayoutManager().onSaveInstanceState());
            outState.putParcelable("hardnonfrvData", hardnonfrvData);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private void restoreRecyclerViews() {
        if (ebData != null) {
            ebookfictionRv.getLayoutManager().onRestoreInstanceState(ebData);
            ebookfictionProgressBar.setVisibility(View.GONE);
            if (paperrvData != null) {
                ebookfictionRvAdapter = new PopularBooksAdapter(getContext(), ebrvData);
                ebookfictionRv.setAdapter(ebookfictionRvAdapter);
            }
        } else loadPopularEBooks();
        if (hardfData != null) {
            hardcoverFictionRv.getLayoutManager().onRestoreInstanceState(hardfData);
            hardcoverFictionProgressBar.setVisibility(View.GONE);
            if (paperrvData != null) {
                hardcoverFictionRvAdapter = new PopularBooksAdapter(getContext(), hardfrvData);
                hardcoverFictionRv.setAdapter(hardcoverFictionRvAdapter);
            }
        } else loadPopularHardCoverFiction();
        if (hardnonfData != null) {
            hardcovernonFictionRv.getLayoutManager().onRestoreInstanceState(hardnonfData);
            hardcovernonFictionProgressBar.setVisibility(View.GONE);
            if (paperrvData != null) {
                hardcovernonFictionRvAdapter = new PopularBooksAdapter(getContext(), hardnonfrvData);
                hardcovernonFictionRv.setAdapter(hardcovernonFictionRvAdapter);
            }
        } else loadPopularHardCoverNonFiction();
        if (paperData != null) {
            paperbacknonfictionRv.getLayoutManager().onRestoreInstanceState(paperData);
            paperbacknonfictionProgressBar.setVisibility(View.GONE);
            if (paperrvData != null) {
                paperbacknonfictionRvAdapter = new PopularBooksAdapter(getContext(), paperrvData);
                paperbacknonfictionRv.setAdapter(paperbacknonfictionRvAdapter);
            }
        } else loadPopularPaperBackNonFiction();
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
                Toast.makeText(getActivity(), "Error while fetching data for home", Toast.LENGTH_SHORT).show();
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
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "Error while fetching data for home", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Error while fetching data for home", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Error while fetching data for home", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error while fetching data for home: " + t.getMessage(), t);
            }
        });
    }

    void createDialog(final String text) {

        //I save history in SharedPreferences
        final SharedPreferences prefs = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You selected \"" + text + "\". Are you sure?")
                .setTitle("Confirm your choice")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
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
                .setNegativeButton("Try again", new DialogInterface.OnClickListener() {
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
                Snackbar.make(takePhoto, "Something went wrong.", Snackbar.LENGTH_LONG).setAction("Try again", new View.OnClickListener() {
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
        recyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                            View v = recyclerView.getChildAt(i);
                            v.setAlpha(0.0f);
                            v.animate().alpha(1.0f)
                                    .setDuration(1300)
                                    .setStartDelay(i * 50)
                                    .start();
                        }

                        return true;
                    }
                });
    }

}
