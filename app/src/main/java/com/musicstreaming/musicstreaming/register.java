package com.musicstreaming.musicstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.login.USERNAME;
import static com.musicstreaming.musicstreaming.splash.DIR_NAME;

public class register extends AppCompatActivity {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */
    EditText et_otp;
    TextInputEditText et_name,et_password,et_email,et_username;
    TextInputLayout nameTextInputLayout, emailTextInputLayout, usernameTextInputLayout, passwordTextInputLayout;
    String TAG="checkingmusicfile";
    String username_otp;
    LinearLayout linearLayoutotp, linearLayoutregister;
    boolean isvis=false;
    public SharedPreferences sharedPreferences;
    Animation appear;
    public static Context REGISTER;
    Activity REGISTER_ACTIVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(this));

        REGISTER=this;
        REGISTER_ACTIVITY=this;

        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);

        et_name=findViewById(R.id.et_name);
        et_email=findViewById(R.id.et_email);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_otp = findViewById(R.id.otp);
        linearLayoutotp = findViewById(R.id.linearotp);
        linearLayoutregister = findViewById(R.id.register);
        nameTextInputLayout=findViewById(R.id.name_textinputlayout);
        emailTextInputLayout=findViewById(R.id.email_textinputlayout);
        usernameTextInputLayout=findViewById(R.id.user_textinputlayout);
        passwordTextInputLayout=findViewById(R.id.pass_textinputlayout);

        appear= AnimationUtils.loadAnimation(this,R.anim.appear);

       Intent intent = getIntent();
       boolean isverified = intent.getBooleanExtra("isverified",true);
       String usernames=intent.getStringExtra("username");
       if(!isverified){
           username_otp=usernames;
           linearLayoutotp.setVisibility(View.VISIBLE);
           linearLayoutregister.setVisibility(View.GONE);
           linearLayoutotp.startAnimation(appear);
       }

       //linearLayoutregister.startAnimation(appear);
        addWatcherToEditText();

        editTextRuntimeChecker(et_username);

    }
    public  void addWatcherToEditText(){

        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.length()>0){
                    emailTextInputLayout.setError("Invalid Email!");
                }else{
                    emailTextInputLayout.setError(null);
                }
            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()<6 && s.toString().length()>0){
                    passwordTextInputLayout.setError("Too Short Password!");
                }else
                {
                    passwordTextInputLayout.setError(null);
                }

            }
        });

        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameTextInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void editTextRuntimeChecker(final TextInputEditText et_username){

        et_username.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernameTextInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String original = s.toString();
                int originalTextLength = original.length();
                int currentSelection = et_username.getSelectionStart();

                StringBuilder builder = new StringBuilder();
                boolean hasChanged=false;
                for(int i=0;i<originalTextLength;i++)
                {
                    char currentChar = original.charAt(i);

                    if(isAllowed(currentChar)){
                        if(!(currentChar>='A' && currentChar<='Z')){
                            builder.append(currentChar);
                        }else {
                            hasChanged=true;
                            currentChar=Character.toLowerCase(currentChar);
                            builder.append(currentChar);
                        }
                    }else{
                        hasChanged=true;
                        if(currentSelection>=i){
                            currentSelection--;
                        }
                    }
                }

                if(hasChanged){
                    String newText = builder.toString().toLowerCase();
                    et_username.setText(newText);
                    et_username.setSelection(currentSelection);
                }

            }

            public boolean isAllowed(char ch){

                return (ch>='a' && ch<='z') || (ch>='A' && ch<='Z') || (ch>='0' && ch<='9' || ch=='_');

            }

        });

    }

    public void login(View view) {

        startActivity(new Intent(register.this,login.class));

    }

    public void signupprocess(View view) throws IOException {

        final String name = et_name.getText().toString();
        final String email = et_email.getText().toString().trim();
        final String username = et_username.getText().toString().trim();
        final String password = et_password.getText().toString().trim();

        String url="https://rentdetails.000webhostapp.com/musicplayer_files/register.php";

        if(name.isEmpty()){
            nameTextInputLayout.setError("Name Required!");
//            Toast.makeText(this, "Name Required", Toast.LENGTH_SHORT).show();
        }else if(email.isEmpty()){
            emailTextInputLayout.setError("Email Required!");
//            Toast.makeText(this, "Email Required", Toast.LENGTH_SHORT).show();
        }else if(username.isEmpty()){
            usernameTextInputLayout.setError("Username Required!");
//            Toast.makeText(this, "Username Required", Toast.LENGTH_SHORT).show();
        }else if(password.isEmpty()){
            passwordTextInputLayout.setError("Password Required!");
//            Toast.makeText(this, "Password Required", Toast.LENGTH_SHORT).show();
        }else if(password.length()<6){
            passwordTextInputLayout.setError("Password Too Short!");
//            Toast.makeText(this, "Password length is too short", Toast.LENGTH_SHORT).show();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailTextInputLayout.setError("Enter Valid Email!");
//            Toast.makeText(this, "Enter Vaild Email", Toast.LENGTH_SHORT).show();
        }else {

            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putString(USERNAME,username);
            editor.apply();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Registering user, Please wait...");
            progressDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();
                    if (response.equals("0")) {
                        linearLayoutotp.setVisibility(View.VISIBLE);
                        linearLayoutregister.setVisibility(View.GONE);
                        linearLayoutotp.startAnimation(appear);
                        username_otp=username;

                        new make_file_in_directory(REGISTER_ACTIVITY,REGISTER,username).write_credential_file(username,password,new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,"file.json"));

                    } else {
                        Toast.makeText(register.this, ""+response, Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "onResponse: responce " + response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressDialog.dismiss();

                    if (error.getMessage() != null) {
                        //new errorlistner().execute("register error : " + error.getMessage());
                    } else {
                        Toast.makeText(register.this, "Unknown error occured!", Toast.LENGTH_SHORT).show();
                    }
                    String err="Error in signupprocess in register "+error.getMessage();
                    new internal_error_report(REGISTER,err,sharedPreferences.getString(USERNAME,"")).execute();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("name", name);
                    params.put("username", username);
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }

    }


    public void checkotp(View view) {

        final String otp = et_otp.getText().toString().trim();
        String url="https://rentdetails.000webhostapp.com/musicplayer_files/otp.php";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Verifying OTP ...");


        if(otp.trim().length()<6){
            Toast.makeText(this, "Enter Vaild OTP", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();

                    if (response.equals("account verified")) {
                        startActivity(new Intent(register.this, login.class));
                    } else {
                        Toast.makeText(register.this, response, Toast.LENGTH_SHORT).show();
                        String err="Error in attemp to enter wrong otp:  "+otp;
                        new internal_error_report(REGISTER,err,sharedPreferences.getString(USERNAME,"")).execute();
                    }

                    Log.d(TAG, "onResponse: otp response is "+response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    if (error.getMessage() != null) {
                        //new errorlistner().execute("otp error: " + error.getMessage());
                        Toast.makeText(register.this, "Error occured: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(register.this, "Unknown error occured!", Toast.LENGTH_SHORT).show();
                    }
                    String err="Error in checkotp in register "+error.getMessage();
                    new internal_error_report(REGISTER,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                    Log.d(TAG, "onErrorResponse: error is "+error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("otp", otp);
                    if(!sharedPreferences.getString(USERNAME,"").equals(""))
                        params.put("username",sharedPreferences.getString(USERNAME,""));

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }


    }

    public void backtoregister(View view) {
        linearLayoutotp.setVisibility(View.GONE);
        linearLayoutregister.setVisibility(View.VISIBLE);
        linearLayoutregister.startAnimation(appear);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,login.class));
    }
}
