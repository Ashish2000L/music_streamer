package com.example.musicstreaming;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class make_file_in_directory {

    Context context;
    String username;
    public make_file_in_directory(Activity activity,Context context,String username) {
        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(activity));

        this.context=context;
        this.username=username;
    }

    public void write_credential_file(String username,String password, File file){
        if(file.exists()) {
            file.delete();

        }
        JSONObject credentials = new JSONObject();
        try {

            credentials.put("username", username);
            credentials.put("password", password);
        } catch (Exception e) {
            Log.d("json_file_writing", "make_credential_file: "+e.getMessage());
        }

        JSONObject version = new JSONObject();
        try {
            version.put("version", BuildConfig.VERSION_CODE);
        } catch (Exception e) {
            Log.d("json_file_writing", "make_credential_file: "+e.getMessage());
        }

        JSONObject final_json = new JSONObject();
        try {
            final_json.put("credential", credentials);
            final_json.put("version", version);

            String json_output = final_json.toString();


            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json_output.getBytes());
            fos.close();

            Log.d("json_file_writing", "make_credential_file: " + final_json);

        } catch (Exception e) {
            Log.d("json_file_writing", "make_credential_file: "+e.getMessage());
            new internal_error_report(context,"Error in make_file_in_directory "+e.getMessage(),username);
        }

    }

    public String read_credentail_file(File file){

        String content="";
        int length= (int)file.length();
        byte[] data = new byte[length];
        try {
            FileInputStream fin = new FileInputStream(file);
            fin.read(data);
            fin.close();

            content = new String(data);
            Log.d("json_file_writing", "read_json_file: "+content);
        }catch (Exception e){
            Log.d("json_file_writing", "read_json_file: "+e.getMessage());
            new internal_error_report(context,"Error in make_file_in_directory "+e.getMessage(),username);
        }
        return content;
    }

    public void write_version_file(File file){

        if(file.exists()) {
            file.delete();

        }

        JSONObject final_json = new JSONObject();
        try {
            final_json.put("version", BuildConfig.VERSION_CODE);

            String json_output = final_json.toString();


            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json_output.getBytes());
            fos.close();

            Log.d("json_file_writing", "make_version_file: " + final_json);

        } catch (Exception e) {
            Log.d("json_file_writing", "make_version_file: "+e.getMessage());
            new internal_error_report(context,"Error in make_file_in_directory "+e.getMessage(),username);
        }

    }

    public int read_version_file(File file){

        int version=0;
        int length= (int)file.length();
        byte[] data = new byte[length];
        try {
            FileInputStream fin = new FileInputStream(file);
            fin.read(data);
            fin.close();

            String content = new String(data);
            JSONObject obj = new JSONObject(content);
            version=obj.getInt("version");
            Log.d("json_file_writing", "read_version_file: "+content);
        }catch (Exception e){
            Log.d("json_file_writing", "read_version_file: "+e.getMessage());
            new internal_error_report(context,"Error in make_file_in_directory "+e.getMessage(),username);
        }
       return version;
    }

    public void write_night_mode(File file,boolean value){
        if(file.exists()) {
            file.delete();

        }

        JSONObject final_json = new JSONObject();
        try {
            final_json.put("night_mode", value);

            String json_output = final_json.toString();


            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json_output.getBytes());
            fos.close();

            Log.d("json_file_writing", "make_night_mode_file: " + final_json);

        } catch (Exception e) {
            Log.d("json_file_writing", "make_night_mode_file "+e.getMessage());
            new internal_error_report(context,"Error in make_file_in_directory "+e.getMessage(),username);
        }
    }

    public boolean read_night_mode(File file){
        boolean IS_NIGHT_MODE=false;
        int length= (int)file.length();
        byte[] data = new byte[length];
        try {
            FileInputStream fin = new FileInputStream(file);
            fin.read(data);
            fin.close();

            String content = new String(data);
            JSONObject obj = new JSONObject(content);
            IS_NIGHT_MODE=obj.getBoolean("night_mode");
            Log.d("json_file_writing", "read_night_mode_file: "+content);
        }catch (Exception e){
            Log.d("json_file_writing", "read_night_mode_file: "+e.getMessage());
            new internal_error_report(context,"Error in make_file_in_directory "+e.getMessage(),username);
        }
        return IS_NIGHT_MODE;
    }

}
