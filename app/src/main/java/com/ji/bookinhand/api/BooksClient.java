package com.ji.bookinhand.api;

import com.ji.bookinhand.api.models.BooksList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BooksClient {

    @GET("volumes?maxResults=20")
    Call<BooksList> getBookWithQuery(@Query("q") String query);
    //for categories should be: e.g. q=subject:Psychology
    //for authors should be: e.g. q=inauthor:Calvino


    @GET("volumes?maxResults=10")
    Call<BooksList> getBookFromAuthor(@Query("q") String query);

    @GET("volumes/{id}")
    Call<BooksList> getBookReviewsById(@Path("id") String id);
}
