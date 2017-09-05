package com.wahyudieko.bakingapp.connection;

import com.wahyudieko.bakingapp.utils.Constant;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by EKO on 04/09/2017.
 */

public class BakingAppClient {

    private static final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.BASE_RECIPE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
