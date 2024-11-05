package com.servicea;


import static com.servicea.app.G.Activity;
import static com.servicea.app.G.KEY_CITY_ID_CHANGED;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.servicea.activities.ListReserveActivity;
import com.servicea.activities.ListServiceCenterActivity;
import com.servicea.activities.ListTimingActivity;
import com.servicea.adapter.AdapterJobCategory;
import com.servicea.adapter.AdapterServiceCenterGrid;
import com.servicea.app.Constants;
import com.servicea.app.CustomMaterialShowcaseView;
import com.servicea.app.RecyclerItemClickListener;
import com.servicea.app.Utils;
import com.servicea.model.ModelJobCategory;
import com.servicea.model.ServiceCenter;
import com.servicea.model.ServiceTiming;

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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ir.servicea.R;

import com.servicea.activities.AddCustomerActivity;
import com.servicea.activity.AddServicesActivity;
import com.servicea.activities.AlarmsActivity;
import com.servicea.activities.WebViewActivity;
import com.servicea.adapter.AdapterListAdvertise;
import com.servicea.adapter.AdapterListAdvertiseTwo;
import com.servicea.adapter.AdapterListGridMain;
import com.servicea.adapter.SliderAdapterExample;
import com.servicea.app.DataBaseHelper;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.app.StateOpenHelper;
import com.servicea.model.ModelAdvertise;
import com.servicea.model.ModelAdvertise2;
import com.servicea.model.ModelItemMain;
import com.servicea.model.SliderItem;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.target.Target;
import uk.co.deanwild.materialshowcaseview.target.ViewTarget;

public class FragmentMain extends Fragment {
    private RecyclerView gridView_main, recycle_advertise_one, recycle_advertise_two, recycle_advertise_top;
    private ShimmerFrameLayout advertise_one_shimmer_layout, advertise_two_shimmer_layout, best_service_centers_shimmer_layout, job_categories_shimmer_layout, slider_shimmer_layout, grid_main_shimmer_layout, next_service_shimmer_layout;
    private ImageView iv_add_more_centers, ic_reserve_list;
    private List<ModelItemMain> number = new ArrayList<>();
    private List<ModelAdvertise> advertises = new ArrayList<>();
    private List<ModelAdvertise> advertisess = new ArrayList<>();
    private List<ModelAdvertise2> advertises2 = new ArrayList<>();
    private TextView txt_tile_action_bar;
    private EditText edt1, edt2, edt3, edt4, edt5, edt6, edt7, edt8;
    private TextView txt_product_name, txt_car_name, txt_date, txt_search_plak, txt_search_phone, txt_title_advertise1, txt_title_advertise2, txt_title_advertisetop, txt_title_category1, txt_best_marakez1, txt_next_serv, txt_empty_next_service;

    private TextView txt_add_more_centers;
    private TextInputLayout edt_phone;
    private TextInputEditText edt_phone_number;
    private LinearLayout ly_plak;
    private DataBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    private ViewPager2 sliderView;
    private SliderAdapterExample adapter;
    PreferenceUtil preferenceUtil;
    private AdapterListAdvertise blogCar;
    private AdapterListAdvertise blogEdu;
    public ViewGroup searchpelak;

    private RecyclerView recycle_done_service_type;
    private List<ServiceCenter> serviceCenters = new ArrayList<>();
    private RecyclerView recycle_service_centers;
    private AdapterServiceCenterGrid adapterServiceCenter;
    private ViewGroup service_badi, layout_item_next_service;

    private String centerScore = "0";
    private int serviceCenterOffset = 0;
    private boolean loadMoreCenters = true;

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        preferenceUtil = new PreferenceUtil(getContext());
        number.add(new ModelItemMain(4, "مشاوره", R.drawable.ic_moshavereh, R.drawable.ic_bg_car_info));
        number.add(new ModelItemMain(3, "خدمات سیار", R.drawable.ic_mail, R.drawable.ic_main_bg_message));
        number.add(new ModelItemMain(1, "سرویس\u200Cها", R.drawable.ic_services, R.drawable.ic_bg_services));
        number.add(new ModelItemMain(5, "خودروها", R.drawable.ic_car_info, R.drawable.ic_bg_customer));
        // number.add(new ModelItemMain(8, "کارشناسی", R.drawable.ic_setting, R.drawable.ic_bg_setting));
        // number.add(new ModelItemMain(7, "فروشگاه", R.drawable.ic_repeat, R.drawable.ic_bg_store));
        // number.add(new ModelItemMain(6, "خلافی", R.drawable.ic_coin, R.drawable.ic_bg_coin));
        // number.add(new ModelItemMain(2, "استوک", R.drawable.ic_report, R.drawable.ic_bg_report));
        //    number.add(new ModelItemMain(7, "گروه کالا", R.drawable.ic_product_group, R.drawable.ic_bg_produce));
        mDBHelper = new DataBaseHelper(getContext());
        mDatabase = mDBHelper.getReadableDatabase();
        context = view.getContext();

        findView(view);
        PlakListener();
        onClick();

//        txt_tile_action_bar.setTypeface(G.ExtraBold);
        txt_title_advertise1.setTypeface(G.ExtraBold);
        txt_title_advertise2.setTypeface(G.ExtraBold);
        txt_title_advertisetop.setTypeface(G.Bold);
        txt_title_category1.setTypeface(G.ExtraBold);
        txt_best_marakez1.setTypeface(G.ExtraBold);
        txt_next_serv.setTypeface(G.ExtraBold);
        edt1.requestFocus();


//        txt_tile_action_bar.setText(preferenceUtil.getName_auto_service());

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recycle_advertise_one.setLayoutManager(horizontalLayoutManagaer);
        blogEdu = new AdapterListAdvertise(getContext(), advertises);
        recycle_advertise_one.setAdapter(blogEdu);

        LinearLayoutManager horizontalLayoutManagaer2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recycle_advertise_two.setLayoutManager(horizontalLayoutManagaer2);
        blogCar = new AdapterListAdvertise(getContext(), advertisess);
        recycle_advertise_two.setAdapter(blogCar);

        advertises2.add(new ModelAdvertise2("فست فود کلیز", R.drawable.slider1));
        advertises2.add(new ModelAdvertise2("فست فود کلیز", R.drawable.slider2));
        advertises2.add(new ModelAdvertise2("فست فود کلیز", R.drawable.slider3));
        advertises2.add(new ModelAdvertise2("فست فود کلیز", R.drawable.slider4));
        advertises2.add(new ModelAdvertise2("فست فود کلیز", R.drawable.slider1));
        advertises2.add(new ModelAdvertise2("فست فود کلیز", R.drawable.slider2));
        advertises2.add(new ModelAdvertise2("فست فود کلیز", R.drawable.slider3));
        advertises2.add(new ModelAdvertise2("فست فود کلیز", R.drawable.slider4));

        LinearLayoutManager horizontalLayoutManagaer3 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recycle_advertise_top.setLayoutManager(horizontalLayoutManagaer3);
        recycle_advertise_top.setAdapter(new AdapterListAdvertiseTwo(getContext(), advertises2));

//        SliderAdapterExample.OnImageSizeLoaded onSliderImageSizeLoaded = new SliderAdapterExample.OnImageSizeLoaded() {
//            @Override
//            public void onSizeLoaded(int height) {
//
//               /* slider_root.getLayoutParams().height = height;
//                slider_root.requestLayout();
//
//                sliderView.getLayoutParams().height = height;
//                sliderView.requestLayout();*/
//            }
//        };
//        adapter = new SliderAdapterExample(getContext(), onSliderImageSizeLoaded);
//        sliderView.setAdapter(adapter);
//        sliderView.setSliderAdapter(adapter);
//        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
//        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
//        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
//        sliderView.setIndicatorSelectedColor(Color.WHITE);
//        sliderView.setIndicatorUnselectedColor(Color.GRAY);
//        sliderView.setScrollTimeInSec(3);
//        sliderView.setAutoCycle(true);
//        sliderView.startAutoCycle();

        getServiceTiming();
//        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
//            @Override
//            public void onIndicatorClicked(int position) {
//                Log.i("GGG", "onIndicatorClicked: " + sliderView.getCurrentPagePosition());
//            }
//        });
        view.findViewById(R.id.txt_title_advertise2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), WebViewActivity.class)
                        .putExtra("LINK", G.LINK_BLOG_Educational)
                        .putExtra("TITLE", "مجله آموزشی"));
            }
        });
        view.findViewById(R.id.txt_title_advertise1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), WebViewActivity.class)
                        .putExtra("LINK", G.LINK_BLOG_Car)
                        .putExtra("TITLE", "مجله خودرو"));
            }
        });


        service_badi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ListTimingActivity.class));

            }
        });

        ic_reserve_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ListReserveActivity.class));

            }
        });

        G.PusherBeam(PreferenceUtil.getUser_id());
        return view;
    }


    public ViewGroup marakez_khadamat_khodro, behtarin_marakez, majale_khodro, majale_amozeshi/*, slider_root*/;

    private List<String> listJobs = new ArrayList<>();
    private List<Integer> listJobsIds = new ArrayList<>();
    private ArrayAdapter spinnerAdapter;

    public void getServiceTiming() {
        //service_badi.setVisibility(View.GONE);
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getReportTiming(PreferenceUtil.getUser_id() + "", 9999 + "");
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                stopShimmer(next_service_shimmer_layout);
                stopShimmer(grid_main_shimmer_layout);
                setupGridMainMenu();
                service_badi.setVisibility(View.VISIBLE);
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
//                                String km_next = obj.getString("km_next");
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
                                    txt_empty_next_service.setVisibility(View.GONE);
                                    layout_item_next_service.setVisibility(View.VISIBLE);
                                    txt_product_name.setText(type + " " + product_group_name);
                                    txt_car_name.setText("خودروی " + car_name);
                                    txt_date.setText("" + G.toShamsi(due_date));
                                }
                            }
                        } else {
                            layout_item_next_service.setVisibility(View.GONE);
                            txt_empty_next_service.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException | IOException e) {
                        G.toast("مشکل در تجزیه اطلاعات");
                        e.printStackTrace();
                    }
                }
//                getSlider();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
//                getSlider();
            }
        });


    }

    public void getJob_categories() {
        marakez_khadamat_khodro.setVisibility(View.GONE);

        listJobs.clear();
        listJobsIds.clear();
        listJobs.add("دسته شغلی");
        listJobsIds.add(0);
        spinnerAdapter = new ArrayAdapter(getContext(), R.layout.item_spiner, listJobs);
        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getJob_categories();
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                assert response.body() != null;
                try {
                    int lastItemActiveStatus = -1;
                    int lastItemDeActiveStatus = -1;
                    String result = response.body().string();
                    G.Log(result);
                    JSONObject object = G.StringtoJSONObject(result);
                    JSONArray records = object.getJSONArray("records");
                    if (records.length() > 0) {
                        marakez_khadamat_khodro.setVisibility(View.VISIBLE);
//                        List<SliderItem> sliderItemList = new ArrayList<>();
                        int posi = 0;
                        List<ModelJobCategory> listJobCategory = new ArrayList<>();
//                       ModelJobCategory modelJobCategory = new ModelJobCategory();
//                        modelJobCategory.setId(-1);
//                        modelJobCategory.setTitle("همه مراکز");
//                        modelJobCategory.setStatus(0);
//
//                        listJobCategory.add(modelJobCategory);
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject obj = records.getJSONObject(i);
//                            SliderItem sliderItem = new SliderItem();
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
                                listJobCategory.add(lastItemActiveStatus + 1, modelJobCategory);
                                lastItemDeActiveStatus++;
                                lastItemActiveStatus = listJobCategory.indexOf(modelJobCategory);
                            } else {
                                modelJobCategory.setStatus(0);
                                if (lastItemDeActiveStatus == -1) {
                                    listJobCategory.add(modelJobCategory);
                                } else {
                                    listJobCategory.add(lastItemDeActiveStatus + 1, modelJobCategory);
                                }
                                lastItemDeActiveStatus = listJobCategory.indexOf(modelJobCategory);
                            }
                            listJobs.add(title);
                            listJobsIds.add(id);
                        }

                        int[] icons = {R.drawable.tavizroghani, R.drawable.mechanic, R.drawable.jelobandi, R.drawable.electric_car, R.drawable.hydraulics, R.drawable.polish, R.drawable.painted_car, R.drawable.car_wash, R.drawable.towed_car,};
                        String[] icons_name = {"tavizroghani", "mechanic", "jelobandi", "electric_car", "hydraulics", "polish", "painted_car", "car_wash", "towed_car"};
                        for (int i = 0; i < icons.length; i++) {
                            if (listJobCategory.get(i).getStatus() == 0) {
                                ModelJobCategory modelJobCategory = listJobCategory.get(i);
                                icons[modelJobCategory.getId() - 1] = getResources().getIdentifier(icons_name[i] + "_deactive", "drawable", context.getPackageName());
                                G.Log("listJobCategory" + listJobCategory.get(i).getTitle() + "//" + listJobCategory.get(i).getStatus() + "//" + icons[listJobCategory.get(i).getId() - 1]);

                            }
                          /*  if (listJobCategory.size() - 1 >= i)
                                listJobCategory.get(i).setIcon(icons[i]);*/
                        }

                        for (int i = 0; i < listJobCategory.size(); i++) {
                            ModelJobCategory modelJobCategory = listJobCategory.get(i);
                            modelJobCategory.setIcon(icons[modelJobCategory.getId() - 1]);
                        }

                        recycle_done_service_type.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                        AdapterJobCategory adapterJobCategory = new AdapterJobCategory(getContext(), listJobCategory);
                        recycle_done_service_type.setAdapter(adapterJobCategory);
                        recycle_done_service_type.addOnItemTouchListener(
                                new RecyclerItemClickListener(G.context, recycle_done_service_type, new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        ModelJobCategory jobCategory = listJobCategory.get(position);
                                        if (jobCategory.getStatus() == 1) {
                                            getContext().startActivity(new Intent(getContext(), ListServiceCenterActivity.class).putExtra("job_id", jobCategory.getId()));
                                        } else {
                                            Toast.makeText(getContext(), "به زودی", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onLongItemClick(View view, int position) {
                                        // do whatever
                                    }
                                })
                        );
                        stopShimmer(job_categories_shimmer_layout);
                        spinnerAdapter.notifyDataSetChanged();

                    } else {
                        G.toast("هیچ دسته شغلی یافت نشد!");
                    }
                } catch (JSONException | IOException e) {
                    G.toast("مشکل در تجزیه اطلاعات");
                    e.printStackTrace();
                }
                getServiceCenters("", 0, serviceCenterOffset);

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
                getServiceCenters("", 0, serviceCenterOffset);
            }
        });


    }


    public void getServiceCenters(String key, int job_id, int offset) {
        if (offset > 0) {
            G.loading(context);
        } else {
            behtarin_marakez.setVisibility(View.GONE);
        }
        //serviceCenters.clear();
        if (true) {
            Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
            String default_city_id = G.preference.getString("city_id", "0");
            String city_id = G.preference.getString("SelectedCityId", default_city_id);

//          Call<ResponseBody> request = api.getServiceCenter("id,gt," + 0, "center_name,cs," + key , 6);
            Call<ResponseBody> request = api.getBestServiceCenterByCity(String.valueOf(job_id), city_id, 6, offset);
            /*if (job_id > 0) {
                request = api.getServiceCenter("job_category_id,eq," + job_id, "center_name,cs," + key, 6);
            }*/
            request.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    getBlogs();
                    G.stop_loading();
                    G.Log(call.request().toString());
                    if (response.body() != null) {
                        try {
                            String result = response.body().string();
                            G.Log(result);
                            // JSONObject object = G.StringtoJSONObject(result);
//                            JSONArray records = object.getJSONArray("records");
                            JSONArray records = G.StringtoJSONArray(result);
                            if (records.length() > 0) {
                                if (records.length() < 6) loadMoreCenters = false;
                                G.Log("SeviceCenter:" + records.length());
                                behtarin_marakez.setVisibility(View.VISIBLE);
                                for (int i = 0; i < records.length(); i++) {
                                    ServiceCenter SC = new ServiceCenter();
                                    JSONObject obj = records.getJSONObject(i);
                                    int sc_id = obj.getInt("id");
                                    SC.setId(sc_id);

                                    String sc_name = obj.getString("center_name");
                                    SC.setTitle(sc_name);
                                    String sc_phone = obj.getString("phone");
                                    SC.setDesc(sc_phone);
                               /*     if (obj.has("user_id")) {
                                        JSONObject user = obj.getJSONObject("user_id");
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
                                        // SC.setScore("4.2");
                                        String sex = user.getString("sex");
                                        String birthdate = user.getString("birthdate");
                                        int province_id = user.getInt("province_id");
                                        int city_id = user.getInt("city_id");
                                    }*/
                                   /* int user_id = obj.getInt("id");
                                    String f_name = obj.getString("f_name");
                                    String l_name = obj.getString("l_name");
                                    String mobile = obj.getString("mobile");*/

                                    if (obj.has("profile_photo")) {
                                        String profile_photo = obj.getString("profile_photo");
                                        SC.setProfile(profile_photo);
                                    }
                                    if (obj.has("header_photo")) {
                                        String header_photo = obj.getString("header_photo");
                                        SC.setHeader(header_photo);
                                    }
                                    SC.setPercent("50%");
                                    SC.setScore(String.format("%.2f", Double.parseDouble(obj.getString("score"))));
                                   /* String sex = obj.getString("sex");
                                    String birthdate = obj.getString("birthdate");
                                    int province_id = obj.getInt("province_id");
                                    int city_id = obj.getInt("city_id");
*/
                                   /* if (obj.has("job_category_id")) {
                                        JSONObject category = obj.getJSONObject("job_category_id");
                                        int cat_id = category.getInt("id");
                                        String cat_title = category.getString("title");
                                        SC.setCategory(cat_title);
                                    }*/
                                    if (obj.has("job_category_id")) {
//                                        int cat_id = obj.getInt("id");
                                        int cat_id = obj.getInt("job_category_id");
                                        String cat_title = obj.getString("title");
                                        SC.setCategory(cat_title);
                                    }


                                  /*  String f_open = obj.getString("f_open");
                                    String f_close = obj.getString("f_close");
                                    String l_open = obj.getString("l_open");
                                    String l_close = obj.getString("l_close");
                                    String numOfBranch = obj.getString("numOfBranch");
                                    String waiting_place = obj.getString("waiting_place");
                                    String bar_serv = obj.getString("bar_serv");
                                    String holidays = obj.getString("holidays");
                                    String mobile_services = obj.getString("mobile_services");
                                    String physical_service = obj.getString("physical_service");
                                    String checking = obj.getString("checking");
                                    String sc_address = obj.getString("address");
                                    String latitude = obj.getString("latitude");
                                    String longitude = obj.getString("longitude");


                                    String created_at = obj.getString("created_at");
                                    String updated_at = obj.getString("updated_at");*/


                                    if (obj.has("password")) {
                                        String password = obj.getString("password");
                                        String now_password = G.preference.getString("PASSWORD", "");
                                        if (now_password.length() < 3) {
                                            G.preference.edit().putString("PASSWORD", password).apply();
                                        }
                                    }

                                    serviceCenters.add(SC);
                                    G.Log("SeviceCenter" + serviceCenters.get(i).title);

                                }
                            } else {
                                G.stop_loading();
                                G.toast("موردی یافت نشد!");
                            }

                        } catch (JSONException | IOException e) {
                            G.stop_loading();
                            G.toast("مشکل در تجزیه اطلاعات");
                            G.Log("service-centers:" + e.getMessage());
                            e.printStackTrace();
                        }

                    }

                    G.stop_loading();

                    //getCentersScore();
                    //if(!loadMoreCenters) iv_add_more_centers.setVisibility(View.GONE);
                  /*  recycle_service_centers.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                    adapterServiceCenter = new AdapterServiceCenterGrid(getContext(), serviceCenters);
//                  adapterListProduceGroup = new AdapterListProduceGroup(ProductGroupActivity.this, mDBHelper.getListProductGroup(mDatabase));
                    recycle_service_centers.setAdapter(adapterServiceCenter);*/
                    G.Log("SeviceCenter1:" + serviceCenterOffset);
                    stopShimmer(best_service_centers_shimmer_layout);
                    adapterServiceCenter.notifyDataSetChanged();

                   /* // pos on which item you want to scroll recycler view
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           *//* recycle_service_centers.smoothScrollToPosition(offset);
                            recycle_service_centers.scrollToPosition(offset);*//*
                     *//* recycle_service_centers.scrollToPosition(adapterServiceCenter.getItemCount()-6);
                            recycle_service_centers.smoothScrollToPosition(adapterServiceCenter.getItemCount()-6);*//*
                            recycle_service_centers.getLayoutManager().scrollToPosition(serviceCenterOffset);
                        }
                    }, 200);*/
                    recycle_service_centers.smoothScrollToPosition(serviceCenterOffset);

                    // if (offset == 0) getServiceTiming();
                    serviceCenterOffset += 6;
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    G.stop_loading();
                    G.toast("مشکل در برقراری ارتباط");
                    // if (offset == 0) getServiceTiming();
                    getBlogs();

                }
            });
        }
    }

    public void onclickAlamrs(View v) {
        startActivity(new Intent(getContext(), AlarmsActivity.class));
    }

    public void checkTag(String tag) {
        tag = G.CreateSyntaxPlak(tag);
        G.loading(getContext());
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);

        api.checkTag("car_plate,cs," + tag).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String result = G.getResult(response);

                JSONObject object = G.StringtoJSONObject(result);
                JSONArray array = G.ObjecttoArray(object, "records");
                G.Log(result);
                if (array.length() > 0) {

                    try {
                        JSONObject info = array.getJSONObject(0);

                        String user_id = "";
                        String name = "";
                        String lastname = "";
                        String sex = "";
                        String birth_date = "";
                        String phone = "";
                        String register_date = "";

                        if (info != null && info.length() > 0 && info.has("cust_id")) {
                            user_id = info.getInt("cust_id") + "";
                            name = info.getString("f_name");
                            lastname = info.getString("l_name");
                            sex = info.getString("sex");
                            if (sex.contains("M") || sex.contains("m")) {
                                sex = "آقا";
                            }
                            if (sex.contains("F") || sex.contains("f")) {
                                sex = "خانم";
                            }
                            birth_date = info.getString("birthdate");
                            phone = info.getString("mobile");
                            register_date = info.getString("cust_created_at");
                        }
                        int car_id = 0;
                        String car_tag = "";
                        String car_name = "";
                        String car_company_name = "";
                        String car_model = "";
                        String car_type = "";
                        String fuel_type = "";
                        int car_name_id = 0, car_tip_id = 0, car_model_id = 0, fuel_type_id = 0, car_company_id = 0;
                        if (info.has("car_id")) {
                            car_id = info.getInt("car_id");
                            car_tag = info.getString("car_plate");
                            car_company_name = (info.getString("car_company_name") + "").replace("null", "");
                            car_type = (info.getString("car_tip") + "").replace("null", "");
                            car_name = (info.getString("car_name") + "").replace("null", "");
                            if (car_company_name.length() > 0) {
                                car_name = car_company_name + " - " + car_name;
                            }
                            if (car_type.length() > 0) {
                                car_name = car_name + " - " + car_type;
                            }
                            car_model = info.getString("car_model");
                            car_type = info.getString("car_tip");
                            fuel_type = info.getString("fuel_type");
                            car_name_id = info.getInt("car_name_id");
                            car_company_id = info.getInt("car_company_id");

                            if (!(info.getString("car_tip_id") + "").contains("null")) {
                                car_tip_id = info.getInt("car_tip_id");
                            }
                            car_model_id = info.getInt("car_model_id");
                            fuel_type_id = info.getInt("fuel_type_id");
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mDBHelper != null) {
                                    mDBHelper.deleteHistoryKhadamt(G.preference.getInt("idService", 0) + "");
                                }
                            }
                        }, 1500);
                        Intent intent = new Intent(getContext(), AddServicesActivity.class);
                        intent.putExtra("idCustomer", user_id + "");

                        intent.putExtra("firstName", name);
                        intent.putExtra("lastName", lastname);
                        intent.putExtra("phone", phone);
                        intent.putExtra("plak", car_tag);
                        intent.putExtra("nameCar", car_name);
                        intent.putExtra("id_customer", user_id + "");

                        intent.putExtra("id_car", car_id + "");

                        intent.putExtra("gender", sex);
                        intent.putExtra("date_birthday", birth_date);
                        intent.putExtra("type_fule", fuel_type);
                        intent.putExtra("date_save", "");
                        intent.putExtra("type_car", car_type);
                        intent.putExtra("model_car", car_model);
                        intent.putExtra("description", "");
                        intent.putExtra("finish", "");
                        intent.putExtra("car_name_id", car_name_id);
                        intent.putExtra("car_tip_id", car_tip_id);
                        intent.putExtra("car_model_id", car_model_id);
                        intent.putExtra("car_company_id", car_company_id);
                        intent.putExtra("fuel_type_id", fuel_type_id);
                        intent.putExtra("fromMain", true);
                        startActivity(intent);


                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "مشکل در دریافت اطلاعات", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    G.stop_loading();
                    Intent intent = new Intent(getContext(), AddCustomerActivity.class);
                    intent.putExtra("firstName", "null");
                    startActivity(intent);
                    preferenceUtil.cashNewCustomerPlak(edt1.getText().toString(), edt2.getText().toString(), edt3.getText().toString(), edt4.getText().toString(), edt5.getText().toString(), edt6.getText().toString(), edt7.getText().toString(), edt8.getText().toString());
                    G.toast("موردی یافت نشد، مشتری جدید را ثبت کنید.");


                }
                G.stop_loading();


            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                Toast.makeText(getContext(), "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.preference.getBoolean("ActivateRad", false)) {
                    getProfile();
                }
            }
        }, 1000);

        if (G.preference.getBoolean(KEY_CITY_ID_CHANGED, false)) {
            serviceCenters.clear();
            // adapterServiceCenter.notifyDataSetChanged();
            serviceCenterOffset = 0;
            getServiceCenters("", 0, serviceCenterOffset);
            G.updateCityIdChanged();
        }
        startShimmer();
    }


    public void checkPhone(String phone) {

        G.loading(getContext());
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        api.checkPhone("mobile,cs," + phone).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                String result = G.getResult(response);
                JSONObject object = G.StringtoJSONObject(result);
                JSONArray array = G.ObjecttoArray(object, "records");
//                Log.e(G.TAG, result);
                if (array.length() > 0) {

                    try {
                        JSONObject info = array.getJSONObject(0);
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
                        String car_company_name = "";

                        if (info.has("car_id")) {
                            car_id = info.getInt("car_id");
                            car_tag = info.getString("car_plate");
                            car_company_name = (info.getString("car_company_name") + "").replace("null", "");
                            car_type = (info.getString("car_tip") + "").replace("null", "");
                            car_name = (info.getString("car_name") + "").replace("null", "");
                            if (car_company_name.length() > 0) {
                                car_name = car_company_name + " - " + car_name;
                            }
                            if (car_type.length() > 0) {
                                car_name = car_name + " - " + car_type;
                            }
                            car_model = info.getString("car_model");
                            car_type = info.getString("car_tip");
                            fuel_type = info.getString("fuel_type");
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mDBHelper != null) {
                                    mDBHelper.deleteHistoryKhadamt(G.preference.getInt("idService", 0) + "");
                                }
                            }
                        }, 0);
                        Intent intent = new Intent(getContext(), AddServicesActivity.class);
                        intent.putExtra("idCustomer", user_id + "");
                        intent.putExtra("firstName", name);
                        intent.putExtra("lastName", lastname);
                        intent.putExtra("phone", phone);
                        intent.putExtra("plak", car_tag);
                        intent.putExtra("nameCar", car_name);
                        intent.putExtra("id_customer", user_id + "");
                        intent.putExtra("id_car", car_id + "");
                        intent.putExtra("gender", sex);
                        intent.putExtra("date_birthday", birth_date);
                        intent.putExtra("type_fule", fuel_type);
                        intent.putExtra("date_save", "");
                        intent.putExtra("type_car", car_type);
                        intent.putExtra("model_car", car_model);
                        intent.putExtra("description", "");
                        intent.putExtra("finish", "");
                        intent.putExtra("fromMain", true);
                        startActivity(intent);

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "مشکل در دریافت اطلاعات", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    G.stop_loading();

                } else {
                    G.stop_loading();
                    Intent intent = new Intent(getContext(), AddCustomerActivity.class);
                    intent.putExtra("firstName", "null");
                    startActivity(intent);
                    preferenceUtil.cashNewCustomerPhone(phone);
                    Toast.makeText(getContext(), "موردی یافت نشد، مشتری جدید را ثبت کنید.", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                Toast.makeText(getContext(), "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onClick() {
        txt_search_plak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plak = edt1.getText().toString() + edt2.getText().toString() + edt3.getText().toString() + edt4.getText().toString() + edt5.getText().toString() + edt6.getText().toString() + edt7.getText().toString() + edt8.getText().toString();
                String phone = edt_phone_number.getText().toString();
                String endPlak = edt8.getText().toString();
                if (!TextUtils.isEmpty(phone) || !TextUtils.isEmpty(plak)) {
                    if (phone.equals("")) {
                        if (!TextUtils.isEmpty(endPlak)) {
                            checkTag(plak);
                        } else if (endPlak.equals("")) edt1.setError("پلاک را به درستی وارد کنید");
                    } else if (plak.equals("")) {
                        if (isValidMobile(phone)) {
                            checkPhone(phone);
                        } else {
                            edt_phone_number.setError("شماره موبایل را به درستی وارد کنید");
                        }
                    }
                } else if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(plak)) {
                    Toast.makeText(getContext(), "پلاک را وارد کنید", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "پلاک را وارد کنید", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txt_search_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), SimpleScannerActivity.class));
//                if (txt_search_phone.getText().equals("جستجو شماره موبایل")) {
//                    txt_search_phone.setText("جستجو پلاک");
//                    edt1.setText("");
//                    edt2.setText("");
//                    edt3.setText("");
//                    edt4.setText("");
//                    edt5.setText("");
//                    edt6.setText("");
//                    edt7.setText("");
//                    edt8.setText("");
//                    ly_plak.setVisibility(View.GONE);
//                    edt_phone.setVisibility(View.VISIBLE);
//                    txt_search_phone.setTextColor(getResources().getColor(R.color.text_low_dark));
//
//                } else if (txt_search_phone.getText().equals("جستجو پلاک")) {
//                    edt_phone_number.setText("");
//                    txt_search_phone.setText("جستجو شماره موبایل");
//                    ly_plak.setVisibility(View.VISIBLE);
//                    edt_phone.setVisibility(View.GONE);
//                    txt_search_phone.setTextColor(getResources().getColor(R.color.text_low_dark));
//                }
            }
        });
    }

    private boolean keydel = false;

    private void PlakListener() {
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
                // TODO Auto-generated method stub
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
                // TODO Auto-generated method stub
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
                // TODO Auto-generated method stub
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
                // TODO Auto-generated method stub
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
                // TODO Auto-generated method stub
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
                // TODO Auto-generated method stub
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
                // TODO Auto-generated method stub
            }

        });
        edt8.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    edt1.requestFocus();
                    txt_search_plak.performClick();
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


    void findView(View view) {
        edt1 = view.findViewById(R.id.edt1);
        edt2 = view.findViewById(R.id.edt2);
        edt3 = view.findViewById(R.id.edt3);
        edt4 = view.findViewById(R.id.edt4);
        edt5 = view.findViewById(R.id.edt5);
        edt6 = view.findViewById(R.id.edt6);
        edt7 = view.findViewById(R.id.edt7);
        edt8 = view.findViewById(R.id.edt8);
        gridView_main = view.findViewById(R.id.gridView_main);
//        txt_tile_action_bar = view.findViewById(R.id.txt_tile_action_bar);
        txt_search_plak = view.findViewById(R.id.txt_search_plak);
        txt_search_phone = view.findViewById(R.id.txt_search_phone);
        edt_phone = view.findViewById(R.id.edt_phone);
        ly_plak = view.findViewById(R.id.ly_plak);
        edt_phone_number = view.findViewById(R.id.edt_phone_number);
        recycle_advertise_two = view.findViewById(R.id.recycle_advertise_two);
        recycle_advertise_one = view.findViewById(R.id.recycle_advertise_one);
        txt_title_advertise1 = view.findViewById(R.id.txt_title_advertise1);
        txt_title_advertise2 = view.findViewById(R.id.txt_title_advertise2);
        txt_title_advertisetop = view.findViewById(R.id.txt_title_advertisetop);
        txt_title_category1 = view.findViewById(R.id.txt_title_category);
        txt_best_marakez1 = view.findViewById(R.id.txt_best_marakez);
        txt_next_serv = view.findViewById(R.id.txt_next_serv);
        txt_empty_next_service = view.findViewById(R.id.empty_next_service);
        recycle_advertise_top = view.findViewById(R.id.recycle_advertise_top);
//        sliderView = view.findViewById(R.id.sliderView);
        searchpelak = view.findViewById(R.id.searchpelak);
      /*  txt_add_more_centers = view.findViewById(R.id.txt_add_more_centers);
        txt_add_more_centers.setTypeface(G.Bold);
*/
        recycle_done_service_type = view.findViewById(R.id.recycle_done_service_type);
        recycle_service_centers = view.findViewById(R.id.recycle_service_centers);
        //iv_add_more_centers = view.findViewById(R.id.iv_add_more_centers);
        txt_product_name = view.findViewById(R.id.txt_product_name);
        txt_car_name = view.findViewById(R.id.txt_car_name);
        txt_date = view.findViewById(R.id.txt_date);

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ShowCase();
            }
        }, 1200);*/
        marakez_khadamat_khodro = view.findViewById(R.id.marakez_khadamat_khodro);
        marakez_khadamat_khodro.setVisibility(View.GONE);

        behtarin_marakez = view.findViewById(R.id.behtarin_marakez);
        behtarin_marakez.setVisibility(View.GONE);


        majale_khodro = view.findViewById(R.id.majale_khodro);
        majale_khodro.setVisibility(View.GONE);

        majale_amozeshi = view.findViewById(R.id.majale_amozeshi);
        majale_amozeshi.setVisibility(View.GONE);

//        slider_root = view.findViewById(R.id.slider_root);
//        slider_root.setVisibility(View.GONE);

        service_badi = view.findViewById(R.id.service_badi);
        layout_item_next_service = view.findViewById(R.id.layout_item_next_service);
        service_badi.setVisibility(View.GONE);

        ic_reserve_list = view.findViewById(R.id.ic_reserve_list);
        ic_reserve_list.setVisibility(View.VISIBLE);

        serviceCenters.clear();
        recycle_service_centers.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        AdapterServiceCenterGrid.OnAddMoreClicked onAddMoreCentersClicked = new AdapterServiceCenterGrid.OnAddMoreClicked() {
            @Override
            public void onAddMoreCentersClicked() {
                if (loadMoreCenters) {
                    getServiceCenters("", 0, serviceCenterOffset);
                }
            }
        };
        adapterServiceCenter = new AdapterServiceCenterGrid(getContext(), serviceCenters, onAddMoreCentersClicked);
//                  adapterListProduceGroup = new AdapterListProduceGroup(ProductGroupActivity.this, mDBHelper.getListProductGroup(mDatabase));
        recycle_service_centers.setAdapter(adapterServiceCenter);

        advertise_one_shimmer_layout = view.findViewById(R.id.advertise_one_shimmer_layout);
        advertise_two_shimmer_layout = view.findViewById(R.id.advertise_two_shimmer_layout);
        best_service_centers_shimmer_layout = view.findViewById(R.id.best_service_centers_shimmer_layout);
        job_categories_shimmer_layout = view.findViewById(R.id.job_categories_shimmer_layout);
        slider_shimmer_layout = view.findViewById(R.id.slider_shimmer_layout);
        grid_main_shimmer_layout = view.findViewById(R.id.grid_main_shimmer_layout);
        next_service_shimmer_layout = view.findViewById(R.id.next_service_shimmer_layout);

    }

    private void startShimmer() {
        advertise_one_shimmer_layout.startShimmer();
        advertise_two_shimmer_layout.startShimmer();
        best_service_centers_shimmer_layout.startShimmer();
        job_categories_shimmer_layout.startShimmer();
        slider_shimmer_layout.startShimmer();
        grid_main_shimmer_layout.startShimmer();
        next_service_shimmer_layout.startShimmer();
    }

    private void setupGridMainMenu() {
        gridView_main.setLayoutManager(new GridLayoutManager(getContext(), 4));
        gridView_main.setAdapter(new AdapterListGridMain(getContext(), number));

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
        config.setShapePadding(50);
        config.setMaskColor(Color.parseColor("#cc222222"));

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "ShoowCaseMain");

        sequence.setConfig(config);

        android.app.Activity activity = getActivity();
        sequence.addSequenceItem(Utils.createCustomShowcaseView(activity,searchpelak, getString(R.string.showcase_search_plak), getString(R.string.next_showcase)));
        sequence.addSequenceItem(Utils.createCustomShowcaseView(activity,AdapterListGridMain.listCustView, getString(R.string.showcase_list_cust_view), getString(R.string.next_showcase)));
        sequence.addSequenceItem(Utils.createCustomShowcaseView(activity,AdapterListGridMain.listServiceView, getString(R.string.showcase_list_service_view), getString(R.string.close_showcase)));
        sequence.start();
    }

//    public void getSlider() {
//        slider_root.setVisibility(View.GONE);
////      G.loading(G.Activity);
//        String d_id = PreferenceUtil.getD_id();
//        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
//        Call<ResponseBody> request = api.getSlider();
//        request.enqueue(new retrofit2.Callback<ResponseBody>() {
//            @Override
//            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
//                assert response.body() != null;
//                try {
//                    String result = response.body().string();
//                    G.Log("slider:" + result);
//                    JSONObject object = G.StringtoJSONObject(result);
//                    JSONArray records = object.getJSONArray("records");
//                    if (records.length() > 0) {
//                        List<SliderItem> sliderItemList = new ArrayList<>();
//                        slider_root.setVisibility(View.VISIBLE);
//                        for (int i = 0; i < records.length(); i++) {
//                            JSONObject obj = records.getJSONObject(i);
//                            SliderItem sliderItem = new SliderItem();
//                            String id = obj.getString("id");
//                            String title = obj.getString("title");
//                            String image = obj.getString("image");
//                            String url = obj.getString("url");
//                            sliderItem.setDescription(title);
//                            sliderItem.setUrl(url);
//                            sliderItem.setImageUrl(G.PreImagesURL + "slides/" + image);
//                            sliderItemList.add(sliderItem);
//                        }
//                        stopShimmer(slider_shimmer_layout);
//                        adapter.renewItems(sliderItemList);
//                    } else {
//                        G.toast("مشکل در دریافت اطلاعات");
//                    }
//                } catch (JSONException | IOException e) {
//                    G.toast("مشکل در تجزیه اطلاعات");
//                    e.printStackTrace();
//                }
//                getJob_categories();
//                G.stop_loading();
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
//                getJob_categories();
//                G.stop_loading();
//                G.toast("مشکل در برقراری ارتباط");
//            }
//        });
//    }

    public void getProfile() {

//        G.loading(G.Activity);
        String user_id = PreferenceUtil.getUser_id();
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getProfile("id,eq," + user_id);
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                assert response.body() != null;
                try {
                    String result = response.body().string();
                    G.Log(result);
                    JSONObject object = G.StringtoJSONObject(result);
                    JSONArray records = object.getJSONArray("records");
                    if (records.length() > 0) {
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject obj = records.getJSONObject(i);
                            String user_id = obj.getString("id");
                            PreferenceUtil.cashUser_id(user_id);
                            String f_name = obj.getString("f_name");
                            String l_name = obj.getString("l_name");
                            String mobile = obj.getString("mobile");
                            String sex = obj.getString("sex");
                            String birthdate = obj.getString("birthdate");
                            String province_id = obj.getString("province_id");
                            String city_id = obj.getString("city_id");
                            G.preference.edit().putString("province_id", province_id).apply();
                            G.preference.edit().putString("city_id", city_id).apply();
                            if (obj.has("profile_photo")) {
                                String profile_photo = obj.getString("profile_photo");
                                G.preference.edit().putString("profile_photo", profile_photo).apply();
                            }
//                                    if (obj.has("header_photo")) {
//                                        String header_photo = obj.getString("header_photo");
//                                        G.preference.edit().putString("header_photo", header_photo).apply();
//                                        Picasso.get().load(G.PreImagesURL + "headers/" + header_photo).error(R.drawable.backpro).placeholder(R.drawable.backpro).into(profile_iv2);
//                                    }
                            if (obj.has("password")) {
                                String password = obj.getString("password");
                                String now_password = G.preference.getString("PASSWORD", "");
                                if (now_password.length() < 3) {
                                    G.preference.edit().putString("PASSWORD", password).apply();
                                }
                            }


                            PreferenceUtil.cashPhone(mobile);
                            PreferenceUtil.cashInfo(f_name, l_name, "", "");
                            StateOpenHelper openHelper = new StateOpenHelper(G.Activity);
                            openHelper.openDatabase();
                            SQLiteDatabase mDatabase = openHelper.getReadableDatabase();
                        }
                    } else {
                        G.toast("مشکل در دریافت اطلاعات");
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

    public void removeLastItem(View view) {
//        if (adapter.getCount() - 1 >= 0)
//            adapter.deleteItem(adapter.getCount() - 1);
    }


    private Boolean isValidMobile(String mobile_number) {
        String MOBILE_NUM_PATTERN_ST = "(\\+98|0)?9\\d{9}";
        Pattern MOBILE_NUM_PATTERN_REX = Pattern.compile(MOBILE_NUM_PATTERN_ST);
        return MOBILE_NUM_PATTERN_REX.matcher(mobile_number).matches();
    }

    @Override
    public void onStart() {
        super.onStart();
        edt1.setText("");
        edt2.setText("");
        edt3.setText("");
        edt4.setText("");
        edt5.setText("");
        edt6.setText("");
        edt7.setText("");
        edt8.setText("");
        edt1.requestFocus();
        edt_phone_number.setText("");
    }


    public void getBlogs() {
        majale_khodro.setVisibility(View.GONE);
        majale_amozeshi.setVisibility(View.GONE);
//        G.loading(getActivity());
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.blogs("");
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String result = G.getResult(response);
                JSONObject object = G.StringtoJSONObject(result);
                JSONArray array = G.ObjecttoArray(object, "records");
                if (array.length() > 0) {
                    try {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.getInt("id");
                            int type = obj.getInt("type");
                            String title = obj.getString("title");
                            String summary = obj.getString("summary");
                            String content = obj.getString("content");
                            String image = obj.getString("image");
                            String slug = obj.getString("slug");
                            if (type == 1)
                                advertises.add(new ModelAdvertise(title, image, slug));
                            if (type == 2)
                                advertisess.add(new ModelAdvertise(title, image, slug));


                        }
                        stopShimmer(advertise_one_shimmer_layout);
                        stopShimmer(advertise_two_shimmer_layout);
                        if (advertises.size() > 0) {
                            majale_khodro.setVisibility(View.VISIBLE);
                        }
                        if (advertisess.size() > 0) {
                            majale_amozeshi.setVisibility(View.VISIBLE);
                        }

                        blogEdu.notifyDataSetChanged();
                        blogCar.notifyDataSetChanged();

                        G.stop_loading();

                    } catch (JSONException e) {
                        G.toast("مشکل در دریافت اطلاعات");
                        e.printStackTrace();
                    }


                } else {

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

    private void stopShimmer(ShimmerFrameLayout shimmerLayout) {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
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
                        serviceCenter.setScore("" + avgScore);
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
                    centersScore = centersScoreTemp.entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue, LinkedHashMap::new));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_reserve_list) {
            startActivity(new Intent(context, ListReserveActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

