package com.servicea.activity;


import static com.servicea.app.G.FILTER_CAR_ID;
import static com.servicea.app.G.FILTER_CAR_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.servicea.adapter.AdapterFilterCheckBox;
import com.servicea.adapter.AdapterFilterRadioButton;
import com.servicea.adapter.GridSpacingItemDecoration;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.ModelJobCategory;
import com.servicea.model.SliderItem;
import com.servicea.model.dbModel.ModelCustomer;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ir.servicea.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


public class ServiceFilterActivity extends AppCompatActivity {

    private AdapterFilterCheckBox adapter;
    private AdapterFilterRadioButton adapterFilterRadioButton;
    private List<ModelJobCategory> listJobCategory;
    private AdapterFilterRadioButton.ItemClickListener itemClickListener;
    private List<ModelCustomer> productList = new ArrayList<>();
    private RecyclerView recycler,recyclerView;
    private Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
    private LinkedHashMap<Integer, String> selectedJobCategoryIds = new LinkedHashMap<>();
    private ModelCustomer selectedCar;

    private TextView txt_title_job_categories, txt_title_car, txt_tile_action_bar;
    private ImageView img_back;
    private AppCompatButton btn_save_filter;

    private ProgressBar pb_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_filter);

        G.Activity = this;
        G.context = this;
        FindViews();
        startLoading();
        onClick();
        txt_tile_action_bar.setText("فیلتر سرویس\u200Cها");
        txt_tile_action_bar.setTypeface(G.Bold);
        getJob_categories();
        setCarsRadioButtons();
    }



    private void FindViews() {
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        img_back = findViewById(R.id.img_back);
        txt_title_job_categories = findViewById(R.id.txt_title_job_categories);
        txt_title_job_categories.setTypeface(G.Bold);
        txt_title_car = findViewById(R.id.txt_title_car);
        txt_title_car.setTypeface(G.Bold);
        recyclerView = findViewById(R.id.recycler_view);
        btn_save_filter = findViewById(R.id.btn_save_filter);
        btn_save_filter.setTypeface(G.Bold);
        pb_loading = findViewById(R.id.pb_loading);
    }

    private void onClick(){
        btn_save_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFilters();
                finish();
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void saveFilters() {
        if(selectedCar != null){
            G.saveFilterCar(selectedCar.getCar_id(), selectedCar.getName_car());
        }
        if(selectedJobCategoryIds.size()>0){
            G.saveFilterJobCategoryIds(selectedJobCategoryIds);
        }
    }

    private void setCarsRadioButtons() {
        int selectedCarId = G.getFilterCar().getCar_id();
        itemClickListener = new AdapterFilterRadioButton.ItemClickListener() {
            @Override
            public void onClick(ModelCustomer car) {
                // Notify adapter
                selectedCar = car;
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapterFilterRadioButton.notifyDataSetChanged();
                    }
                });
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterFilterRadioButton = new AdapterFilterRadioButton(productList, selectedCarId, itemClickListener);
        recyclerView.setAdapter(adapterFilterRadioButton);

    }


    public void getJob_categories() {
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getJob_categories();
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                assert response.body() != null;
                try {
                    String result = response.body().string();
                    JSONObject object = G.StringtoJSONObject(result);
                    JSONArray records = object.getJSONArray("records");
                    if (records.length() > 0) {
                        //    marakez_khadamat_khodro.setVisibility(View.VISIBLE);
                        List<SliderItem> sliderItemList = new ArrayList<>();
                        int posi = 0;
                        listJobCategory = new ArrayList<>();
//                       ModelJobCategory modelJobCategory = new ModelJobCategory();
//                        modelJobCategory.setId(-1);
//                        modelJobCategory.setTitle("همه مراکز");
//                        modelJobCategory.setStatus(0);
//
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
                            if (obj.getBoolean("status")) {
                                modelJobCategory.setStatus(1);
                            } else {
                                modelJobCategory.setStatus(0);
                            }

                            listJobCategory.add(modelJobCategory);
                         /*   listJobs.add(title);
                            listJobsIds.add(id);*/
                        }


                        int[] icons = {R.drawable.tavizroghani, R.drawable.mechanic, R.drawable.jelobandi, R.drawable.electric_car, R.drawable.hydraulics, R.drawable.polish, R.drawable.painted_car, R.drawable.car_wash, R.drawable.towed_car,};
                        String[] icons_name = {"tavizroghani", "mechanic", "jelobandi", "electric_car", "hydraulics", "polish", "painted_car", "car_wash", "towed_car"};
                        for (int i = 0; i < icons.length; i++) {
                            if (listJobCategory.get(i).getStatus() == 0) {
                                icons[i] = getResources().getIdentifier(icons_name[i] + "_deactive", "drawable", getPackageName());
                            }
                            if (listJobCategory.size() - 1 >= i)
                                listJobCategory.get(i).setIcon(icons[i]);
                        }

                        createJobCategoriesCheckBox();

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
                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }


    private void createJobCategoriesCheckBox() {
        recycler = findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new GridLayoutManager(this, 3));
        recycler.setHasFixedSize(true);
        recycler.addItemDecoration(new GridSpacingItemDecoration(3, 50, false));
        if(selectedJobCategoryIds != null) selectedJobCategoryIds.clear();
        selectedJobCategoryIds = G.getFilterJobCategoryIds();
        if(selectedJobCategoryIds == null) selectedJobCategoryIds = new LinkedHashMap<>();


        AdapterFilterCheckBox.OnItemClicked onItemClicked = new AdapterFilterCheckBox.OnItemClicked() {
            @Override
            public void onCheckedChanged(ModelJobCategory jobCategory, boolean isChecked) {
               /* if(selectedJobCategoryIds != null) selectedJobCategoryIds.clear();
                selectedJobCategoryIds = G.getFilterJobCategoryIds();
                if(selectedJobCategoryIds == null) selectedJobCategoryIds = new LinkedHashMap<>();*/
                if (isChecked) {
                    if (!selectedJobCategoryIds.containsKey(jobCategory.getId())) {
                        selectedJobCategoryIds.put(jobCategory.getId(), jobCategory.getTitle());
                    }
                } else {
                        if (selectedJobCategoryIds.containsKey(jobCategory.getId())) {
                            selectedJobCategoryIds.remove(jobCategory.getId());
                    }
                }
            }
        };
        adapter = new AdapterFilterCheckBox(listJobCategory,selectedJobCategoryIds, onItemClicked);
        recycler.setAdapter(adapter);
        // adapter.notifyDataSetChanged();
        listCustomer();

    }

    public void listCustomer() {
        if (PreferenceUtil.getUser_id() != null) {
            String user_id = PreferenceUtil.getUser_id();
            Call<ResponseBody> request = api.listCustomer("cust_id,eq," + user_id, 1);

            request.enqueue(new retrofit2.Callback<ResponseBody>() {
                @SuppressLint({"StaticFieldLeak", "NotifyDataSetChanged"})
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    stopLoading();
                    G.Log(call.request().toString());
                    String result = G.getResult(response);
                    G.Log(result);
                    JSONObject object = G.StringtoJSONObject(result);
                    JSONArray array = G.ObjecttoArray(object, "records");
                    if (array.length() > 0) {
                        try {
                            for (int l = 0; l < array.length(); l++) {
                                JSONObject info = array.getJSONObject(l);
                                int user_id = info.getInt("cust_id");
                                String name = info.getString("f_name");
                                String lastname = info.getString("l_name");
                                String sex = info.getString("sex");
                                if (sex.contains("M") || sex.contains("m")) {
                                    sex = "آقا";
                                }
                                if (sex.contains("F") || sex.contains("f")) {
                                    sex = "خانم";
                                }
                                String birth_date = info.getString("birthdate");
                                String phone = info.getString("mobile");
                                String register_date = info.getString("cust_created_at");

                                int car_id = 0;
                                String car_tag = "";
                                String car_name = "";
                                String car_model = "";
                                String car_type = "";
                                String fuel_type = "";
                                int car_name_id = 0, car_tip_id = 0, car_model_id = 0, fuel_type_id = 0, car_company_id = 0;
                                if (info.has("car_id")) {
                                    car_id = info.getInt("car_id");
                                    car_tag = info.getString("car_plate");
                                    String car_company_name = (info.getString("car_company_name") + "").replace("null", "");
                                    String car_tip = (info.getString("car_tip") + "").replace("null", "");
                                    car_name = info.getString("car_name");
                                    if (car_company_name.length() > 0) {
                                        car_name = car_company_name + " - " + car_name;
                                    }
                                    if (car_tip.length() > 0) {
                                        car_name = car_name + " - " + car_tip;
                                    }
                                    car_model = ("" + info.getString("car_model")).replace("null", "");
                                    car_type = ("" + info.getString("car_tip")).replace("null", "");
                                    fuel_type = info.getString("fuel_type");
                                    car_name_id = info.getInt("car_name_id");
                                    if (!(info.getString("car_tip_id") + "").contains("null")) {
                                        car_tip_id = info.getInt("car_tip_id");
                                    }
//                                    if(!info.getString("car_model_id").contains("null")) {
                                    car_model_id = info.getInt("car_model_id");
                                    car_company_id = info.getInt("car_company_id");
//                                    }
                                    fuel_type_id = info.getInt("fuel_type_id");
                                }
                                ModelCustomer modelCustomer = new ModelCustomer();
                                modelCustomer.setId(user_id);
                                modelCustomer.setFirst_name(name);
                                modelCustomer.setLast_name(lastname);
                                modelCustomer.setDate_birthday(birth_date);
                                modelCustomer.setGender(sex);
                                modelCustomer.setPhone(phone);
                                modelCustomer.setPlak(car_tag);
                                modelCustomer.setName_car(car_name);
                                modelCustomer.setType_fuel(fuel_type);
                                modelCustomer.setCar_name_id(car_name_id);
                                modelCustomer.setCar_tip_id(car_tip_id);
                                modelCustomer.setCar_model_id(car_model_id);
                                modelCustomer.setCar_company_id(car_company_id);
                                modelCustomer.setFuel_type_id(fuel_type_id);
                                modelCustomer.setCar_id(car_id);
                                modelCustomer.setDate_save_customer(register_date);
                                G.Log(car_model);
                                G.Log(car_type);
                                if (car_model.length() <= 1) {
                                    car_model = "نامشخص";
                                }
                                modelCustomer.setCar_model(car_model);
                                if (car_type.length() <= 1) {
                                    car_type = "نامشخص";
                                }
                                G.Log(car_model);
                                G.Log(car_type);
                                modelCustomer.setCar_tip(car_type);
                                String full_name = name + " " + lastname;

                                productList.add(modelCustomer);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    setCarsRadioButtons();
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    stopLoading();
                    G.toast("مشکل در برقراری ارتباط");
                }
            });
        }
    }

    private void startLoading() {
        pb_loading.setVisibility(View.VISIBLE);
    }
    private void stopLoading() {
        pb_loading.setVisibility(View.INVISIBLE);
    }

}



