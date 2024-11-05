package com.servicea.activities;

import static com.servicea.activities.ListReserveActivity.RESERVE_TYPE.*;
import static com.servicea.app.Constants.PLAK_TYPE.PLAK_GENERAL;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.servicea.activity.AddServicesActivity;
import com.servicea.adapter.AdapterDoneServiceType;
import com.servicea.app.CalendarTool;
import com.servicea.app.Constants;
import com.servicea.app.DataBaseHelper;
import com.servicea.app.G;
import com.servicea.app.PLakUtils;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.ModelServicesCustomer;
import com.servicea.model.dbModel.ModelSaveKhadamat;
import com.servicea.model.dbModel.ModelServices;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.servicea.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class InformationServiceCarActivity extends AppCompatActivity {

    private TextView txt_tile_action_bar;
    private TextView txt_detail_title,txt_name_customer, txt_phone_customer, txt_name_car, txt_plak_customer1, txt_plak_customer2, txt_plak_customer3, txt_plak_customer4, txt_show_km_now, txt_show_km_next, txt_date_service, txt_avg_function, txt_description, txt_price_service, txt_edit_cart_service;
    private ImageView img_back;
    private Button btnAddComment,btnEditReserveTime, btnCancelReserve;
    private TextView txt_next_service, txt_last_service;
    private RecyclerView recycle_done_service_type;
    DataBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    AdapterDoneServiceType adapterListServiceCar;
    String idCustomer;
    private List<Integer> list_id = new ArrayList<>();
    private List<ModelServices> list_model = new ArrayList<>();
    int count = 0;
    int position = 0;
    int current_id = 0;
    private ViewGroup previous, next, ly_km,ly_avg_km;
    private ModelServicesCustomer msc;
    private String plak;

    private boolean is_reserve_list = false;

    private ListReserveActivity.RESERVE_TYPE reserve_type;
    private String reserved_service_id = "0";
    private boolean dataHasChanged = false;

    private ViewGroup ly_plk_general,ly_plk_taxi,ly_plk_edari,ly_plk_entezami,ly_plk_malolin,ly_plk_azad_new,ly_plk_azad_old;

    Constants.PLAK_TYPE plak_type = PLAK_GENERAL;
    ViewGroup plak_layout;

    private Intent intentThatOpenInformationService;

    @Override
    public void onResume() {
        super.onResume();
        G.Activity = this;
        G.context = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_service_car);
        G.Activity = this;
        G.context = this;
        mDBHelper = new DataBaseHelper(this);
        mDatabase = mDBHelper.getReadableDatabase();



        FindView();
        onClick();
        Intent intent = getIntent();
        intentThatOpenInformationService = getIntent();
        setPlakLayout();
        is_reserve_list = intent.getBooleanExtra("is_reserve_list", false);
        txt_next_service.setTypeface(G.ExtraBold);
        txt_last_service.setTypeface(G.ExtraBold);
        txt_tile_action_bar.setText("اطلاعات سرویس خودرو");
        txt_tile_action_bar.setTypeface(G.Bold);
        if (getIntent().getExtras().getInt("position") > 0) {
            position = getIntent().getExtras().getInt("position");
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queueService("qt", service_id + "");
//                if ((position + 1) <= G.services.size()) {
//                    showService(position + 1);
//                }

            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queueService("lt", service_id + "");
//                if (position > 0) {
//                    showService(position - 1);
//                }
            }
        });
        idCustomer = getIntent().getExtras().getString("idCustomer") + "";
        String service_idx = getIntent().getExtras().getString("id_service");
        if (service_idx != null && service_idx.length() > 0) {
            service_id = Integer.parseInt(service_idx);
        }
        Log.e("idCustomer:", idCustomer + "");
        Log.e("idservice:", service_id + "");
        if (service_id == 1) {
            txt_last_service.setTextColor(getResources().getColor(R.color.text_color_hint));
        } else if (service_id > 1) {
            txt_last_service.setTextColor(getResources().getColor(R.color.button));
        }

        if (service_id > last_service_id) {
            last_service_id = service_id;
        }
        txt_name_customer.setTypeface(G.ExtraBold);


        center_id = getIntent().getExtras().getString("center_id") + "";
        Log.d("Informa", "onCreate: " + getIntent().getExtras().getInt("rate_saved"));
        if (intent.hasExtra("rate_saved")) {
            if (center_id.length() > 0) {
                if (intent.getExtras().getInt("rate_saved") == 2) {
                    btnAddComment.setVisibility(View.VISIBLE);
                } else {
                    btnAddComment.setVisibility(View.GONE);
                }
            }
        }


        center_name = intent.getExtras().getString("center_name").replace("null", getString(R.string.registered_by_user));
//        txt_name_customer.setText(getIntent().getExtras().getString("firstName") + " " + getIntent().getExtras().getString("lastName"));
        txt_name_customer.setText(getIntent().getExtras().getString("center_name") + "");
        txt_name_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InformationServiceCarActivity.this, ServiceCenterActivity.class).putExtra("id", center_id + ""));

            }
        });
        if (intent.hasExtra("phone")){
            txt_phone_customer.setText(intent.getExtras().getString("phone"));
            txt_phone_customer.setTypeface(G.ExtraBold);
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
        }

        txt_name_car.setText(intent.getExtras().getString("nameCar"));
        txt_name_car.setTypeface(G.ExtraBold);
        plak = (getIntent().getExtras().getString("plak") + "").replace(" ", "").replace("null", "");
        if (plak.length() > 2) {
          /*  String c1 = plak.substring(0, 2);
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
            txt_plak_customer4.setTypeface(G.ExtraBold);*/
            setPlakBasedViewType(plak, plak_type);
            Log.d("PLAK", "onCreate:InformationServiceCar:plak_type: " + plak);
            Log.d("PLAK", "onCreate:InformationServiceCar:plak_type: " + plak_type);
        }
        if(is_reserve_list){
            showReserveViews(intent);
        }else{
            txt_show_km_now.setText(G.getDecimalFormattedString(getIntent().getExtras().getString("km_now") + ""));
            txt_show_km_now.setTypeface(G.ExtraBold);
            txt_show_km_next.setText(G.getDecimalFormattedString(getIntent().getExtras().getString("km_next") + ""));
            txt_show_km_next.setTypeface(G.ExtraBold);


            last_service_id = G.preference.getInt("last_service_id", 0);
            first_service_id = G.preference.getInt("first_service_id", 0);
            if (service_id == last_service_id) {
                next.setVisibility(View.INVISIBLE);
            } else {
                next.setVisibility(View.VISIBLE);

            }
            if (service_id == first_service_id) {
                previous.setVisibility(View.INVISIBLE);
            } else {
                previous.setVisibility(View.VISIBLE);
            }

        }

        txt_date_service.setText(intent.getExtras().getString("date_service"));
        txt_avg_function.setText(G.getDecimalFormattedString(getIntent().getExtras().getString("avg_function") + ""));
        txt_description.setText(intent.getExtras().getString("description"));
        String price = (intent.getExtras().getString("services_price") + "").replace(" ", "").replace("null", "");
        if (price.length() > 0) {
            txt_price_service.setTypeface(G.ExtraBold);
            txt_price_service.setText(G.getDecimalFormattedString(price));
        }



        String detail_service = intent.getExtras().getString("detail_service");
        showDetailService(detail_service);
    }

    private void showReserveViews(Intent intent) {
        reserved_service_id = intent.getStringExtra("id");
        ly_km.setVisibility(View.GONE);
        txt_detail_title.setText("سرویس های رزرو شده");
        next.setVisibility(View.GONE);
        previous.setVisibility(View.GONE);
        ly_avg_km.setVisibility(View.GONE);
        reserve_type = (ListReserveActivity.RESERVE_TYPE) intent.getSerializableExtra("reserve_type");
        if(reserve_type== DONE || reserve_type== CANCELED){
            btnCancelReserve.setVisibility(View.GONE);
            btnEditReserveTime.setVisibility(View.GONE);
        }else{
            if(reserve_type== DOING) btnCancelReserve.setVisibility(View.VISIBLE);
            btnEditReserveTime.setVisibility(View.VISIBLE);
        }
    }


    public int service_id = 0;
    public int last_service_id = 0;
    public int first_service_id = 0;
    public String center_id = "0";
    public String center_name = "0";

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
                String result = G.getResult(response);
                JSONObject object = G.StringtoJSONObject(result);
                JSONArray array = G.ObjecttoArray(object, "records");
                boolean newx = false;
                if (array.length() > 0) {

                    try {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            msc = new ModelServicesCustomer();
                            msc.setId(obj.getInt("service_id"));

                            if (msc.getId() != service_id) {
                                newx = true;
                            } else {
                                G.toast("موردی یافت نشد");
                            }
                            service_id = msc.getId();
                            if (service_id > last_service_id) {
                                last_service_id = service_id;
                                G.preference.edit().putInt("last_service_id", last_service_id).apply();
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
                            msc.setPhone(obj.getString("mobile") + "");
                            msc.setPhonec(obj.getString("mobile") + "");
                            msc.setDate_birthday(obj.getString("birthdate") + "");
                            msc.setDate_save_customer(obj.getString("cust_created_at") + "");
                            msc.setKm_next(obj.getString("km_next") + "");
                            msc.setKm_now(obj.getString("km_now") + "");
                            msc.setDetail_service(obj.getString("detail_service") + "");
                            msc.setDate_services(obj.getString("service_date_time").replace("00:00:00", "") + "");
                            String date = msc.getDate_services();
                            msc.setCenter_name(obj.getString("center_name") + "");
                            msc.setCenter_id(obj.getString("center_id") + "");
                            center_id = obj.getString("center_id") + "";
                            center_name = obj.getString("center_name") + "";
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
                            Intent intent = new Intent(InformationServiceCarActivity.this, InformationServiceCarActivity.class);
                            intent.putExtra("position", position);
                            intent.putExtra("idCustomer", msc.getId_customer() + "");
                            intent.putExtra("id_service", msc.getId() + "");
                            intent.putExtra("idservice", msc.getId() + "");
                            intent.putExtra("firstName", msc.getFirst_name() + "");
                            intent.putExtra("lastName", msc.getLast_name() + "");
                            intent.putExtra("phone", msc.getPhone() + "");
                            intent.putExtra("plak", msc.getPlak() + "");
                            intent.putExtra(Constants.CAR_PLATE_TYPE, msc.getPlak_type());
                            intent.putExtra("km_now", msc.getKm_now() + "");
                            intent.putExtra("nameCar", msc.getName_car() + "");
                            intent.putExtra("km_next", msc.getKm_next() + "");
                            intent.putExtra("avg_function", msc.getAvg_function() + "");
                            intent.putExtra("description", msc.getDescription() + "");
                            intent.putExtra("date_service", msc.getDate_services() + "");
                            intent.putExtra("services_price", msc.getAll_services_price() + "");
                            intent.putExtra("detail_service", msc.getDetail_service() + "");

                            intent.putExtra("center_id", msc.getCenter_id() + "");
                            intent.putExtra("center_name", msc.getCenter_name() + "");
                            if (newx) {
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();
                            }


                        }


                    } catch (JSONException e) {
                        G.toast("مشکل در دریافت اطلاعات");
                        e.printStackTrace();
                    }

                } else {

                    first_service_id = service_id;
                    G.preference.edit().putInt("first_service_id", first_service_id).apply();
                    G.toast("موردی یافت نشد");
                }
                G.stop_loading();
                if (!newx) {
                    if (service_id == last_service_id) {
                        next.setVisibility(View.INVISIBLE);
                    } else {
                        next.setVisibility(View.VISIBLE);

                    }
                    if (service_id == first_service_id) {
                        previous.setVisibility(View.INVISIBLE);
                    } else {
                        previous.setVisibility(View.VISIBLE);
                    }
                }
                Log.e("service_id", service_id + "");
                Log.e("first_service_id", first_service_id + "");
                Log.e("last_service_id", last_service_id + "");
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
//                swipeRefreshLayout.setRefreshing(false);
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }

    private void onClick() {
        btnAddComment = findViewById(R.id.add_comment);
        btnAddComment.setOnClickListener(view -> dialogAddComment());
        ;
        idCustomer = getIntent().getExtras().getString("idCustomer");

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txt_edit_cart_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mDBHelper != null) {
                            mDBHelper.deleteHistoryKhadamt(G.preference.getInt("idService", 0) + "");
                        }
                    }
                }, 0);
                Intent intent = new Intent(InformationServiceCarActivity.this, AddServicesActivity.class);
                intent.putExtra("idCustomer", getIntent().getExtras().getInt("idCustomer") + "");
                intent.putExtra("firstName", getIntent().getExtras().getString("firstName"));
                intent.putExtra("lastName", getIntent().getExtras().getString("lastName"));
                intent.putExtra("phone", getIntent().getExtras().getString("phone"));
                intent.putExtra("nameCar", getIntent().getExtras().getString("nameCar"));
                intent.putExtra("plak", getIntent().getExtras().getString("plak"));
                intent.putExtra(Constants.CAR_PLATE_TYPE, getIntent().getExtras().getString(Constants.CAR_PLATE_TYPE));
                intent.putExtra("km_now", getIntent().getExtras().getString("km_now"));
                intent.putExtra("km_next", getIntent().getExtras().getString("km_next"));
                intent.putExtra("date_service", getIntent().getExtras().getString("date_service"));
                intent.putExtra("avg_function", getIntent().getExtras().getString("avg_function"));
                intent.putExtra("description", getIntent().getExtras().getString("description"));
                intent.putExtra("services_price", getIntent().getExtras().getString("services_price"));
                intent.putExtra("finish", "");
                intent.putExtra("fromMain", false);
                startActivity(intent);
            }
        });


        if (mDBHelper.getServishayeGhabli(idCustomer, mDatabase).isEmpty()) {

        } else {
            list_model.addAll(mDBHelper.getServishayeGhabli(idCustomer, mDatabase));
            String fullname = getIntent().getExtras().getString("firstName") + " " + getIntent().getExtras().getString("lastName");
            String plak = getIntent().getExtras().getString("plak");
            String phone = getIntent().getExtras().getString("phone");
            String nameCar = getIntent().getExtras().getString("nameCar");
            String kmNow = list_model.get(current_id).getKm_now();
            String kmNext = list_model.get(current_id).getKm_next();
            String avg = list_model.get(current_id).getAvg_function();
            String price = list_model.get(current_id).getAll_services_price();
            String desc = list_model.get(current_id).getDescription();
            String date = list_model.get(current_id).getDate_services();
            txt_name_customer.setText(fullname + "");
            txt_phone_customer.setText(phone + "");
            txt_name_car.setText(nameCar + "");
//            txt_plak_customer.setText(plak + "");
            txt_show_km_now.setText(kmNow + "");
            txt_show_km_next.setText(kmNext + "");
            txt_avg_function.setText(avg + "");
            txt_price_service.setText(price + "");
            txt_description.setText(desc + "");
            txt_date_service.setText(date + "");
        }

        for (int i = 0; i < mDBHelper.getServishayeGhabli(idCustomer, mDatabase).size(); i++) {
            list_id.add(mDBHelper.getServishayeGhabli(idCustomer, mDatabase).get(i).getId());
            // Toast.makeText(InformationServiceCarActivity.this, list_id.get(i) + "", Toast.LENGTH_SHORT).show();
        }
        if (list_id.size() < 2) {

        } else {
            count = list_id.size();

            if (current_id < count) {

                txt_next_service.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (current_id == 0) {

                            current_id += 1;
                            String kmNow = list_model.get(current_id).getKm_now();
                            String kmNext = list_model.get(current_id).getKm_next();
                            String avg = list_model.get(current_id).getAvg_function();
                            String price = list_model.get(current_id).getAll_services_price();
                            String desc = list_model.get(current_id).getDescription();
                            String date = list_model.get(current_id).getDate_services();
                            txt_show_km_now.setText(kmNow + "");
                            txt_show_km_next.setText(kmNext + "");
                            txt_avg_function.setText(avg + "");
                            txt_price_service.setText(price + "");
                            txt_description.setText(desc + "");
                            txt_date_service.setText(date + "");

                            Toast.makeText(InformationServiceCarActivity.this, current_id + "", Toast.LENGTH_SHORT).show();


                        } else if (current_id > 0 && count - 1 != current_id) {

                            current_id += 1;
                            String kmNow = list_model.get(current_id).getKm_now();
                            String kmNext = list_model.get(current_id).getKm_next();
                            String avg = list_model.get(current_id).getAvg_function();
                            String price = list_model.get(current_id).getAll_services_price();
                            String desc = list_model.get(current_id).getDescription();
                            String date = list_model.get(current_id).getDate_services();
                            txt_show_km_now.setText(kmNow + "");
                            txt_show_km_next.setText(kmNext + "");
                            txt_avg_function.setText(avg + "");
                            txt_price_service.setText(price + "");
                            txt_description.setText(desc + "");
                            txt_date_service.setText(date + "");
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
                            txt_show_km_now.setText(kmNow + "");
                            txt_show_km_next.setText(kmNext + "");
                            txt_avg_function.setText(avg + "");
                            txt_price_service.setText(price + "");
                            txt_description.setText(desc + "");
                            txt_date_service.setText(date + "");
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
                            txt_show_km_now.setText(kmNow + "");
                            txt_show_km_next.setText(kmNext + "");
                            txt_avg_function.setText(avg + "");
                            txt_price_service.setText(price + "");
                            txt_description.setText(desc + "");
                            txt_date_service.setText(date + "");
                            if (current_id == 0) {
                                txt_last_service.setTextColor(getResources().getColor(R.color.text_color_hint));
                            }
                        }
                    }
                });

            }
        }

        btnEditReserveTime.setOnClickListener(v -> {
            Intent intentThatStartsReserveActivity = new Intent(InformationServiceCarActivity.this, ReserveActivity.class);
            intentThatStartsReserveActivity.putExtra(Constants.UPDATE_RESERVE_TIME, true);
            intentThatStartsReserveActivity.putExtra(Constants.RESERVED_SERVICE_ID,reserved_service_id);
            startActivity(intentThatStartsReserveActivity);
        });

        btnCancelReserve.setOnClickListener(v -> {
            dialogCancelReserve();
        });
    }

    public void dialogCancelReserve() {

        final Dialog[] dialog = {new Dialog(this)};
        dialog[0].setContentView(R.layout.dialog_cancel_reserve);
        dialog[0].show();

//        EditText add_score=findViewById(R.id.add_score);
//        int score= Integer.parseInt(add_score.getText().toString());
        dialog[0].findViewById(R.id.img_close).setOnClickListener(view -> dialog[0].dismiss());
        dialog[0].findViewById(R.id.btn_cancel_reserve).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog[0].dismiss();
                cancelReserve();
            }
        });
        dialog[0].findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog[0].dismiss();
            }
        });

    }


    public void cancelReserve() {

        G.loading(this);
        String deleted_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());

        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        JSONObject object = new JSONObject();
        try {
            object.put("deleted_at", deleted_at);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<ResponseBody> request = api.updateReserve(reserved_service_id,G.returnBody(object.toString()));
        Log.d("GuidCar_ArenaTeam_", "addServiceRequest: " + request.request());
        Log.d("GuidCar_ArenaTeam_", "addServiceRequest: object" + object.toString());
        Log.d("GuidCar_ArenaTeam_", "addServiceRequest: object2" + G.returnBody(object.toString()));
        Log.d("GuidCar_ArenaTeam_", "addServiceRequest: reserved_service_id:" + reserved_service_id);

        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                G.stop_loading();
                Log.d("GuidCar_ArenaTeam_", "addService: response" + response);
                String result = G.getResult(response);
                Log.d("GuidCar_ArenaTeam_", "addService: result" + result);

                if (result.length() > 0 && result.length() < 10) {
                    G.toast("سرویس رزرو شده با موفقیت لغو شد");
                    startActivity(new Intent(InformationServiceCarActivity.this, ListReserveActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
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


    private void FindView() {
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        img_back = findViewById(R.id.img_back);
        txt_name_customer = findViewById(R.id.txt_name_customer);
        txt_phone_customer = findViewById(R.id.txt_phone_customer);
        txt_name_car = findViewById(R.id.txt_name_car);
        txt_plak_customer1 = findViewById(R.id.txt_plak_customer1);
        txt_plak_customer2 = findViewById(R.id.txt_plak_customer2);
        txt_plak_customer3 = findViewById(R.id.txt_plak_customer3);
        txt_plak_customer4 = findViewById(R.id.txt_plak_customer4);
        txt_show_km_now = findViewById(R.id.txt_show_km_now);
        txt_show_km_next = findViewById(R.id.txt_show_km_next);
        txt_date_service = findViewById(R.id.txt_date_service);
        txt_avg_function = findViewById(R.id.txt_avg_function);
        txt_description = findViewById(R.id.txt_description);
        txt_price_service = findViewById(R.id.txt_price_service);
        txt_next_service = findViewById(R.id.txt_next_service);
        txt_last_service = findViewById(R.id.txt_last_service);
        txt_edit_cart_service = findViewById(R.id.txt_edit_cart_service);
        recycle_done_service_type = findViewById(R.id.recycle_done_service_type);
        ly_km = findViewById(R.id.ly_km);
        ly_avg_km = findViewById(R.id.ly_avg_km);
        txt_detail_title = findViewById(R.id.txt_detail_title);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        btnEditReserveTime = findViewById(R.id.btn_edit_reserve_time);
        btnCancelReserve = findViewById(R.id.btn_cancel_reserve);

        ly_plk_general = findViewById(R.id.ly_plk_general);
        ly_plk_taxi = findViewById(R.id.ly_plk_taxi);
        ly_plk_edari= findViewById(R.id.ly_plk_edari);
        ly_plk_entezami = findViewById(R.id.ly_plk_entezami);
        ly_plk_malolin = findViewById(R.id.ly_plk_malolin);
        ly_plk_azad_new = findViewById(R.id.ly_plk_azad_new);
        ly_plk_azad_old = findViewById(R.id.ly_plk_azad_old);
        plak_layout = ly_plk_general;
    }

    public void onclickAlamrs(View v) {
        startActivity(new Intent(InformationServiceCarActivity.this, AlarmsActivity.class));
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context));
    }


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
                    if (!is_reserve_list) {
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
                    }
                    productList.add(product);
                    G.Log("detail_service: " + product.getTitle());
                }
                recycle_done_service_type.setLayoutManager(new LinearLayoutManager(InformationServiceCarActivity.this, RecyclerView.HORIZONTAL, false));
                //  adapterListServiceCar = new AdapterDoneServiceType(InformationServiceCarActivity.this, mDBHelper.getListKhadamatType(mDatabase, getIntent().getExtras().getInt("idservice")));
                adapterListServiceCar = new AdapterDoneServiceType(InformationServiceCarActivity.this, productList, is_reserve_list);
                recycle_done_service_type.setAdapter(adapterListServiceCar);
            }
        } catch (JSONException e) {
            Log.d("detail_service", "showDetailService: "+ e.getMessage());
            e.printStackTrace();
        }
    }


    public void dialogAddComment() {

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
                addComment(content, rtscore[0], center_id);
            }
        });
        dialog[0].findViewById(R.id.btn_rate_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog[0].dismiss();
                updateServiceScoreState(service_id, 2);
            }
        });

    }

    public void updateServiceScoreState(int service_id, int state) {
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
                dataHasChanged = true;
                ModelServicesCustomer model = G.mscs.get(position);
                model.setRate_state(state);
                G.mscs.set(position, model);
                if (result.length() > 0 && result.length() < 10) {
                    //G.toast("با موفقیت ثبت شد پس از تأیید در لیست نظرات قرار می\u200Cگیرد.");

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


    public void addComment(String content, float score, String center_id) {
        G.loading(this);
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
                G.stop_loading();
                String result = G.getResult(response);
                if (result.length() > 0 && result.length() < 10) {
                    G.toast("با موفقیت ثبت شد پس از تأیید در لیست نظرات قرار می\u200Cگیرد.");
                    findViewById(R.id.add_comment).setVisibility(View.GONE);
                    updateServiceScoreState(service_id, 1);

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
    public void onBackPressed() {
        if (dataHasChanged) setResult(RESULT_OK);
        super.onBackPressed();
    }

    private void setPlakLayout() {
        if(intentThatOpenInformationService.hasExtra(Constants.CAR_PLATE_TYPE)){
            plak_type = (Constants.PLAK_TYPE) intentThatOpenInformationService.getSerializableExtra(Constants.CAR_PLATE_TYPE);
        }
     /*   plak_type = PLAK_TYPE.PLAK_EDARI;
        plak_layout = ly_plk_edari;*/
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

    private void setPlakBasedViewType(String plak, Constants.PLAK_TYPE plakType) {
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
                Log.d("PLAK", "setPlakBasedViewType:addService: " + plak_layout );

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
                textViewsInPlakLayout.get(3).setText(c4);
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
        txt_plak_customer1.setTypeface(G.ExtraBold);
        txt_plak_customer2.setTypeface(G.ExtraBold);
        txt_plak_customer3.setTypeface(G.ExtraBold);
        txt_plak_customer4.setTypeface(G.ExtraBold);
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