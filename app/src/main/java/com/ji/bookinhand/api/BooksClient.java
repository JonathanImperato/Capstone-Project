package com.ji.bookinhand.api;

import com.ji.bookinhand.models.BooksList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BooksClient {

    @GET("volumes")
    Call<BooksList> getBookWithQuery(@Query("q") String query);

}
