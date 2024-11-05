package com.servicea.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.servicea.activities.AlarmsActivity;
import com.servicea.activities.CustomerActivity;
import com.servicea.activities.MainActivity;
import com.servicea.activities.ServiceCenterActivity;
import com.servicea.adapter.AdapterFilterRadioButton;
import com.servicea.adapter.AdapterJobCategory;
import com.servicea.adapter.AdapterListDetectGroup;
import com.servicea.adapter.AdapterListProduceGroup;
import com.servicea.adapter.AdapterReserveProductGroup;
import com.servicea.app.CalendarTool;
import com.servicea.app.Constants;
import com.servicea.app.DataBaseHelper;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.app.RecyclerItemClickListener;
import com.servicea.model.ModelJobCategory;
import com.servicea.model.ModelML;
import com.servicea.model.ModelProduct;
import com.servicea.model.SliderItem;
import com.servicea.model.dbModel.ModelCustomer;
import com.servicea.model.dbModel.ModelKhadamat;
import com.servicea.model.dbModel.ModelProduceGroup;
import com.servicea.model.dbModel.ModelSaveKhadamat;
import com.servicea.model.dbModel.ReserveModel;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.servicea.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ReserveProductGroupActivity extends AppCompatActivity {
    private TextView txt_tile_action_bar, txt_product_cost, txt_rial, txt_product_cost_message, txt_final_cost, txt_choose_product_group;
    private EditText edt_takhfif;
    private ImageView img_back, img_add_message;
    private RecyclerView recycle_produce_group;
    public static List<ModelProduceGroup> listGroup = new ArrayList<>();

    // public static List<ModelSaveKhadamat> save = new ArrayList<>();
    private AdapterReserveProductGroup adapterReserveProductGroup;
    private CheckBox checkAll;
    private boolean check = false;

    private Button btn_save;
    private AlertDialog alertDialogs_offer_group;
    private DataBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    String onvan = "";
    private AdapterReserveProductGroup.OnItemClicked onItemClickListener;

    private Handler handler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner spinner_job;
    private boolean fromrefresh = false;
    private EditText search;
    private RecyclerView recycle_done_service_type;
    List<ModelProduceGroup> modelKhadamats = new ArrayList<>();

    private int finalCost = 0;

    String service = "";
    Api api;
    public int sendid = 0;

    int changeVages = 5000;

    private String selectedDate = "";
    private int jobCategoryId = 1;
    private int serviceCenterId = 1;

    private ProgressBar pb_loading;

    private static final String TAG = "ReserveProductGroupActi";

    public void onclickAlamrs(View v) {
        startActivity(new Intent(ReserveProductGroupActivity.this, AlarmsActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        G.preference.edit().putInt("CasheSelectedJobCategory", -1).apply();
        G.Activity = this;
        G.context = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_product_group);
        G.Activity = this;
        G.context = this;
        onItemClickListener = new AdapterReserveProductGroup.OnItemClicked() {
            @Override
            public void onCheckedChanged(ModelProduceGroup jobCategory, boolean isChecked) {
                if (isChecked) {
                    if (!modelKhadamats.contains(jobCategory)) {
                        modelKhadamats.add(jobCategory);
                    }
                } else {
                    if (modelKhadamats.contains(jobCategory)) {
                        modelKhadamats.remove(jobCategory);
                    }
                }
                updateChangeWage();
                // Log.d(TAG, "onCheckedChanged: "+modelKhadamats.toString());

            }
        };
        FindView();
        startLoading();


        onClick();
        getReserveInformationFromIntent();


        txt_tile_action_bar.setText("انتخاب خدمات");
        txt_tile_action_bar.setTypeface(G.Bold);
        selectedDate = getIntent().getStringExtra("selected_reserve_time");
        jobCategoryId = getIntent().getIntExtra(Constants.job_category_id, 1);
        serviceCenterId = getIntent().getIntExtra(Constants.service_center_id, 1);

        //   getServicesServiceCenter("1");
        getJob_services("", jobCategoryId);


    }

    private void getReserveInformationFromIntent() {
      /*  Intent intentFromReserveActivity = getIntent();
        jobCategoryId = intentFromReserveActivity.getIntExtra(Constants.customer_car_id,0);
        jobCategoryId = intentFromReserveActivity.getIntExtra(Constants.job_category_id,0);
        serviceCenterId = intentFromReserveActivity.getIntExtra(Constants.service_center_id,0);
        selectedDate = intentFromReserveActivity.getStringExtra(Constants.selected_reserve_time);*/
        ReserveModel reserveModel = G.getReserveModel();
        jobCategoryId = reserveModel.getCar_id();
        jobCategoryId = reserveModel.getJob_category_id();
        serviceCenterId = reserveModel.getService_center_id();
        selectedDate = reserveModel.getSelectedDate();
    }

    private List<String> listJobs = new ArrayList<>();
    private List<Integer> listJobsIds = new ArrayList<>();
    private ArrayAdapter spinnerAdapter;

    public class PG {
        public PG() {
        }

        public int id;
        public boolean status;
    }

    private List<PG> enables = new ArrayList<>();


    private void FindView() {
        spinner_job = findViewById(R.id.spinner_job);
       /* ImageView img_add_message = findViewById(R.id.img_add_message);
        img_add_message.setVisibility(View.INVISIBLE);*/

        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        txt_choose_product_group = findViewById(R.id.txt_choose_product_group);
        txt_choose_product_group.setTypeface(G.Normal);

        txt_product_cost = findViewById(R.id.txt_product_cost);
        txt_product_cost.setTypeface(G.Bold);

        txt_rial = findViewById(R.id.txt_rial);
        txt_rial.setTypeface(G.Normal);

        txt_final_cost = findViewById(R.id.txt_final_cost);
        txt_final_cost.setTypeface(G.Bold);

        txt_product_cost_message = findViewById(R.id.txt_product_cost_message);
        txt_product_cost_message.setTypeface(G.Normal);

        edt_takhfif = findViewById(R.id.edt_takhfif);
        edt_takhfif.setTypeface(G.Normal);

        img_back = findViewById(R.id.img_back);
        //swipeRefreshLayout = findViewById(R.id.swipe);

        btn_save = findViewById(R.id.btn_save);
        btn_save.setTypeface(G.Bold);

        recycle_produce_group = findViewById(R.id.recycle_produce_group);
        adapterReserveProductGroup = new AdapterReserveProductGroup(listGroup, onItemClickListener);
        recycle_produce_group.setLayoutManager(new GridLayoutManager(this, 2));
        recycle_produce_group.setAdapter(adapterReserveProductGroup);
        pb_loading = findViewById(R.id.pb_loading);
    }

    private void onClick() {
        img_back.setOnClickListener(view -> finish());
        btn_save.setOnClickListener(view -> {
            /*G.stop_loading();
            G.toast("ثبت انجام شد");
            finish();*/
            // saveServiceDetail();
            // getProductGroupChangeVages();
            //updateChangeWage();
            G.saveReserveInformation("1", serviceCenterId, "", String.valueOf(finalCost), jobCategoryId, "");
            ReserveModel reserveModel = G.getReserveModel();
            reserveModel.setFinal_cost(String.valueOf(finalCost));
            G.saveReserveModel(reserveModel);
            if (G.preference.contains(Constants.reserve_product_group))
                G.preference.edit().remove(Constants.reserve_product_group).apply();
            G.saveReserveProductGroups(modelKhadamats);
            //addServiceRequest();
            //   G.preference.edit().putString(Constants.reserve_product_group,modelKhadamats.toString()).apply();

            if (finalCost == 0) {
                Toast.makeText(ReserveProductGroupActivity.this, "حداقل یک گروه شغلی را انتخاب نمایید", Toast.LENGTH_SHORT).show();
            } else {
                requestPayment(finalCost);
            }
        });
    }

    private void updateChangeWage() {
        int cost = 0;
        for (int i = 0; i < modelKhadamats.size(); i++) {
            cost += modelKhadamats.get(i).getChange_wage();
        }
        txt_final_cost.setText(cost + "");
        finalCost = cost;
    }


    public String ids_ServiceAvailable = "";

    public void listServiceAvailable() {

        // G.loading(this);
        ids_ServiceAvailable = "";
        String d_id = PreferenceUtil.getD_id();
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.listProductGroupAvailable("service_center_id,eq," + 1);
        G.Log(request.request().toString());

        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                stopLoading();
                G.stop_loading();
                try {
                    G.Log("ReserveProductGroupActivity:" + call.request().toString());
                    String result = G.getResult(response);
                    G.Log("ReserveProductGroupActivity:" + result);

                    JSONObject object = G.StringtoJSONObject(result);
                    JSONArray records = object.getJSONArray("records");
                    if (records.length() > 0) {
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject obj = records.getJSONObject(i);
                            int job_id = obj.getInt("id");
                            int product_group_id = obj.getInt("product_group_id");
                            //String product_group_title = obj.getString("title");
                            ids_ServiceAvailable += "" + job_id + ",";
                            for (int j = 0; j < listGroup.size(); j++) {
                                if ((listGroup.get(j).getId() + "").equals(obj.getString("product_group_id"))) {
                                    boolean status = obj.getInt("status") == 1;
                                    if (!status) {
                                        listGroup.remove(j);
                                    }
                                    /*listGroup.get(j).setCheck(status);
                                    listGroup.get(j).setJob_id(job_id);*/
                                }
                                /*else{
                                    ModelProduceGroup modelProduceGroup = new ModelProduceGroup();
                                    modelProduceGroup.setId(product_group_id);
                                    modelProduceGroup.setTitle(product_group_title);
                                    listGroup.add(modelProduceGroup);
                                }
*/
                            }
                        }
//                        ids_ServiceAvailable = ids_ServiceAvailable.substring(0,ids_ServiceAvailable.length()-1);
                        ids_ServiceAvailable = ids_ServiceAvailable.replace(",,", "");
                        adapterReserveProductGroup.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    G.toast("مشکل در تجزیه اطلاعات");
                    e.printStackTrace();
                }
                //G.stop_loading();

                //swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                //swipeRefreshLayout.setRefreshing(false);
                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }

    public void getJob_services(String key, int job_category_id) {
        if (job_category_id == 0) {
            job_category_id = G.preference.getInt("job_category_id", 1);
        }
        String where = "eq";
        if (job_category_id == -1) {
            where = "gt";
        }
        String d_id = PreferenceUtil.getD_id();
        G.Log(G.preference.getString("city_id", "0"));

        int city_id = 1;
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getProduct_groupsByCityId(job_category_id, city_id);
        //Call<ResponseBody> request = api.getProduct_groups("job_category_id," + where + "," + job_category_id);

        G.Log("ReserveProductGroupActivity:getJobS:request:" + request.request().toString());

       /* if (key.length() > 0) {
            swipeRefreshLayout.setRefreshing(true);
            request = api.getProduct_groupsBySearch("job_category_id," + where + "," + job_category_id, "title,cs," + key);

        } else {
            G.loading(this);
        }*/

        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                assert response.body() != null;
                // G.loading(ReserveProductGroupActivity.this);

                listGroup.clear();
                try {
                    String result = response.body().string();
                    G.Log("ReserveProductGroupActivity:getJobS:result:" + result);

                    JSONArray object = G.StringtoJSONArray(result);
                    if (object != null) {
                        if (object.length() > 0) {
                            List<SliderItem> sliderItemList = new ArrayList<>();

                            for (int i = 0; i < object.length(); i++) {
                                JSONObject obj = object.getJSONObject(i);
                                SliderItem sliderItem = new SliderItem();
                                int id = obj.getInt("id");
                                String km_usagex = obj.getString("km_usage") + "";
                                int km_usage = 0;
                                if (!km_usagex.contains("null")) {
                                    km_usage = obj.getInt("km_usage");
                                }
                                String title = obj.getString("title");
                                String send_msgx = (obj.getString("send_msg") + "").replace("null", "0");
                                String change_wage = (obj.getString("change_wage") + "").replace("null", "0");
                                int change_wage_percent = obj.getInt("percent");
                                int final_change_wage = 0;
                                if (change_wage.length() > 0) {
                                    final_change_wage = (Integer.parseInt(change_wage) * change_wage_percent)/100 + Integer.parseInt(change_wage);
                                }
                                boolean send_msg = send_msgx.contains("1");
//                                check = checkProductGroup(id);
                                check = false;
                                ModelProduceGroup modelProduceGroup = new ModelProduceGroup();
                                modelProduceGroup.setTitle(title);
                                modelProduceGroup.setId(id);
                                modelProduceGroup.setChange_wage(final_change_wage);
                                listGroup.add(modelProduceGroup);


                            }


                            listServiceAvailable();

                        } else {
                            G.stop_loading();
                            G.toast("هیچ گروه کالا\u200Cای در این دسته شغلی یافت نشد");
                        }
                    }
                } catch (JSONException | IOException e) {
                    G.stop_loading();
                    G.toast("مشکل در تجزیه اطلاعات");
                    e.printStackTrace();
                    G.Log("Reserve:" + e.getMessage());
                }
             /*   adapterReserveProductGroup = new AdapterReserveProductGroup(listGroup, onItemClickListener);
//        adapterListProduceGroup = new AdapterListProduceGroup(ProductGroupActivity.this, mDBHelper.getListProductGroup(mDatabase));
                recycle_produce_group.setAdapter(adapterReserveProductGroup);*/
//                swipeRefreshLayout.setRefreshing(false);

                adapterReserveProductGroup.notifyDataSetChanged();
                fromrefresh = false;


            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                fromrefresh = false;
                //swipeRefreshLayout.setRefreshing(false);
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }

    public int countxx = 0;

    public void addServiceRequest(
            String customer_car_id, String service_center_id
            , String description, String prepayment_amount
            , String status, String paid, String job_category_id, String counter, String request_reserve) {

        String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());
        String request_date_time = created_at;
        String reserve_date_time = created_at;
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
                object.put("product_group_id", model.getId());
               /* object.put("created_at", created_at);
                object.put("updated_at", created_at);
                object.put("send_msg", model.isSend_msg());*/
                array.put(object);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        service = array.toString();


//        String car_id = getIntent().getExtras().getString("id_car");
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        JSONObject object = new JSONObject();
        try {
            // object.put("service_center_id", d_id);
            object.put("customer_car_id", customer_car_id);
            object.put("service_center_id", service_center_id);
            object.put("request_date_time", created_at);
            object.put("reserve_date_time", reserve_date_time);
            object.put("description", description);
            object.put("prepayment_amount", prepayment_amount);
            object.put("detail_service", service);
            object.put("status", 1);
            object.put("paid", paid);
            object.put("job_category_id", job_category_id);
            object.put("counter", counter);
            object.put("request_reserve", request_reserve);
            object.put("created_at", created_at);
            object.put("updated_at", created_at);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<ResponseBody> request = api.addServiceRequest(G.returnBody(object.toString()));
        Log.d("GuidCar_ArenaTeam_", "addServiceRequest: " + request.request());
        Log.d("GuidCar_ArenaTeam_", "addServiceRequest: object" + object.toString());
        Log.d("GuidCar_ArenaTeam_", "addServiceRequest: object2" + G.returnBody(object.toString()));
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
                String result = G.getResult(response);
                Log.d("GuidCar_ArenaTeam_", "addService: result" + result);

                if (result.length() > 0 && result.length() < 10) {
                    Log.d(TAG, "onResponse: success");
                    addServicesDetail(result);
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
                String product_group_id = (object.getString("product_group_id"));

                JSONObject objx = new JSONObject();
                objx.put("service_request_id", service_id);
                objx.put("product_group_id", product_group_id);

                objx.put("created_at", created_at);
                objx.put("updated_at", created_at);
                objx.put("deleted_at", null);

                G.Log("add service request detail: " + objx.toString());
                Call<ResponseBody> request = api.addServiceRequestDetail(G.returnBody(objx.toString()));
                G.Log("add services request detail: request" + request.request().toString());
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
                G.preference.edit().putInt("AvgKm", 0).apply();
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

/*
    private void getProductGroupChangeVages(){
        String city_id = G.preference.getString("city_id", "1");

        String[] product_groups = new String[modelKhadamats.size()];
        for (int i = 0; i < modelKhadamats.size(); i++) {
            product_groups[i] = String.valueOf(modelKhadamats.get(i).getId());
            G.Log("getProductGroupChangeVages: product_groups[i]:" + product_groups[i]);

        }
        G.Log("getProductGroupChangeVages: product_groups" + Arrays.toString(product_groups));
        String filter = "product_group_id,in," + Arrays.toString(product_groups);
        G.Log("getProductGroupChangeVages: filter:" + filter);

        api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);

        Call<ResponseBody> request = api.getChangeVage(filter, "city_id,eq," + city_id);
        G.Log("getProductGroupChangeVages: request" + request.request().toString());
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                G.Log(call.request().toString());
                String result = G.getResult(response);
                G.Log("getProductGroupChangeVages: result" + result);

                try {
                    JSONObject object = G.StringtoJSONObject(result);
                    JSONArray records = object.getJSONArray("records");
                    if (records.length() > 0) {
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject obj = records.getJSONObject(i);
                            changeVages += Integer.parseInt(obj.getString("change_vage"));
                        }
                        Log.d(TAG, "onResponse: changeVages"+ changeVages);
                        txt_final_cost.setText(changeVages+"");
                        if (changeVages > 0) {
                            G.toast("لطفأ صبر کنید...");
                            requestPayment(changeVages);
                        }

                    } else {
                        G.toast("هیچ موردی یافت نشد!");
                    }
                } catch (JSONException e) {
                    G.toast("مشکل در تجزیه اطلاعات");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط با سرور");
            }
        });
        txt_final_cost.setText(changeVages+"");
        if (changeVages > 0) {
            G.toast("لطفأ صبر کنید...");
            requestPayment(changeVages);
        }
    }
*/

    private void requestPayment(int amount) {
//        try {
//            ZarinPal purchase = ZarinPal.getPurchase(G.context);
//            PaymentRequest payment = ZarinPal.getPaymentRequest();
//            payment.setMerchantID(G.MerchantID);
//            payment.setAmount(amount);
//            payment.setDescription(Constants.reserve_service);
//            payment.setCallbackURL("paymentzarrindriver://app");
//            payment.setMobile(G.preference.getString("phone_user", ""));
//            //payment.setEmail("imannamix@gmail.com");
//            purchase.startPayment(payment, new OnCallbackRequestPaymentListener() {
//                @Override
//                public void onCallbackResultPaymentRequest(int status, String authority, Uri paymentGatewayUri, Intent intent) {
//                    if (status == 100) {
//                        try {
//                            intent.putExtra("reserve_payment_status", true);
//                            startActivity(intent);
//                        } catch (Exception e) {
//                            G.toast("مرورگری برای پرداخت یافت نشد!");
//                        }
//                    } else {
//                        G.toast("پرداخت آنلاین به مشکل برخورده");
//                        intent.putExtra("reserve_payment_status", false);
//                        startActivity(intent);
//                    }
//
//                }
//            });
//        } catch (Exception e) {
//            G.toast("مشکل در درگاه پرداخت!!");
//        }

    }


    private void saveServiceDetail() {
        String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());

        JSONArray array = new JSONArray();
        for (int c = 0; c < modelKhadamats.size(); c++) {
            JSONObject object = new JSONObject();
            ModelProduceGroup model = modelKhadamats.get(c);
            try {
                object.put("product_group_id", model.getId());
               /* object.put("created_at", created_at);
                object.put("updated_at", created_at);
                object.put("send_msg", model.isSend_msg());*/
                array.put(object);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        //G.preference.edit().putString("detail_service", array.toString()).apply();

        Log.d(TAG, "saveServiceDetail: " + array.toString());


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onclickBack(View view) {
        finish();
    }

    private void startLoading() {
        pb_loading.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        pb_loading.setVisibility(View.INVISIBLE);
    }
}