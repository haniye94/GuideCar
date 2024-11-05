package com.servicea.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.servicea.adapter.AdapterFilterRadioButton;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.dbModel.ModelCustomer;
import com.servicea.model.dbModel.ReserveModel;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.servicea.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ListCarActivity extends AppCompatActivity {
    private RecyclerView rv_cars_list;
    private ModelCustomer selectedCar;
    private AdapterFilterRadioButton adapterFilterRadioButton;
    private Button btn_save;
    private ImageView img_back;

    private List<ModelCustomer> carsList = new ArrayList<>();

    private ProgressBar pb_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_car);
        FindView();
        startLoading();
        onClick();
        listCustomer();
    }

    private void FindView() {
        img_back = findViewById(R.id.img_back);
        rv_cars_list = findViewById(R.id.rv_cars_list);
        btn_save = findViewById(R.id.btn_save);
        pb_loading = findViewById(R.id.pb_loading);
    }
    private void onClick() {
        img_back.setOnClickListener(view -> finish());
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ReserveModel reserveModel =  G.getReserveModel();
               reserveModel.setCar_id(selectedCar.getCar_id());
               G.saveReserveModel(reserveModel);
               startActivity(new Intent(ListCarActivity.this, ReserveProductGroupActivity.class));
            }
        });
    }
    public void listCustomer() {
        G.loading(this);
        if (PreferenceUtil.getUser_id() != null) {
            String user_id = PreferenceUtil.getUser_id();
            Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
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

                                carsList.add(modelCustomer);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    G.stop_loading();
                    setCarsRadioButtons();
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    G.stop_loading();
                    G.toast("مشکل در برقراری ارتباط");
                }
            });
        }
    }

    private void setCarsRadioButtons() {
        if (carsList.size()>0)  selectedCar = carsList.get(0);
        AdapterFilterRadioButton.ItemClickListener itemClickListener = new AdapterFilterRadioButton.ItemClickListener() {
            @Override
            public void onClick(ModelCustomer car) {
                // Notify adapter
                selectedCar = car;
                rv_cars_list.post(new Runnable() {
                    @Override
                    public void run() {
                        adapterFilterRadioButton.notifyDataSetChanged();
                    }
                });
            }
        };

        rv_cars_list.setLayoutManager(new LinearLayoutManager(this));
        adapterFilterRadioButton = new AdapterFilterRadioButton(carsList, selectedCar.getCar_id(), itemClickListener);
        rv_cars_list.setAdapter(adapterFilterRadioButton);

    }

    private void startLoading() {
        pb_loading.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        pb_loading.setVisibility(View.INVISIBLE);
    }
}