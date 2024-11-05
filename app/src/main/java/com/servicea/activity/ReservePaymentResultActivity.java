package com.servicea.activity;


import static com.servicea.app.Constants.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.servicea.activities.ReserveActivity;
import com.servicea.app.Constants;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.dbModel.ModelProduceGroup;
import com.servicea.model.dbModel.ReserveModel;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import ir.servicea.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ReservePaymentResultActivity extends AppCompatActivity {

    private TextView txt_tile_action_bar, txt_payment_result, txt_reserve_product_group, txt_reserve_result_message;
    private Button btn_map_direction, btn_change_reserve_time;
    private ImageView iv_reserve_payment_result, img_back;
    private boolean paymentResult = false;
    private boolean changeReserveTime = false;
    String service = "";
    Api api;
    public int sendid = 0;
    private boolean isReserved = false;
    private static final String TAG = "ReservePaymentResultAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_payment_result);
        FindViews();
        onClick();
        getPaymentResultFromIntent();
    }

    private void onClick() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ReserveModel reserveModel = G.getReserveModel();
        btn_map_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "geo:" + reserveModel.getService_center_lat() + "," + reserveModel.getService_center_lng();
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
            }
        });

    }

    private void getReserveInformationFromPreference() {
       /* String customerCarId = G.preference.getString(Constants.customer_car_id, "0");
        String serviceCenterId = String.valueOf(G.preference.getInt(Constants.service_center_id, 0));
        String description = G.preference.getString(Constants.reserve_description, "");
        String paymentAmount = G.preference.getString(Constants.reserve_payment_amount, "0");
        String jobCategoryId = String.valueOf(G.preference.getInt(Constants.job_category_id, 0));
        String requestReserve = G.preference.getString(Constants.reserve_request_reserve, "0");*/
        ReserveModel reserveModel = G.getReserveModel();
        String customerCarId = String.valueOf(reserveModel.getCar_id());
        String serviceCenterId = String.valueOf(reserveModel.getService_center_id());
        String description = reserveModel.getDescription();
        String paymentAmount = reserveModel.getFinal_cost();
        String jobCategoryId = String.valueOf(reserveModel.getJob_category_id());
        String requestReserve = reserveModel.getReserve_request();
        String reserveDateTime = reserveModel.getSelectedDate();
        String reserveEndTime = reserveModel.getEnd_time();
        Log.d(TAG, "getReserveInformationFromPreference: " + reserveDateTime);
        String paid = (paymentResult ? "1" : "0");
        String user_id = PreferenceUtil.getUser_id();
        checkTimeIsReserved(reserveDateTime, service_center_id, new CheckTimeIsReservedCallback() {
            @Override
            public void onTimeCheckCompleted(boolean isReserved) {
                String productGroupsStr = "";

                if (isReserved) {
                    changeReserveTime = true;
                    Log.d(TAG, "getReserveInformationFromPreference: time is reserved");
                    addServiceRequest(customerCarId, user_id, serviceCenterId, description, paymentAmount, "3", paid, jobCategoryId, "0", requestReserve, reserveDateTime);

                } else {
                    changeReserveTime = false;
                    btn_map_direction.setVisibility(View.VISIBLE);
                    Log.d(TAG, "getReserveInformationFromPreference: time is not reserved");
                    addServiceRequest(customerCarId, user_id, serviceCenterId, description, paymentAmount, "1", paid, jobCategoryId, "0", requestReserve, reserveDateTime);
                    List<ModelProduceGroup> produceGroups = G.getReserveProductGroups();
                    G.Log("produceGroups" + produceGroups.toString());
                    for (int i = 0; i < produceGroups.size(); i++) {
                        productGroupsStr += produceGroups.get(i).getTitle();
                        if (i != produceGroups.size() - 1) {
                            productGroupsStr += " - ";
                        }
                    }
                    G.Log("productGroupsStr" + productGroupsStr);

                    txt_reserve_product_group.setText(productGroupsStr);
                    txt_reserve_product_group.setTypeface(G.Bold);

//        ReserveModel reserveInformation = G.getReserveModel();
                    G.Log("reserveInformation" + reserveModel.getService_center_name());
                    String service_center_address = reserveModel.getService_center_address();
                    int lastIndexOfAddress = reserveModel.getService_center_address().lastIndexOf('-');
                    service_center_address = service_center_address.substring(0, lastIndexOfAddress);
                    txt_reserve_result_message.setText(reserveModel.getService_center_name() + " " + "در تاریخ " + reserveModel.getDate() + " " + "ساعت " + reserveModel.getStart_time() + "الی " + reserveModel.getEnd_time() + " آماده ارائه خدمات فوق در مرکز به آدرس " + service_center_address + " می باشد.");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        txt_reserve_result_message.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
                    }
                }
            }
        });
    }

    private void ShowChangeReserveDataButton(String service_request_id) {
        btn_change_reserve_time.setVisibility(View.VISIBLE);
        btn_change_reserve_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReservePaymentResultActivity.this, ReserveActivity.class);
                intent.putExtra(UPDATE_RESERVE_TIME, true);
                intent.putExtra(RESERVED_SERVICE_ID, service_request_id);
                startActivity(intent);
                finish();
            }
        });
        Snackbar.make(btn_change_reserve_time, R.string.reserved_time_message, Snackbar.LENGTH_LONG)
                .setAction("متوجه شدم", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();

    }


    private void getPaymentResultFromIntent() {
        Intent intentFromMainActivity = getIntent();
        paymentResult = intentFromMainActivity.getBooleanExtra(Constants.reserve_payment_result, false);
        if (paymentResult) {
            getReserveInformationFromPreference();
            iv_reserve_payment_result.setImageDrawable(getResources().getDrawable(R.drawable.success_payment));
            txt_payment_result.setText(R.string.payment_success_text);
            txt_payment_result.setTextColor(getResources().getColor(R.color.success_color));
        } else {
            //getReserveInformationFromPreference();
            iv_reserve_payment_result.setImageDrawable(getResources().getDrawable(R.drawable.failed_payment));
            txt_reserve_product_group.setVisibility(View.GONE);
            btn_map_direction.setVisibility(View.GONE);
            txt_payment_result.setText(R.string.payment_failed_text);
            txt_reserve_result_message.setText("رزرو با موفقیت انجام نشد!\n برای رزرو مجددا اقدام کنید");
            // txt_reserve_result_message.setTextColor(getResources().getColor(R.color.failed_color));
            txt_payment_result.setTextColor(getResources().getColor(R.color.failed_color));
            txt_payment_result.setTypeface(G.Bold);
        }
    }

    private void FindViews() {
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        txt_tile_action_bar.setText(R.string.payment_confirmation);
        txt_tile_action_bar.setTypeface(G.Bold);
        img_back = findViewById(R.id.img_back);

        RelativeLayout rv_toolbar_alarms = findViewById(R.id.alarms);
        rv_toolbar_alarms.setVisibility(View.INVISIBLE);

        txt_payment_result = findViewById(R.id.txt_payment_result);
        txt_payment_result.setTypeface(G.Bold);

        txt_reserve_result_message = findViewById(R.id.txt_reserve_result_message);
        txt_reserve_result_message.setTypeface(G.Normal);

        txt_reserve_product_group = findViewById(R.id.txt_reserve_product_groups);
        txt_reserve_product_group.setTypeface(G.Bold);

        btn_map_direction = findViewById(R.id.btn_map_direction);
        btn_map_direction.setTypeface(G.Bold);

        btn_change_reserve_time = findViewById(R.id.btn_change_reserve_time);
        btn_change_reserve_time.setTypeface(G.Bold);

        iv_reserve_payment_result = findViewById(R.id.iv_reserve_payment_result);
    }

    public interface CheckTimeIsReservedCallback {
        void onTimeCheckCompleted(boolean isReserved);
    }

    public void checkTimeIsReserved(String reserveTime, String service_center_id, CheckTimeIsReservedCallback callback) {
        //G.loading(rv_calendar.getContext());
        isReserved = false;
        String d_id = PreferenceUtil.getD_id();
        String detail_serviceS = G.preference.getString("detail_service", "[]");
        G.Log("ReserveActivity::" + detail_serviceS);
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        try {
            String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());

            JSONObject objx = new JSONObject();
            objx.put("service_center_id", service_center_id);
            Log.d("ReservePaymentResult", "reserveTime:" + reserveTime);

            objx.put("reserve_time", reserveTime);
            G.Log("ReserveActivity:object:" + objx);


            Call<ResponseBody> request = api.checkTimeIsReserved(G.returnBody(objx.toString()));
            G.Log("ReserveActivity: request" + request.request());
            request.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    G.Log(call.request().toString());
                    String result = G.getResult(response);
                    G.Log("ReserveActivity:result:" + result);
                    Log.d("ReserveActivity", "result: " + result);
                    if (result.length() > 0) {
                        try {
                            JSONObject timeIsReserved = new JSONObject(result);
                            if (timeIsReserved.has(Constants.IS_RESERVED)) {
                                isReserved = timeIsReserved.getBoolean(Constants.IS_RESERVED);
                                callback.onTimeCheckCompleted(isReserved);

                            }
                            Log.d("ReserveActivity", "onResponse: " + isReserved);

                        } catch (JSONException e) {
                            G.stop_loading();
                            Log.d("ReserveActivity", "JSONException: " + e.getMessage());
                            throw new RuntimeException(e);
                        }

                    } else {
                        G.stop_loading();
                        // finish();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    G.stop_loading();
                    G.toast("مشکل در برقراری ارتباط با سرور");
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addServiceRequest(String customer_car_id, String user_id, String service_center_id, String description, String prepayment_amount, String status, String paid, String job_category_id, String counter, String request_reserve, String reserve_date_time) {

        G.loading(this);
        List<ModelProduceGroup> modelKhadamats = G.getReserveProductGroups();
        String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());
        String request_date_time = created_at;
     /*   String dateTime = "";
         dateTime = dateTime.replace("/", "-");
        if (dateTime.contains("-")) {
            CalendarTool calendarTool = new CalendarTool();
            String[] dates = dateTime.split("-");
            calendarTool.setIranianDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
            dateTime = calendarTool.getGregorianDate();

        }*/
        //List<ModelSaveKhadamat> msks = mDBHelper.getListsave_khadamat(mDatabase, mDBHelper.getLastIdService(mDatabase) + 1);

        JSONArray array = new JSONArray();
        for (int c = 0; c < modelKhadamats.size(); c++) {
            JSONObject object = new JSONObject();
            ModelProduceGroup model = modelKhadamats.get(c);
            try {
                object.put("id", model.getId());
                object.put("name", model.getTitle());
                /* object.put("created_at", created_at);
                object.put("updated_at", created_at);
                object.put("send_msg", model.isSend_msg());*/
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        service = array.toString();

//      String car_id = getIntent().getExtras().getString("id_car");
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        JSONObject object = new JSONObject();
        try {
            // object.put("service_center_id", d_id);
            object.put("customer_car_id", customer_car_id);
            object.put("service_center_id", service_center_id);
            object.put("user_id", user_id);
            object.put("request_date_time", created_at);
            object.put("reserve_date_time", reserve_date_time);
            object.put("description", description);
            object.put("prepayment_amount", prepayment_amount);
            object.put("detail_service", service);
            object.put("status", "1");
            object.put("paid", paid);
            object.put("job_category_id", job_category_id);
            object.put("counter", counter);
//            object.put("request_reserve", request_reserve);
            object.put("created_at", created_at);
            object.put("updated_at", created_at);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<ResponseBody> request = api.addServiceRequest(G.returnBody(object.toString()));
        Log.d("GuidCar_ArenaTeam_", "addServiceRequest: " + request.request());
        Log.d("GuidCar_ArenaTeam_", "addServiceRequest: object" + object);
        Log.d("GuidCar_ArenaTeam_", "addServiceRequest: object2" + G.returnBody(object.toString()));
      /*  if (service_id.length() > 0) {
            idService = service_id;
            request = api.updateService(service_id, G.returnBody(object.toString()));
        } else {
            idService = "";
        }
*/
        String[] dateTimeArray = reserve_date_time.split(" ");
        String notificationDateTime = dateTimeArray[0] + " " + dateTimeArray[1];
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d("GuidCar_ArenaTeam_", "addService: response" + response);
                String result = G.getResult(response);
                Log.d("GuidCar_ArenaTeam_", "addService: result" + result);

                if (result.length() > 0 && result.length() < 10) {
                    Log.d(TAG, "onResponse: success");
                    addServicesDetail(result);
                    addNotificationDetails(user_id, notificationDateTime, job_category_id, result, created_at);
                    if(isReserved){
                        ShowChangeReserveDataButton(result);
                    }
                } else {
                    G.toast("مشکل در ثبت اطلاعات");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });
    }

    private void addNotificationDetails(String userId, String reserveDateTime, String jobCategoryId, String serviceRequestId, String createdAt) {
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        JSONObject object = new JSONObject();

        try {
            object.put(USER_TYPE, G.preference.getInt(USER_TYPE, 2));
            object.put(DATE_TIME, reserveDateTime);
            object.put(STATUS, 0);
            object.put(SMS_NOTIFICATION, 2);
            object.put(RECEIVER_ID, Integer.parseInt(userId));
            object.put(JOB_CATEGORY_ID, Integer.parseInt(jobCategoryId));
            object.put(PROVINCE_ID, Integer.parseInt(G.preference.getString(PROVINCE_ID, "1")));
            object.put(CITY_ID, Integer.parseInt(G.preference.getString(CITY_ID, "1")));
            object.put(NOTIFICATION_PROV_ID, Constants.RESERVE_NOTIFICATION_PROV_ID);
            object.put(USER_ID, Integer.parseInt(userId));
            object.put(SERVICE_REQUEST_ID, serviceRequestId);
            object.put(CREATED_AT, createdAt);
            object.put(UPDATED_AT, createdAt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<ResponseBody> request = api.addNotificationDetails(G.returnBody(object.toString()));
        Log.d("GuidCar_ArenaTeam_", "addNotificationDetails: " + request.request());
        Log.d("GuidCar_ArenaTeam_", "addNotificationDetails: object" + object);
        Log.d("GuidCar_ArenaTeam_", "addNotificationDetails: object2" + G.returnBody(object.toString()));
      /*  if (service_id.length() > 0) {
            idService = service_id;
            request = api.updateService(service_id, G.returnBody(object.toString()));
        } else {
            idService = "";
        }
      */

        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d("GuidCar_ArenaTeam_", "addNotificationDetails: response" + response);
                String result = G.getResult(response);
                Log.d("GuidCar_ArenaTeam_", "addNotificationDetails: result" + result);
                if (result.length() > 0 && result.length() < 10) {
                    Log.d(TAG, "onResponse: success");
                    Log.d("GuidCar_ArenaTeam_", "addNotificationDetails: onResponse:success");
                } else {
                    G.toast("مشکل در ثبت اطلاعات");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });

    }


    public void addServicesDetail(String service_id) {
        //G.loading(this);
        String d_id = PreferenceUtil.getD_id();
        // String detail_serviceS = G.preference.getString("detail_service", "[]");
        G.Log("addServicesDetail:detail_serviceS::" + service);
        api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);

        try {
            JSONArray array = new JSONArray(service);
            String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());
            if (array.length() > 0 && sendid < array.length()) {
                JSONObject object = array.getJSONObject(sendid);
                String product_group_id = (object.getString("id"));

                JSONObject objx = new JSONObject();
                objx.put("service_request_id", service_id);
                objx.put("product_group_id", product_group_id);

                objx.put("created_at", created_at);
                objx.put("updated_at", created_at);
                objx.put("deleted_at", null);

                G.Log("add service request detail: " + objx);
                Call<ResponseBody> request = api.addServiceRequestDetail(G.returnBody(objx.toString()));
                G.Log("add services request detail: request" + request.request());
                request.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        G.Log(call.request().toString());
                        String result = G.getResult(response);
                        if (result.length() > 0 && result.length() < 10) {
                            sendid++;
                            addServicesDetail(service_id);
                        } else {
                            G.stop_loading();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        G.stop_loading();
                        G.toast("مشکل در برقراری ارتباط با سرور");
                    }
                });


            } else {
                G.stop_loading();
                G.toast("سرویس با موفقیت ثبت شد");
                /*if (send_msg_prov) {
                    G.sendSMSProv(txt_phone_customer.getText().toString(), G.PROV_ADD_Service);
                }*/
                // getProductGroupChangeVages();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}