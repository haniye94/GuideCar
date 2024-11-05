package com.servicea.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.servicea.adapter.AdapterAddress;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.Address;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.servicea.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ListAddressesActivity extends AppCompatActivity {
    private TextView txt_tile_action_bar;
    private ImageView img_back;
    public static List<Address> addresses = new ArrayList<>();
    public static AdapterAddress adapterAddress;
    private Handler handler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText search;
    private RecyclerView recycle_produce_group;

    public void onclickAlamrs(View v) {
        startActivity(new Intent(ListAddressesActivity.this, AlarmsActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        G.preference.edit().putBoolean("FromListAddress",true).apply();
        G.preference.edit().putInt("CasheSelectedJobCategory", -1).apply();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("job_id")) {
            int job_id = intent.getIntExtra("job_id", -1);

            G.preference.edit().putInt("CasheSelectedJobCategory", job_id).apply();

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getAddress("");
            }
        }, 500);
        G.Activity = this;
        G.context = this;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_addresses);
        G.Activity = this;
        G.context = this;
        FindView();
        onClick();
        txt_tile_action_bar.setText("انتخاب آدرس");
        txt_tile_action_bar.setTypeface(G.Bold);
        search = findViewById(R.id.search);
        handler = new Handler();
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {

                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getAddress(search.getText().toString());
                        }
                    }, 250);

                } else if (s.length() == 0) {
//                    getJob_services("", listJobsIds.get(spinner_job.getSelectedItemPosition()));
                    getAddress("");
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


            }
        });
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.button));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        getAddress("");

                    }
                }, 250);
            }
        });


    }


    private void FindView() {
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        img_back = findViewById(R.id.img_back);
        recycle_produce_group = findViewById(R.id.recycle_produce_group);
        TextView txt_title_advertise1 = findViewById(R.id.txt_title_advertise1);
        txt_title_advertise1.setTypeface(G.ExtraBold);

    }

    private void onClick() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.add_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListAddressesActivity.this, MapxActivity.class);
                startActivity(intent);
            }
        });

    }

    public void getAddress(String key) {
        swipeRefreshLayout.setRefreshing(true);
        addresses.clear();

        if (true) {
            Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
            Call<ResponseBody> request = api.getAddresses( PreferenceUtil.getUser_id(),key);
            request.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    addresses.clear();
                    int selectedPosition = -1;
                    G.Log(call.request().toString());
                    if (response.body() != null) {
                        try {
                            String result = response.body().string();
                            G.Log(result);
//                            JSONObject object = G.StringtoJSONObject(result);
//                            JSONArray records = object.getJSONArray("records");
                            JSONArray records = G.StringtoJSONArray(result);
                            int SelectedAddressID = G.preference.getInt("SelectedAddressID", 0);
                            if (records.length() > 0) {
                                for (int i = 0; i < records.length(); i++) {
                                    Address address = new Address();
                                    JSONObject obj = records.getJSONObject(i);
                                    int id = obj.getInt("id");
                                    address.setId(id);
                                    String title = obj.getString("title");
                                    address.setTitle(title + "");
                                    String addrs = obj.getString("address");
                                    address.setAddress(addrs + "");
                                    String latitude = obj.getString("latitude");
                                    address.setLatitude(latitude + "");
                                    String longitude = obj.getString("longitude");
                                    address.setLongitude(longitude + "");
                                    String status = obj.getString("status");
                                    address.setStatus(0 + "");
                                    String city_id = obj.getString("city_id").replace("null", "0");
                                    address.setCity_id(city_id);
                                    if(id == SelectedAddressID){
                                        address.setStatus(1 + "");
                                        selectedPosition = i;
                                    }
                                    String created_at = obj.getString("created_at");
                                    address.setCreated_at(created_at + "");
                                    String updated_at = obj.getString("updated_at");
                                    address.setUpdated_at(updated_at + "");
                                    String deleted_at = obj.getString("deleted_at");
                                    address.setDeleted_at(deleted_at + "");

                                    addresses.add(address);
                                    G.Log("addresses:" + address.getTitle());
                                }
                                // Add first location for default location
                                if(selectedPosition == -1) {
                                    G.preference.edit().putInt("SelectedAddressID", addresses.get(0).getId()).apply();
                                    G.preference.edit().putString("SelectedAddressTitle", addresses.get(0).getTitle()).apply();
                                    G.preference.edit().putString("SelectedAddressLat", addresses.get(0).getLatitude()).apply();
                                    G.preference.edit().putString("SelectedAddressLng", addresses.get(0).getLongitude()).apply();
                                    G.preference.edit().putString("SelectedCityId", addresses.get(0).getCity_id()).apply();
                                }
                            } else {
                                G.toast("موردی یافت نشد!");
                                G.preference.edit().putInt("SelectedAddressID", 0).apply();
                                G.preference.edit().putString("SelectedAddressTitle", "").apply();
                                G.preference.edit().putString("SelectedAddressLat", "0").apply();
                                G.preference.edit().putString("SelectedAddressLng", "0").apply();
                                String city_id = G.preference.getString("city_id", "0");
                                G.preference.edit().putString("SelectedCityId",city_id).apply();

                            }

                        } catch (JSONException | IOException e) {
                            G.toast("مشکل در تجزیه اطلاعات");
                            e.printStackTrace();
                        }

                    }
                    G.stop_loading();


                    recycle_produce_group.setLayoutManager(new LinearLayoutManager(ListAddressesActivity.this, RecyclerView.VERTICAL, false));
                    adapterAddress = new AdapterAddress(ListAddressesActivity.this, addresses);
                    recycle_produce_group.setAdapter(adapterAddress);
                    swipeRefreshLayout.setRefreshing(false);

                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    G.stop_loading();
                    G.toast("مشکل در برقراری ارتباط");
                }
            });

        }


    }


}