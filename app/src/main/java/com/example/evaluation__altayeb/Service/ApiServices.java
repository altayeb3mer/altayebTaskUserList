package com.example.evaluation__altayeb.Service;

import com.example.evaluation__altayeb.Model.UserModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ApiServices {
    public static String ROOT_URL = "https://gorest.co.in/";
    public static String API_TOKEN = "eb56dc77ac69e3cf1bbb36a07dc0343f29507175d54d853d12ce011abde2e9ec";
    public  interface UsersService {
        @GET("public/v2/users")
        Call<ResponseBody> getUsers(@Query("page") int page, @Query("per_page") int per_page);
        @GET("public/v2/users/{user_id}")
        Call<ResponseBody> getUserDetails(@Path("user_id") int page);
        @POST("public/v2/users")
        Call<ResponseBody> addUser(@Body UserModel userModel);
    }
}
