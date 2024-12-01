package com.servicea.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.servicea.activity.ReservePaymentResultActivity;
import com.servicea.adapter.AdapterTabLayout;
import com.servicea.app.Constants;
import com.servicea.app.CustomViewPager;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.servicea.R;

import com.servicea.model.ZarinVerify;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    CustomViewPager viewPager;
    PreferenceUtil preferenceUtil;

    private static final String TAG = "MainActivity";
    private int[] tabIcons = {

            R.drawable.ic_home,
            R.drawable.ic_profile,
    };


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(Constants.PUSH_NOTIFICATION)) {
            G.Log("push notification1");
        }
        Log.d(TAG, "onNewIntent: " + intent.getData().toString());
        // Check if this intent is started via custom scheme link
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            // may be some checks with your custom uri
            String status = uri.getQueryParameter("Status");
            if (status.equals("OK")) {
                // Payment was successful
            } else {
                // Payment failed
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        if (getIntent().hasExtra(Constants.PUSH_NOTIFICATION)) {
            G.Log("push notification1");
        }
        G.Activity = this;
        G.context = this;
        preferenceUtil = new PreferenceUtil(this);
        G.deleteServiceFilters();
        FindView();
        preferenceUtil.cashFirstRun(false);


        tabLayout.addTab(tabLayout.newTab().setText("خانه"));
        tabLayout.addTab(tabLayout.newTab().setText("پروفایل"));

        tabLayout.setSelectedTabIndicatorHeight(0);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final AdapterTabLayout adapterr = new AdapterTabLayout(this, getSupportFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapterr);
        viewPager.setPagingEnabled(false);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.button);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.graymenu);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        setupTabIcons();


        Uri data = getIntent().getData();

        //comment by hani
//        ZarinPal.getPurchase(this).verificationPayment(data, new OnCallbackVerificationPaymentListener() {
//            @Override
//            public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, String refID, PaymentRequest paymentRequest) {
//                Log.d(TAG, "onCallbackResultVerificationPayment: " + isPaymentSuccess);
//                Log.d(TAG, "onCallbackResultVerificationPayment: paymentRequest.getDescription():" + paymentRequest.getDescription());
////                final ArrayList<NameValuePair> params = new ArrayList<>();
////                params.add(new BasicNameValuePair("user_id", "" + G.preferences.getString("ID", "")));
////                if (isPaymentSuccess)
////                    params.add(new BasicNameValuePair("isPaymentSuccess", 1 + ""));
////                else
////                    params.add(new BasicNameValuePair("isPaymentSuccess", 0 + ""));
////                params.add(new BasicNameValuePair("refID", "" + refID));
////                params.add(new BasicNameValuePair("amount", "" + paymentRequest.getAmount()));
//                if (paymentRequest.getDescription().equals(Constants.reserve_service)){
//                    if (isPaymentSuccess) {
//                       startActivity(new Intent(MainActivity.this, ReservePaymentResultActivity.class).putExtra(Constants.reserve_payment_result, true));
//                    } else {
//                        startActivity(new Intent(MainActivity.this, ReservePaymentResultActivity.class).putExtra(Constants.reserve_payment_result, false));
//                    }
//                }else{
//                    if (isPaymentSuccess) {
//                        String message = "پرداخت با موفقیت انجام شد";
//                        changeCharge(G.preference.getInt("amount_charge", 0));
//                        G.toast(message);
//                    } else {
//                        String message = "پرداخت انجام نشد";
//                        G.toast(message);
//                    }
//                }
//
////                payment(params, paymentRequest.getAmount() + "");
//
//
//            }
//        });

        getZarinPallVerify();
        cheack_update();
    }

    public void getZarinPallVerify() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(G.zarinPallBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SharedPreferences prefs = getSharedPreferences("AUTHORITY_PREFS_NAME", MODE_PRIVATE);
        String authority = prefs.getString("authority", "");
        int amount = prefs.getInt("amount", 0);
        Boolean verified = prefs.getBoolean("verified", false);

        Api apiService = retrofit.create(Api.class);
        ZarinVerify zarinVerify = new ZarinVerify();
        zarinVerify.setMerchant_id(G.MerchantID);
        zarinVerify.setAmount(amount);
        zarinVerify.setAuthority(authority);
        Call<ResponseBody> call = apiService.verifyZarinPall(Constants.reserve_service_accept, Constants.reserve_service_content, zarinVerify);
      if (!verified){ call.enqueue(new Callback<ResponseBody>() {
          @Override
          public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
              String result = G.getResult(response);
        /*        if (result.equals("") && !verified) {
                    try {
                        result = G.getErrorResult(response);
                        JSONObject errorResponse = new JSONObject(result);
                        JSONObject errors = errorResponse.getJSONObject("errors");
                        int code = errors.getInt("code");
                        G.Log("zarin Error code = " + code);
                        G.saveAuthority(authority, amount, true);
                        startActivity(new Intent(MainActivity.this, ReservePaymentResultActivity.class).putExtra(Constants.reserve_payment_result, false));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {*/
              try {
                  JSONObject verifyResponse = new JSONObject(result);
                  JSONObject data = verifyResponse.getJSONObject("data");
                  String message = data.getString("message");
                  int code = data.getInt("code");

                  if (code == 100) {
                      changeCharge(G.preference.getInt("amount_charge", 0));
                      if (message.equals("Paid")) {
                          G.saveAuthority(authority, amount, true);
                          startActivity(new Intent(MainActivity.this, ReservePaymentResultActivity.class).putExtra(Constants.reserve_payment_result, true));
                      }
                  }
                  if (code == 101) {
                  } else {
                      startActivity(new Intent(MainActivity.this, ReservePaymentResultActivity.class).putExtra(Constants.reserve_payment_result, true));
                  }
              } catch (JSONException e) {
//                    throw new RuntimeException(e);
              }
          }

//            }

          @Override
          public void onFailure(Call<ResponseBody> call, Throwable t) {

          }
      });}

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void FindView() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context));
    }


    public void onclickAlamrs(View v) {
        startActivity(new Intent(MainActivity.this, AlarmsActivity.class));
    }

    @Override
    public void onResume() {

        G.Activity = this;
        G.context = this;
        G.stop_loading();
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0);
        } else {
            G.toast("برای خروج دوباره کلیک کنید");
            this.doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }

    }

    public int temp_charge_remain = 0;

    public void changeCharge(int charge) {
//        G.loading(MainActivity.this);
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        JSONObject object = new JSONObject();
//        name, lastName, shop_name, shop_phone, phone, email, sex,
//                b_date, category, service, openTime, closeTime, numOfBranch, waiting, catering, province, city, address
        temp_charge_remain = G.preference.getInt("charge_remain", 0) + charge;
        try {
            object.put("charge_total", charge);
            object.put("charge_remain", temp_charge_remain);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String d_id = PreferenceUtil.getD_id();
        Call<ResponseBody> request = api.changeCharge(d_id, G.returnBody(object.toString()));
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                String result = G.getResult(response);
                if (result.length() > 0 && result.length() < 10) {
                    G.toast("اطلاعات با موفقیت ثبت شد");
                    G.preference.edit().putInt("charge_total", charge).apply();
                    G.preference.edit().putInt("charge_remain", temp_charge_remain).apply();

                } else {
                    G.toast("مشکل در ثبت اطلاعات");
                }
                G.stop_loading();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                Toast.makeText(MainActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void cheack_update() {
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.check_update();
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    String result = G.getResult(response);
                    G.Log(result);
                    JSONObject object = G.StringtoJSONObject(result);
                    if (object.has("records")) {
                        JSONArray records = object.getJSONArray("records");
                        if (records.length() > 0) {
                            JSONObject obj = records.getJSONObject(0);
                            String file_dir = (obj.getString("file_dir") + "").replace("null", "");
                            String version = (obj.getString("version") + "").replace("null", "");
                            Boolean major = (obj.getBoolean("major"));
                            if (G.AndroidAppVersion.length() > 0 && !G.AndroidAppVersion.equals(version)) {
                                dialogUpdate(file_dir, major);
                            }

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });


    }

    public void dialogUpdate(String link, boolean major) {

        SweetAlertDialog s = new SweetAlertDialog(G.context, SweetAlertDialog.WARNING_TYPE)

                .setTitleText("بروزرسانی برنامه")
                .setContentText("لطفا نسخه جدید برنامه را نصب کنید")
                .setCancelText(" بستن ")
                .setConfirmText(" دانلود ")
                .showCancelButton(false)

                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        if (major) {
                            finish();
                        }
                    }
                })

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();

                        SweetAlertDialog pDialog = new SweetAlertDialog(G.context, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        pDialog.setTitleText("لطفا صبر کنید");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        openBrowser(link, pDialog);
//                        if (isStoragePermissionGranted()) {
//                            install();
//                        }
                    }
                });
        if (major) {
            s.setCancelable(false);
        } else {
            s.setCancelable(true);
        }
        s.show();

    }

    public void openBrowser(String url, SweetAlertDialog pDialog) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pDialog.dismiss();
            }
        }, 1000);

    }

}