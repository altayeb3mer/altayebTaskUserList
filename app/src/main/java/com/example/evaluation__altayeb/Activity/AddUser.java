package com.example.evaluation__altayeb.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.evaluation__altayeb.Model.ResponseException;
import com.example.evaluation__altayeb.Model.UserModel;
import com.example.evaluation__altayeb.R;
import com.example.evaluation__altayeb.Service.ApiClient;
import com.example.evaluation__altayeb.Service.ApiServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class AddUser extends AppCompatActivity implements View.OnClickListener {
    AppCompatButton btn_add;
    LinearLayout backBtn;
    TextInputEditText inputEditTextName, inputEditTextEmail;
    AppCompatTextView toolbar_title;
    RadioGroup radioGroupGender, radioGroupStatus;
    RadioButton radioMale, radioFemale, radioActive, radioInActive;
    String gender = "", status = "active";
    TextInputLayout inputLayoutName, inputLayoutEmail;
    LinearLayoutCompat layoutProgressBar;

    private void addNewUser(UserModel userModel) {
        showProgress();
        Retrofit retrofit = ApiClient.getRetrofitClient(ApiServices.API_TOKEN);
        ApiServices.UsersService usersService = retrofit.create(ApiServices.UsersService.class);
        usersService.addUser(userModel).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    hideProgress();

                    Gson gson = new Gson();
                    if (response.code() == 201) {
                        String res = response.body().string();
                        System.out.println("User created successfully! ------------>step1");
                        UserModel user = gson.fromJson(res, UserModel.class);
                        System.out.println("User created successfully! ------------>step2");
                        showSuccessDialog(user);
                        System.out.println("User created successfully! ------------>step3");
                    } else if (response.code() == 422){
                        String res = response.errorBody().string();
                        Type listType = new TypeToken<List<ResponseException>>() {
                        }.getType();
                        List<ResponseException> errorResponseList = gson.fromJson(res, listType);
                        final  ResponseException responseException=errorResponseList.get(0);
                        showExceptionDialog(responseException);
                    }else{
                        Toast.makeText(peekAvailableContext().getApplicationContext(), "Error" + response.code(), Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e) {
                    hideProgress();
                    e.printStackTrace();
                    Toast.makeText(peekAvailableContext().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideProgress();
                Toast.makeText(peekAvailableContext().getApplicationContext(), "Error adding new user", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initView() {
        initToolBar();
        layoutProgressBar = findViewById(R.id.layoutProgressBar);
        btn_add = findViewById(R.id.add_btn);
        btn_add.setOnClickListener(this);
        //inputLayout
        inputLayoutName = findViewById(R.id.layName);
        inputLayoutEmail = findViewById(R.id.layEmail);
        //input
        inputEditTextName = findViewById(R.id.name);
        inputEditTextEmail = findViewById(R.id.email);
        inputEditTextName.setFilters(new InputFilter[]{new CharAndSpaceInputFilter(inputLayoutName)});
        inputEditTextEmail.setFilters(new InputFilter[]{new EmailInputFilter(inputLayoutEmail)});
        //radioGroupGender
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == radioMale.getId()) {
                    gender = "male";
                } else  if(checkedId == radioFemale.getId()){
                    gender = "female";
                }else{
                    gender = "";
                }
            }
        });
        //radioGroupStatus
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        radioActive = findViewById(R.id.radioActive);
        radioInActive = findViewById(R.id.radioInactive);
        radioGroupStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == radioActive.getId()) {
                    status = "active";
                } else if(checkedId == radioInActive.getId()) {
                    status = "inactive";
                }else{
                    status = "";
                }
            }
        });

    }

    private void initToolBar() {
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("New User");
        backBtn = findViewById(R.id.layBack);
        backBtn.setOnClickListener(this);
    }

    //validation
    private boolean isValidEmail(CharSequence email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidInput(String input) {
        return input.matches("[a-zA-Z ]+");
    }

    //progress view show
    private void showProgress() {
        layoutProgressBar.setVisibility(View.VISIBLE);
    }

    //progress view hide
    private void hideProgress() {
        layoutProgressBar.setVisibility(View.GONE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        initView();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_btn: {
                //validate before send
                validateBeforeSend();
                break;
            }
            case R.id.layBack: {
                finish();
                break;
            }
        }
    }

    private void validateBeforeSend() {
        String userName = inputEditTextName.getText().toString().trim();
        String userEmail = inputEditTextEmail.getText().toString().trim();
        if (userName.isEmpty() || !isValidInput(userName)) {
            Toast.makeText(peekAvailableContext().getApplicationContext(), "Error name,insure to type valid name", Toast.LENGTH_LONG).show();
            return;

        }
        if (userEmail.isEmpty() || !isValidEmail(userEmail)) {
            Toast.makeText(peekAvailableContext().getApplicationContext(), "Error email,insure to type valid email", Toast.LENGTH_LONG).show();
            return;
        }

        if (gender.isEmpty()) {
            Toast.makeText(peekAvailableContext().getApplicationContext(), "Error gender,select the gender", Toast.LENGTH_LONG).show();
            return;
        }
        if (status.isEmpty()) {
            Toast.makeText(peekAvailableContext().getApplicationContext(), "Error status,select the status", Toast.LENGTH_LONG).show();
            return;
        }

        UserModel userModel = new UserModel(0, userName, userEmail, gender, status);
        addNewUser(userModel);
//        showSuccessDialog(userModel);

    }

    //input validation while typing ->email
    private static class EmailInputFilter implements InputFilter {
        TextInputLayout inputLayoutEmail;

        public EmailInputFilter(TextInputLayout inputLayoutEmail) {
            this.inputLayoutEmail = inputLayoutEmail;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // Build the new string by replacing the filtered characters
            StringBuilder filteredStringBuilder = new StringBuilder();
            for (int i = start; i < end; i++) {
                char currentChar = source.charAt(i);
                // Check if the character is valid for email (alphanumeric, dot, underscore, hyphen)
                if (Character.isLetterOrDigit(currentChar) ||
                        currentChar == '.' || currentChar == '_' || currentChar == '-' || currentChar == '@') {
                    filteredStringBuilder.append(currentChar);
                    inputLayoutEmail.setError(null);
                } else {
                    inputLayoutEmail.setError("Invalid email!");
                }
            }
            return filteredStringBuilder.toString();
        }
    }

    //input validation while typing ->name
    private static class CharAndSpaceInputFilter implements InputFilter {
        TextInputLayout inputLayoutName;

        public CharAndSpaceInputFilter(TextInputLayout inputLayoutName) {
            this.inputLayoutName = inputLayoutName;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // Build the new string by replacing the filtered characters
            StringBuilder filteredStringBuilder = new StringBuilder();
            for (int i = start; i < end; i++) {
                char currentChar = source.charAt(i);
                // Check if the character is a letter or a space
                if (Character.isLetter(currentChar) || currentChar == ' ') {
                    filteredStringBuilder.append(currentChar);
                    inputLayoutName.setError(null);
                } else {
                    inputLayoutName.setError("Name should contain character and space only!");
                }
            }
            return filteredStringBuilder.toString();
        }
    }

    //dialog success
    private void showSuccessDialog(UserModel userModel) {
        // Inflate the custom layout/view
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_success, null);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Create the AlertDialog object
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        // Find and set the dialog elements
        ImageView icon = dialogView.findViewById(R.id.dialog_icon);
        TextView message = dialogView.findViewById(R.id.dialog_message);
        Button okButton = dialogView.findViewById(R.id.button_ok);
        Button cancelButton = dialogView.findViewById(R.id.button_cancel);

        // Set the icon and message
        icon.setImageResource(R.drawable.ic_success); // Use your icon resource
        message.setText("User " + userModel.getName() + " added successfully do you want add another user?");

        // Set button click listeners
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle OK button click
                clearInputs();
                dialog.dismiss();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Cancel button click
                dialog.dismiss();
                goBackAndReloadList();
            }
        });

        // Show the dialog
        dialog.show();
    }
    //dialog exception
    private void showExceptionDialog(ResponseException responseException) {
        // Inflate the custom layout/view
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_exception, null);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Create the AlertDialog object
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        // Find and set the dialog elements
        TextView message = dialogView.findViewById(R.id.dialog_message);
        Button okButton = dialogView.findViewById(R.id.button_ok);

        // Set the icon and message
        message.setText(responseException.getField()+"\n "+responseException.getMessage());

        // Set button click listeners
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle OK button click
                dialog.dismiss();

            }
        });
        // Show the dialog
        dialog.show();
    }
    private void clearInputs() {
        inputEditTextName.setText("");
        inputEditTextName.requestFocus();
        inputEditTextEmail.setText("");
        gender="";
        status="";
        radioGroupGender.clearCheck();
        radioGroupStatus.clearCheck();
    }

    private void goBackAndReloadList() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("reload", true);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}