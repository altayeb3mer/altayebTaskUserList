package com.example.evaluation__altayeb.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;


import com.example.evaluation__altayeb.Model.ResponseException;
import com.example.evaluation__altayeb.Model.UserModel;
import com.example.evaluation__altayeb.R;
import com.example.evaluation__altayeb.Service.ApiClient;
import com.example.evaluation__altayeb.Service.ApiServices;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UserDetails extends AppCompatActivity implements View.OnClickListener {

    private ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout backBtn;
    LinearLayoutCompat details_view;
    AppCompatTextView toolbar_title;
    AppCompatTextView textViewName, textViewEmail, textViewGender, textViewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        initToolBar();
        initView();
        int userId = getIntent().getIntExtra("id", 0);
//        Toast.makeText(this, String.valueOf(userId), Toast.LENGTH_SHORT).show();
        fetchUserData(userId);
    }

    private void fetchUserData(int userId) {
        startShimmer();
        Retrofit retrofit = ApiClient.getRetrofitClient(ApiServices.API_TOKEN);
        ApiServices.UsersService usersService = retrofit.create(ApiServices.UsersService.class);
        usersService.getUserDetails(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    stopShimmer();
                    Gson gson = new Gson();
                    String res = response.body().string();
                    if (response.code() == 200) {
                        UserModel userModel = gson.fromJson(res, UserModel.class);
                        fillValues(userModel);
                    } else {
                        ResponseException responseException = gson.fromJson(res, ResponseException.class);
                        Toast.makeText(peekAvailableContext().getApplicationContext(), responseException.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e) {
                    stopShimmer();
                    e.printStackTrace();
                    Toast.makeText(peekAvailableContext().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                stopShimmer();
                Toast.makeText(peekAvailableContext().getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initView() {
        details_view = findViewById(R.id.details_view);
        textViewName = findViewById(R.id.name);
        textViewEmail = findViewById(R.id.email);
        textViewGender = findViewById(R.id.gender);
        textViewStatus = findViewById(R.id.status);
        //shimmer
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
    }

    void startShimmer() {
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        details_view.setVisibility(View.GONE);
    }

    void stopShimmer() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        details_view.setVisibility(View.VISIBLE);
    }

    private void initToolBar() {
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("User Details");
        backBtn = findViewById(R.id.layBack);
        backBtn.setOnClickListener(this);
    }

    private void fillValues(UserModel userModel) {
        textViewName.setText(userModel.getName());
        textViewEmail.setText(userModel.getEmail());
        textViewGender.setText(userModel.getGender());
        textViewStatus.setText(userModel.getStatus());
        final String status = userModel.getStatus();
        if (status.equalsIgnoreCase("active")) {
            textViewStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_active, 0, 0, 0);
        } else {
            textViewStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_in_active, 0, 0, 0);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layBack: {
                finish();
                break;
            }
        }
    }
}