package com.ji.bookinhand.api;

import com.ji.bookinhand.api.models.BooksList;
import com.ji.bookinhand.api.models.Review;
import com.ji.bookinhand.api.nytmodels.NytBooksList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BooksClient {

    @GET("volumes?maxResults=20")
    Call<BooksList> getBookWithQuery(@Query("q") String query);
    //for categories should be: e.g. q=subject:Psychology

    @GET("volumes?maxResults=10")
    Call<BooksList> getBookFromAuthor(@Query("q") String query);
    //for authors should be: e.g. q=inauthor:Calvino


    @GET("lists.json?")
    Call<NytBooksList> getPopularBooks(@Query("list") String category, @Query("api-key") String api_key); //e.g. list=e-book-fiction


    @GET("reviews.json?")
    Call<Review> getBookReviewsByTitle(@Query("title") String title, @Query("api-key") String api_key);//, @Query("author") String author);
}
