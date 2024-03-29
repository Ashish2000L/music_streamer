package com.musicstreaming.musicstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.musicstreaming.musicstreaming.splash.DIR_NAME;

public class login extends AppCompatActivity {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */

    MediaPlayer mediaPlayer;
    String userAgent,TAG="checkforerrorinlogin";
    TextInputEditText et_username, et_password;
    TextInputLayout usernameTextInputLayout, passwordTextInputLayout;
    EditText forgot_username;
    public static String SHARED_PREF="sharedpref";
    public static String USERNAME="username";
    public static String PASSWORD="password";
    public static String NAME="name";
    public static String IMAGE="image";
    public static String EMAIL="email";
    LinearLayout mainlogin;
    RelativeLayout forgot,helpwanted;
    boolean isvisible=false;
    public SharedPreferences sharedPreferences;
    Animation appear;
    public static Context LOGIN;
    Activity LOGIN_ACTIVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(this));

        LOGIN=this;
        LOGIN_ACTIVITY=this;

        et_username = findViewById(R.id.username_login);
        et_password = findViewById(R.id.password_login);
        forgot_username=findViewById(R.id.forgot_username);
        mainlogin=findViewById(R.id.mainlogin);
        forgot=findViewById(R.id.forgotpassword);
        helpwanted=findViewById(R.id.wanthelp);
        usernameTextInputLayout=findViewById(R.id.username_textinputlayout);
        passwordTextInputLayout=findViewById(R.id.password_textinputlayout);

        appear= AnimationUtils.loadAnimation(this,R.anim.appear);

        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordTextInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mainlogin.startAnimation(appear);

        editTextRuntimeChecker(et_username);
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

                return (ch>='a' && ch<='z') || (ch>='A' && ch<='Z') || (ch>='0' && ch<='9') || ch=='_';

            }

        });

    }

    public void singup(View view) {

        startActivity(new Intent(login.this, register.class));

    }

    public void loginaction(View view) {
        final String username, password,url,urlforuserdetail;
        username = et_username.getText().toString().trim().toLowerCase();
        password = et_password.getText().toString().trim();
        urlforuserdetail="https://rentdetails.000webhostapp.com/musicplayer_files/getuserdata.php";
        url="https://rentdetails.000webhostapp.com/musicplayer_files/login.php";

        usernameTextInputLayout.setError(null);
        passwordTextInputLayout.setError(null);

        if (username.isEmpty()) {
            usernameTextInputLayout.setError("Username Required!");
//            Toast.makeText(this, "Username required!", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            passwordTextInputLayout.setError("Password Required!");
//            Toast.makeText(this, "Password required!", Toast.LENGTH_SHORT).show();
        } else
            if(password.length()<6){
                passwordTextInputLayout.setError("Invalid Password!");
//                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
            }else{

                SharedPreferences.Editor editor =sharedPreferences.edit();
                editor.putString(USERNAME,username);
                editor.apply();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Verifying User...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();

                    if(response.equalsIgnoreCase("0")){
                        Toast.makeText(login.this, "Please verify your email! ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login.this,register.class)
                        .putExtra("isverified",false)
                        .putExtra("username",username));

                    }else if(response.equalsIgnoreCase("1")){
                        new getuserdetails(urlforuserdetail,username,password).execute();
                    }else{
                        Toast.makeText(login.this, response, Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "onResponse: response is :"+response);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressDialog.dismiss();
                    Log.d(TAG, "onErrorResponse: error response "+error.getMessage());
                    Toast.makeText(login.this, "Unable to connect, Try again later...", Toast.LENGTH_SHORT).show();
                    String err="Error in login "+error.getMessage();
                    new internal_error_report(LOGIN,err,sharedPreferences.getString(USERNAME,"")).execute();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();

                    params.put("username",username);
                    params.put("password",password);

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }

    }

    public void forgotpass(View view) {

        forgot.setVisibility(View.VISIBLE);
        mainlogin.setVisibility(View.GONE);
        forgot.startAnimation(appear);

    }

    public void getback(View view) {

        mainlogin.setVisibility(View.VISIBLE);
        forgot.setVisibility(View.GONE);
        mainlogin.startAnimation(appear);
    }

    public void newpass(View view) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting new password ");
        progressDialog.setCancelable(false);


        final String forgotusername=forgot_username.getText().toString().trim();

        if(forgotusername.isEmpty()){
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.show();
            String url = "https://rentdetails.000webhostapp.com/musicplayer_files/generatenewpass.php";
            final RelativeLayout details = findViewById(R.id.enter_forgot_detail);
            final TextView finalmessage = findViewById(R.id.positivemessage);
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();

                    if (response.equalsIgnoreCase("successful")) {

                        details.setVisibility(View.GONE);
                        finalmessage.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(login.this, response, Toast.LENGTH_SHORT).show();
                    }

                    Log.d(TAG, "onResponse: response is " + response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d(TAG, "onErrorResponse: error is " + error.getMessage());
                    String err="Error in new pass in login "+error.getMessage();
                    new internal_error_report(LOGIN,err,sharedPreferences.getString(USERNAME,"")).execute();
                    progressDialog.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", forgotusername);

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }

    }

    public void needhelp(View view) {

        mainlogin.setVisibility(View.GONE);
        helpwanted.setVisibility(View.VISIBLE);
        helpwanted.startAnimation(appear);

    }

    public void backtologin(View view) {

        helpwanted.setVisibility(View.GONE);
        mainlogin.setVisibility(View.VISIBLE);
        mainlogin.startAnimation(appear);

    }

    public class getuserdetails extends AsyncTask<String,Void,Void>{
        String url,username,password;

        public getuserdetails(String url,String username,String password) {
            this.url = url;
            this.username=username;
            this.password=password;
        }

        @Override
        protected Void doInBackground(String... strings) {

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {



                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (success.equals("1")) {
                            JSONObject object = jsonArray.getJSONObject(0);

                            String name = object.getString("name");
                            String email = object.getString("email");
                            String image = object.getString("image");

                            String customurl = "https://rentdetails.000webhostapp.com/musicplayer_files/musicimages/"+image;

                            SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(USERNAME,username);
                            editor.putString(PASSWORD,password);
                            editor.putString(NAME,name);
                            editor.putString(IMAGE,customurl);
                            editor.putString(EMAIL,email);
                            editor.apply();

                            new make_file_in_directory(LOGIN_ACTIVITY,LOGIN,username).write_credential_file(username,password,new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,"file.json"));

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    startActivity(new Intent(login.this,MainActivity.class));
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(login.this, "Unable to connect, Try again later...", Toast.LENGTH_SHORT).show();
                    String err="Error in getting user detail in login "+error.getMessage();
                    new internal_error_report(LOGIN,err,sharedPreferences.getString(USERNAME,"")).execute();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();

                    params.put("username",username);

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(login.this);

            queue.add(request);

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
