package com.servicea.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.servicea.adapter.AdapterJobCategory;
import com.servicea.adapter.AdapterJobCategorySsecond;
import com.servicea.adapter.AdapterServiceCenter;
import com.servicea.app.DataBaseHelper;
import com.servicea.app.G;
import com.servicea.app.RecyclerItemClickListener;
import com.servicea.model.ModelJobCategory;
import com.servicea.model.ServiceCenter;
import com.servicea.model.SliderItem;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.servicea.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ListServiceCenterActivity extends AppCompatActivity {
    private TextView txt_tile_action_bar, count;
    private ImageView img_back, img_add_message;
    private RecyclerView recycle_produce_group;
    public static List<ServiceCenter> serviceCenters = new ArrayList<>();
    // public static List<ModelSaveKhadamat> save = new ArrayList<>();
    private AdapterServiceCenter adapterServiceCenter;
    private CheckBox checkAll;
    private final boolean check = false;
    private DataBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    String onvan = "";
    private Handler handler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout service_centers_list_shimmer_layout;
    private ViewGroup searchbar_layout;
    private Spinner spinner_job;
    private boolean fromrefresh = false;
    private EditText search;
    private RecyclerView recycle_done_service_type2;

    private int SelectedAddressID;

    private String SelectedAddressLat = "0";
    private String SelectedAddressLng ;

    private final String centerScore = "0";

    private boolean forceLoading = false;

    private static final String distance = "2000";

    private AdapterServiceCenter.OnCenterClickListener onCenterClickListener;

    public void onclickAlamrs(View v) {
        startActivity(new Intent(ListServiceCenterActivity.this, AlarmsActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (forceLoading) {
            getInitialInfromation();
            getJob_categories();
            startShimmer();


        }
    }

    private void startShimmer() {
        service_centers_list_shimmer_layout.startShimmer();
    }

    private void stopShimmer() {
        service_centers_list_shimmer_layout.stopShimmer();
        service_centers_list_shimmer_layout.setVisibility(GONE);
        swipeRefreshLayout.setVisibility(VISIBLE);
        searchbar_layout.setVisibility(VISIBLE);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_service_center);
        G.Activity = this;
        G.context = this;
        FindView();
        onClick();
        txt_tile_action_bar.setText("لیست مراکز");
        txt_tile_action_bar.setTypeface(G.Bold);
        txt_tile_action_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forceLoading = true;
                startActivity(new Intent(ListServiceCenterActivity.this, ListAddressesActivity.class));
            }
        });
        mDBHelper = new DataBaseHelper(this);
        mDatabase = mDBHelper.getReadableDatabase();

        search = findViewById(R.id.search);
        handler = new Handler();
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !fromrefresh) {

                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getServiceCenters(search.getText().toString(), listJobsIds.get(spinner_job.getSelectedItemPosition()));
                        }
                    }, 250);

                } else if (s.length() == 0) {
//                    getJob_services("", listJobsIds.get(spinner_job.getSelectedItemPosition()));
                    getServiceCenters("", G.preference.getInt("CasheSelectedJobCategory", 0));
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.button));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (listJobsIds.size() > 0) {
                            fromrefresh = true;
//                            getJob_services("", listJobsIds.get(spinner_job.getSelectedItemPosition()));
                            getServiceCenters("", G.preference.getInt("CasheSelectedJobCategory", 0));
                        } else {

//                            getJob_services("", 0);
                        }
//                        search.setText("");
                    }
                }, 250);
            }
        });
        getInitialInfromation();
        getJob_categories();

    }

    private final List<String> listJobs = new ArrayList<>();
    private final List<Integer> listJobsIds = new ArrayList<>();
    private ArrayAdapter spinnerAdapter;

    public class PG {
        public PG() {
        }

        public int id;
        public boolean status;
    }

    private final List<PG> enables = new ArrayList<>();
    public boolean job_cat_downloaded = false;

    public void getJob_categories() {
        listJobs.clear();
        listJobsIds.clear();
        listJobs.add("دسته شغلی");
        listJobsIds.add(0);
        spinnerAdapter = new ArrayAdapter(ListServiceCenterActivity.this, R.layout.item_spiner, listJobs);
        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_job.setAdapter(spinnerAdapter);
        spinner_job.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!fromrefresh) {
//                    getJob_services("", listJobsIds.get(spinner_job.getSelectedItemPosition()));
                    getServiceCenters("", G.preference.getInt("CasheSelectedJobCategory", 0));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getJob_categories();
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                job_cat_downloaded = true;
                assert response.body() != null;
                try {
                    String result = response.body().string();
                    JSONObject object = G.StringtoJSONObject(result);
                    JSONArray records = object.getJSONArray("records");
                    if (records.length() > 0) {
                        List<SliderItem> sliderItemList = new ArrayList<>();
                        int posi = 0;
                        List<ModelJobCategory> listJobCategory = new ArrayList<>();
//                        ModelJobCategory modelJobCategory = new ModelJobCategory();
//                        modelJobCategory.setId(-1);
//                        modelJobCategory.setTitle("همه");
//                        if (G.preference.getInt("CasheSelectedJobCategory", -1) > 0) {
//                            modelJobCategory.setStatus(0);
//                        } else {
//                            modelJobCategory.setStatus(1);
//                        }


//                        listJobCategory.add(modelJobCategory);
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject obj = records.getJSONObject(i);
                            SliderItem sliderItem = new SliderItem();
                            int id = obj.getInt("id");
                            String title = obj.getString("title");
                            if (id == G.preference.getInt("job_category_id", 1)) {
                                posi = i + 1;
                            }
                            ModelJobCategory modelJobCategory = new ModelJobCategory();
                            modelJobCategory.setId(id);
                            modelJobCategory.setTitle(title);
                            modelJobCategory.setStatus(0);

                            if (id == G.preference.getInt("CasheSelectedJobCategory", 0)) {
                                modelJobCategory.setStatus(1);
                            }


                            listJobCategory.add(modelJobCategory);
                            listJobs.add(title);
                            listJobsIds.add(id);
                        }
                        // layout for choose service type
                     /*   recycle_done_service_type2 = findViewById(R.id.recycle_done_service_type1);
                        recycle_done_service_type2.setLayoutManager(new LinearLayoutManager(ListServiceCenterActivity.this, RecyclerView.HORIZONTAL, false));
                        AdapterJobCategorySsecond adapterJobCategory1 = new AdapterJobCategorySsecond(ListServiceCenterActivity.this, listJobCategory);
                        recycle_done_service_type2.setAdapter(adapterJobCategory1);
                        recycle_done_service_type2.addOnItemTouchListener(
                                new RecyclerItemClickListener(G.context, recycle_done_service_type2, new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        for (int i = 0; i < listJobCategory.size(); i++) {
                                            listJobCategory.get(i).setStatus(0);
                                        }

                                        listJobCategory.get(position).setStatus(1);
                                        adapterJobCategory1.notifyDataSetChanged();
                                        G.preference.edit().putInt("CasheSelectedJobCategory", listJobCategory.get(position).getId()).apply();
                                        getServiceCenters("", G.preference.getInt("CasheSelectedJobCategory", 0));
                                    }

                                    @Override
                                    public void onLongItemClick(View view, int position) {
                                        // do whatever
                                    }
                                })
                        );*/
                        spinnerAdapter.notifyDataSetChanged();
                        int finalPosi = posi;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                spinner_job.post(new Runnable() {
                                    public void run() {
                                        spinner_job.setSelection(finalPosi);
                                    }
                                });
                            }
                        }, 10);
                        getServiceCenters("", G.preference.getInt("CasheSelectedJobCategory", 0));
                    } else {
                        G.toast("هیچ دسته شغلی یافت نشد!");
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
        spinner_job = findViewById(R.id.spinner_job);
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        img_back = findViewById(R.id.img_back);
        img_add_message = findViewById(R.id.img_add_message);
        checkAll = findViewById(R.id.checkAll);
        swipeRefreshLayout = findViewById(R.id.swipe);

        recycle_produce_group = findViewById(R.id.recycle_produce_group);
        count = findViewById(R.id.count);
        count.setTypeface(G.ExtraBold);
        service_centers_list_shimmer_layout = findViewById(R.id.service_centers_list_shimmer_layout);
        searchbar_layout = findViewById(R.id.searchbar_layout);
    }

    private void onClick() {
        onCenterClickListener = new AdapterServiceCenter.OnCenterClickListener() {
            @Override
            public void onCenterClick(ServiceCenter center) {
                forceLoading = false;
                Intent intentStartsServiceCenterActivity = new Intent(ListServiceCenterActivity.this, ServiceCenterActivity.class);
                intentStartsServiceCenterActivity.putExtra("id", String.valueOf(center.getId()));
                intentStartsServiceCenterActivity.putExtra("services_count", center.getServicesCount());
                startActivity(intentStartsServiceCenterActivity);
            }
        };
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void getInitialInfromation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SelectedAddressID = G.preference.getInt("SelectedAddressID", 0);
                String SelectedAddressTitle = G.preference.getString("SelectedAddressTitle", "");
                SelectedAddressLat = G.preference.getString("SelectedAddressLat", "0");
                SelectedAddressLng = G.preference.getString("SelectedAddressLng", "0");
                G.Log("SelectedAddressID:" + SelectedAddressID);
                G.Log("SelectedAddressLat:" + SelectedAddressLat);
                G.Log("SelectedAddressLng:" + SelectedAddressLng);
                G.Log("SelectedAddressTitle:" + SelectedAddressTitle);
                G.Log("SelectedAddressTitleLength:" + SelectedAddressTitle.length());

                if (SelectedAddressID > 0 && SelectedAddressTitle.length() >= 0) {
                    txt_tile_action_bar.setText(SelectedAddressTitle + " ▼");
//                    txt_tile_action_bar.setTextDirection(View.TEXT_DIRECTION_LOCALE);
                } else {
                    txt_tile_action_bar.setText("تعیین آدرس ▼");
                    Toast.makeText(ListServiceCenterActivity.this, "ابتدا آدرس مورد نظر خود را انتخاب نمایید", Toast.LENGTH_SHORT).show();
                    if (!forceLoading) {
                        startActivity(new Intent(ListServiceCenterActivity.this, ListAddressesActivity.class));
                    }
                    forceLoading = true;
                }
                if (job_cat_downloaded) {
                    getServiceCenters("", G.preference.getInt("CasheSelectedJobCategory", 0));
                }
            }
        }, 500);


        G.preference.edit().putInt("CasheSelectedJobCategory", -1).apply();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("job_id")) {
            int job_id = intent.getIntExtra("job_id", -1);
            G.preference.edit().putInt("CasheSelectedJobCategory", job_id).apply();

        }

        G.Activity = this;
        G.context = this;
    }

    public void getServiceCenters(String key, int job_id) {
        count.setText("تعداد مراکز: 0");
        serviceCenters.clear();
        recycle_produce_group.setLayoutManager(new LinearLayoutManager(ListServiceCenterActivity.this, RecyclerView.VERTICAL, false));
        adapterServiceCenter = new AdapterServiceCenter(ListServiceCenterActivity.this, serviceCenters, onCenterClickListener);
//                  adapterListProduceGroup = new AdapterListProduceGroup(ProductGroupActivity.this, mDBHelper.getListProductGroup(mDatabase));
        recycle_produce_group.setAdapter(adapterServiceCenter);

        if (!SelectedAddressLat.equals("0")) {
            swipeRefreshLayout.setRefreshing(true);

            if (true) {

                Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);

            /*Call<ResponseBody> request = api.getServiceCenter("id,gt," + 0, "center_name,cs," + key, 80);
            if (job_id > 0) {
                request = api.getServiceCenter("job_category_id,eq," + job_id, "center_name,cs," + key, 80);
            }*/

                Call<ResponseBody> request = api.getNearServiceCenter(null, "center_name,cs," + key, SelectedAddressLat, SelectedAddressLng, distance);
                if (job_id > 0) {
                    request = api.getNearServiceCenter(String.valueOf(job_id), "center_name,cs," + key, SelectedAddressLat, SelectedAddressLng, distance);
                }
                Log.d("ListService", "getServiceCenters: request" + request.request().url());
                Log.d("ListService", "getServiceCenters: key" + key);
                Log.d("ListService", "getServiceCenters: job_id" + job_id);
                request.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        serviceCenters.clear();
                        G.Log(call.request().toString());
                        // Log.d("ListService", "getServiceCenters: response " + response.body().toString());
                        if (response.body() != null) {
                            try {
                                String result = response.body().string();
                                G.Log(result);
                                Log.d("ListService", "getServiceCenters: result " + result);

                                JSONArray records = G.StringtoJSONArray(result);
                                //JSONArray records = object.getJSONArray("records");
                                count.setText("تعداد مراکز: " + records.length());
                                if (records.length() > 0) {
                                    for (int i = 0; i < records.length(); i++) {
                                        ServiceCenter SC = new ServiceCenter();
                                        JSONObject obj = records.getJSONObject(i);
                                        int sc_id = obj.getInt("id");
                                        SC.setId(sc_id);
                                        String sc_name = obj.getString("center_name");
                                        SC.setTitle(sc_name);
                                        String sc_phone = obj.getString("phone");
                                        if (obj.has("user_id")) {
                                            JSONObject user = obj.getJSONObject("user");
                                            //JSONObject user = obj.getJSONObject("user_id");
                                            int user_id = user.getInt("id");
                                            String f_name = user.getString("f_name");
                                            String l_name = user.getString("l_name");
                                            String mobile = user.getString("mobile");

                                            if (user.has("profile_photo")) {
                                                String profile_photo = user.getString("profile_photo");
                                                SC.setProfile(profile_photo);

                                            }
                                            if (user.has("header_photo")) {
                                                String header_photo = user.getString("header_photo");
                                                SC.setHeader(header_photo);
                                            }
                                            SC.setPercent("50%");
//                                        SC.setScore("4.2");
                                            String sex = user.getString("sex");
                                            String birthdate = user.getString("birthdate");
                                            int province_id = user.getInt("province_id");
                                            int city_id = user.getInt("city_id");
                                        }
                                    /*if (obj.has("job_category_id")) {
                                        JSONObject category = obj.getJSONObject("job_category_id");
                                        int cat_id = category.getInt("id");
                                        String cat_title = category.getString("title");
                                        SC.setCategory(cat_title);
                                    }*/

                                        String f_open = obj.getString("f_open");
                                        String f_close = obj.getString("f_close");
                                        String l_open = obj.getString("l_open");
                                        String l_close = obj.getString("l_close");
                                        String numOfBranch = obj.getString("numOfBranch");
                                        int waiting_place = obj.getInt("waiting_place");
                                        int bar_serv = obj.getInt("bar_serv");
                                        int holidays = obj.getInt("holidays");
                                        int mobile_services = obj.getInt("mobile_services");
                                        String physical_service = obj.getString("physical_service");
                                        int checking = obj.getInt("checking");
                                        String sc_address = obj.getString("address");
                                        SC.setDesc(sc_address);
                                        String latitude = obj.getString("latitude");
                                        String longitude = obj.getString("longitude");


                                        String services_count = obj.getString("services_count");
                                        SC.setServicesCount(services_count);

                                        String created_at = obj.getString("created_at");
                                        //  String updated_at = obj.getString("updated_at");


                                 /*   if (obj.has("password")) {
                                        String password = obj.getString("password");
                                        String now_password = G.preference.getString("PASSWORD", "");
                                        if (now_password.length() < 3) {
                                            G.preference.edit().putString("PASSWORD", password).apply();
                                        }
                                    }*/

                                        serviceCenters.add(SC);
                                    }
                                } else {
                                    G.toast("موردی یافت نشد!");
                                }

                            } catch (JSONException | IOException e) {
                                G.toast("مشکل در تجزیه اطلاعات");
                                e.printStackTrace();
                            }

                        }
                  /*  for (int i = 0; i < serviceCenters.size(); i++) {
                        ServiceCenter serviceCenter = serviceCenters.get(i);
                        getAvgScore(i, serviceCenter);
                        Log.d("Guide", "serviceCentersScores: " + serviceCenters.get(i).getScore());
                    }*/
                        G.stop_loading();
                        adapterServiceCenter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        if (serviceCenters.size() > 0) {
                            getCentersScore();
                        } else {
                            stopShimmer();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        G.stop_loading();
                        G.toast("مشکل در برقراری ارتباط");
                    }
                });

            }

        } else stopShimmer();
    }

    public void getCentersScore() {
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        for (int i = 0; i < serviceCenters.size(); i++) {
            array.put(serviceCenters.get(i).getId());
        }
        try {
            object.put("centers_list", array);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        G.Log(object.toString());

        Call<ResponseBody> request = api.getCentersScore(G.returnBody(object.toString()));
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                stopShimmer();
                G.Log(call.request().toString());
                assert response.body() != null;
                HashMap<String, String> centersScoreTemp = new HashMap<>();
                LinkedHashMap<String, String> centersScore = new LinkedHashMap<>();
                try {
                    G.Log(call.request().toString());
                    String result = response.body().string();
                    G.Log(result);
                    if (result.startsWith("[")) {
                        JSONArray obj = G.StringtoJSONArray(result);
                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject center = obj.getJSONObject(i);
                            double score = Double.parseDouble(center.getString("score")) / Double.parseDouble(center.getString("count"));
                            centersScoreTemp.put(center.getString("service_center_id"), String.format("%.2f", score));

                        }

                    } else {
                        G.toast("مشکل در دریافت اطلاعات");
                        Log.d("ServiceCenterActivity", "getServicesCount: Error");
                    }
                } catch (JSONException | IOException e) {
                    G.toast("مشکل در تجزیه اطلاعات");
                    e.printStackTrace();
                }
                G.stop_loading();
                List<ServiceCenter> centersList = new ArrayList<>();
                if (centersScoreTemp.size() > 0) {
                    centersList.addAll(serviceCenters);
                    serviceCenters.clear();
                    centersScore = centersScoreTemp.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
                    centersScore.forEach((centerId, centerScore) -> {
                        for (int j = 0; j < centersList.size(); j++) {
                            if (centersList.get(j).getId() == Integer.parseInt(centerId)) {
                                ServiceCenter center = centersList.get(j);
                                center.setScore(centerScore);
                                serviceCenters.add(center);
                                centersList.remove(center);
                            }
                        }
                    });

                    for (int i = 0; i < centersList.size(); i++) {
                        serviceCenters.add(centersList.get(i));
                    }
                    adapterServiceCenter.notifyDataSetChanged();

                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }

    public String getAvgScore(int index, ServiceCenter serviceCenter) {
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getScore("service_center_id,eq," + serviceCenter.getId());
        Log.d("Guide", "getAvgScore: request:" + request.request());
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.body() != null) {
                    double avgScore = 0;
                    try {
                        String result = response.body().string();
                        Log.d("Guide", "getAvgScore: result:" + result);
                        JSONObject obje = G.StringtoJSONObject(result);
                        JSONArray records = obje.getJSONArray("records");
                        if (records.length() > 0) {
                            for (int i = 0; i < records.length(); i++) {
                                JSONObject obj = records.getJSONObject(i);
                                double score = obj.getDouble("score");
                                avgScore += score;
                            }
                            avgScore = avgScore / records.length();
                        }
                    } catch (JSONException | IOException e) {
                        G.toast("مشکل در تجزیه اطلاعات");
                        e.printStackTrace();
                    }
                    if (avgScore > 0) {
                        serviceCenter.setScore(String.format("%.2f", avgScore));
                    } else {
                        serviceCenter.setScore(String.valueOf(avgScore));
                    }
                    serviceCenters.set(index, serviceCenter);
                    if (index == serviceCenters.size() - 1) {
                        adapterServiceCenter.notifyDataSetChanged();
                    }
                }
                //  G.stop_loading();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                //  G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });
        return centerScore;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}