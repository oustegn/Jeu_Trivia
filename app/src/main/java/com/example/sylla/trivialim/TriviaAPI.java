package com.example.sylla.trivialim;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TriviaAPI {
    @GET("api.php")
    Call<Enonce> getFeed(@Query("amount") int nbQuestions, @Query("type") String type, @Query("difficulty") String difficulte);

    String URL="https://opentdb.com/";
    Retrofit retrofit=new Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
