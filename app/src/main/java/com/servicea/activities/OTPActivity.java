package com.servicea.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.servicea.app.Constants;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.app.StateOpenHelper;
import com.servicea.model.State;

import ir.servicea.R;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {
    TextView txt_sms_text, txt_sms_t,txt_timer, license;
    OtpView otp_view;

    private long minutes;
    private long seconds;

    private boolean isTimerFinished = false;


    public void onclickAlamrs(View v) {
        startActivity(new Intent(OTPActivity.this, AlarmsActivity.class));
    }
    @Override
    public void onResume(){
        super.onResume();
        G.Activity = this;
        G.context = this;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        G.Activity = this;
        G.context = this;
        G.preference.edit().putString("phoneAgain", PreferenceUtil.getPhone()).apply();
        FindView();
        if (G.preference.getBoolean("sendAgain", false)) {
            setSendAgainVisibility(View.GONE);
        }

        setTimerForSendAgainCode();

        findViewById(R.id.sendAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTimerFinished){
                    G.preference.edit().putBoolean("sendAgain", true).apply();
                    G.preference.edit().putBoolean("changeNumber", false).apply();
                    G.preference.edit().putString("phoneAgain", PreferenceUtil.getPhone()).apply();
                    startActivity(new Intent(OTPActivity.this, LoginActivity.class));
                }
            }
        });
        findViewById(R.id.changeNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                G.preference.edit().putBoolean("sendAgain", false).apply();
                G.preference.edit().putBoolean("changeNumber", true).apply();
                G.preference.edit().putString("phoneAgain", PreferenceUtil.getPhone()).apply();
                startActivity(new Intent(OTPActivity.this, LoginActivity.class));
            }
        });

        txt_sms_text.setTypeface(G.Bold);
        txt_sms_t.setTypeface(G.Bold);
        txt_timer.setTypeface(G.Normal);
        license.setTypeface(G.Bold);
        otp_view.setTypeface(G.Bold);
        otp_view.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                register(otp, PreferenceUtil.getPhone());
            }
        });
    }

    private void setTimerForSendAgainCode() {
        new CountDownTimer(Constants.SEND_AGAIN_OTP_CODE_TIME, 1000) {
            public void onTick(long millisUntilFinished) {
                minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished % 60000);
                txt_timer.setText(getString(R.string.timer_format, minutes, seconds));
            }

            public void onFinish() {
                isTimerFinished = true;
                setSendAgainVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void setSendAgainVisibility(int visibility) {
        txt_timer.setVisibility((visibility == View.GONE)? View.GONE: View.VISIBLE);
        findViewById(R.id.sendAgain).setVisibility(visibility);
        findViewById(R.id.divid).setVisibility(visibility);
    }

    public void register(String code, String phone) {

        if (code.equals(G.preference.getInt("codesend", 0) + "")) {
            G.loading(OTPActivity.this);

            Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
            api.register("mobile,eq," + phone).enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    G.Log(call.request().toString());
                    assert response.body() != null;
                    try {

                        String result = response.body().string();
                        G.Log(result);
                        JSONObject object = G.StringtoJSONObject(result);
                        if (object.has("records")) {
                            JSONArray records = object.getJSONArray("records");
                            if (records.length() > 0) {
                                G.stop_loading();
                                PreferenceUtil.cashPhone(phone);
                                PreferenceUtil.cachLogin();
                                JSONObject info = records.getJSONObject(0);
                                String user_id = info.getString("id");
                                PreferenceUtil.cashUser_id(user_id);
                                PreferenceUtil.cashD_id("");
                                if (records.length() > 0 && PreferenceUtil.getUser_id().length() > 0) {
                                    getProfile(PreferenceUtil.getUser_id());
                                }
                            } else {
                                G.preference.edit().putBoolean("sendAgain", false).apply();
                                G.preference.edit().putBoolean("changeNumber", false).apply();
                                G.preference.edit().putString("phoneAgain", PreferenceUtil.getPhone()).apply();
                                Intent intent = new Intent(OTPActivity.this, ProfileActivity.class);
                                intent.putExtra("editPro", "");
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            G.toast("مشکل در سرور");
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
                    Toast.makeText(OTPActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            G.toast("کد اشتباه است");
        }
    }


    public void getProfile(String d_id) {
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getProfileByUser_id("id,eq," + d_id);
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                G.Log(call.request().toString());
                assert response.body() != null;
                try {
                    String result = response.body().string();
                    JSONObject object = G.StringtoJSONObject(result);
                    JSONArray records = object.getJSONArray("records");
                    if (records.length() > 0) {
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject obj = records.getJSONObject(i);



                            String user_id = obj.getString("id");
                            PreferenceUtil.cashUser_id(user_id);
                            String name = obj.getString("f_name");
                            String lastname = obj.getString("l_name");
                            String phone = obj.getString("mobile");
                            String sex = obj.getString("sex");
                            String b_date = obj.getString("birthdate");
                            String province = obj.getString("province_id");
                            String city = obj.getString("city_id");

                            boolean access =false;
                            if(obj.has("services_center")) {
                                JSONArray services_centers = obj.getJSONArray("services_center");
                                if(services_centers.length()>0){
                                    access  =true;
                                    JSONObject services_center = services_centers.getJSONObject(i);

                                    String d_id = services_center.getString("id");
                                    PreferenceUtil.cashD_id(d_id);
                                    String shop_name = services_center.getString("center_name");
                                    String shop_phone = services_center.getString("phone");
                                    String address = services_center.getString("address");
                                    String job_category = services_center.getString("job_category_id");
                                    String opentime = services_center.getString("f_open");
                                    String closetime = services_center.getString("f_close");
                                    String opentime2 = services_center.getString("l_open");
                                    String closetime2 = services_center.getString("l_close");
                                    int num_branch = services_center.getInt("numOfBranch");
                                    int waiting_space = services_center.getBoolean("waiting_place") ? 1 : 0;
                                    int catering_service = services_center.getBoolean("bar_serv") ? 1 : 0;
                                    boolean status = services_center.getBoolean("status");
                                    G.preference.edit().putBoolean("ServiceCenterStatus", status).apply();

                                    if (services_center.has("charge_total") && services_center.has("charge_remain")) {
                                        int charge_total = services_center.getInt("charge_total");
                                        int charge_remain = services_center.getInt("charge_remain");
                                        G.preference.edit().putInt("charge_total", charge_total).apply();
                                        G.preference.edit().putInt("charge_remain", charge_remain).apply();
                                    } else {
                                        G.preference.edit().putInt("charge_total", 0).apply();
                                        G.preference.edit().putInt("charge_remain", 0).apply();
                                    }

                                    PreferenceUtil.preferenceUtil.edit().putString("openTime", opentime).apply();
                                    PreferenceUtil.preferenceUtil.edit().putString("closeTime", closetime).apply();
                                    PreferenceUtil.preferenceUtil.edit().putString("openTime2", opentime2).apply();
                                    PreferenceUtil.preferenceUtil.edit().putString("closeTime2", closetime2).apply();
                                    PreferenceUtil.preferenceUtil.edit().putInt("numOfBranch", num_branch).apply();
                                    PreferenceUtil.preferenceUtil.edit().putInt("waiting", waiting_space).apply();
                                    PreferenceUtil.preferenceUtil.edit().putInt("catering", catering_service).apply();


                                    if (job_category.length() > 0) {
                                        try {
                                            int jobcat = Integer.parseInt(job_category);
                                            G.preference.edit().putInt("job_category_id", jobcat).apply();
                                        } catch (NumberFormatException nfe) {
                                            System.out.println("Could not parse " + nfe);
                                        }

                                    }

                                    PreferenceUtil.cashD_id(d_id);
                                    PreferenceUtil.cashPhone(phone);
                                    PreferenceUtil.cashInfo(name, lastname, address, shop_name);
                                    StateOpenHelper openHelper = new StateOpenHelper(G.Activity);
                                    openHelper.openDatabase();
                                    SQLiteDatabase mDatabase = openHelper.getReadableDatabase();
                                    if (province.length() > 0) {
                                        Cursor cursor = mDatabase.query("Tbl_Ostan", null, null, null, null, null, null);
                                        cursor.moveToFirst();
                                        List<String> stateName = new ArrayList<>();
                                        for (int x = 0; x < cursor.getCount(); x++) {
                                            State state = new State();
                                            state.setCityName(cursor.getString(cursor.getColumnIndex("Title")));
                                            state.setCityId(cursor.getInt(cursor.getColumnIndex("ID")));
                                            if ((state.getCityId() + "").equals(province)) {
                                                PreferenceUtil.cashOstan(Integer.parseInt(province), state.getCityName());
                                                PreferenceUtil.cashCity(Integer.parseInt(city), state.getCityName());
//                                            G.Log(state.getCityId() + " " + state.getCityName());
                                            }

                                            stateName.add(state.getCityName());
                                            cursor.moveToNext();
                                        }


                                    }
                                    if (city.length() > 0) {
                                        Cursor cursor = mDatabase.query("Tbl_shahrestan", null, "PK_Ostan = ?", new String[]{province}, null, null, null);
                                        cursor.moveToFirst();
                                        for (int x = 0; x < cursor.getCount(); x++) {
                                            State state = new State();
                                            state.setCityName(cursor.getString(cursor.getColumnIndex("Title")));
                                            state.setCityId(cursor.getInt(cursor.getColumnIndex("ID")));
                                            if ((state.getCityId() + "").equals(city)) {
                                                PreferenceUtil.cashCity(Integer.parseInt(city), state.getCityName());

//                                            G.Log(state.getCityId() + " " + state.getCityName());

                                            }
                                            cursor.moveToNext();
                                        }


                                    }
                                }
                            }
                            if(access){
                                G.stop_loading();
                                G.preference.edit().putBoolean("sendAgain", false).apply();
                                G.preference.edit().putBoolean("changeNumber", false).apply();
                                G.preference.edit().putString("phoneAgain", PreferenceUtil.getPhone()).apply();
                                startActivity(new Intent(OTPActivity.this, MainActivity.class));
                                finish();
                            }else{
                                G.preference.edit().putBoolean("sendAgain", false).apply();
                                G.preference.edit().putBoolean("changeNumber", false).apply();
                                G.preference.edit().putString("phoneAgain", PreferenceUtil.getPhone()).apply();
                                Intent intent = new Intent(OTPActivity.this, ProfileActivity.class);
                                intent.putExtra("editPro", "");
                                startActivity(intent);
                                finish();
                            }



                        }
                    } else {
                        G.toast("مشکل در دریافت اطلاعات");
                    }
                } catch (JSONException | IOException e) {
                    G.toast("مشکل در تجزیه اطلاعات");
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }

    private void FindView() {
        txt_sms_text = findViewById(R.id.txt_sms_text);
        txt_sms_t = findViewById(R.id.txt_sms_t);
        otp_view = findViewById(R.id.otp_view);
        txt_timer = findViewById(R.id.txt_timer);
        license = findViewById(R.id.license);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context));
    }
}