package com.example.numberbook.network;

import com.example.numberbook.model.ApiResponse;
import com.example.numberbook.model.Contact;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ContactApi {

    @POST("insertContact.php")
    Call<ApiResponse> insertContacts(@Body Map<String, List<Contact>> body);

    @GET("getAllContacts.php")
    Call<ApiResponse> getAllContacts();

    @GET("searchContact.php")
    Call<ApiResponse> searchContacts(@Query("keyword") String keyword);
}