package com.servicea.activities;

import static com.servicea.app.Constants.CITY_ID;
import static com.servicea.app.Constants.PROVINCE_ID;
import static com.servicea.app.Constants.USER_TYPE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.servicea.app.Constants;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.app.StateOpenHelper;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.servicea.R;
import me.aflak.libraries.callback.FingerprintDialogSecureCallback;
import me.aflak.libraries.callback.PasswordCallback;
import me.aflak.libraries.dialog.DialogAnimation;
import me.aflak.libraries.dialog.FingerprintDialog;
import me.aflak.libraries.dialog.PasswordDialog;
import me.aflak.libraries.utils.FingerprintToken;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity implements PasswordCallback, FingerprintDialogSecureCallback {
    DilatingDotsProgressBar mDilatingDotsProgressBar;
    private String PASSWORD = "";

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.hasExtra(Constants.PUSH_NOTIFICATION)){
            G.Log("push notification");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new PreferenceUtil(this);
        G.context = this;
        G.Activity = this;
        setContentView(R.layout.activity_splash);
        mDilatingDotsProgressBar = findViewById(R.id.progress);
        G.preference.edit().putInt("AvgKm", 0).apply();
        if(getIntent().hasExtra(Constants.PUSH_NOTIFICATION)){
            G.Log("push_notification");
            Log.d("SplashActivity", "onCreate: push_notification");
        }
        String user_id = PreferenceUtil.getUser_id();
        if (user_id.length() > 0 && PreferenceUtil.getcachLogin()) {
            if (G.preference.getBoolean("ActivateRad", false)) {
                getProfile(user_id);
            }
        }
        PASSWORD = (G.preference.getString("PASSWORD", "") + "");
        if (PASSWORD.length() < 3) {
            PASSWORD = "kfjdhgdfghjkghuihgerhguerhgruhg";
        }
        new Handler().postDelayed(() -> {
            G.Log(PreferenceUtil.getcachLogin() + "  PreferenceUtil.getcachLogin()");
            if (!PreferenceUtil.getcachLogin()) {
                Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                mDilatingDotsProgressBar.hideNow();
                SplashActivity.this.finish();
            } else if (PreferenceUtil.getUser_id().length() <= 0) {
                G.preference.edit().putBoolean("ActivateRad", false).apply();
                Intent mainIntent = new Intent(SplashActivity.this, ProfileActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                mDilatingDotsProgressBar.hideNow();
                SplashActivity.this.finish();
            } else {
                if (G.preference.getBoolean("AccessFingerprint", false) && android.os.Build.VERSION.SDK_INT >= 23 && FingerprintDialog.isAvailable(G.context)) {
                    FingerprintDialog.initialize(G.context)
                            .title(R.string.fingerprint_title)
                            .tryLimit(4, fingerprintDialog -> {
                                Toast.makeText(SplashActivity.this, "شما بیش از 4 بار اثرانگشت اشتباه زدید", Toast.LENGTH_SHORT).show();
                                G.preference.edit().clear().apply();
                                fingerprintDialog.dismiss();
                                finish();
                            })
                            .enterAnimation(DialogAnimation.Enter.RIGHT)
                            .exitAnimation(DialogAnimation.Exit.RIGHT)
                            .circleScanningColor(R.color.button)
                            .fingerprintScanningColor(R.color.white)
                            .cancelOnPressBack(false)
                            .cancelOnTouchOutside(false)
                            .usePasswordButton(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PasswordDialog.initialize(SplashActivity.this)
                                            .title(R.string.password_title)
                                            .message(R.string.password_message)
                                            .callback(SplashActivity.this)
                                            .cancelOnPressBack(false)
                                            .cancelOnTouchOutside(false)
                                            .dimBackground(true)
                                            .enterAnimation(DialogAnimation.Enter.RIGHT)
                                            .exitAnimation(DialogAnimation.Exit.RIGHT)
                                            .passwordType(PasswordDialog.PASSWORD_TYPE_TEXT)
                                            .show();
                                }
                            })
                            .message(R.string.fingerprint_message)
                            .callback(SplashActivity.this, "KeyName1")
                            .show();

                } else if (G.preference.getBoolean("AccessPassword", false)) {
                    PasswordDialog.initialize(SplashActivity.this)
                            .title(R.string.password_title)
                            .message(R.string.password_message)
                            .callback(SplashActivity.this)
                            .dimBackground(true)
                            .cancelOnPressBack(false)
                            .cancelOnTouchOutside(false)
                            .enterAnimation(DialogAnimation.Enter.RIGHT)
                            .exitAnimation(DialogAnimation.Exit.RIGHT)
                            .passwordType(PasswordDialog.PASSWORD_TYPE_TEXT)
                            .show();
                } else {
                    G.preference.edit().putBoolean("ActivateRad", true).apply();
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    mDilatingDotsProgressBar.hideNow();
                    SplashActivity.this.finish();
                }

            }
        }, 2300);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("Splash", "Fetching FCM registration token failed", task.getException());
                    return;
                }

                String token = task.getResult();
                Log.d("Splash", "onComplete: " + token);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        G.Activity = this;
        G.context = this;
    }

    public void getProfile(String user_id) {
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getProfile("id,eq," + user_id);
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @SuppressLint("Range")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                assert response.body() != null;
                try {
                    String result = response.body().string();
                    G.Log(result);
                    JSONObject object = G.StringtoJSONObject(result);
                    JSONArray records = object.getJSONArray("records");
                    if (records.length() > 0) {
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject obj = records.getJSONObject(i);
                            String user_id = obj.getString("id");
                            PreferenceUtil.cashUser_id(user_id);
                            String f_name = obj.getString("f_name");
                            String l_name = obj.getString("l_name");
                            String mobile = obj.getString("mobile");
                            int user_type = obj.getInt("user_type");
                            String sex = obj.getString("sex");
                            String birthdate = obj.getString("birthdate");
                            String province_id = obj.getString("province_id");
                            String city_id = obj.getString("city_id");
                            G.preference.edit().putInt(USER_TYPE, user_type).apply();
                            G.preference.edit().putString(PROVINCE_ID, province_id).apply();
                            G.preference.edit().putString(CITY_ID, city_id).apply();
                            if (obj.has("profile_photo")) {
                                String profile_photo = obj.getString("profile_photo");
                                G.preference.edit().putString("profile_photo", profile_photo).apply();
                              }
//                                    if (obj.has("header_photo")) {
//                                        String header_photo = obj.getString("header_photo");
//                                        G.preference.edit().putString("header_photo", header_photo).apply();
//                                        Picasso.get().load(G.PreImagesURL + "headers/" + header_photo).error(R.drawable.backpro).placeholder(R.drawable.backpro).into(profile_iv2);
//                                    }
                            if (obj.has("password")) {
                                String password = obj.getString("password");
                                String now_password = G.preference.getString("PASSWORD", "");
                                if (now_password.length() < 3) {
                                    G.preference.edit().putString("PASSWORD", password).apply();
                                }
                            }


                            PreferenceUtil.cashPhone(mobile);
                            PreferenceUtil.cashInfo(f_name, l_name, "", "");
                            StateOpenHelper openHelper = new StateOpenHelper(G.Activity);
                            openHelper.openDatabase();
                            SQLiteDatabase mDatabase = openHelper.getReadableDatabase();
                        }
                    } else {
                        G.toast("مشکل در دریافت اطلاعات");
                    }

                } catch (JSONException | IOException e) {
                    G.toast("مشکل در تجزیه اطلاعات");
                    e.printStackTrace();
                }
                G.stop_loading();

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }

    @Override
    public void onPasswordSucceeded() {
        G.preference.edit().putBoolean("ActivateRad", true).apply();
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        if (mDilatingDotsProgressBar != null)
            mDilatingDotsProgressBar.hideNow();
        SplashActivity.this.finish();
    }

    @Override
    public boolean onPasswordCheck(String password) {
        return password.equals(PASSWORD);
    }

    @Override
    public void onPasswordCancel() {
        finish();
    }

    @Override
    public void onAuthenticationSucceeded() {
        G.preference.edit().putBoolean("ActivateRad", true).apply();
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        if (mDilatingDotsProgressBar != null)
            mDilatingDotsProgressBar.hideNow();
        SplashActivity.this.finish();
    }

    @Override
    public void onAuthenticationCancel() {
        finish();
    }

    @Override
    public void onNewFingerprintEnrolled(FingerprintToken token) {
        PasswordDialog.initialize(SplashActivity.this)
                .title(R.string.password_title)
                .message(R.string.password_message)
                .callback(SplashActivity.this)
                .cancelOnPressBack(false)
                .cancelOnTouchOutside(false)
                .enterAnimation(DialogAnimation.Enter.RIGHT)
                .exitAnimation(DialogAnimation.Exit.RIGHT)
                .passwordType(PasswordDialog.PASSWORD_TYPE_TEXT)
                .show();
    }
}