package com.ji.bookinhand.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
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

public class HomeFragment extends Fragment {

    FloatingActionButton takePhoto;
    private String TAG = this.getClass().getSimpleName();
    private static final int RC_OCR_CAPTURE = 9003;
    RecyclerView ebookfictionRv, paperbacknonfictionRv, hardcovernonFictionRv, hardcoverFictionRv;


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

                        startActivityForResult(intent, RC_OCR_CAPTURE);
                    }
                }).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void createDialog(final String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        startActivityForResult(intent, RC_OCR_CAPTURE);

                    }
                });

        builder.create().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ebookfictionRv = v.findViewById(R.id.ebookfictionRv);
        paperbacknonfictionRv = v.findViewById(R.id.paperbacknonfictionRv);
        hardcovernonFictionRv = v.findViewById(R.id.hardcovernonFictionRv);
        hardcoverFictionRv = v.findViewById(R.id.hardcoverFictionRv);

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
        setUpRecyclerViews();
        return v;
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
                    PopularBooksAdapter adapter = new PopularBooksAdapter(getContext(), lista);
                    hardcoverFictionRv.setAdapter(adapter);
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

                    PopularBooksAdapter adapter = new PopularBooksAdapter(getContext(), lista);
                    hardcovernonFictionRv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<NytBooksList> call, Throwable t) {
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
                    PopularBooksAdapter adapter = new PopularBooksAdapter(getContext(), lista);
                    ebookfictionRv.setAdapter(adapter);
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
                    PopularBooksAdapter adapter = new PopularBooksAdapter(getContext(), lista);
                    paperbacknonfictionRv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<NytBooksList> call, Throwable t) {
                Toast.makeText(getActivity(), "Error while fetching data for home", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error while fetching data for home: " + t.getMessage(), t);
            }
        });
    }

}
