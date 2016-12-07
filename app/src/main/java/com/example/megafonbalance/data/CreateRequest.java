package com.example.megafonbalance.data;


import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateRequest{

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String HOST_MEGAFON_API = "api.megafon.ru/mlk";
    public static final String USER_AGENT = "MLK Android Phone 2.2.2";

    private OkHttpClient httpClient;

    public Response authCheck(){
        httpClient = new OkHttpClient();
        Request requestCheck = new Request.Builder()
                .addHeader("User-Agent", USER_AGENT)
                .url(HTTP + HOST_MEGAFON_API + "/auth/check")
                .get()
                .build();
        try {
            Response responseLogin = httpClient.newCall(requestCheck).execute();
            return responseLogin;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response getCaptcha(String cookie){
        httpClient = new OkHttpClient();
        Request requestCaptcha = new Request.Builder()
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Cookie", cookie)
                .url(HTTPS + HOST_MEGAFON_API + "/auth/captcha")
                .get()
                .build();
        try {
            Response responseCaptcha = httpClient.newCall(requestCaptcha).execute();
            return responseCaptcha;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response login(String login, String password, String cookie){
        httpClient = new OkHttpClient();

        RequestBody requestBodyPost = new FormBody.Builder()
                .add("login", login)
                .add("password", password)
                .build();

        Request requestLogin = getRequestLogin(cookie, requestBodyPost);
        try {
            Response responseLogin = httpClient.newCall(requestLogin).execute();
            Log.d("MainActivity", "responseLogin = " + responseLogin.toString());
            return responseLogin;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response login(String login, String password, String captcha, String cookie){

        httpClient = new OkHttpClient();

        RequestBody requestBodyPost = new FormBody.Builder()
                .add("login", login)
                .add("password", password)
                .add("captcha", captcha)
                .build();

        Request requestLogin = getRequestLogin(cookie, requestBodyPost);
        try {
            Log.d("MainActivity", "requestLogin = " + requestLogin);
            Response responseLogin = httpClient.newCall(requestLogin).execute();
            return responseLogin;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Request getRequestLogin(String cookie, RequestBody requestBodyPost) {
        try{
            return new Request.Builder()
                    .addHeader("User-Agent", USER_AGENT)
                    .addHeader("Cookie", cookie)
                    .url(HTTPS + HOST_MEGAFON_API + "/login")
                    .post(requestBodyPost)
                    .build();
        }catch (NullPointerException e){
            return null;
        }
    }

    public Response balance(String cookie){
        httpClient = new OkHttpClient();

        Request requestBalance = new Request.Builder()
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Cookie", cookie)
                .url(HTTPS + HOST_MEGAFON_API + "/api/main/info")
                .get()
                .build();
        try {
            Response responseBalance = httpClient.newCall(requestBalance).execute();
            Log.d("MainActivity", "responseBalance = " + responseBalance.toString());
            return responseBalance;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
