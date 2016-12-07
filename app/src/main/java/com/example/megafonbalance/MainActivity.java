package com.example.megafonbalance;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.megafonbalance.data.CreateRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ImageView imageCaptcha;
    EditText editLogin, editPassword, editCaptcha;
    TextView textBalance;
    Button buttonLogin, buttonBalance;
    CreateRequest request = new CreateRequest();
    String login, password, captchaText;

    private boolean isCaptchaNeed;
    private ProgressDialog dialog;
    private String widgetKey;
    private String cookie;

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageCaptcha = (ImageView) findViewById(R.id.activity_main_image_captcha);
        editLogin = (EditText) findViewById(R.id.activity_main_edit_login);
        editPassword = (EditText) findViewById(R.id.activity_main_edit_password);
        editCaptcha = (EditText) findViewById(R.id.activity_main_edit_captcha);
        textBalance = (TextView) findViewById(R.id.activity_main_text_balance);
        buttonLogin = (Button) findViewById(R.id.activity_main_button_login);
        buttonBalance = (Button) findViewById(R.id.activity_main_button_balance);

        new RequestCaptcha().execute();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = editLogin.getText().toString();
                password = editPassword.getText().toString();
                captchaText = editCaptcha.getText().toString();
                RequestTask requestTask = new RequestTask();
                requestTask.execute();
            }
        });

        buttonBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = editLogin.getText().toString();
                password = editPassword.getText().toString();
                new BalanceTask().execute();

            }
        });

    }

    class RequestTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {

            try {
                Log.d(TAG, "Нужна ли капча? = " + isCaptchaNeed);
                if(isCaptchaNeed){
                    Log.d(TAG, "Идем с капчей");

                    Response responseLoginCaptcha = request.login(login, password, captchaText, cookie);

                    cookie = responseLoginCaptcha.headers().get("Set-Cookie");
                    String responseLoginCaptchaText = responseLoginCaptcha.body().string();
                    Log.d(TAG, "CAPTCHA Response = " + responseLoginCaptchaText);
                    JSONObject jsonResponseLoginCaptcha = new JSONObject(responseLoginCaptchaText);
                    widgetKey = jsonResponseLoginCaptcha.getString("widgetKey");
                    Log.d(TAG, "widgetKey = " + widgetKey);

                    return null;
                }else {
                    Log.d(TAG, "Идем без капчи");
                    Response responseLogin = request.login(login, password, cookie);

                    try{
                        cookie = responseLogin.headers().get("Set-Cookie");
                    } catch (Exception e){}

                    Log.d(TAG, "КУКИ БЕЗ КАПЧИ = " + cookie);
                    String responseLoginText = responseLogin.body().string();
                    JSONObject jsonResponseLogin = new JSONObject(responseLoginText);
                    widgetKey = jsonResponseLogin.getString("widgetKey");
                    Log.d(TAG, "widgetKey = " + widgetKey);
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Логинимся...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
        }

    }

    class RequestCaptcha extends AsyncTask<Bitmap, Bitmap, Bitmap>{

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            Response responseCheck = request.authCheck();
            try {
                cookie = responseCheck.headers().get("Set-Cookie");
                String responseCheckText = responseCheck.body().string();
                Log.d(TAG, "CHECK text = " + responseCheckText);
                JSONObject jsonResponseCheck = new JSONObject(responseCheckText);
                isCaptchaNeed = jsonResponseCheck.getBoolean("captchaNeeded");
                if(isCaptchaNeed){
                    Response responseCaptcha = request.getCaptcha(cookie);
                    cookie = responseCaptcha.headers().get("Set-Cookie");
                    byte[] image = responseCaptcha.body().bytes();
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    return imageBitmap;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            dialog.dismiss();
            imageCaptcha.setImageBitmap(s);
        }
    }

    class BalanceTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {

            try {
                Response responseBalance = request.balance(cookie);
                String responseBalanceText = responseBalance.body().string();
                Log.d(TAG, "balance text = " + responseBalanceText);
                JSONObject jsonResponseBalance = new JSONObject(responseBalanceText);
                double balance = jsonResponseBalance.getDouble("originalBalance");
                return String.valueOf(balance);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Загружаем баланс...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            textBalance.setText("Ваш баланс составляет: " + s + " рублей!");
        }
    }
}
