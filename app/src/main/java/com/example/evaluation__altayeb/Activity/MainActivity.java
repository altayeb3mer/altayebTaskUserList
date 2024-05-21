package com.example.evaluation__altayeb.Activity;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.evaluation__altayeb.Activity.AddUser;
import com.example.evaluation__altayeb.Adapter.UserAdapter;
import com.example.evaluation__altayeb.Model.UserModel;
import com.example.evaluation__altayeb.Model.UsersResponse;
import com.example.evaluation__altayeb.R;
import com.example.evaluation__altayeb.Service.ApiClient;
import com.example.evaluation__altayeb.Service.ApiServices;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    FloatingActionButton btn_add;
    private List<UserModel> users = new ArrayList<>();
    private UserAdapter userAdapter;
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setUpPagination();
        fetchUsers(1);
    }


    private void setUpPagination() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        // Load more data
                        //
                        userAdapter.nextPage();
                        fetchUsers(userAdapter.page);
                    }
                }
            }
        });
    }

    private void initView() {
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(this);
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        //adapter
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(peekAvailableContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        userAdapter = new UserAdapter(users, this);
        recyclerView.setAdapter(userAdapter);
        //shimmer
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        startShimmer();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add: {
//                startActivity(new Intent(view.getContext(), AddUser.class));
                activityResultLauncher.launch(new Intent(view.getContext(), AddUser.class));
//                finish();
                break;
            }
        }
    }

    private void fetchUsers(int page) {

        Retrofit retrofit = ApiClient.getRetrofitClient(ApiServices.API_TOKEN);
        swipeRefreshLayout.setRefreshing(true);
        isLoading = true;
        ApiServices.UsersService usersService = retrofit.create(ApiServices.UsersService.class);
        usersService.getUsers(page, 50).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    stopShimmer();
                    Gson gson = new Gson();
                    String res = response.body().string();
                    Type listType = new TypeToken<List<UserModel>>() {
                    }.getType();
                    List<UserModel> usersResponse = gson.fromJson(res, listType);
                    users.addAll(usersResponse);
                    userAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    isLoading = false;

                } catch (Exception e) {
                    stopShimmer();
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                    Toast.makeText(peekAvailableContext().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                stopShimmer();
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(peekAvailableContext().getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show();

            }
        });
    }

    void startShimmer() {
        swipeRefreshLayout.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
    }

    void stopShimmer() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    private void refreshList() {
        swipeRefreshLayout.setRefreshing(false);
        userAdapter.pageClear();
        userAdapter.clearData();
        if (!isLoading) {
            fetchUsers(1);
        }
    }

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            boolean resultValue = data.getBooleanExtra("reload", false);
                            // Handle the result here
                            if (resultValue == true) {
                                refreshList();
                            }
                        }
                    }
                }
            });
}