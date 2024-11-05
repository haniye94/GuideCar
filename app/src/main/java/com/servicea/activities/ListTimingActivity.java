package com.servicea.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.servicea.adapter.AdapterTiming;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.ServiceTiming;
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

public class ListTimingActivity extends AppCompatActivity {
    private TextView txt_tile_action_bar,txt_product_name,txt_car_name,txt_date;
    private ImageView img_back;
    private ViewGroup service_badi;
    public static List<ServiceTiming> serviceTimings = new ArrayList<>();
    private AdapterTiming adapterTiming;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout service_timing_shimmer_layout;
    private RecyclerView recycle_produce_group;

    public void onclickAlamrs(View v) {
        startActivity(new Intent(ListTimingActivity.this, AlarmsActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        G.preference.edit().putInt("CasheSelectedJobCategory", -1).apply();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("job_id")) {
            int job_id = intent.getIntExtra("job_id", -1);

            G.preference.edit().putInt("CasheSelectedJobCategory", job_id).apply();

        }


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
        setContentView(R.layout.activity_list_service_timing);
        G.Activity = this;
        G.context = this;
        FindView();
        onClick();
        txt_tile_action_bar.setText("زمانبندی سرویس\u200C\u200Eها");
        txt_tile_action_bar.setTypeface(G.Bold);


        getServiceTiming();

    }


    private void FindView() {
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        img_back = findViewById(R.id.img_back);
        recycle_produce_group = findViewById(R.id.recycle_produce_group);
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
        service_timing_shimmer_layout = findViewById(R.id.service_timing_shimmer_layout);
    }

    private void onClick() {
        img_back.setOnClickListener(view -> finish());
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            new Handler().postDelayed(() -> getServiceTiming(), 250);
        });

    }

    public void getServiceTiming() {
        service_badi.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        serviceTimings.clear();
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getReportTiming(PreferenceUtil.getUser_id() + "",9999+"");
        G.Log(request.request().toString());
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                stopShimmer();
                G.Log(call.request().toString());
                serviceTimings.clear();
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
                                if(center_name == "null"){
                                    center_name = "ثبت توسط کاربر";
                                }
                                ST.setCenter_name(center_name + "");
                                String km_now = obj.getString("km_now");
                                ST.setKm_now(km_now + "");
//                                String km_next = obj.getString("km_next");
                                String km_usage = obj.getString("km_usage");
                                int km_usage_int = 0;
                                if(km_usage != "null"){
                                    km_usage_int = Integer.parseInt(km_usage);
                                }
                                int km_next = Integer.parseInt(km_now) + km_usage_int ;
                                ST.setKm_next(km_next + "");
                                String visited_change = (obj.getString("visited_change")+"");
                                ST.setChanged(visited_change.equals("1"));
                                serviceTimings.add(ST);
                                if(i==0){
                                    String type = "بازدید";
                                    if(ST.isChanged()){
                                        type = "تعویض";
                                    }
                                    service_badi.setVisibility(View.VISIBLE);
                                    txt_product_name.setText(type+" "+product_group_name);
                                    txt_car_name.setText("خودروی "+car_name);
                                    txt_date.setText(""+G.toShamsi(due_date));
                                }
                            }
                        } else {
                            G.toast("موردی یافت نشد!");
                        }
                    } catch (JSONException | IOException e) {
                        G.toast("مشکل در تجزیه اطلاعات");
                        G.Log("timing"+ e.getMessage());
                        e.printStackTrace();
                    }
                }
                G.stop_loading();
                recycle_produce_group.setLayoutManager(new LinearLayoutManager(ListTimingActivity.this, RecyclerView.VERTICAL, false));
                adapterTiming = new AdapterTiming(ListTimingActivity.this, serviceTimings);
                recycle_produce_group.setAdapter(adapterTiming);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }

    private void stopShimmer() {
        service_timing_shimmer_layout.stopShimmer();
        service_timing_shimmer_layout.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        service_badi.setVisibility(View.VISIBLE);
    }


}