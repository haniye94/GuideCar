package com.servicea.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.servicea.activity.AddServicesActivity;
import com.servicea.activity.ServiceFilterActivity;
import com.servicea.adapter.AdapterListServices;
import com.servicea.adapter.AdapterShowFilterToolBar;
import com.servicea.app.CalendarTool;
import com.servicea.app.Constants;
import com.servicea.app.DataBaseHelper;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.FilterModel;
import com.servicea.model.ModelServicesCustomer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.servicea.R;

import com.servicea.model.ServiceTiming;
import com.servicea.model.dbModel.ModelCustomer;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LastServiseDoneActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    TextView txt_tile_action_bar;
    RecyclerView recycle_services, recycle_show_filter;
    ;
    AdapterListServices adapterListService;
    AdapterShowFilterToolBar adapterShowFilterToolBar;
    EditText edt1, edt2, edt3, edt4, edt5, edt6, edt7, edt8;
    List<ModelServicesCustomer> listCustomers = new ArrayList<>();
    List<FilterModel> listFilter = new ArrayList<>();
    LinkedHashMap<Integer, String> selectedJobCategories;
    List<Integer> selectedJobCategoryID;
    int selectedCarID;
    ModelCustomer selectedCar;
    TextView txt_type_search;
    TextInputLayout edt_name, edt_phone;
    LinearLayout ly_plak, ly_empty;
    AlertDialog alertDialogs_detect_type_search;
    ImageView img_back, img_search_service;
    DataBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    TextInputEditText edt_phone_number, edt_nameFamily;
    private AdapterListServices.OnItemClickListener onItemClickService;
    int idCustomer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout last_service_done_shimmer_layout;
    private boolean keydel = false;
    private ProgressBar loadingPB;

    public int page = 1;
    boolean isLoading = false;
    private TextView txt_product_name, txt_car_name, txt_date;
    private ViewGroup service_badi, ly_filtering;

    private static final int LAUNCH_SECOND_ACTIVITY = 243;

    private List<ModelServicesCustomer> selectedServices = new ArrayList<>();
    private AdapterShowFilterToolBar.OnCancelFilterClicked onCancelFilterClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_servise_done);
        G.Activity = this;
        G.context = this;

        FindView();
        onClick();
        this.getSharedPreferences("preference", MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(() -> listService("", ""), 200);
            }
        });
        mDBHelper = new DataBaseHelper(this);
        mDatabase = mDBHelper.getReadableDatabase();

        txt_tile_action_bar.setText("لیست سرویس\u200Cها");
        txt_tile_action_bar.setTypeface(G.Bold);
        PlakListener();

        onItemClickService = new AdapterListServices.OnItemClickListener() {
            @Override
            public void onItemClick(ModelServicesCustomer model, ImageView item, AdapterListServices.ViewHolder holder, int position) {

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LastServiseDoneActivity.this, R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(LastServiseDoneActivity.this)
                        .inflate(R.layout.layout_button_sheet_service, (LinearLayout) holder.itemView.findViewById(R.id.ly_bottom_sheet_lang));
                bottomSheetDialog.setCancelable(true);
                LinearLayout ly_show_info, ly_edit_service, ly_delete_service;
                bottomSheetDialog.setContentView(bottomSheetView);
                ly_show_info = bottomSheetDialog.findViewById(R.id.ly_show_info);
                ly_edit_service = bottomSheetDialog.findViewById(R.id.ly_edit_service);
                ly_delete_service = bottomSheetDialog.findViewById(R.id.ly_delete_service);

                ly_show_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ((model.getCenter_id().length() > 0) && model.getRate_state() == 0) {
                            dialogAddComment(model.getCenter_id(), model.getId(), model, position);
                        } else {
                            bottomSheetDialog.dismiss();
                            showServiceInformationActivity(model, position);
                        }
                    }
                });
                ly_edit_service.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mDBHelper != null) {
                                    mDBHelper.deleteHistoryKhadamt(G.preference.getInt("idService", 0) + "");
                                }
                            }
                        }, 0);
                        Intent intent = new Intent(LastServiseDoneActivity.this, AddServicesActivity.class);
                        intent.putExtra("idCustomer", model.getId_customer() + "");
                        intent.putExtra("id_car", model.getCar_id() + "");
                        intent.putExtra("firstName", model.getFirst_name());
                        intent.putExtra("lastName", model.getLast_name());
                        intent.putExtra("km_now", model.getKm_now());
                        intent.putExtra("nameCar", model.getName_car());
                        intent.putExtra("km_next", model.getKm_next());
                        intent.putExtra("phone", model.getPhone());
                        intent.putExtra("plak", model.getPlak());
                        intent.putExtra("plak", model.getPlak_type());
                        intent.putExtra("date_service", model.getDate_services());
                        intent.putExtra("description", model.getDescription());
                        intent.putExtra("services_price", model.getAll_services_price());
                        intent.putExtra("avg_function", model.getAvg_function());
                        intent.putExtra("id_service", model.getId() + "");
                        intent.putExtra("car_name_id", model.getCar_name_id());
                        intent.putExtra("car_tip_id", model.getCar_tip_id());
                        intent.putExtra("car_model_id", model.getCar_model_id());
                        intent.putExtra("fuel_type_id", model.getFuel_type_id());

                        intent.putExtra("finish", "");
                        intent.putExtra("fromMain", false);
                        //    Toast.makeText(LastServiseDoneActivity.this, getIntent().getExtras().getInt("idCustomer")+"", Toast.LENGTH_SHORT).show();
                        //    intent.putExtra("type_fule", mDBHelper.getCustomersInfo(mDatabase,getIntent().getExtras().getInt("idCustomer")).get(0).getType_fuel());
                        intent.putExtra("type_fule", model.getType_fuel());
                        intent.putExtra("type_car", model.getType_car());
                        intent.putExtra("date_birthday", model.getDate_birthday());
                        intent.putExtra("gender", model.getGender());
                        startActivity(intent);
                    }
                });

                ly_delete_service.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                        deleteService(model.getId() + "");

                    }
                });
                // bottomSheetDialog.show();
                if ((model.getCenter_id().length() > 0) && model.getRate_state() == 0) {
                    dialogAddComment(model.getCenter_id(), model.getId(), model, position);
                } else {
                    bottomSheetDialog.dismiss();
                    showServiceInformationActivity(model, position);
                }

            }
        };
        G.services.clear();
        G.mscs.clear();


        recycle_services.setLayoutManager(new LinearLayoutManager(LastServiseDoneActivity.this, RecyclerView.VERTICAL, false));
        adapterListService = new AdapterListServices(LastServiseDoneActivity.this, LastServiseDoneActivity.this, G.mscs, onItemClickService);
        recycle_services.setAdapter(adapterListService);
        if (getIntent().getExtras().getInt("car_id") > 0) {
            new Handler().postDelayed(() -> listService("car_id", "" + getIntent().getExtras().getInt("car_id")), 200);
        } else {
            new Handler().postDelayed(() -> listService("", ""), 200);
        }
        recycle_services.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading && G.mscs.size() >= 20) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == G.mscs.size() - 1) {
                        page++;
                        loadingPB.setVisibility(View.VISIBLE);
                        recycle_services.post(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(adapterListService.getItemCount() - 1);
                            }
                        });
                        isLoading = true;
                        listService(last_filter, last_key);
                    }
                }
            }
        });
        getServiceTiming();
    }

    private void showServiceInformationActivity(ModelServicesCustomer model, int position) {

        Intent intent = new Intent(LastServiseDoneActivity.this, InformationServiceCarActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("idCustomer", model.getId_customer() + "");
        Log.e("idCustomer:", model.getId_customer() + "");
        Log.e("idservice:", model.getId() + "");
        intent.putExtra("id_car", model.getCar_id() + "");
        intent.putExtra("idservice", model.getId() + "");
        intent.putExtra("id_service", model.getId() + "");
        intent.putExtra("firstName", model.getFirst_name());
        intent.putExtra("lastName", model.getLast_name());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("plak", model.getPlak());
        intent.putExtra(Constants.CAR_PLATE_TYPE, model.getPlak_type());
        intent.putExtra("km_now", model.getKm_now());
        intent.putExtra("nameCar", model.getName_car());
        intent.putExtra("km_next", model.getKm_next());
        intent.putExtra("avg_function", model.getAvg_function());
        intent.putExtra("description", model.getDescription());
        intent.putExtra("date_service", model.getDate_services());
        intent.putExtra("services_price", model.getAll_services_price());
        intent.putExtra("detail_service", model.getDetail_service());
        intent.putExtra("car_name_id", model.getCar_name_id());
        intent.putExtra("car_tip_id", model.getCar_tip_id());
        intent.putExtra("car_model_id", model.getCar_model_id());
        intent.putExtra("fuel_type_id", model.getFuel_type_id());
        intent.putExtra("center_id", model.getCenter_id() + "");
        intent.putExtra("center_name", model.getCenter_name() + "");
        intent.putExtra("rate_saved", model.getRate_state());
        startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                listService(last_filter, last_key);
            }
        }

    }

    public void onclickAlamrs(View v) {
        startActivity(new Intent(LastServiseDoneActivity.this, AlarmsActivity.class));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.getSharedPreferences("preference", MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        // Log.d(TAG, "onResume: ");
        adapterShowFilterToolBar.notifyDataSetChanged();
        last_service_done_shimmer_layout.startShimmer();
    }


    public void deleteService(String service_id) {
        Log.e(G.TAG, service_id);
        G.loading(this);
        String d_id = PreferenceUtil.getD_id();
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        JSONObject object = new JSONObject();
        try {
            String deleted_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());
            object.put("deleted_at", deleted_at);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<ResponseBody> request = api.deleteService(service_id + "", G.returnBody(object.toString()));
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                assert response.body() != null;
                String result = G.getResult(response);
                if (result.length() > 0 && result.length() < 10) {
                    finish();
                    startActivity(new Intent(G.Activity, LastServiseDoneActivity.class).putExtra("idCustomer", 0));
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

    Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
    String last_filter = "";
    String last_key = "";
    int last_page = 1;

    public void listService(String filter, String key) {

        if (!last_key.equals(key)) {
            G.services.clear();
            G.mscs.clear();
            key = G.converToEn(key);
            page = 1;
        }
        last_filter = filter;
        last_key = key;
        if (page == 1) {
            swipeRefreshLayout.setRefreshing(true);
        }
        String user_id = PreferenceUtil.getUser_id();
        Call<ResponseBody> request = api.listService("cust_id,eq," + user_id, page);
        if (key.length() >= 1) {
            request = api.searchService("cust_id,eq," + user_id, filter + ",cs," + key, page);
        }
        String finalKey = key;
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @SuppressLint({"StaticFieldLeak", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                stopShimmer();
                if (!last_key.equals(finalKey) || last_page == page) {
                    G.services.clear();
                    G.mscs.clear();
                }
                last_page = page;
                G.Log(call.request().toString());
                String result = G.getResult(response);
                G.Log(result);
                JSONObject object = G.StringtoJSONObject(result);
                JSONArray array = G.ObjecttoArray(object, "records");
                if (array.length() > 0) {
                    try {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            ModelServicesCustomer msc = new ModelServicesCustomer();
                            msc.setId(obj.getInt("service_id"));
                            G.services.add(msc.getId());
                            msc.setId_khadamat(obj.getInt("service_id"));

                            msc.setCar_id(obj.getInt("car_id"));
                            String car_company_name = (obj.getString("car_company_name") + "").replace("null", "");
                            String car_tip = (obj.getString("car_tip") + "").replace("null", "");
                            String car_name = obj.getString("car_name");
                            if (car_company_name.length() > 0) {
                                car_name = car_company_name + " - " + car_name;
                            }
                            if (car_tip.length() > 0) {
                                car_name = car_name + " - " + car_tip;
                            }
                            msc.setName_car(car_name + "");
                            msc.setPlak(obj.getString("car_plate") + "");
                            msc.setPlak_type(Constants.findById(obj.getInt("car_plate_type")));
                            msc.setType_fuel(obj.getString("fuel_type") + "");
                            int car_name_id = 0, car_tip_id = 0, car_model_id = 0, fuel_type_id = 0;
                            if (obj.has("car_name_id")) {
                                car_name_id = obj.getInt("car_name_id");
                                if (!(obj.getString("car_tip_id") + "").contains("null")) {
                                    car_tip_id = obj.getInt("car_tip_id");
                                }
//                                if(!obj.getString("car_model_id").contains("null")) {
                                car_model_id = obj.getInt("car_model_id");
//                                }

                                fuel_type_id = obj.getInt("fuel_type_id");
                            }
                            msc.setCar_name_id(car_name_id);
                            msc.setCar_tip_id(car_tip_id);
                            msc.setCar_model_id(car_model_id);
                            msc.setFuel_type_id(fuel_type_id);
                            msc.setIdc(obj.getInt("user_id"));
                            msc.setId_customer(obj.getInt("user_id"));
                            msc.setFirst_name(obj.getString("f_name") + "");
                            msc.setLast_name(obj.getString("l_name") + "");
                            msc.setGender(obj.getString("sex") + "");
                            msc.setPhone(obj.getString("mobile") + "");
                            msc.setPhonec(obj.getString("mobile") + "");
                            msc.setDate_birthday(obj.getString("birthdate") + "");
                            msc.setDate_save_customer(obj.getString("cust_created_at") + "");


                            msc.setKm_next(obj.getString("km_next") + "");
                            msc.setDetail_service(obj.getString("detail_service") + "");
                            msc.setKm_now(obj.getString("km_now") + "");
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

                            msc.setAll_services_price(obj.getString("price"));
                            String centerId = obj.getString("center_id");
                            if (!centerId.equals("null")) {
                                msc.setRate_state(obj.getInt("rate_saved"));
                            }
                            msc.setCenter_id((obj.getString("center_id") + "").replace("null", ""));
                            //  msc.setCenter_id((obj.getString("center_id") + ""));
                            msc.setCenter_name((obj.getString("center_name") + "").replace("null", getString(R.string.registered_by_user)));
                            msc.setJob_category_id(obj.getString("job_category_id").replace("null", "0"));
                            G.mscs.add(msc);
                        }

                    } catch (JSONException e) {
                        G.toast("مشکل در دریافت اطلاعات");
                        e.printStackTrace();
                    }
                }
                if (page == 1) {
                    if (G.mscs.size() <= 0) {
                        ly_empty.setVisibility(View.VISIBLE);
                        recycle_services.setVisibility(View.GONE);
                    } else {
                        recycle_services.setVisibility(View.VISIBLE);
                        ly_empty.setVisibility(View.GONE);

                    }
                }
                if (page > 1) {
                    loadingPB.setVisibility(View.GONE);
                }
                swipeRefreshLayout.setRefreshing(false);
                adapterListService.notifyDataSetChanged();
                isLoading = false;
                getFilterValuesAndSetupFilters();


            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
//                G.stop_loading();
                Log.d("lastService", "updateServiceScoreState: mscs" + t.getMessage());

                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }

    private void stopShimmer() {
        last_service_done_shimmer_layout.stopShimmer();
        last_service_done_shimmer_layout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    private void FindView() {
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        img_back = findViewById(R.id.img_back);
        edt1 = findViewById(R.id.edt1);
        edt2 = findViewById(R.id.edt2);
        edt3 = findViewById(R.id.edt3);
        edt4 = findViewById(R.id.edt4);
        edt5 = findViewById(R.id.edt5);
        edt6 = findViewById(R.id.edt6);
        edt7 = findViewById(R.id.edt7);
        edt8 = findViewById(R.id.edt8);
        recycle_services = findViewById(R.id.recycle_services);
        txt_type_search = findViewById(R.id.txt_type_search);
        edt_phone = findViewById(R.id.edt_phone);
        edt_name = findViewById(R.id.edt_name);
        ly_plak = findViewById(R.id.ly_plak);
        ly_empty = findViewById(R.id.ll_empty);
        img_search_service = findViewById(R.id.img_search_service);
        edt_phone_number = findViewById(R.id.edt_phone_number);
        edt_nameFamily = findViewById(R.id.edt_nameFamily);
        loadingPB = findViewById(R.id.idPBLoading);
        swipeRefreshLayout = findViewById(R.id.swipe);
        txt_product_name = findViewById(R.id.txt_product_name);
        txt_car_name = findViewById(R.id.txt_car_name);
        txt_date = findViewById(R.id.txt_date);
        service_badi = findViewById(R.id.service_badi);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.button));
        TextView count = findViewById(R.id.count);
        count.setTypeface(G.ExtraBold);
        TextView title = findViewById(R.id.title);
        title.setTypeface(G.ExtraBold);
        recycle_show_filter = findViewById(R.id.recycle_show_filter);
        onCancelFilterClicked = new AdapterShowFilterToolBar.OnCancelFilterClicked() {
            @Override
            public void onCancelClicked(FilterModel filterModel) {
                deleteFilterItem(filterModel);
            }
        };
        adapterShowFilterToolBar = new AdapterShowFilterToolBar(this, listFilter, onCancelFilterClicked);
        last_service_done_shimmer_layout = findViewById(R.id.last_service_done_shimmer_layout);
    }

    private Handler mHandler = new Handler();

    private void onClick() {
        service_badi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  startActivity(new Intent(LastServiseDoneActivity.this, ServiceFilterActivity.class));
                startActivity(new Intent(LastServiseDoneActivity.this, ReserveActivity.class));
            }
        });

        txt_type_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogDetectTypeSearch(LastServiseDoneActivity.this, txt_type_search, ly_plak, edt_name, edt_phone, edt_phone_number, edt_nameFamily, edt1, edt2, edt3, edt4, edt5, edt6, edt7, edt8);
            }
        });

        img_search_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = edt_phone_number.getText().toString();
                String nameFamily = edt_nameFamily.getText().toString();
                String plak = edt1.getText().toString() + edt2.getText().toString() + edt3.getText().toString() + edt4.getText().toString() + edt5.getText().toString() + edt6.getText().toString() + edt7.getText().toString() + edt8.getText().toString();
                if (!TextUtils.isEmpty(phone) && TextUtils.isEmpty(nameFamily) && TextUtils.isEmpty(plak)) {
                    listService("mobile", phone);
                } else if (TextUtils.isEmpty(phone) && !TextUtils.isEmpty(nameFamily) && TextUtils.isEmpty(plak)) {
                    listService("fullname", nameFamily);
                } else if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(nameFamily) && !TextUtils.isEmpty(plak)) {
                    listService("pelak", (plak));
                } else {
                    G.toast("بر اساس شماره یا نام و نام خانوادگی جستجو کنید");
                }
            }
        });

        Runnable searchPhone = new Runnable() {
            @Override
            public void run() {
                if (edt_phone_number.getText().toString().length() > 0) {
                    String phone = edt_phone_number.getText().toString();
                    listService("mobile", phone);
                } else if (edt_phone_number.getText().toString().length() == 0) {
                    page = 1;
                    listService("", "");
                }
            }
        };
        edt_phone_number.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                swipeRefreshLayout.setRefreshing(true);
                if (mHandler != null) {
                    mHandler.removeCallbacks(searchPhone);
                    mHandler.postDelayed(searchPhone, 1000);
                }


            }
        });
        Runnable searchName = new Runnable() {
            @Override
            public void run() {
                if (edt_nameFamily.getText().toString().length() > 0) {
                    String nameFamily = edt_nameFamily.getText().toString();
                    listService("fullname", nameFamily);
                } else if (edt_nameFamily.getText().toString().length() == 0) {
                    page = 1;
                    listService("", "");
                }
            }
        };
        edt_nameFamily.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                swipeRefreshLayout.setRefreshing(true);
                if (mHandler != null) {
                    mHandler.removeCallbacks(searchName);
                    mHandler.postDelayed(searchName, 1000);
                }
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void PlakListener() {
        Runnable searchPelak = new Runnable() {
            @Override
            public void run() {
                if (edt1.getText().toString().length() > 0) {
                    String plak = edt1.getText().toString() + edt2.getText().toString() + edt3.getText().toString() + edt4.getText().toString() + edt5.getText().toString() + edt6.getText().toString() + edt7.getText().toString() + edt8.getText().toString();
                    listService("pelak", (plak));
                } else if (edt1.getText().toString().length() == 0) {
                    page = 1;
                    listService("", "");
                }
            }
        };
        edt1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edt1.getText().toString().length() == 1)     //size as per your requirement
                {
                    edt2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                swipeRefreshLayout.setRefreshing(true);
                if (mHandler != null) {
                    mHandler.removeCallbacks(searchPelak);
                    mHandler.postDelayed(searchPelak, 1000);
                }
            }

        });

        edt2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edt2.getText().toString().length() == 1)     //size as per your requirement
                {
                    edt3.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                swipeRefreshLayout.setRefreshing(true);
                if (mHandler != null) {
                    mHandler.removeCallbacks(searchPelak);
                    mHandler.postDelayed(searchPelak, 1000);
                }

            }

        });

        edt3.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edt3.getText().toString().length() == 1)     //size as per your requirement
                {
                    edt4.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                swipeRefreshLayout.setRefreshing(true);
                if (mHandler != null) {
                    mHandler.removeCallbacks(searchPelak);
                    mHandler.postDelayed(searchPelak, 2000);
                }

            }

        });

        edt4.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edt4.getText().toString().length() == 1)     //size as per your requirement
                {
                    edt5.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                swipeRefreshLayout.setRefreshing(true);
                if (mHandler != null) {
                    mHandler.removeCallbacks(searchPelak);
                    mHandler.postDelayed(searchPelak, 1000);
                }

            }

        });

        edt5.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edt5.getText().toString().length() == 1)     //size as per your requirement
                {
                    edt6.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                swipeRefreshLayout.setRefreshing(true);
                if (mHandler != null) {
                    mHandler.removeCallbacks(searchPelak);
                    mHandler.postDelayed(searchPelak, 1000);
                }

            }

        });

        edt6.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edt6.getText().toString().length() == 1)     //size as per your requirement
                {
                    edt7.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                swipeRefreshLayout.setRefreshing(true);
                if (mHandler != null) {
                    mHandler.removeCallbacks(searchPelak);
                    mHandler.postDelayed(searchPelak, 1000);
                }

            }

        });

        edt7.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (edt7.getText().toString().length() == 1)     //size as per your requirement
                {
                    edt8.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                swipeRefreshLayout.setRefreshing(true);
                if (mHandler != null) {
                    mHandler.removeCallbacks(searchPelak);
                    mHandler.postDelayed(searchPelak, 1000);
                }

            }

        });
        edt8.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {


            }

            public void afterTextChanged(Editable s) {
                swipeRefreshLayout.setRefreshing(true);
                if (mHandler != null) {
                    mHandler.removeCallbacks(searchPelak);
                    mHandler.postDelayed(searchPelak, 1000);
                }

            }

        });
        edt8.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    edt1.requestFocus();
                    img_search_service.performClick();
                    return true;
                }
                return false;
            }


        });
        View.OnKeyListener keylistener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && !keydel) {
                    keydel = true;
                    if (v == edt8) {
                        edt8.setText("");
                        edt7.requestFocus();
                    } else if (v == edt7) {
                        edt7.setText("");
                        edt6.requestFocus();
                    } else if (v == edt6) {
                        edt6.setText("");
                        edt5.requestFocus();
                    } else if (v == edt5) {
                        edt5.setText("");
                        edt4.requestFocus();
                    } else if (v == edt4) {
                        edt4.setText("");
                        edt3.requestFocus();
                    } else if (v == edt3) {
                        edt3.setText("");
                        edt2.requestFocus();
                    } else if (v == edt2) {
                        edt2.setText("");
                        edt1.requestFocus();
                    } else if (v == edt1) {
                        edt1.setText("");
                        edt1.requestFocus();
                    }
//                    edt1.setText("");
//                    edt2.setText("");
//                    edt3.setText("");
//                    edt4.setText("");
//                    edt5.setText("");
//                    edt6.setText("");
//                    edt7.setText("");
//                    edt8.setText("");
//                    edt1.requestFocus();
                } else {
                    keydel = false;
                }
                return false;
            }
        };
        edt1.setOnKeyListener(keylistener);
        edt2.setOnKeyListener(keylistener);
        edt3.setOnKeyListener(keylistener);
        edt4.setOnKeyListener(keylistener);
        edt5.setOnKeyListener(keylistener);
        edt6.setOnKeyListener(keylistener);
        edt7.setOnKeyListener(keylistener);
        edt8.setOnKeyListener(keylistener);
    }

    void DialogDetectTypeSearch(Context context, TextView textView, LinearLayout ly_plak, TextInputLayout edt_name, TextInputLayout edt_phone, EditText edt_phone_number, EditText edt_nameFamily, EditText ed1, EditText ed2, EditText edt3, EditText edt4, EditText edt5, EditText edt6, EditText edt7, EditText edt8) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_item_dialog_type_search, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(true);

        alertDialogs_detect_type_search = alertDialogBuilder.create();

        alertDialogs_detect_type_search.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        alertDialogs_detect_type_search.setCancelable(false);
        WindowManager.LayoutParams layoutParams = alertDialogs_detect_type_search.getWindow().getAttributes();
        alertDialogs_detect_type_search.getWindow().setAttributes(layoutParams);
        alertDialogs_detect_type_search.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shap_simple_rec));
        alertDialogs_detect_type_search.show();
        ImageView img_close;
        TextView txt_search_plak, txt_search_phone, txt_search_name;
        img_close = view.findViewById(R.id.img_close);
        txt_search_plak = view.findViewById(R.id.txt_search_plak);
        txt_search_phone = view.findViewById(R.id.txt_search_phone);
        txt_search_name = view.findViewById(R.id.txt_search_name);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialogs_detect_type_search.dismiss();
            }
        });

        txt_search_plak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(txt_search_plak.getText().toString());
                ly_plak.setVisibility(View.VISIBLE);
                edt_name.setVisibility(View.GONE);
                edt_phone.setVisibility(View.GONE);
                alertDialogs_detect_type_search.dismiss();
                edt_nameFamily.setText("");
                edt_phone_number.setText("");
            }
        });
        txt_search_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(txt_search_phone.getText().toString());
                ly_plak.setVisibility(View.GONE);
                edt_name.setVisibility(View.GONE);
                edt_phone.setVisibility(View.VISIBLE);
                alertDialogs_detect_type_search.dismiss();
                edt_nameFamily.setText("");
                ed1.setText("");
                ed2.setText("");
                edt3.setText("");
                edt4.setText("");
                edt5.setText("");
                edt6.setText("");
                edt7.setText("");
                edt8.setText("");

            }
        });
        txt_search_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(txt_search_name.getText().toString());
                ly_plak.setVisibility(View.GONE);
                edt_name.setVisibility(View.VISIBLE);
                edt_phone.setVisibility(View.GONE);
                alertDialogs_detect_type_search.dismiss();
                edt_phone_number.setText("");
                ed1.setText("");
                ed2.setText("");
                edt3.setText("");
                edt4.setText("");
                edt5.setText("");
                edt6.setText("");
                edt7.setText("");
                edt8.setText("");

            }
        });
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        width = (int) ((width) * ((double) 9 / 10));
        alertDialogs_detect_type_search.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    public void getServiceTiming() {
        service_badi.setVisibility(View.GONE);

        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getReportTiming(PreferenceUtil.getUser_id() + "", 9999 + "");


        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                G.Log(call.request().toString());
                if (response.body() != null) {
                    try {
                        String result = response.body().string();
                        G.Log(result);
                        JSONArray records = G.StringtoJSONArray(result);
                        if (records.length() > 0) {
                            for (int i = 0; i < records.length(); i++) {
                                ServiceTiming ST = new ServiceTiming();
                                JSONObject obj = records.getJSONObject(i);
                                int sc_id = obj.getInt("id");
                                ST.setId(sc_id);
                                String product_group_name = obj.getString("product_group_name");
                                ST.setProduct_group_name(product_group_name + "");
                                String product_name = obj.getString("product_name");
                                ST.setProduct_name(product_name + "");
                                String car_name = obj.getString("car_name");
                                ST.setCar_name(car_name + "");
                                String car_company_name = obj.getString("car_company_name");
                                ST.setCar_company_name(car_company_name + "");
                                String due_date = obj.getString("due_date");
                                ST.setService_date_time(due_date + "");
                                String center_name = obj.getString("center_name");
                                ST.setCenter_name(center_name + "");
                                String km_now = obj.getString("km_now");
                                ST.setKm_now(km_now + "");
                                String km_usage = obj.getString("km_usage");
                                int km_usage_int = 0;
                                if (km_usage != "null") {
                                    km_usage_int = Integer.parseInt(km_usage);
                                }
                                int km_next = Integer.parseInt(km_now) + km_usage_int;
                                ST.setKm_next(km_next + "");
                                String visited_change = (obj.getString("visited_change") + "");
                                ST.setChanged(visited_change.equals("1"));
                                if (i == 0) {
                                    String type = "بازدید";
                                    if (ST.isChanged()) {
                                        type = "تعویض";
                                    }
                                    service_badi.setVisibility(View.VISIBLE);
                                    txt_product_name.setText(type + " " + product_group_name);
                                    txt_car_name.setText("خودروی " + car_name);
                                    txt_date.setText("" + G.toShamsi(due_date));
                                }
                            }
                        }
                    } catch (JSONException | IOException e) {
                        G.toast("مشکل در تجزیه اطلاعات");
                        e.printStackTrace();
                    }
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
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context));
    }


    public void addComment(String content, float score, String center_id, int service_id, ModelServicesCustomer model, int position) {
        try {
            G.loading(LastServiseDoneActivity.this);
        }
        catch (WindowManager.BadTokenException e) {
            G.Log(e.getMessage());
        }
        String user_id = PreferenceUtil.getUser_id();
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);

        JSONObject object = new JSONObject();
        String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());
        try {
            object.put("user_id", user_id);
            object.put("service_center_id", center_id);
            object.put("parent_id", JSONObject.NULL);
            object.put("content", content);
            object.put("score", score);
            object.put("date_time", created_at);
            object.put("status", 0);
            object.put("created_at", created_at);
            object.put("updated_at", created_at);
            object.put("deleted_at", JSONObject.NULL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<ResponseBody> request = api.addComment(G.returnBody(object.toString()));

        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                //G.stop_loading();
                String result = G.getResult(response);
                if (result.length() > 0 && result.length() < 10) {
                    G.toast("با موفقیت ثبت شد پس از تأیید در لیست نظرات قرار می\u200Cگیرد.");
//                   findViewById(R.id.add_comment).setVisibility(View.GONE);
                    updateServiceScoreState(service_id, 1, model, position);
                } else {
                    G.toast("مشکل در ثبت اطلاعات");
                    G.stop_loading();
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });
    }

    public void updateServiceScoreState(int service_id, int state, ModelServicesCustomer model, int position) {
        //G.loading(this);
        String user_id = PreferenceUtil.getUser_id();
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);

        JSONObject object = new JSONObject();
        try {
            object.put("rate_saved", state);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<ResponseBody> request = api.updateServiceScoreState(String.valueOf(service_id) + "", G.returnBody(object.toString()));

        Log.d("lastService", "updateServiceScoreState: request" + request.request().toString());
        Log.d("lastService", "updateServiceScoreState: service_id" + service_id);
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                G.stop_loading();
                String result = G.getResult(response);
                Log.d("lastService", "updateServiceScoreState: result" + result);
                model.setRate_state(state);
                G.mscs.set(position, model);
                adapterListService.notifyDataSetChanged();
                showServiceInformationActivity(model, position);

                /*if (result.length() > 0 && result.length() < 10) {
                    //G.toast("با موفقیت ثبت شد پس از تأیید در لیست نظرات قرار می\u200Cگیرد.");
                    listService(last_filter, last_key);
                    showServiceInformationActivity(model, position);

                } else {
                    G.toast("مشکل در ثبت اطلاعات");
                }*/

            }


            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });
    }


    public void dialogAddComment(String center_id, int service_id, ModelServicesCustomer model, int position) {

        final Dialog[] dialog = {new Dialog(this)};
        dialog[0].setContentView(R.layout.dialog_add_comment);
        dialog[0].show();
        final EditText[] edt_content = {dialog[0].findViewById(R.id.edt_content)};
        RatingBar rt = dialog[0].findViewById(R.id.ratingBar1);
        final float[] rtscore = {0};
        rt.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rtscore[0] = rt.getRating();
            }
        });


//        EditText add_score=findViewById(R.id.add_score);
//        int score= Integer.parseInt(add_score.getText().toString());
        dialog[0].findViewById(R.id.img_close).setOnClickListener(view -> dialog[0].dismiss());
        dialog[0].findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = edt_content[0].getText().toString();
                dialog[0].dismiss();
                addComment(content, rtscore[0], center_id, service_id, model, position);
            }
        });
        dialog[0].findViewById(R.id.btn_rate_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog[0].dismiss();
                updateServiceScoreState(service_id, 2, model, position);
            }
        });

    }


    private void setupFilter(LinkedHashMap<Integer, String> selectedJobCategories, ModelCustomer selectedCar, boolean filterBasedJobCategory,boolean filterBasedCarId) {
        ly_filtering = findViewById(R.id.ly_filtering);

        LinearLayout img_filter = findViewById(R.id.ly_show_all);
        recycle_show_filter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycle_show_filter.setAdapter(adapterShowFilterToolBar);
       // selectedJobCategoryID = G.getFilterJobCategoryIds();
        String selectedCarName = selectedCar.getName_car();
        if(filterBasedCarId){
            FilterModel selectedCarFilterModel = new FilterModel(selectedCar.getId(), selectedCar.getName_car(), false, true);
            listFilter.add(selectedCarFilterModel);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && filterBasedJobCategory) {
            selectedJobCategories.forEach((jobCategoryId, jobCategoryTitle) -> {
                listFilter.add(new FilterModel(jobCategoryId,jobCategoryTitle, true, false));
            });
        }

        adapterShowFilterToolBar.swapList(listFilter);
       // adapterShowFilterToolBar.notifyDataSetChanged();

        img_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LastServiseDoneActivity.this, ServiceFilterActivity.class));
            }
        });
        ly_filtering.setVisibility(View.VISIBLE);
        filterServices(filterBasedJobCategory, filterBasedCarId);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Log.i(TAG, "onSharedPreferenceChanged: ");
        if (key.equals(G.KEY_FILTER_ITEM_CHANGED)) {
            //  Log.i(TAG, "onSharedPreferenceChanged: ");
          getFilterValuesAndSetupFilters();
        }
        G.updateFilterItemChanged();
    }

    private void getFilterValuesAndSetupFilters(){
        listFilter = new ArrayList<>();
        selectedCar = G.getFilterCar();
        if(selectedJobCategories != null) selectedJobCategories.clear();
        selectedJobCategories = G.getFilterJobCategoryIds();
        if(selectedJobCategories == null) selectedJobCategories = new LinkedHashMap<>();
        boolean filterBasedJobCategory = false;
        boolean filterBasedCarId = false;
        if(selectedJobCategories.size() > 0) filterBasedJobCategory = true;
        if (selectedCar.getCar_id() != 0) filterBasedCarId = true;
        Log.d("FilterService", "filterServices:service:selectedCar: "+ selectedCar.getCar_id());
        setupFilter(selectedJobCategories, selectedCar, filterBasedJobCategory, filterBasedCarId);
    }

    private void filterServices(boolean filterBasedJobCategory,boolean filterBasedCarId) {
        selectedServices.clear();
        for (int i = 0; i < G.mscs.size(); i++) {
            ModelServicesCustomer service = G.mscs.get(i);
            Log.d("FilterService", "filterServices:service:jobCategoryId: "+ service.getJob_category_id());
            Log.d("FilterService", "filterServices:service:filterBasedCarId: "+ filterBasedCarId);
            if (filterBasedJobCategory && !filterBasedCarId){
                if(selectedJobCategories.containsKey(Integer.parseInt(service.getJob_category_id()))){
                    selectedServices.add(service);
                }
            }else if(!filterBasedJobCategory && filterBasedCarId){
                if(service.getCar_id() == selectedCar.getCar_id()){
                    selectedServices.add(service);
                }
            } else if(filterBasedJobCategory && filterBasedCarId){
                if ((selectedJobCategories.containsKey(Integer.parseInt(service.getJob_category_id()))) && service.getCar_id() == selectedCar.getCar_id()){
                    selectedServices.add(service);
                }
            } else {
                selectedServices.add(service);
            }
        }

        adapterListService.swapList(selectedServices);
    }

    private void deleteFilterItem(FilterModel filterModel){
        if(filterModel.isCarFilter()){
            G.deleteCarFilter();
        } else if(filterModel.isJobCategoryFilter()){
            G.deleteJobCategoryFilter(filterModel.getId());
        }
        listFilter.remove(filterModel);
        adapterShowFilterToolBar.notifyDataSetChanged();
    }


}
