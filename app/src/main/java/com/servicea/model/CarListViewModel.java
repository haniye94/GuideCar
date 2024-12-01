package com.servicea.model;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.servicea.app.Constants;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.dbModel.ModelCustomer;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author haniye94 .
 * @since on 11/14/2024.
 */

public class CarListViewModel extends ViewModel {

    private final MutableLiveData<List<ModelCustomer>> carList = new MutableLiveData<>();
    private final List<ModelCustomer> productList = new ArrayList<>();
    public int page = 0;
    String last_key = "";
    Api api;

    public CarListViewModel() {
        api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        fetchListCustomer(last_key);
    }

    public LiveData<List<ModelCustomer>> getCarList() {
        return carList;
    }

    public void fetchListCustomer(String key) {
        last_key = key;
        if (key.length() > 0) {
            productList.clear();
            key = G.converToEn(key);
            page = 0;
        } else {
            if (page == 0) {
                productList.clear();
                page = 1;
            }
        }
        if (page == 1) {
        }
        if (PreferenceUtil.getUser_id() != null) {
            String user_id = PreferenceUtil.getUser_id();
            Call<ResponseBody> request = api.listCustomer("cust_id,eq," + user_id, page);
            if (key.length() >= 2) {
//                request = api.searchCustomer("center_id,eq," + d_id, "f_name,cs," + key, "l_name,cs," + key, "mobile,cs," + key);
            }
            String finalKey = key;
            request.enqueue(new retrofit2.Callback<ResponseBody>() {
                @SuppressLint({"StaticFieldLeak", "NotifyDataSetChanged"})
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
//                    stopShimmer();
                    if (finalKey.length() > 0) {
                        productList.clear();
                    }
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
                                int car_plate_type = 1;
                                String car_name = "";
                                String car_model = "";
                                String car_type = "";
                                String fuel_type = "";
                                int car_name_id = 0, car_tip_id = 0, car_model_id = 0, fuel_type_id = 0, car_company_id = 0;
                                if (info.has("car_id")) {
                                    car_id = info.getInt("car_id");
                                    car_tag = info.getString("car_plate");
                                    car_plate_type = info.getInt(Constants.CAR_PLATE_TYPE);
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
                                modelCustomer.setPlak_type(Constants.findById(car_plate_type));
                                Log.d(Constants.ADD_CUSTOMER_ACTIVITY_TAG, "productList1: " + modelCustomer.getPlak_type());
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
                                if (finalKey.length() >= 1) {
                                    if (full_name.contains(finalKey) || name.contains(finalKey) || lastname.contains(finalKey) || phone.contains(finalKey)) {
                                        productList.add(modelCustomer);
                                    }
                                } else {
                                    productList.add(modelCustomer);
                                }

                            }
                            fetchProductGallery();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
//                    if (page == 1) {
//                        if (productList.size() <= 0) {
//                            ly_empty.setVisibility(View.VISIBLE);
//                            ll_car_detail.setVisibility(View.GONE);
//                        } else {
//                            ll_car_detail.setVisibility(View.VISIBLE);
//                            ly_empty.setVisibility(View.GONE);
//                        }
//                    }
//                    if (page > 1) {
//                        loadingPB.setVisibility(View.GONE);
//                    } else {
////                        swipeRefreshLayout.setRefreshing(false);
//                    }
////                    adapterListCustomer.notifyDataSetChanged();
//                    isLoading = false;

                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
//                    swipeRefreshLayout.setRefreshing(false);
//                    loadingPB.setVisibility(View.GONE);
                    G.toast("مشکل در برقراری ارتباط");
                }
            });

        }

//        getProductGallery();

    }

    private void fetchProductGallery() {

        Call<ResponseBody> request = api.getProductGallery();

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String result = G.getResult(response);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray records = jsonObject.getJSONArray("records");

                    for (int i = 0; i < productList.size(); i++) {
                        for (int j = 0; j < records.length(); j++) {
                            JSONObject object = records.getJSONObject(j);

                            if (productList.get(i).getCar_name_id() == object.getInt("id")) {
                                productList.get(i).setCar_image(object.getString("image"));
                            }

                        }
                    }


                    fetchCarKilometer();
//                    carList.postValue(productList);

                } catch (JSONException e) {
//                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void fetchCarKilometer() {
        Call<ResponseBody> request = api.getProductKilometer();

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String result = G.getResult(response);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray records = jsonObject.getJSONArray("records");

                    for (int i = 0; i < productList.size(); i++) {
                        for (int j = 0; j < records.length(); j++) {

                            JSONObject object = records.getJSONObject(j);
                            if (productList.get(i).getCar_name_id() == object.getInt("customer_car_id"))
                                productList.get(i).setCar_kilometer(object.getString("last_km_now"));
                        }
                    }
                    fetchServiceTiming();

                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void fetchServiceTiming() {

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
                            for (int i = 0; i < productList.size(); i++) {
                                for (int j = 0; j < records.length(); j++) {
                                    ServiceTiming ST = new ServiceTiming();
                                    JSONObject obj = records.getJSONObject(j);
                                    int sc_id = obj.getInt("id");
                                    ST.setId(sc_id);
                                    String product_group_name = obj.getString("product_group_name");
                                    ST.setProduct_group_name(product_group_name + "");

                                    String due_date = obj.getString("due_date");
                                    ST.setService_date_time(due_date + "");

                                    productList.get(i).setProduct_group_name(product_group_name);
                                    productList.get(i).setProduct_due_date(G.toShamsi(due_date));
                                }
                            }
                        }
                        carList.postValue(productList);
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
}
