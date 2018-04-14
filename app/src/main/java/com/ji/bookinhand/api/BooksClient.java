package com.ji.bookinhand.api;

import com.ji.bookinhand.api.models.BooksList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BooksClient {

    @GET("volumes?maxResults=10")
    Call<BooksList> getBookWithQuery(@Query("q") String query);

}
