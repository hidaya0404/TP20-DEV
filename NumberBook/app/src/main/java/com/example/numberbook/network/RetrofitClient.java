package com.example.numberbook.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    /*
       Pour l'émulateur Android :
       10.0.2.2 représente localhost de ton PC.

       Donc si ton API est ici sur PC :
       http://localhost/numberbook-api/api/

       Dans Android, on écrit :
       http://10.0.2.2/numberbook-api/api/
    */
    private static final String BASE_URL = "http://10.0.2.2/numberbook-api/api/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}