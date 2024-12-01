package com.servicea.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.servicea.activities.AddNewCarActivity;
import com.servicea.activities.AlarmsActivity;
import com.servicea.activities.CarListActivity;
import com.servicea.adapter.AdapterDoneServiceType;
import com.servicea.app.CalendarTool;
import com.servicea.app.Constants;
import com.servicea.app.DataBaseHelper;
import com.servicea.app.G;
import com.servicea.app.MyNumberWatcher;
import com.servicea.app.PLakUtils;
import com.servicea.app.PreferenceUtil;
import com.servicea.app.Utils;
import com.servicea.model.ModelServicesCustomer;
import com.servicea.model.dbModel.ModelSaveKhadamat;
import com.servicea.model.dbModel.ModelServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;
import ir.servicea.R;

import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import com.servicea.app.Constants.PLAK_TYPE;

public class AddServicesActivity extends AppCompatActivity {
    private TextView txt_tile_action_bar, txt_phone_customer, txt_name_car, txt_name_customer, btn_date_service;
    public TextView txt_edit_info, txt_late_service, txt_type_customer;
    private ImageView img_back;
    private Button btn_save_service, btn_cancle, btn_choose_service;
    private PersianDatePickerDialog mDatePicker;
    private DataBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    private EditText edt_km_now, edt_all_price, edt_km_next, edt_avg_function, edt_description;
    TextView txt_plak_customer1, txt_plak_customer2, txt_plak_customer3, txt_plak_customer4;
    ViewGroup plaks;
    private AlertDialog alertDialogs_service_ghabli;
    private List<Integer> list_id = new ArrayList<>();
    private List<ModelServices> list_model = new ArrayList<>();
    int count = 0;
    int current_id = 0;
    private ModelServicesCustomer msc;
    private String ids_ServiceDetails = "";
    private int last_km_now = 0, last_average_id = 0;

    private long last_average = 0;

    private String last_service_date_time = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());

    public boolean fromMain = true;
    public String plak = "";

    private int jobCategoryId;
    private String date_service;

    private ViewGroup ly_plk_general,ly_plk_taxi,ly_plk_edari,ly_plk_entezami,ly_plk_malolin,ly_plk_azad_new,ly_plk_azad_old;

    PLAK_TYPE plak_type = PLAK_TYPE.PLAK_GENERAL;
    ViewGroup plak_layout;

    private Intent intentThatOpenAddService;
    public void finishActivity() {
        this.finish();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_services);
        G.Activity = this;
        G.context = this;
        // boolean status = G.preference.getBoolean("ServiceCenterStatus", false);
        boolean status = true;
        if (!status) {
            G.toast(getResources().getString(R.string.statusfalse));
            finish();
        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        G.Activity = this;
        mDBHelper = new DataBaseHelper(this);
        mDatabase = mDBHelper.getReadableDatabase();
        intentThatOpenAddService = getIntent();

        FindView();
        onClick();
        setPlakLayout();

        txt_tile_action_bar.setText("ثبت سرویس جدید");
        txt_tile_action_bar.setTypeface(G.Bold);
        PersianCalendar initDate = new PersianCalendar();
        btn_date_service.setText(initDate.getPersianYear() + "/" + initDate.getPersianMonth() + "/" + initDate.getPersianDay());


        if (getIntent().hasExtra("job_id")) {
            jobCategoryId = getIntent().getIntExtra("job_id", 0);
        }
        if (getIntent().getExtras().getString("id_service") != null) {
            idService = getIntent().getExtras().getString("id_service");
            listServiceAvailable(idService);
        } else {
            G.preference.edit().putString("historyKhadamat", "[]").apply();
            chooseService();
        }
        mDBHelper.deleteHistoryKhadamt(G.preference.getInt("idService", 0) + "");


        if (getIntent().getExtras().getString("phone").equals("null")) {
            txt_phone_customer.setText("");
            txt_name_car.setText("");
            txt_name_customer.setText("");
            plaks.setVisibility(View.GONE);
            btn_date_service.setText("");
            edt_km_next.setText("");
            edt_km_now.setText("");
            edt_description.setText("");
            edt_all_price.setText("");
            edt_avg_function.setText("");
        } else {


            txt_phone_customer.setText(getIntent().getExtras().getString("phone"));
            txt_phone_customer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = txt_phone_customer.getText().toString();
                    if (phone.length() >= 10) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                    }
                }
            });
            txt_name_car.setText(getIntent().getExtras().getString("nameCar"));
            txt_name_customer.setText(getIntent().getExtras().getString("firstName") + " " + getIntent().getExtras().getString("lastName"));

            // btn_date_service.setText(getIntent().getExtras().getString("date_service"));
            edt_km_next.setText(getIntent().getExtras().getString("km_next"));
            edt_km_now.setText(getIntent().getExtras().getString("km_now"));
            edt_description.setText(getIntent().getExtras().getString("description"));
            edt_all_price.setText(getIntent().getExtras().getString("services_price"));
            edt_avg_function.setText(getIntent().getExtras().getString("avg_function"));
            String type_fule = (getIntent().getExtras().getString("type_fule") + "").replace("null", "");
            String type_car = (getIntent().getExtras().getString("type_car") + "").replace("null", "");
            txt_type_customer.setText(type_car + " _ " + type_fule);
            txt_type_customer.setText("وفادار");

            plak = (getIntent().getExtras().getString("plak") + "").replace(" ", "").replace("null", "");
            if (plak.length() > 3) {
                setPlakBasedViewType(plak, plak_type);
            } else {
                plaks.setVisibility(View.GONE);
            }
            fromMain = getIntent().getExtras().getBoolean("fromMain");
            if (fromMain) {
                txt_phone_customer.setVisibility(View.GONE);
                txt_name_customer.setVisibility(View.GONE);
//                txt_edit_info.setVisibility(View.GONE);

            } else {
                txt_phone_customer.setVisibility(View.VISIBLE);
                txt_name_customer.setVisibility(View.VISIBLE);
//                txt_edit_info.setVisibility(View.VISIBLE);
            }
        }

        edt_description.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btn_save_service.performClick();
                    return true;
                }
                return false;
            }
        });

        getAverageFunction();
    }

    public void onclickAlamrs(View v) {
        startActivity(new Intent(AddServicesActivity.this, AlarmsActivity.class));
    }

    private void FindView() {
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        img_back = findViewById(R.id.img_back);
        btn_save_service = findViewById(R.id.btn_save_service);
        btn_cancle = findViewById(R.id.btn_cancle);
        btn_choose_service = findViewById(R.id.btn_choose_service);
        btn_date_service = findViewById(R.id.btn_date_service);
        btn_date_service.setTypeface(G.ExtraBold);
        txt_phone_customer = findViewById(R.id.txt_phone_customer);
        txt_phone_customer.setTypeface(G.ExtraBold);
        txt_name_car = findViewById(R.id.txt_name_car);
        txt_name_car.setTypeface(G.ExtraBold);
        txt_name_customer = findViewById(R.id.txt_name_customer);
        txt_name_customer.setTypeface(G.ExtraBold);

        edt_km_now = findViewById(R.id.edt_km_now);
        edt_all_price = findViewById(R.id.edt_all_price);
        edt_km_next = findViewById(R.id.edt_km_next);
        edt_avg_function = findViewById(R.id.edt_avg_function);
        edt_description = findViewById(R.id.edt_description);
        txt_edit_info = findViewById(R.id.txt_edit_info);
        txt_late_service = findViewById(R.id.txt_late_service);
        txt_type_customer = findViewById(R.id.txt_type_customer);
        txt_late_service.setTypeface(G.ExtraBold);
        txt_edit_info.setTypeface(G.ExtraBold);
        txt_type_customer.setTypeface(G.ExtraBold);

        edt_km_now.addTextChangedListener(new MyNumberWatcher(edt_km_now, edt_km_next));
        edt_all_price.addTextChangedListener(new MyNumberWatcher(edt_all_price, null));
        edt_km_next.addTextChangedListener(new MyNumberWatcher(edt_km_next, null));
 /*       edt_km_next.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d("Guid", "onFocusChange: AvgKm" + G.preference.getBoolean("ChangeAvgKm", false) );
                if (G.preference.getBoolean("ChangeAvgKm", false)) {
                    if (edt_km_now.getText().toString().length() > 0) {
                        int edt_km_nowX = Integer.parseInt(G.converToEn(edt_km_now.getText().toString()));
                        edt_km_next.setText((edt_km_nowX + G.preference.getInt("AvgKm", 0)) + "");
                        Log.d("Guid", "onFocusChange: AvgKm" + G.preference.getInt("AvgKm", 0) );
                        Log.d("Guid", "onFocusChange: edt_km_next" + edt_km_next.getText().toString() );

                        G.preference.edit().putBoolean("ChangeAvgKm", false).apply();
                    }
                }
            }
        });*/
        edt_avg_function.addTextChangedListener(new MyNumberWatcher(edt_avg_function, null));
        plaks = findViewById(R.id.plaks);
        txt_plak_customer1 = findViewById(R.id.txt_plak_customer1);
        txt_plak_customer2 = findViewById(R.id.txt_plak_customer2);
        txt_plak_customer3 = findViewById(R.id.txt_plak_customer3);
        txt_plak_customer4 = findViewById(R.id.txt_plak_customer4);
        txt_plak_customer1.setTypeface(G.ExtraBold);
        txt_plak_customer2.setTypeface(G.ExtraBold);
        txt_plak_customer3.setTypeface(G.ExtraBold);
        txt_plak_customer4.setTypeface(G.ExtraBold);

        ly_plk_general = findViewById(R.id.ly_plk_general);
        ly_plk_taxi = findViewById(R.id.ly_plk_taxi);
        ly_plk_edari= findViewById(R.id.ly_plk_edari);
        ly_plk_entezami = findViewById(R.id.ly_plk_entezami);
        ly_plk_malolin = findViewById(R.id.ly_plk_malolin);
        ly_plk_azad_new = findViewById(R.id.ly_plk_azad_new);
        ly_plk_azad_old = findViewById(R.id.ly_plk_azad_old);
        plak_layout = ly_plk_general;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ShowCase();
            }
        }, 1200);
    }

    public void ShowCase() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        config.setMaskColor(Color.parseColor("#cc222222"));

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(AddServicesActivity.this, "ShowCaseAddService");

        sequence.setConfig(config);

        sequence.addSequenceItem(Utils.createCustomShowcaseView(this, btn_choose_service, getString(R.string.showcase_btn_choose_service), getString(R.string.next_showcase)));
        sequence.addSequenceItem(Utils.createCustomShowcaseView(this, edt_km_now, getString(R.string.showcase_edt_km_now), getString(R.string.next_showcase)));
        sequence.addSequenceItem(Utils.createCustomShowcaseView(this, edt_km_next, getString(R.string.showcase_edt_km_next), getString(R.string.next_showcase)));
        sequence.addSequenceItem(Utils.createCustomShowcaseView(this, edt_avg_function, getString(R.string.showcase_edt_avg_function), getString(R.string.next_showcase)));
        sequence.addSequenceItem(Utils.createCustomShowcaseView(this, edt_all_price, getString(R.string.showcase_edt_all_price), getString(R.string.close_showcase)));
        sequence.start();
    }

    public String idService = "";

    public void addService(String dateTime
            , String avg_function, String km_next
            , String km_now, String description, String price
            , String service, String service_id) {

        String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());
        dateTime = dateTime.replace("/", "-");
        if (dateTime.contains("-")) {
            CalendarTool calendarTool = new CalendarTool();
            String[] dates = dateTime.split("-");
            calendarTool.setIranianDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
            dateTime = calendarTool.getGregorianDate();

        }
        List<ModelSaveKhadamat> msks = mDBHelper.getListsave_khadamat(mDatabase, mDBHelper.getLastIdService(mDatabase) + 1);

        JSONArray array = new JSONArray();
        for (ModelSaveKhadamat msk : msks) {
            JSONObject object = new JSONObject();
            try {
                object.put("name", msk.getTitle());
                object.put("sub_group_id", msk.getId_product());
                object.put("date_time", dateTime);

                if (msk.getStatus() == 1) {
                    object.put("have_visit", "1");
                    object.put("have_change", "0");
                } else if (msk.getStatus() == 2) {
                    object.put("have_change", "1");
                    object.put("have_visit", "0");
                } else {
                    object.put("have_change", "0");
                    object.put("have_visit", "0");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(object);
        }
        service = array.toString();

        G.loading(G.Activity);
        String d_id = PreferenceUtil.getD_id();
        String car_id = getIntent().getExtras().getString("id_car");
        String discount = "0";
        String remaining = "0";
        Log.e(G.TAG, "D_id: " + d_id);
        Log.e(G.TAG, "car_id: " + car_id);
        Log.e(G.TAG, "dateTime: " + dateTime);
        Log.e(G.TAG, "avg_function: " + avg_function);
        Log.e(G.TAG, "km_next: " + km_next);
        Log.e(G.TAG, "km_now: " + km_now);
        Log.e(G.TAG, "description: " + description);
        Log.e(G.TAG, "price: " + price);
        Log.e(G.TAG, "service: " + service);
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        JSONObject object = new JSONObject();
        try {
            // object.put("service_center_id", d_id);
            object.put("service_center_id", null);
            object.put("customer_car_id", car_id);
            object.put("km_now", km_now);
            object.put("km_next", km_next);
            object.put("service_date_time", dateTime);
            object.put("service_status", 1);
            object.put("description", description);
            object.put("status", 1);
            object.put("avg_function", avg_function);
            object.put("price", price);
            object.put("detail_service", G.preference.getString("detail_service", "[]"));
            object.put("created_at", created_at);
            object.put("updated_at", created_at);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<ResponseBody> request = api.addService(G.returnBody(object.toString()));
        Log.d("GuidCar_ArenaTeam_", "addService: " + request.request());
        Log.d("GuidCar_ArenaTeam_", "addService: object" + object.toString());
        Log.d("GuidCar_ArenaTeam_", "addService: object2" + G.returnBody(object.toString()));
        if (service_id.length() > 0) {
            idService = service_id;
            request = api.updateService(service_id, G.returnBody(object.toString()));
        } else {
            idService = "";
        }

        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                addAverageFunction(km_now, avg_function);
                String result = G.getResult(response);
                Log.d("GuidCar_ArenaTeam_", "addService: result" + result);

                if (result.length() > 0 && result.length() < 10) {

                    if (idService.length() < 1) {
                        idService = result;
                        addServicesPayDetail(idService, price);
                    }

                    mDBHelper.deleteHistoryKhadamt(G.preference.getInt("idService", 0) + "");
                    deleteServiceDetails(idService);
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

    @Override
    public void onResume() {
        super.onResume();
        G.Activity = this;
        G.context = this;
        if (G.preference.getBoolean("ChangeAvgKm", false)) {
            String km_now = edt_km_now.getText().toString();
            if (km_now.length() > 0) {
                int edt_km_nowX = Integer.parseInt(G.converToEn(km_now));
                edt_km_next.setText((edt_km_nowX + G.preference.getInt("AvgKm", 0)) + "");
                G.preference.edit().putBoolean("ChangeAvgKm", false).apply();
            }
        }
    }

    public void getAverageFunction() {
//        G.loading(this);
        String car_id = getIntent().getExtras().getString(Constants.CAR_ID);
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getAverageFunction("customer_car_id,eq," + car_id);
        Log.d("TEST", "AddServiesActivity:getAverageFunction:request " +request.request().toString() );

        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    String result = G.getResult(response);
                    Log.d("TEST", "AddServiesActivity:getAverageFunction:result " + result );
                    JSONObject objectget = G.StringtoJSONObject(result);

                    if (objectget.has("records")) {
                        JSONArray records = objectget.getJSONArray("records");

                        if (records.length() > 0) {
                            JSONObject obj = records.getJSONObject(0);
                            last_average_id = obj.getInt("id");
                            int customer_car_id = obj.getInt("customer_car_id");
                            last_km_now = obj.getInt("last_km_now");
                            last_average = obj.getInt("average");
                            if (last_average > 0) {
                                edt_avg_function.setText(last_average + "");
                            }
                            last_service_date_time = obj.getString("service_date_time");
                            String created_at = obj.getString("created_at");
                            String updated_at = obj.getString("updated_at");

                        }
                    }
                } catch (JSONException e) {
                    G.toast("مشکل در تجزیه اطلاعات");
                    e.printStackTrace();
                    Log.d("TEST", "AddServiesActivity:getAverageFunction:e " + e.getMessage().toString() );
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

    public String idsServiceTiming = "";

    public void getServicesTiming(String service_id, String services_detail_id, int km_usage, String product_group_id, int visited_change, boolean send_msg) {
       // G.loading(this);
        G.Log("getServicesTiming");
        idsServiceTiming = "";
        String idCustomer = getIntent().getExtras().getString("idCustomer");
        String car_id = getIntent().getExtras().getString("id_car");
        if (visited_change == 1 && send_msg) {
            Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
            Call<ResponseBody> request = api.getServicesTiming("user_id,eq," + idCustomer, "car_id,eq," + car_id, "product_group_id,eq," + product_group_id);
            request.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {
                        String result = G.getResult(response);
                        G.Log("getServicesTiming");
                        G.Log(call.request().toString());
                        G.Log(result);
                        JSONObject objectget = G.StringtoJSONObject(result);
                        if (objectget.has("records")) {
                            JSONArray records = objectget.getJSONArray("records");
                            for (int i = 0; i < records.length(); i++) {
                                JSONObject obj = records.getJSONObject(i);
                                idsServiceTiming += "" + obj.getString("id") + ",";
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    idsServiceTiming = idsServiceTiming.replace(",,", "");
                    deleteServicesTiming(service_id, services_detail_id, km_usage, product_group_id, visited_change, send_msg);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    deleteServicesTiming(service_id, services_detail_id, km_usage, product_group_id, visited_change, send_msg);
                }
            });
        } else {
            deleteServicesTiming(service_id, services_detail_id, km_usage, product_group_id, visited_change, send_msg);
        }


    }

    public void deleteServicesTiming(String service_id, String services_detail_id, int km_usage, String product_group_id, int visited_change, boolean send_msg) {
        G.Log("deleteServicesTiming");
       // G.loading(this);
        String d_id = PreferenceUtil.getD_id();
        if (visited_change == 1 && send_msg) {
            Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
            Call<ResponseBody> request = api.deleteServicesTiming(idsServiceTiming);

            request.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    String result = G.getResult(response);
                    G.Log("deleteServicesTiming");
                    G.Log(result);
                    addServicesTimingFinal(service_id, services_detail_id, km_usage, product_group_id, visited_change, send_msg);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    addServicesTimingFinal(service_id, services_detail_id, km_usage, product_group_id, visited_change, send_msg);
                }
            });
        } else {
            addServicesTimingFinal(service_id, services_detail_id, km_usage, product_group_id, visited_change, send_msg);
        }


    }

    public void addServicesTimingFinal(String service_id, String services_detail_id, int km_usage, String product_group_id, int visited_change, boolean send_msg) {
     //   G.loading(this);
        G.Log("addServicesTimingFinal");
        if (visited_change == 1 && send_msg) {
            String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());
            JSONObject object = new JSONObject();
            try {
                String avg_function = (G.converToEn(edt_avg_function.getText().toString() + ""));
                G.Log(km_usage + "");
                G.Log(avg_function + "");
                G.Log(created_at + "");
                //last_average
                int days = (km_usage / ((int) Integer.parseInt(avg_function)));
                G.Log(days + "");
                String msg_reminder_date = G.addDaysToDate(days - 2, date_service);
                String due_date = G.addDaysToDate(days, created_at);
                G.Log(due_date + "");
                object.put("services_id", service_id);
                object.put("services_detail_id", services_detail_id);
                object.put("due_date", due_date);
                object.put("msg_reminder_date", msg_reminder_date);
                object.put("product_group_id", product_group_id);
                object.put("status", 1);
                object.put("from_web", 0);
                object.put("created_at", created_at);
                object.put("updated_at", created_at);
                object.put("deleted_at", null);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            G.Log(object.toString());

            Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);

            Call<ResponseBody> request = api.addServicesTiming(G.returnBody(object.toString()));
            G.Log("addServicesTimingFinal:request"  +request.request().toString());
            G.Log("addServicesTimingFinal:object"  +object.toString());

            request.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    String result = G.getResult(response);
                    G.Log("addServicesTimingFinal");
                    G.Log(result);
                    addServicesDetail(service_id);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    G.stop_loading();
                    G.toast("مشکل در برقراری ارتباط با سرور");
                }
            });
        } else {
            addServicesDetail(service_id);
        }
    }

    public void addAverageFunction(String km_now, String avg_function) {
        String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());
        String car_id = getIntent().getExtras().getString("id_car");
        JSONObject object = new JSONObject();

        try {
            object.put("customer_car_id", car_id);
            object.put("last_km_now", km_now);
            object.put("service_date_time", created_at);
            object.put("average", avg_function);
            object.put("created_at", created_at);
            object.put("updated_at", created_at);
            object.put("deleted_at", null);

        } catch (JSONException e) {
            e.printStackTrace();
        }
       // G.loading(this);
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.addAverageFunction(G.returnBody(object.toString()));
        if (last_average_id == 0) {
            try {
                object.put("created_at", created_at);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            long days = G.dateDifference(G.StringToDate(last_service_date_time), G.StringToDate(created_at));
            if (days <= 0) {
                days = 1;
            }
            int X = Integer.parseInt(km_now) - last_km_now;
            try {
                if (days > 0) {
                    long newAvg = (X / days);
                    if (newAvg <= 0) {
                        object.put("average", avg_function);
                    } else {
                        object.put("average", newAvg);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = api.editAverageFunction(last_average_id + "", G.returnBody(object.toString()));
        }
        try {
            last_average = object.getLong("average");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if (last_average <= 0) {
            String x = (G.converToEn(edt_avg_function.getText().toString() + "")).replace("null", "");
            if (x.length() > 0) {
                last_average = Long.parseLong(x);
            }
        }
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط با سرور");
            }
        });
    }

    public void addServicesPayDetail(String services_id, String amount) {

        String user_id = PreferenceUtil.getUser_id();
       // G.loading(this);

        String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());

        JSONObject object = new JSONObject();
        try {


            object.put("services_id", services_id);
            object.put("checking_request_id", null);
            object.put("tug_request_id", null);
            object.put("invoice_amount", amount);
            object.put("discount", null);
            object.put("copon_id", null);
            object.put("festival_id", null);
            object.put("pay_type", 1);
            object.put("remaining", 0);
            object.put("created_at", created_at);
            object.put("updated_at", created_at);
            object.put("deleted_at", null);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.addServicesPayDetail(G.returnBody(object.toString()));

        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

//                G.stop_loading();


            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط با سرور");
            }
        });


    }

    public void listServiceAvailable(String service_id) {
        G.preference.edit().putString("historyKhadamat", "[]").apply();
        ids_ServiceDetails = "";
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.listServicesDetail("services_id,eq," + service_id);
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    String result = G.getResult(response);
                    G.Log(result.toString());
                    JSONObject object = G.StringtoJSONObject(result);
                    JSONArray records = object.getJSONArray("records");
                    if (records.length() > 0) {
                        JSONArray saveKhadamat = new JSONArray();
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject obj = records.getJSONObject(i);

                            String product_group_title = "";
                            String product_group_id = "";
                            if (obj.has("product_group_id")) {
                                JSONObject product_group = obj.getJSONObject("product_group_id");
                                product_group_id = product_group.getString("id");
                                product_group_title = product_group.getString("title");
                            }
                            String product_name = obj.getString("product_name");
                            String product_name_id = obj.getString("product_name_id");
                            String visited_change = obj.getString("visited_change");
                            String value = obj.getString("value");
                            int services_id = obj.getInt("services_id");
                            G.Log(services_id + " - " + product_group_title + " - ");
                            saveKhadamat.put(obj);
                            if (visited_change.equals("1")) {
                                mDBHelper.deleteRow(1 + "", product_group_title);
                                mDBHelper.insertdetectProGroup(product_group_title, 2, 1, product_name, value);
                                G.preference.edit().putString(product_group_title, product_name).apply();


                            } else {
                                mDBHelper.deleteRow(getIntent().getExtras().getInt("idService") + "", product_group_title);
                                // mDBHelper.updateKhadamat(models.get(position).getId(), 2, idCustomer);
                                mDBHelper.insertdetectProGroup(product_group_title, 1, 1, product_name, value);
                                G.preference.edit().putString(product_group_title, "").apply();
                            }

                            ids_ServiceDetails += "" + obj.getString("id") + ",";

                        }
                        G.preference.edit().putString("historyKhadamat", saveKhadamat.toString()).apply();
                        ids_ServiceDetails = ids_ServiceDetails.replace(",,", "");

                    }
                } catch (JSONException e) {
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

    public void deleteServiceDetails(String service_id) {
        if (ids_ServiceDetails.length() > 0) {
            //G.loading(this);
            Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);

            Call<ResponseBody> request = api.deleteServicesDetail(ids_ServiceDetails);
            G.Log(request.request().toString());
            request.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    String result = G.getResult(response);
                    G.Log(result);
                    addServicesDetail(service_id);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    G.stop_loading();
                    G.toast("مشکل در برقراری ارتباط");
                }
            });

        } else {
            addServicesDetail(service_id);
        }

    }

    public int sendid = 0;

    public boolean send_msg_prov = false;

    public void addServicesDetail(String service_id) {
        //G.loading(this);
        String d_id = PreferenceUtil.getD_id();
        String detail_serviceS = G.preference.getString("detail_service", "[]");
        G.Log("addServicesDetail:detail_serviceS::" + detail_serviceS);
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        try {
            JSONArray array = new JSONArray(detail_serviceS);
            String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());
            if (array.length() > 0 && sendid < array.length()) {
                JSONObject object = array.getJSONObject(sendid);
                boolean selb = (object.getBoolean("selt"));
                boolean selt = (object.getBoolean("selb"));
                String value = (object.getString("value"));
                String product_group_id = (object.getString("id"));
                String product_group_name = (object.getString("name"));
                String product_name_name = (object.getString("type"));
                int product_name_id = (object.getInt("type_id"));
                int km_usage = (object.getInt("km_usage"));
                boolean send_msg = (object.getBoolean("send_msg"));
                if (send_msg) {
                    send_msg_prov = true;
                }
                int visited_change = 1;
                if (selt && !selb) {
                    visited_change = 0;
                }


                JSONObject objx = new JSONObject();
                objx.put("services_id", service_id);
                objx.put("product_group_id", product_group_id);
                if (product_name_name.length()>0) {
                    objx.put("product_name", product_name_name);
                    if(product_name_id>0){
                        objx.put("product_name_id", product_name_id);
                    }else{
                        objx.put("product_name_id", JSONObject.NULL);
                    }

                } else {
                    objx.put("product_name", JSONObject.NULL);
                    objx.put("product_name_id", JSONObject.NULL);
                }
                objx.put("visited_change", visited_change);
                objx.put("value", value);
                objx.put("created_at", created_at);
                objx.put("updated_at", created_at);
                objx.put("deleted_at", null);

                G.Log("add services detail: " + objx.toString());
                Call<ResponseBody> request = api.addServicesDetail(G.returnBody(objx.toString()));
                G.Log("add services detail: request" + request.request().toString());
                int finalVisited_change = visited_change;
                request.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        G.Log(call.request().toString());
                        String result = G.getResult(response);
                        if (result.length() > 0 && result.length() < 10) {
                            sendid++;
                            G.Log("finalVisited_change: " + finalVisited_change);
                            G.Log("send_msg: " + send_msg);
                            getServicesTiming(service_id, result, km_usage, product_group_id, finalVisited_change, send_msg);

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
                if (send_msg_prov) {
                    G.sendSMSProv(txt_phone_customer.getText().toString(), G.PROV_ADD_Service);
                }
                Intent intentThatStartsCarListActivity = new Intent(AddServicesActivity.this, CarListActivity.class);
                intentThatStartsCarListActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentThatStartsCarListActivity);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onClick() {
        btn_save_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_service = btn_date_service.getText().toString();
                String km_now = G.converToEn(edt_km_now.getText().toString());
                String km_next = G.converToEn(edt_km_next.getText().toString());
                String all_price = G.converToEn(edt_all_price.getText().toString());
                String avg_function = G.converToEn(edt_avg_function.getText().toString());
                String description = edt_description.getText().toString();

                if (!TextUtils.isEmpty(date_service) && !TextUtils.isEmpty(km_now) && !TextUtils.isEmpty(km_next) && !TextUtils.isEmpty(all_price) && !TextUtils.isEmpty(avg_function)) {

                    if (getIntent().getExtras().getString("id_service") == null || getIntent().getExtras().getString("id_service").length() <= 0) {
//                        mDBHelper.insertServices(date_service, km_now, km_next, avg_function, all_price, description, getIntent().getExtras().getString("firstName"), getIntent().getExtras().getString("lastName"), getIntent().getExtras().getString("nameCar"), getIntent().getExtras().getString("phone"), getIntent().getExtras().getString("plak"), getIntent().getExtras().getString("idCustomer"));
                        addService(date_service, avg_function, km_next, km_now, description, all_price, "", "");
                    } else {
                        addService(date_service, avg_function, km_next, km_now, description, all_price, "", getIntent().getExtras().getString("id_service") + "");
//                        mDBHelper.updateServices(getIntent().getExtras().getInt("id_service"), date_service, km_now, km_next, avg_function, all_price, description);
                    }
//                    Toast.makeText(AddServicesActivity.this, "سرویس جدید با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(AddServicesActivity.this, LastServiseDoneActivity.class);
                    //   intent.putExtra("type_fule", getIntent().getExtras().getString("type_fule"));
                    // intent.putExtra("idCustomer", getIntent().getExtras().getString("idCustomer"));
                    // startActivity(intent);
//                    if (getIntent().getExtras().getString("finish").equals("finish")) {
//                        customer.finish();
//                        finish();
//                    } else
//                        finish();
                }
                if (km_now.equals("")) {
                    edt_km_now.setError("کیلومتر فعلی را به درستی وارد کنید");
                }
                if (km_next.equals("")) {
                    edt_km_next.setError("کیلومتر بعدی را به درستی وارد کنید");
                }
                if (all_price.equals("")) {
                    edt_all_price.setError("مبلغ را به درستی وارد کنید");
                }
                if (avg_function.equals("")) {
                    edt_avg_function.setError("میانگین را به درستی وارد کنید");
                }
            }
        });

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(AddServicesActivity.this, CarListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        btn_choose_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseService();
            }
        });

        btn_date_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeDatePicker();
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txt_edit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddServicesActivity.this, AddNewCarActivity.class);
                intent.putExtra("idCustomer", getIntent().getExtras().getString("idCustomer"));
                intent.putExtra("id_car", getIntent().getExtras().getString("id_car"));
                intent.putExtra("firstName", getIntent().getExtras().getString("firstName"));
                intent.putExtra("lastName", getIntent().getExtras().getString("lastName"));
                intent.putExtra("phone", getIntent().getExtras().getString("phone"));
                intent.putExtra("nameCar", getIntent().getExtras().getString("nameCar"));
                intent.putExtra("plak", getIntent().getExtras().getString("plak"));
                intent.putExtra(Constants.CAR_PLATE_TYPE, getIntent().getExtras().getString(Constants.CAR_PLATE_TYPE));
                intent.putExtra("gender", getIntent().getExtras().getString("gender"));
                intent.putExtra("date_birthday", getIntent().getExtras().getString("date_birthday"));
                intent.putExtra("type_fule", getIntent().getExtras().getString("type_fule"));
                intent.putExtra("date_save", getIntent().getExtras().getString("date_save"));
                intent.putExtra("type_car", getIntent().getExtras().getString("type_car"));
                intent.putExtra("car_name_id", getIntent().getExtras().getInt("car_name_id"));
                intent.putExtra("car_tip_id", getIntent().getExtras().getInt("car_tip_id"));
                intent.putExtra("car_model_id", getIntent().getExtras().getInt("car_model_id"));
                intent.putExtra("car_company_id", getIntent().getExtras().getInt("car_company_id"));
                intent.putExtra("fuel_type_id", getIntent().getExtras().getInt("fuel_type_id"));
                startActivity(intent);
            }
        });

        txt_late_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_id = 0;
                queueService("eq", service_id + "");
            }
        });
    }

    public void chooseService() {
        Intent intent = new Intent(AddServicesActivity.this, ListServiceCarActivity.class);
        intent.putExtra("idCustomer", getIntent().getExtras().getString("idCustomer"));
        intent.putExtra("idService", mDBHelper.getLastIdService(mDatabase) + 1);
        intent.putExtra("job_id", jobCategoryId);
        G.preference.edit().putInt("idService", mDBHelper.getLastIdService(mDatabase) + 1).apply();
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == 50) {
                finish();
            }
        }
    }

    private void initializeDatePicker() {
        PersianCalendar initDate = new PersianCalendar();
        // btn_date_service.setText(initDate.getPersianYear() + "/" + initDate.getPersianMonth() + "/" + initDate.getPersianDay());
        mDatePicker = new PersianDatePickerDialog(AddServicesActivity.this)
                .setPositiveButtonString("تأیید")
                .setNegativeButton("بستن")
                .setTodayButton("امروز")
                .setTodayButtonVisible(true)
                .setMinYear(1400)
                .setMaxYear(1410)
                .setInitDate(initDate)
                .setTitleColor(getResources().getColor(R.color.text_dark))
                .setActionTextColor(Color.GRAY)
                //  .setPickerBackgroundColor(Color.DKGRAY)
                //.setBackgroundColor(Color.DKGRAY)
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(new Listener() {
                    @Override
                    public void onDateSelected(PersianCalendar persianCalendar) {
                        btn_date_service.setText(persianCalendar.getPersianYear() + "/" + persianCalendar.getPersianMonth() + "/" + persianCalendar.getPersianDay());

                    }

                    @Override
                    public void onDismissed() {
                    }
                });
        mDatePicker.show();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context));
    }

    public int service_id = 0;
    public int last_service_id = 0;
    public int first_service_id = 0;
    public TextView txt_next_service, txt_last_service;
    public ViewGroup img_next, img_lest;

    void DialogServiceGhabli(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_item_dialog_service_ghabli, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(true);
        if (alertDialogs_service_ghabli != null && alertDialogs_service_ghabli.isShowing()) {
            alertDialogs_service_ghabli.dismiss();
        }
        alertDialogs_service_ghabli = alertDialogBuilder.create();

        alertDialogs_service_ghabli.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        alertDialogs_service_ghabli.setCancelable(false);
        WindowManager.LayoutParams layoutParams = alertDialogs_service_ghabli.getWindow().getAttributes();
        alertDialogs_service_ghabli.getWindow().setAttributes(layoutParams);
        alertDialogs_service_ghabli.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shap_simple_rec));
        alertDialogs_service_ghabli.show();
        ImageView img_close;
        TextView txt_name_customer, txt_phone_customer, txt_name_car, txt_plak_customer,
                txt_km_now, txt_km_next, txt_avg_function, txt_all_price, txt_description, txt_date;


        TextView txt_plak_customer1, txt_plak_customer2, txt_plak_customer3, txt_plak_customer4;
        ViewGroup plaks;
        img_close = view.findViewById(R.id.img_close);
        txt_name_customer = view.findViewById(R.id.txt_name_customer);
        txt_phone_customer = view.findViewById(R.id.txt_phone_customer);
        txt_name_car = view.findViewById(R.id.txt_name_car);

        if (fromMain) {
            txt_name_customer.setVisibility(View.GONE);
            txt_phone_customer.setVisibility(View.GONE);
            view.findViewById(R.id.ghablobad).setVisibility(View.GONE);
        } else {
            txt_name_customer.setVisibility(View.VISIBLE);
            txt_phone_customer.setVisibility(View.VISIBLE);
            view.findViewById(R.id.ghablobad).setVisibility(View.VISIBLE);
        }
        plaks = view.findViewById(R.id.plaks);
        txt_plak_customer1 = view.findViewById(R.id.txt_plak_customer1);
        txt_plak_customer2 = view.findViewById(R.id.txt_plak_customer2);
        txt_plak_customer3 = view.findViewById(R.id.txt_plak_customer3);
        txt_plak_customer4 = view.findViewById(R.id.txt_plak_customer4);
        txt_plak_customer1.setTypeface(G.ExtraBold);
        txt_plak_customer2.setTypeface(G.ExtraBold);
        txt_plak_customer3.setTypeface(G.ExtraBold);
        txt_plak_customer4.setTypeface(G.ExtraBold);
        txt_name_car.setTypeface(G.ExtraBold);


        txt_km_now = view.findViewById(R.id.txt_show_km_now);
        txt_km_next = view.findViewById(R.id.txt_show_km_next);
        txt_avg_function = view.findViewById(R.id.txt_avg_function);
        txt_all_price = view.findViewById(R.id.txt_all_price);
        txt_description = view.findViewById(R.id.txt_description);
        txt_date = view.findViewById(R.id.txt_date_service);
        txt_next_service = view.findViewById(R.id.txt_next_service);
        txt_last_service = view.findViewById(R.id.txt_last_service);
        img_next = view.findViewById(R.id.img_next);
        img_lest = view.findViewById(R.id.img_lest);
        recycle_done_service_type = view.findViewById(R.id.recycle_done_service_type);
        txt_next_service.setVisibility(View.GONE);
        img_next.setVisibility(View.GONE);
        txt_next_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                queueService("qt", service_id + "");
            }
        });
        txt_last_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                queueService("lt", service_id + "");
            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogs_service_ghabli.dismiss();
            }
        });

        String id = getIntent().getExtras().getString("idCustomer");


        if (getIntent().getExtras().getString("phone").equals("null") && msc == null) {
            txt_phone_customer.setText("");
            txt_name_car.setText("");
            txt_name_customer.setText("");
            plaks.setVisibility(View.GONE);
            btn_date_service.setText("");
            edt_km_next.setText("");
            edt_km_now.setText("");
            edt_description.setText("");
            edt_all_price.setText("");
            edt_avg_function.setText("");
        } else {
            txt_phone_customer.setText(getIntent().getExtras().getString("phone"));
            txt_phone_customer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = txt_phone_customer.getText().toString();
                    if (phone.length() >= 10) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                    }
                }
            });
            txt_name_car.setText(getIntent().getExtras().getString("nameCar"));
            txt_name_customer.setText(getIntent().getExtras().getString("firstName") + " " + getIntent().getExtras().getString("lastName"));


            txt_avg_function.setText(G.getDecimalFormattedString(msc.getAvg_function() + ""));
            txt_all_price.setText(G.getDecimalFormattedString(msc.getAll_services_price() + ""));

//                txt_date.setText(date + "");
            txt_name_customer.setText(msc.getFirst_name() + " " + msc.getLast_name());
            txt_name_car.setText(msc.getName_car().toString());
            txt_km_now.setText(G.getDecimalFormattedString(msc.getKm_now().toString() + ""));
            txt_km_next.setText(G.getDecimalFormattedString(msc.getKm_next().toString() + ""));
            txt_date.setText(msc.getDate_services().toString());
            txt_description.setText(msc.getDescription() + "");

            String plak = (msc.getPlak() + "").replace(" ", "").replace("null", "");
            if (plak.length() > 3) {
                plaks.setVisibility(View.VISIBLE);
                String c1 = plak.substring(0, 2);
                String c2 = plak.substring(2, plak.length() - 3);
                String c3 = plak.substring(plak.length() - 3, plak.length() - 1);
                String c4 = plak.substring(plak.length() - 1);
                txt_plak_customer1.setText(c1);
                txt_plak_customer2.setText(c4);
                txt_plak_customer3.setText(c2);
                txt_plak_customer4.setText(c3);
                txt_plak_customer1.setTypeface(G.ExtraBold);
                txt_plak_customer2.setTypeface(G.ExtraBold);
                txt_plak_customer3.setTypeface(G.ExtraBold);
                txt_plak_customer4.setTypeface(G.ExtraBold);
            } else {
                plaks.setVisibility(View.GONE);
            }
            showDetailService(msc.getDetail_service() + "");

        }

        for (int i = 0; i < mDBHelper.getServishayeGhabli(id, mDatabase).size(); i++) {
            list_id.add(mDBHelper.getServishayeGhabli(id, mDatabase).get(i).getId());
        }
        if (list_id.size() < 2) {
            txt_last_service.setTextColor(getResources().getColor(R.color.text_color_hint));
            txt_next_service.setTextColor(getResources().getColor(R.color.text_color_hint));
        } else {
            count = list_id.size();
            txt_next_service.setTextColor(getResources().getColor(R.color.button));
            if (current_id < count) {
                if (current_id == 0) {
                    txt_next_service.setTextColor(getResources().getColor(R.color.button));
                    txt_last_service.setTextColor(getResources().getColor(R.color.text_color_hint));
                }
                txt_next_service.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (current_id == 0) {
                            txt_next_service.setTextColor(getResources().getColor(R.color.button));
                            //   txt_last_service.setTextColor(getResources().getColor(R.color.text_color_hint));
                            current_id += 1;
                            String kmNow = list_model.get(current_id).getKm_now();
                            String kmNext = list_model.get(current_id).getKm_next();
                            String avg = list_model.get(current_id).getAvg_function();
                            String price = list_model.get(current_id).getAll_services_price();
                            String desc = list_model.get(current_id).getDescription();
                            String date = list_model.get(current_id).getDate_services();
                            txt_km_now.setText(kmNow + "");
                            txt_km_next.setText(kmNext + "");
                            txt_avg_function.setText(avg + "");
                            txt_all_price.setText(price + "");
                            txt_description.setText(desc + "");
                            txt_date.setText(date + "");
                            txt_last_service.setTextColor(getResources().getColor(R.color.button));

                            if (count - 1 == current_id) {
                                txt_next_service.setTextColor(getResources().getColor(R.color.text_color_hint));
                                txt_last_service.setTextColor(getResources().getColor(R.color.button));

                            }
                        } else if (current_id > 0 && count - 1 != current_id) {
                            txt_next_service.setTextColor(getResources().getColor(R.color.button));
                            txt_last_service.setTextColor(getResources().getColor(R.color.button));
                            current_id += 1;
                            String kmNow = list_model.get(current_id).getKm_now();
                            String kmNext = list_model.get(current_id).getKm_next();
                            String avg = list_model.get(current_id).getAvg_function();
                            String price = list_model.get(current_id).getAll_services_price();
                            String desc = list_model.get(current_id).getDescription();
                            String date = list_model.get(current_id).getDate_services();
                            txt_km_now.setText(kmNow + "");
                            txt_km_next.setText(kmNext + "");
                            txt_avg_function.setText(avg + "");
                            txt_all_price.setText(price + "");
                            txt_description.setText(desc + "");
                            txt_date.setText(date + "");
                            if (count - 1 == current_id) {
                                txt_next_service.setTextColor(getResources().getColor(R.color.text_color_hint));
                                txt_last_service.setTextColor(getResources().getColor(R.color.button));

                            }
                        }
                    }
                });

                txt_last_service.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (current_id > 0 && count - 1 != current_id) {
                            txt_next_service.setTextColor(getResources().getColor(R.color.button));
                            txt_last_service.setTextColor(getResources().getColor(R.color.button));
                            current_id -= 1;
                            String kmNow = list_model.get(current_id).getKm_now();
                            String kmNext = list_model.get(current_id).getKm_next();
                            String avg = list_model.get(current_id).getAvg_function();
                            String price = list_model.get(current_id).getAll_services_price();
                            String desc = list_model.get(current_id).getDescription();
                            String date = list_model.get(current_id).getDate_services();
                            txt_km_now.setText(kmNow + "");
                            txt_km_next.setText(kmNext + "");
                            txt_avg_function.setText(avg + "");
                            txt_all_price.setText(price + "");
                            txt_description.setText(desc + "");
                            txt_date.setText(date + "");
                            if (current_id == 0) {
                                txt_last_service.setTextColor(getResources().getColor(R.color.text_color_hint));
                            }
                        } else if (count - 1 == current_id) {
                            txt_next_service.setTextColor(getResources().getColor(R.color.button));
                            txt_last_service.setTextColor(getResources().getColor(R.color.button));
                            current_id -= 1;
                            String kmNow = list_model.get(current_id).getKm_now();
                            String kmNext = list_model.get(current_id).getKm_next();
                            String avg = list_model.get(current_id).getAvg_function();
                            String price = list_model.get(current_id).getAll_services_price();
                            String desc = list_model.get(current_id).getDescription();
                            String date = list_model.get(current_id).getDate_services();
                            txt_km_now.setText(kmNow + "");
                            txt_km_next.setText(kmNext + "");
                            txt_avg_function.setText(avg + "");
                            txt_all_price.setText(price + "");
                            txt_description.setText(desc + "");
                            txt_date.setText(date + "");
                            if (current_id == 0) {
                                txt_last_service.setTextColor(getResources().getColor(R.color.text_color_hint));
                            }
                        }
                    }
                });

            }
        }
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        width = (int) ((width) * ((double) 9 / 10));
        alertDialogs_service_ghabli.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    public void queueService(String filter, String key) {


        G.loading(this);
        G.services.clear();
        String d_id = PreferenceUtil.getD_id();
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        String idCustomer = getIntent().getExtras().getString("idCustomer");
        String car_plate = G.CreateSyntaxPlak(plak);
        Call<ResponseBody> request = api.queueService("user_id,eq," + idCustomer, "car_plate,cs," + car_plate, "");

        if (key.length() > 0 && !key.equals("0")) {
            request = api.queueService("user_id,eq," + idCustomer, "car_plate,cs," + car_plate, "service_id" + "," + filter + "," + key);
        }
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.e("saaa", call.request().toString() + "");
                String result = G.getResult(response);
                JSONObject object = G.StringtoJSONObject(result);
                JSONArray array = G.ObjecttoArray(object, "records");
                if (array.length() > 0) {

                    try {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            msc = new ModelServicesCustomer();
                            msc.setId(obj.getInt("service_id"));
                            service_id = msc.getId();
                            if (service_id > last_service_id) {
                                last_service_id = service_id;
                            }
                            msc.setId_khadamat(obj.getInt("service_id"));
                            msc.setCar_id(obj.getInt("car_id"));
                            String car_name = "";
                            String car_company_name = "";
                            String car_type = "";
                            car_company_name = (obj.getString("car_company_name") + "").replace("null", "");
                            car_type = (obj.getString("car_tip") + "").replace("null", "");
                            car_name = (obj.getString("car_name") + "").replace("null", "");
                            if (car_company_name.length() > 0) {
                                car_name = car_company_name + " - " + car_name;
                            }
                            if (car_type.length() > 0) {
                                car_name = car_name + " - " + car_type;
                            }
                            msc.setName_car(car_name + "");
                            msc.setPlak(obj.getString("car_plate") + "");
                            msc.setType_fuel(obj.getString("fuel_type") + "");
                            msc.setIdc(obj.getInt("user_id"));
                            msc.setId_customer(obj.getInt("user_id"));
                            msc.setFirst_name(obj.getString("f_name") + "");
                            msc.setLast_name(obj.getString("l_name") + "");
                            msc.setGender(obj.getString("sex") + "");
                            if (msc.getGender().contains("M") || msc.getGender().contains("m")) {
                                msc.setGender("آقا");
                            }
                            if (msc.getGender().contains("F") || msc.getGender().contains("f")) {
                                msc.setGender("خانم");
                            }
                            msc.setPhone(obj.getString("mobile") + "");
                            msc.setPhonec(obj.getString("mobile") + "");
                            msc.setDate_birthday(obj.getString("birthdate") + "");
                            msc.setDate_save_customer(obj.getString("cust_created_at") + "");
                            msc.setKm_next(obj.getString("km_next") + "");
                            msc.setKm_now(obj.getString("km_now") + "");
                            msc.setDetail_service(obj.getString("detail_service") + "");
                            msc.setDate_services(obj.getString("service_date_time").replace("00:00:00", "") + "");
                            String date = msc.getDate_services();
                            if (date.contains("-") && date.contains(":") && date.contains(" ")) {
                                CalendarTool calendarTool = new CalendarTool();
                                String[] date_time = date.split(" ");
                                String[] dates = date_time[0].split("-");
                                calendarTool.setGregorianDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
                                date = calendarTool.getIranianDate() + " " + date_time[1];
                                msc.setDate_services(date);
                            } else if (date.contains("-")) {
                                date = date.replace(" ", "");
                                CalendarTool calendarTool = new CalendarTool();
                                String[] dates = date.split("-");
                                calendarTool.setGregorianDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
                                date = calendarTool.getIranianDate();
                                msc.setDate_services(date);
                            }
                            msc.setAvg_function(obj.getString("avg_function") + "");
                            msc.setDescription(obj.getString("description") + "");
                            msc.setAll_services_price(obj.getString("price") + "");
                            DialogServiceGhabli(AddServicesActivity.this);
                        }


                    } catch (JSONException e) {
                        G.toast("مشکل در دریافت اطلاعات");
                        e.printStackTrace();
                    }

                } else {

                    first_service_id = service_id;

                    G.toast("موردی یافت نشد");
                }

                G.stop_loading();
                if (service_id == last_service_id) {
                    if (txt_next_service != null && img_next != null) {
                        txt_next_service.setVisibility(View.GONE);
                        img_next.setVisibility(View.GONE);
                    }
                } else {
                    if (txt_next_service != null && img_next != null) {
                        txt_next_service.setVisibility(View.VISIBLE);
                        img_next.setVisibility(View.VISIBLE);
                    }
                }
                if (txt_last_service != null && img_lest != null) {
                    if (service_id == first_service_id) {
                        txt_last_service.setVisibility(View.GONE);
                        img_lest.setVisibility(View.GONE);
                    } else {
                        txt_last_service.setVisibility(View.VISIBLE);
                        img_lest.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
//                swipeRefreshLayout.setRefreshing(false);
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }

    AdapterDoneServiceType adapterListServiceCar;
    private RecyclerView recycle_done_service_type;

    public void showDetailService(String detail_service) {
        try {

            if (detail_service != null && detail_service.length() > 5 && detail_service.startsWith("[")) {
                JSONArray array1 = null;
                G.Log(detail_service);
                array1 = new JSONArray(detail_service);

                List<ModelSaveKhadamat> productList = new ArrayList<>();
                for (int z = 0; z < array1.length(); z++) {
                    JSONObject objx = array1.getJSONObject(z);


                    ModelSaveKhadamat product = new ModelSaveKhadamat();
                    product.setId(objx.getInt("id"));
                    product.setTitle(objx.getString("name"));

                    G.Log(objx.getString("name"));
                    product.setStatus(1);
                    product.setSelb(objx.getBoolean("selt"));
                    product.setSelt(objx.getBoolean("selb"));
                    if (objx.has("type")) {
                        product.setType(objx.getString("type"));
                    } else {
                        product.setType("");
                    }
                    if (objx.has("value")) {
                        product.setValue(objx.getString("value"));
                    } else {
                        product.setType("");
                    }
                    productList.add(product);

                }
                recycle_done_service_type.setLayoutManager(new LinearLayoutManager(AddServicesActivity.this, RecyclerView.HORIZONTAL, false));
                //  adapterListServiceCar = new AdapterDoneServiceType(InformationServiceCarActivity.this, mDBHelper.getListKhadamatType(mDatabase, getIntent().getExtras().getInt("idservice")));
                adapterListServiceCar = new AdapterDoneServiceType(AddServicesActivity.this, productList, false);
                recycle_done_service_type.setAdapter(adapterListServiceCar);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setPlakLayout() {
        if(intentThatOpenAddService.hasExtra(Constants.CAR_PLATE_TYPE)){
            plak_type = (PLAK_TYPE) intentThatOpenAddService.getSerializableExtra(Constants.CAR_PLATE_TYPE);
        }

        switch (plak_type){
            case PLAK_GENERAL: {
                plak_layout = ly_plk_general;
                ly_plk_general.setVisibility(View.VISIBLE);
                ly_plk_taxi.setVisibility(View.GONE);
                ly_plk_edari.setVisibility(View.GONE);
                ly_plk_entezami.setVisibility(View.GONE);
                ly_plk_malolin.setVisibility(View.GONE);
                ly_plk_azad_new.setVisibility(View.GONE);
                ly_plk_azad_old.setVisibility(View.GONE);
                break;
            }
            case PLAK_TAXI: {
                plak_layout = ly_plk_taxi;
                ly_plk_general.setVisibility(View.GONE);
                ly_plk_taxi.setVisibility(View.VISIBLE);
                ly_plk_edari.setVisibility(View.GONE);
                ly_plk_entezami.setVisibility(View.GONE);
                ly_plk_malolin.setVisibility(View.GONE);
                ly_plk_azad_new.setVisibility(View.GONE);
                ly_plk_azad_old.setVisibility(View.GONE);
                break;
            }
            case PLAK_EDARI: {
                plak_layout = ly_plk_edari;
                ly_plk_general.setVisibility(View.GONE);
                ly_plk_taxi.setVisibility(View.GONE);
                ly_plk_edari.setVisibility(View.VISIBLE);
                ly_plk_entezami.setVisibility(View.GONE);
                ly_plk_malolin.setVisibility(View.GONE);
                ly_plk_azad_new.setVisibility(View.GONE);
                ly_plk_azad_old.setVisibility(View.GONE);
                break;
            }
            case PLAK_ENTEZAMI: {
                plak_layout = ly_plk_entezami;
                ly_plk_general.setVisibility(View.GONE);
                ly_plk_taxi.setVisibility(View.GONE);
                ly_plk_edari.setVisibility(View.GONE);
                ly_plk_entezami.setVisibility(View.VISIBLE);
                ly_plk_malolin.setVisibility(View.GONE);
                ly_plk_azad_new.setVisibility(View.GONE);
                ly_plk_azad_old.setVisibility(View.GONE);
                break;
            }
            case PLAK_MAOLOIN: {
                plak_layout = ly_plk_malolin;
                ly_plk_general.setVisibility(View.GONE);
                ly_plk_taxi.setVisibility(View.GONE);
                ly_plk_edari.setVisibility(View.GONE);
                ly_plk_entezami.setVisibility(View.GONE);
                ly_plk_malolin.setVisibility(View.VISIBLE);
                ly_plk_azad_new.setVisibility(View.GONE);
                ly_plk_azad_old.setVisibility(View.GONE);
                break;
            }
            case PLAK_AZAD_NEW: {
                plak_layout = ly_plk_azad_new;
                ly_plk_general.setVisibility(View.GONE);
                ly_plk_taxi.setVisibility(View.GONE);
                ly_plk_edari.setVisibility(View.GONE);
                ly_plk_entezami.setVisibility(View.GONE);
                ly_plk_malolin.setVisibility(View.GONE);
                ly_plk_azad_new.setVisibility(View.VISIBLE);
                ly_plk_azad_old.setVisibility(View.GONE);
                break;
            }
            case PLAK_AZAD_OLD: {
                plak_layout = ly_plk_azad_old;
                ly_plk_general.setVisibility(View.GONE);
                ly_plk_taxi.setVisibility(View.GONE);
                ly_plk_edari.setVisibility(View.GONE);
                ly_plk_entezami.setVisibility(View.GONE);
                ly_plk_malolin.setVisibility(View.GONE);
                ly_plk_azad_new.setVisibility(View.GONE);
                ly_plk_azad_old.setVisibility(View.VISIBLE);
                break;
            }
        }

    }

    private void setPlakBasedViewType(String plak, PLAK_TYPE plakType) {
       // plaks.setVisibility(View.VISIBLE);

        List<TextView> textViewsInPlakLayout = findTextsInLayout(plak_layout);
        Log.d("PLAK", "setPlakBasedViewType:addService: " + plak_layout );

        switch (plakType) {
            case PLAK_GENERAL:
            case PLAK_TAXI:
            case PLAK_EDARI:
            case PLAK_ENTEZAMI: {
                String c1 = plak.substring(0, 2);
                String c2 = plak.substring(2, plak.length() - 3);
                String c3 = plak.substring(plak.length() - 3, plak.length() - 1);
                String c4 = plak.substring(plak.length() - 1);
                textViewsInPlakLayout.get(0).setText(c1);
                textViewsInPlakLayout.get(1).setText(c4);
                textViewsInPlakLayout.get(2).setText(c2);
                textViewsInPlakLayout.get(3).setText(c3);
                break;
            }
            case PLAK_MAOLOIN:
            {
                String c1 = plak.substring(0, 2);
                String c2 = plak.substring(2, plak.length() - 3);
                String c3 = plak.substring(plak.length() - 3, plak.length() - 1);
                textViewsInPlakLayout.get(0).setText(c1);
                textViewsInPlakLayout.get(1).setVisibility(View.GONE);
                textViewsInPlakLayout.get(2).setText(c2);
                textViewsInPlakLayout.get(3).setText(c3);
                break;
            }
            case PLAK_AZAD_NEW: {
                String c1 = plak.substring(0, 6);
                String c4 = plak.substring(6, plak.length());
                textViewsInPlakLayout.get(0).setText(c1);
                textViewsInPlakLayout.get(1).setText(c4);
                textViewsInPlakLayout.get(2).setText(PLakUtils.convertPersianToEnglish(c1));
                textViewsInPlakLayout.get(3).setText(PLakUtils.convertPersianToEnglish(c4));
                break;
            }
            case PLAK_AZAD_OLD: {
                String c1 = plak.substring(0, 6);
                String c4 = plak.substring(6, plak.length());
                textViewsInPlakLayout.get(0).setText(c4);
                textViewsInPlakLayout.get(1).setText(c1);
                textViewsInPlakLayout.get(2).setText(PLakUtils.convertPersianToEnglish(c1));
                textViewsInPlakLayout.get(3).setVisibility(View.GONE);
                break;
            }
        }
      /* txt_plak_customer1.setTypeface(G.ExtraBold);
       txt_plak_customer2.setTypeface(G.ExtraBold);
       txt_plak_customer3.setTypeface(G.ExtraBold);
       txt_plak_customer4.setTypeface(G.ExtraBold);*/
    }

    private List<TextView> findTextsInLayout(ViewGroup layout) {
        List<TextView> textViews = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view instanceof TextView) {
                textViews.add((TextView) view);
            } else if (view instanceof ViewGroup) {
                textViews.addAll(findTextsInLayout((ViewGroup) view));
            }
        }
        return textViews;
    }
}