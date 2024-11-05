package com.servicea.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.data.BarEntry;
import com.servicea.adapter.AdapterComment;
import com.servicea.adapter.AdapterListDetectGroup;
import com.servicea.app.CalendarTool;
import com.servicea.app.CircleTransform;
import com.servicea.app.Constants;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.Comment;
import com.servicea.model.ModelProduct;
import com.servicea.model.dbModel.ReserveModel;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

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

public class ServiceCenterActivity extends AppCompatActivity {
    private ShimmerFrameLayout service_center_shimmer_layout;
    private ViewGroup root_layout;
    public ImageView img_profile, img_header, img_loc;
    public Button btn_tab1, btn_tab2, btn_tab3, btn_tab4, btn_reserve;
    public TextView txt_shop_name, txt_category_name, txt_count_service, txt_score, txt_loc, txt_work_calender, txt_feather;
    private String f_open, f_close, l_open, l_close;
    private ViewGroup location, tab1, tab2, tab3, tab4;
    private RecyclerView recycle_services;
    private AdapterListDetectGroup adapterServices;

    private List<ModelProduct> listGroup = new ArrayList<>();

    private int servicesCount = 0;

    private int job_category_id;
    private String centerName, sc_name, sc_address, latitude, longitude;
    private boolean reservation = false;
    private AdapterListDetectGroup.OnItemClickListener onServicesItemClickListener;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context));
    }

    @Override
    public void onResume() {
        super.onResume();
        G.Activity = this;
        G.context = this;
        startShimmer();
    }

    private void startShimmer() {
        service_center_shimmer_layout.startShimmer();
    }
    private void stopShimmer() {
        service_center_shimmer_layout.stopShimmer();
        service_center_shimmer_layout.setVisibility(View.GONE);
        root_layout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_center);
        G.Activity = this;
        G.context = this;
        FindView();
        onClick();

        resetTab();
        btn_tab1.performClick();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            center_id = intent.getStringExtra("id");
            getServiceCenter(center_id);
        } else {
            G.toast("کد خالی");
            finish();
        }
        if (intent != null && intent.hasExtra("services_count")) {
            servicesCount = Integer.parseInt(intent.getStringExtra("services_count"));
        }
    }

    public String center_id = "";

    public void FindView() {
        View cardView = findViewById(R.id.cardview);
        cardView.setBackgroundResource(R.drawable.card_view_bg);
        img_profile = findViewById(R.id.img_profile);
        img_header = findViewById(R.id.img_header);
        txt_shop_name = findViewById(R.id.txt_shop_name);
        txt_category_name = findViewById(R.id.txt_category_name);
        txt_count_service = findViewById(R.id.txt_count_service);
        txt_score = findViewById(R.id.txt_score);
        location = findViewById(R.id.location);
        img_loc = findViewById(R.id.img_loc);
        txt_loc = findViewById(R.id.txt_loc);
        txt_work_calender = findViewById(R.id.txt_work_calender);
        txt_feather = findViewById(R.id.txt_feather);
        btn_tab1 = findViewById(R.id.btn_tab1);
        btn_tab2 = findViewById(R.id.btn_tab2);
        btn_tab3 = findViewById(R.id.btn_tab3);
        btn_tab4 = findViewById(R.id.btn_tab4);
        tab1 = findViewById(R.id.tab1);
        tab2 = findViewById(R.id.tab2);
        tab3 = findViewById(R.id.tab3);
        tab4 = findViewById(R.id.tab4);
        recycle_services = findViewById(R.id.recycle_services);
        btn_reserve = findViewById(R.id.reserv);
        service_center_shimmer_layout = findViewById(R.id.service_center_shimmer_layout);
        root_layout = findViewById(R.id.root_layout);
    }

    public void onClick() {
        btn_tab1.setOnClickListener(view -> {
            resetTab();
            tab1.setVisibility(View.VISIBLE);
            btn_tab1.setBackgroundResource(R.drawable.btn_tab_active);
            btn_tab1.setTextColor(Color.parseColor("#ffffff"));
        });
        btn_tab2.setOnClickListener(view -> {
            resetTab();
            tab2.setVisibility(View.VISIBLE);
            btn_tab2.setBackgroundResource(R.drawable.btn_tab_active);
            btn_tab2.setTextColor(Color.parseColor("#ffffff"));
        });
        btn_tab3.setOnClickListener(view -> {
            resetTab();
            tab3.setVisibility(View.VISIBLE);
            btn_tab3.setBackgroundResource(R.drawable.btn_tab_active);
            btn_tab3.setTextColor(Color.parseColor("#ffffff"));
        });
        btn_tab4.setOnClickListener(view -> {
            resetTab();
            tab4.setVisibility(View.VISIBLE);
            btn_tab4.setBackgroundResource(R.drawable.btn_tab_active);
            btn_tab4.setTextColor(Color.parseColor("#ffffff"));
        });
        onServicesItemClickListener = new AdapterListDetectGroup.OnItemClickListener() {
            @Override
            public void onItemClick(ModelProduct model, CheckBox item, AdapterListDetectGroup.ViewHolder holder, int position) {
                item.setChecked(!item.isChecked());


            }
        };
        findViewById(R.id.show_all_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ServiceCenterActivity.this, ListCommentActivity.class)
                        .putExtra("center_id", center_id)
                        .putExtra("category_name", txt_category_name.getText().toString())
                        .putExtra("center_name", txt_shop_name.getText().toString())
                        .putExtra("center_score", txt_score.getText().toString())
                );
            }
        });
    }

    public void resetTab() {
        btn_tab1.setBackgroundResource(R.drawable.btn_tab);
        btn_tab2.setBackgroundResource(R.drawable.btn_tab);
        btn_tab3.setBackgroundResource(R.drawable.btn_tab);
        btn_tab4.setBackgroundResource(R.drawable.btn_tab);
        btn_tab1.setTextColor(Color.parseColor("#646464"));
        btn_tab2.setTextColor(Color.parseColor("#646464"));
        btn_tab3.setTextColor(Color.parseColor("#646464"));
        btn_tab4.setTextColor(Color.parseColor("#646464"));
        tab1.setVisibility(View.GONE);
        tab2.setVisibility(View.GONE);
        tab3.setVisibility(View.GONE);
        tab4.setVisibility(View.GONE);
    }

    public void getServicesServiceCenter(String id) {
        listGroup.clear();
        recycle_services.setLayoutManager(new LinearLayoutManager(ServiceCenterActivity.this, RecyclerView.VERTICAL, false));
        adapterServices = new AdapterListDetectGroup(ServiceCenterActivity.this, listGroup, onServicesItemClickListener);
        recycle_services.setAdapter(adapterServices);
//        G.loading(ServiceCenterActivity.this);
        if (id.length() > 0) {

            Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
            Call<ResponseBody> request = api.getServicesServiceCenter("service_center_id,eq," + id);
            request.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    stopShimmer();
                    G.Log(call.request().toString());
                    if (response.body() != null) {
                        try {
                            String result = response.body().string();
                            G.Log(result);
                            JSONObject object = G.StringtoJSONObject(result);
                            JSONArray records = object.getJSONArray("records");
                            if (records.length() > 0) {
                                for (int i = 0; i < records.length(); i++) {
                                    JSONObject obj = records.getJSONObject(i);
                                    if (obj.has("job_service_id")) {
                                        JSONObject job_service = obj.getJSONObject("job_service_id");
                                        String title = job_service.getString("title");
                                        String job_category_id = job_service.getString("job_category_id");
                                        String id = job_service.getString("id");
                                        listGroup.add(new ModelProduct(id, title, true));
                                    }

                                }
                            } else {
//                                G.toast("مشکل در دریافت اطلاعات");
                            }

                        } catch (JSONException | IOException e) {
                            G.toast("مشکل در تجزیه اطلاعات");
                            e.printStackTrace();
                        }

                    }
                    adapterServices.notifyDataSetChanged();
                    //G.stop_loading();
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    // G.stop_loading();
                    G.toast("مشکل در برقراری ارتباط");
                }
            });

        }


    }

    public void getServicesCount(String id) {
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getReportService(id, "YEAR");
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                G.Log(call.request().toString());
                assert response.body() != null;
                try {
                    G.Log(call.request().toString());
                    String result = response.body().string();
                    if (result.startsWith("{")) {
                        JSONObject obj = G.StringtoJSONObject(result);
                        if(obj.has("sum")) {
                            JSONObject sum = obj.getJSONObject("sum");
                            if(sum.has("count")){
                                String count = sum.getString("count");
                                txt_count_service.setText(G.getDecimalFormattedString(count + ""));
                            }
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
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }



    public void getServiceCenter(String id) {
        if (id.length() > 0) {
            Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
            Call<ResponseBody> request = api.getServiceCenter("id,eq," + id, "center_name,cs,", 1);
            request.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    G.Log(call.request().toString());
                    if (response.body() != null) {
                        try {
                            String result = response.body().string();
                            G.Log(result);
                            JSONObject object = G.StringtoJSONObject(result);
                            JSONArray records = object.getJSONArray("records");
                            if (records.length() > 0) {
                                for (int i = 0; i < records.length(); i++) {
                                    JSONObject obj = records.getJSONObject(i);
                                    int sc_id = obj.getInt("id");
                                    getComment(sc_id + "");
                                    getAvgScore(sc_id + "");
                                    sc_name = obj.getString("center_name");
                                    txt_shop_name.setText(sc_name);
                                    String sc_phone = obj.getString("phone");

                                    if (obj.has("user_id")) {
                                        JSONObject user = obj.getJSONObject("user_id");
                                        int user_id = user.getInt("id");
                                        String f_name = user.getString("f_name");
                                        String l_name = user.getString("l_name");
                                        String mobile = user.getString("mobile");

                                        if (user.has("profile_photo")) {
                                            String profile_photo = user.getString("profile_photo");
                                            G.preference.edit().putString("profile_photo", profile_photo).apply();
                                            Picasso.get().load(G.PreImagesURL + "profiles/" + profile_photo).error(R.drawable.ic_user).placeholder(R.drawable.ic_user).transform(new CircleTransform()).into(img_profile);
                                        }
                                        if (user.has("header_photo")) {
                                            String header_photo = user.getString("header_photo");
                                            G.preference.edit().putString("header_photo", header_photo).apply();
                                            Picasso.get().load(G.PreImagesURL + "headers/" + header_photo).error(R.drawable.backpro).placeholder(R.drawable.backpro).into(img_header);
                                        }
                                        String sex = user.getString("sex");
                                        String birthdate = user.getString("birthdate");
                                        int province_id = user.getInt("province_id");
                                        int city_id = user.getInt("city_id");
                                    }
                                    if (obj.has("job_category_id")) {
                                        JSONObject category = obj.getJSONObject("job_category_id");
                                         job_category_id = category.getInt("id");
                                        String cat_title = category.getString("title");
                                        txt_category_name.setText(cat_title);
                                    }


                                     f_open = obj.getString("f_open").replace("null", "00:00:00");
                                     f_close = obj.getString("f_close").replace("null", "00:00:00");
                                     l_open = obj.getString("l_open").replace("null", "00:00:00");
                                     l_close = obj.getString("l_close").replace("null", "00:00:00");
                                    String numOfBranch = obj.getString("numOfBranch");
                                    boolean waiting_place = obj.getBoolean("waiting_place");
                                    boolean bar_serv = obj.getBoolean("bar_serv");
                                    boolean holidays = obj.getBoolean("holidays");
                                    boolean mobile_services = obj.getBoolean("mobile_services");
                                    String physical_service = obj.getString("physical_service");
                                    boolean checking = obj.getBoolean("checking");
                                    reservation = obj.getBoolean("reservation");

                                     sc_address = obj.getString("address");
                                     latitude = obj.getString("latitude");
                                     longitude = obj.getString("longitude");
                                    if (f_open.length() >= 6) {
                                        f_open = f_open.replace("00:00", "00");
                                        if (f_open.length() >= 6) {
                                            f_open = f_open.replace(":00", "");
                                        }
                                    }

                                    if (f_close.length() >= 6) {
                                        f_close = f_close.replace("00:00", "00");
                                        if (f_close.length() >= 6) {
                                            f_close = f_close.replace(":00", "");
                                        }
                                    }

                                    if (l_open.length() >= 6) {
                                        l_open = l_open.replace("00:00", "00");
                                        if (l_open.length() >= 6) {
                                            l_open = l_open.replace(":00", "");
                                        }
                                    }
                                    if (l_close.length() >= 6) {
                                        l_close = l_close.replace("00:00", "00");
                                        if (l_close.length() >= 6) {
                                            l_close = l_close.replace(":00", "");
                                        }
                                    }
                                    showLocation(latitude, longitude, sc_address);
                                    txt_work_calender.setText("ساعات کاری: ");
                                    txt_work_calender.append(f_open + " - " + f_close);
                                    txt_work_calender.append("    ");
                                    txt_work_calender.append(l_open + " - " + l_close);
                                    txt_work_calender.append("\n");
                                    txt_work_calender.append("فعالیت در روزهای تعطیل: ");
                                    txt_work_calender.append(holidays ? "دارد" : "ندارد");

                                    txt_feather.setText("تعداد جایگاه سرویس: ");
                                    txt_feather.append(numOfBranch);
                                    txt_feather.append("\n");
                                    txt_feather.append("محل انتظار مشتریان: ");
                                    txt_feather.append(waiting_place ? "دارد" : "ندارد");
                                    txt_feather.append("\n");
                                    txt_feather.append("پذیرایی از مشتریان: ");
                                    txt_feather.append(bar_serv ? "دارد" : "ندارد");
                                    txt_feather.append("\n");
                                    txt_feather.append("ارائه خدمات سیار: ");
                                    txt_feather.append(mobile_services ? "دارد" : "ندارد");
                                    txt_feather.append("\n");
                                    txt_feather.append("کارشناسی خودرو: ");
                                    txt_feather.append(checking ? "دارد" : "ندارد");
                                    txt_feather.append("\n");
                                    findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                            intent.setData(Uri.parse("tel:" + sc_phone));
                                            startActivity(intent);
                                        }
                                    });

                                    btn_reserve.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ReserveModel reserveModel = new ReserveModel();
                                            reserveModel.setService_center_id(Integer.parseInt(center_id));
                                            reserveModel.setService_center_name(sc_name);
                                            reserveModel.setService_center_address(sc_address);
                                            reserveModel.setService_center_lat(latitude);
                                            reserveModel.setService_center_lng(longitude);
                                            reserveModel.setF_open(f_open);
                                            reserveModel.setF_close(f_close);
                                            reserveModel.setL_open(l_open);
                                            reserveModel.setL_close(l_close);
                                            reserveModel.setJob_category_id(job_category_id);
                                            G.saveReserveModel(reserveModel);
                                            Intent intentThatOpenReserveActivity= new Intent(ServiceCenterActivity.this, ReserveActivity.class);
                                            intentThatOpenReserveActivity.putExtra(Constants.f_open, f_open);
                                            intentThatOpenReserveActivity.putExtra(Constants.f_close, f_close);
                                            intentThatOpenReserveActivity.putExtra(Constants.l_open, l_open);
                                            intentThatOpenReserveActivity.putExtra(Constants.l_close, l_close);
                                            intentThatOpenReserveActivity.putExtra(Constants.job_category_id, job_category_id);
                                            intentThatOpenReserveActivity.putExtra(Constants.service_center_id, center_id);
                                            startActivity(intentThatOpenReserveActivity);

                                          //  G.toast("بزودی ...");
                                        }
                                    });

                                    getServicesServiceCenter(sc_id + "");

                                    String created_at = obj.getString("created_at");
                                    String updated_at = obj.getString("updated_at");

                                    getServicesCount(center_id);
                                   // txt_count_service.setText(servicesCount + "");
                                    //txt_score.setText(3.7 + "");


                                    if (obj.has("password")) {
                                        String password = obj.getString("password");
                                        String now_password = G.preference.getString("PASSWORD", "");
                                        if (now_password.length() < 3) {
                                            G.preference.edit().putString("PASSWORD", password).apply();
                                        }
                                    }


                                }
                            } else {
                                G.toast("مشکل در دریافت اطلاعات");
                            }

                        } catch (JSONException | IOException e) {
                            Log.d("ServiceCenterActivity", "onResponse: " + e.getMessage());
                            G.toast("مشکل در تجزیه اطلاعات");
                            e.printStackTrace();
                        }

                    }
//                    G.stop_loading();
                    checkReserveState();
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    G.toast("مشکل در برقراری ارتباط");
                }
            });

        }


    }

    private void hideReserveButton() {
        btn_reserve.setVisibility(View.GONE);
    }

    private void checkReserveState() {
        if (job_category_id == 1 && reservation){
            btn_reserve.setVisibility(View.VISIBLE);
        }else{
            btn_reserve.setVisibility(View.GONE);
        }
    }


    public void showLocation(String lat, String lon, String address) {
        txt_loc.setText(address);
        String loc_imgl = "https://api.mapbox.com/styles/v1/mapbox/outdoors-v11/static/pin-m-star+23a2cd(" + lon + "," + lat + ")/" + lon + "," + lat + ",15,0,30/600x300@2x?attribution=false&logo=false&access_token=pk.eyJ1IjoiZXJmYW5zYiIsImEiOiJjbGJraXQzZWcwMHFpM3ZtZTR4cXMxcGpkIn0.b5C8oRkOZzqvpZh2EStNwg";
        Picasso.get().load(loc_imgl).error(R.drawable.noimage).into(img_loc);
        img_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "geo:" + lat + "," + lon;
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
            }
        });
        G.stop_loading();
    }

    public static List<Comment> comments = new ArrayList<>();
    private AdapterComment adapterComment;
    private RecyclerView recycle_produce_group;

    public void getComment(String center_id) {


        comments.clear();
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getComments("service_center_id,eq," + center_id, 3 + "");
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                G.Log(call.request().toString());
                comments.clear();
                if (response.body() != null) {
                    try {
                        String result = response.body().string();
                        G.Log(result);
                        JSONObject obje = G.StringtoJSONObject(result);
                        JSONArray records = obje.getJSONArray("records");
                        if (records.length() > 0) {
                            findViewById(R.id.commentempty).setVisibility(View.GONE);
                            if (records.length() >= 3) {
                                findViewById(R.id.show_all_comment).setVisibility(View.VISIBLE);
                            }
                            for (int i = 0; i < records.length(); i++) {
                                Comment comment = new Comment();
                                JSONObject obj = records.getJSONObject(i);
                                int sc_id = obj.getInt("id");
                                comment.setId(sc_id);
                                if (obj.has("user_id")) {
                                    JSONObject user = obj.getJSONObject("user_id");
                                    String user_id = user.getString("id");
                                    String f_name = user.getString("f_name");
                                    String l_name = user.getString("l_name");
                                    String profile_photo = user.getString("profile_photo");
                                    comment.setUser_id(user_id);
                                    comment.setUser_name(f_name + " " + l_name);
                                    comment.setUser_profile(profile_photo);
                                }

                                String content = obj.getString("content");
                                String date_time = obj.getString("date_time");
                                comment.setContent(content);
                                comment.setDate(date_time);
                                comment.setScore("3.7");


                                String status = obj.getString("status");
                                comment.setStatus(status + "");
                                String created_at = obj.getString("created_at");
                                comment.setCreated_at(created_at + "");
                                String updated_at = obj.getString("updated_at");
                                comment.setUpdated_at(updated_at + "");
                                String deleted_at = obj.getString("deleted_at");
                                comment.setDeleted_at(deleted_at + "");

                                comments.add(comment);

                            }
                        } else {
                            findViewById(R.id.commentempty).setVisibility(View.VISIBLE);
                            findViewById(R.id.show_all_comment).setVisibility(View.GONE);
                        }
                    } catch (JSONException | IOException e) {
                        G.toast("مشکل در تجزیه اطلاعات");
                        e.printStackTrace();
                    }
                }
                G.stop_loading();
                recycle_produce_group = findViewById(R.id.recycle_produce_group);
                recycle_produce_group.setLayoutManager(new LinearLayoutManager(ServiceCenterActivity.this, RecyclerView.VERTICAL, false));
                adapterComment = new AdapterComment(ServiceCenterActivity.this, comments);
                recycle_produce_group.setAdapter(adapterComment);

            }


            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });
    }

    public void getAvgScore(String center_id) {
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getScore("service_center_id,eq," + center_id);
        Log.d("Guide", "getAvgScore: request:"+ request.request());
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.body() != null) {
                    double avgScore = 0;
                    try {
                        String result = response.body().string();
                        Log.d("Guide", "getAvgScore: result:"+ result);

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
                    txt_score.setText(String.format("%.2f", avgScore));
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
