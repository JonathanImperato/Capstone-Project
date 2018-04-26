package com.ji.bookinhand.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ji.bookinhand.R;
import com.ji.bookinhand.adapters.BooksListAdapter;
import com.ji.bookinhand.api.BooksClient;
import com.ji.bookinhand.api.models.BooksList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ji.bookinhand.R.id;
import static com.ji.bookinhand.R.layout;

public class ResultsActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();
    RecyclerView recyclerView;
    ProgressBar pbar;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_result);

        Toolbar toolbar = (Toolbar) findViewById(id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        pbar = findViewById(id.loadingBar);
        pbar.setIndeterminate(true);
        pbar.setVisibility(View.VISIBLE);

        //recyclerview stuff
        recyclerView = findViewById(id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        int columns = getNumberOfColumns();
        recyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("result") != null) {
            Boolean isCategSearch = getIntent().getExtras().getBoolean("isCat");
            if (isCategSearch) {
                result = getIntent().getExtras().getString("result");
                getBookList("subject:" + result);
                toolbar.setTitle(result);
            } else {
                result = getIntent().getExtras().getString("result");
                getBookList(result);
                toolbar.setTitle(result);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getBookList(String query) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BooksClient service = retrofit.create(BooksClient.class);
        Call<BooksList> books = service.getBookWithQuery(query);
        books.enqueue(new Callback<BooksList>() {
            @Override
            public void onResponse(Call<BooksList> call, Response<BooksList> response) {
                BooksList result = response.body();
                Log.d(TAG, "Numero di libri: " + result.getTotalItems());
                BooksListAdapter adapter = new BooksListAdapter(ResultsActivity.this, result);
                pbar.setVisibility(View.GONE);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<BooksList> call, Throwable t) {
                pbar.setVisibility(View.GONE);
                Toast.makeText(ResultsActivity.this, "Error while fetching data :(", Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage(), t);
            }
        });

    }

    public int getNumberOfColumns() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int numberOfCs = (int) (dpWidth / 120);
        return numberOfCs;
    }


}
