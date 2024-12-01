package com.servicea.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.servicea.activity.JobCategoriesActivity;
import com.servicea.adapter.CarImageSliderAdapter;
import com.servicea.app.Constants;
import com.servicea.app.G;
import com.servicea.app.PLakUtils;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.CarListViewModel;
import com.servicea.model.dbModel.ModelCustomer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ir.servicea.R;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class CarListActivity extends AppCompatActivity {
    private ShimmerFrameLayout activity_car_list_shimmer;
    private ViewPager viewPager;
    private AppCompatImageView before, next;
    private LinearLayout indicatorLayout, ll_add_btn;

    private int currentPage = 0;
    private List<ModelCustomer> productList = new ArrayList<>();
    public int page = 0;

    boolean isLoading = false;

    private LinearLayout ll_empty, ll_car_detail, ll_next_service;
    private ScrollView root_activity_list_car;
    private TextView txt_tile_action_bar;

    public static Activity customer;

    private CarListViewModel carListViewModel;

    TextView txt_fuel_type;
    TextView txt_km_now;
    TextView txt_product_group, txt_due_date;
    TextView txt_name_car, txt_model_car;
    ImageView img_back, img_add;
    Button btn_edit_customer, service, btn_add_car, btn_add_service;
    TextView txt_plak_customer1, txt_plak_customer2, txt_plak_customer3, txt_plak_customer4;
    ViewGroup plaks;
    private ViewGroup ly_plk_general, ly_plk_taxi, ly_plk_edari, ly_plk_entezami, ly_plk_malolin, ly_plk_azad_new, ly_plk_azad_old;

    Constants.PLAK_TYPE plak_type = Constants.PLAK_TYPE.PLAK_GENERAL;
    ViewGroup plak_layout;

    private Intent intentThatOpenInformationCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);

        Intent intent = getIntent();
        String source = intent.getStringExtra("source");

        intentThatOpenInformationCustomer = getIntent();
        findViews();

        onClick();
        if (G.finalProductLst != null && Objects.equals(source, "main")) {
            stopShimmer(activity_car_list_shimmer);
            updateCarDetails(currentPage);
        } else {
            activity_car_list_shimmer.startShimmer();
        }
        carListViewModel = new CarListViewModel();
        startPulseAnimation();

        getProductList();


        customer = this;
        txt_tile_action_bar.setText("خودروهای من");
        txt_tile_action_bar.setTypeface(G.Bold);
    }

    private void onClick() {
        before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage > 0) {
                    currentPage--;
                    viewPager.setCurrentItem(currentPage);
                    updateIndicators(currentPage);
                    updateCarDetails(currentPage);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage < Objects.requireNonNull(viewPager.getAdapter()).getCount() - 1) {
                    currentPage++;
                    viewPager.setCurrentItem(currentPage);
                    updateIndicators(currentPage);
                    updateCarDetails(currentPage);
                }
            }
        });
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_add_btn.setVisibility(View.VISIBLE);
            }
        });
        btn_add_car.setTypeface(G.Normal);

        btn_add_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarListActivity.this, AddNewCarActivity.class);
                intent.putExtra("firstName", "null");
                startActivity(intent);
                finish();
            }
        });
        btn_add_service.setTypeface(G.Normal);

        btn_add_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarListActivity.this, JobCategoriesActivity.class);

                intent.putExtra(Constants.CUSTOMER_MODEL, (Serializable) G.finalProductLst.get(currentPage));
                intent.putExtra("fromMain", false);
                Log.d("CustomerActivity", "onNewServiceClick: " + G.finalProductLst.get(currentPage).toString());
                startActivity(intent);
                finish();
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_edit_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    Intent intent = new Intent(CarListActivity.this, AddNewCarActivity.class);
                    intent.putExtra("idCustomer", PreferenceUtil.getUser_id());
                    intent.putExtra("car_id", G.finalProductLst.get(currentPage).getCar_id());
//                    intent.putExtra("id_car", getIntent().getExtras().getString("id_car"));
                    intent.putExtra("firstName", G.finalProductLst.get(currentPage).getFirst_name());
                    intent.putExtra("lastName", G.finalProductLst.get(currentPage).getLast_name());
                    intent.putExtra("phone", G.finalProductLst.get(currentPage).getPhone());
                    intent.putExtra("nameCar", G.finalProductLst.get(currentPage).getLast_name());
                    intent.putExtra("plak", G.finalProductLst.get(currentPage).getPlak());
                    intent.putExtra(Constants.CAR_PLATE_TYPE, G.finalProductLst.get(currentPage).getPlak_type());
                    intent.putExtra("gender", G.finalProductLst.get(currentPage).getGender());
                    intent.putExtra("date_birthday", G.finalProductLst.get(currentPage).getDate_birthday());
                    intent.putExtra("type_fule", G.finalProductLst.get(currentPage).getType_fuel());
                    intent.putExtra("date_save", G.finalProductLst.get(currentPage).getDate_save_customer());
                    intent.putExtra("type_car", G.finalProductLst.get(currentPage).getType_car());
                    intent.putExtra("car_name_id", G.finalProductLst.get(currentPage).getCar_name_id());
                    intent.putExtra("car_tip_id", G.finalProductLst.get(currentPage).getCar_tip_id());
                    intent.putExtra("car_model_id", G.finalProductLst.get(currentPage).getCar_model_id());
                    intent.putExtra("car_company_id", G.finalProductLst.get(currentPage).getCar_company_id());
                    intent.putExtra("fuel_type_id", G.finalProductLst.get(currentPage).getFuel_type_id());
                    startActivity(intent);
                    finish();
                }
            }
        });
        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarListActivity.this, LastServiseDoneActivity.class);
                intent.putExtra("car_id", G.finalProductLst.get(currentPage).getCar_id());
                startActivity(intent);
            }
        });
        ll_next_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CarListActivity.this, ListTimingActivity.class));
            }
        });
    }

    private void findViews() {
        activity_car_list_shimmer = findViewById(R.id.activity_car_list_shimmer);
        before = findViewById(R.id.before);
        next = findViewById(R.id.next);
        ll_empty = findViewById(R.id.ll_empty);
        ll_car_detail = findViewById(R.id.ll_car_details);
        txt_product_group = findViewById(R.id.txt_product_group);
        txt_due_date = findViewById(R.id.tv_due_date);
        ll_next_service = findViewById(R.id.ll_next_service);
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        ll_add_btn = findViewById(R.id.ll_add_btn);
        btn_add_car = findViewById(R.id.btn_add_car);
        btn_add_service = findViewById(R.id.btn_add_service);
        img_add = findViewById(R.id.ic_add);
//        startHeartAnimation();
        root_activity_list_car = findViewById(R.id.root_activity_car_list);
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        txt_fuel_type = findViewById(R.id.txt_fuel_type);
        txt_km_now = findViewById(R.id.tv_kilometer);
        img_back = findViewById(R.id.img_back);
        btn_edit_customer = findViewById(R.id.btn_edit_customer);
        service = findViewById(R.id.service);
        txt_name_car = findViewById(R.id.txt_name_car);
        txt_model_car = findViewById(R.id.txt_model_car);
        plaks = findViewById(R.id.plaks);
        txt_plak_customer1 = findViewById(R.id.txt_plak_customer1);
        txt_plak_customer2 = findViewById(R.id.txt_plak_customer2);
        txt_plak_customer3 = findViewById(R.id.txt_plak_customer3);
        txt_plak_customer4 = findViewById(R.id.txt_plak_customer4);

        ly_plk_general = findViewById(R.id.ly_plk_general);
        ly_plk_taxi = findViewById(R.id.ly_plk_taxi);
        ly_plk_edari = findViewById(R.id.ly_plk_edari);
        ly_plk_entezami = findViewById(R.id.ly_plk_entezami);
        ly_plk_malolin = findViewById(R.id.ly_plk_malolin);
        ly_plk_azad_new = findViewById(R.id.ly_plk_azad_new);
        ly_plk_azad_old = findViewById(R.id.ly_plk_azad_old);
        plak_layout = ly_plk_general;
    }

    private void initCarImageViewPager() {
        viewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);

        CarImageSliderAdapter adapter = new CarImageSliderAdapter(this, G.finalProductLst);
        viewPager.setAdapter(adapter);
        setupIndicators();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                currentPage = position;
            }

            @Override
            public void onPageSelected(int position) {
                updateIndicators(position);
                updateCarDetails(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupIndicators() {

        for (int i = 0; i < G.finalProductLst.size(); i++) {
            View dot = new View(this);
            dot.setBackgroundResource(R.drawable.indicator_unselected); // shape for inactive dot
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);
            indicatorLayout.addView(dot, params);
        }
        updateIndicators(0); // initialize first indicator as active
        updateCarDetails(0);
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            View dot = indicatorLayout.getChildAt(i);
            dot.setBackgroundResource(i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected);
        }
    }

    private void updateCarDetails(int position) {
        txt_name_car.setText(G.finalProductLst.get(position).getName_car());
        txt_name_car.setTypeface(G.Normal);
        txt_model_car.setText(G.finalProductLst.get(position).getCar_model());
        txt_model_car.setTypeface(G.Normal);

        String plak = (G.finalProductLst.get(position).getPlak() + "").replace(" ", "").replace("null", "");
        if (plak.length() > 3) {
            String c1 = plak.substring(0, 2);
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
            txt_plak_customer4.setTypeface(G.ExtraBold);
            setPlakBasedViewType(plak, plak_type);

        } else {
            plaks.setVisibility(View.GONE);
        }
        txt_fuel_type.setTypeface(G.Normal);
        if (G.finalProductLst.get(position).getType_fuel().contains("بنزین")) {
            txt_fuel_type.setText("بنزین");
        } else if (G.finalProductLst.get(position).getType_fuel().contains("دیزل")) {
            txt_fuel_type.setText("دیزل");

        } else if (G.finalProductLst.get(position).getType_fuel().contains("دوگانه")) {
            txt_fuel_type.setText("دوگانه");

        } else if (G.finalProductLst.get(position).getType_fuel().contains("هیبرید")) {
            txt_fuel_type.setText("هیبرید");

        }
        txt_product_group.setTypeface(G.Normal);
        if (G.finalProductLst.get(currentPage).getProduct_group_name() == null) {
            txt_product_group.setText("شما درحال حاضر سرویس نزدیکی ندارید");
        } else {
            txt_product_group.setText(G.finalProductLst.get(currentPage).getProduct_group_name());
            txt_due_date.setText(G.finalProductLst.get(currentPage).getProduct_due_date());
            txt_due_date.setTypeface(G.Normal);
        }
        txt_km_now.setTypeface(G.ExtraBold);
        if (G.finalProductLst.get(position).getCar_kilometer() == null)
            txt_km_now.setText("ندارد");
        else
            txt_km_now.setText(G.finalProductLst.get(position).getCar_kilometer());

        setPlakLayout(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        G.Activity = this;
        G.context = this;
        if (G.preference.getBoolean("changeCar", false)) {
            page = 0;
            G.preference.edit().putBoolean("changeCar", false).apply();
        }

//        getProductList();
//        customers_shimmer_layout.startShimmer();
    }

    public void getProductList() {
        carListViewModel.getCarList().observe(this, new Observer<List<ModelCustomer>>() {
            @Override
            public void onChanged(List<ModelCustomer> modelCustomers) {

                stopShimmer(activity_car_list_shimmer);
                productList = modelCustomers;
                G.finalProductLst = productList;
                initCarImageViewPager();

            }

        });

    }

    private void setPlakLayout(int position) {

//        if (intentThatOpenInformationCustomer.hasExtra(Constants.CAR_PLATE_TYPE)) {
//            plak_type = (Constants.PLAK_TYPE) intentThatOpenInformationCustomer.getSerializableExtra(Constants.CAR_PLATE_TYPE);
//        }
        if (G.finalProductLst.get(position).getPlak_type() != null) {
            plak_type = G.finalProductLst.get(position).getPlak_type();
        }

        switch (plak_type) {
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
        Log.d("PLAK", "setPlakBasedViewType:addService: " + plak_layout);

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
                break;
            }
            case PLAK_MAOLOIN: {
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

    private void stopShimmer(ShimmerFrameLayout shimmerLayout) {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
        root_activity_list_car.setVisibility(View.VISIBLE);
    }

    ;

    private void startHeartAnimation() {
        // Load the scale animation and start it
        img_add.setScaleX(1f); // Reset scale to avoid unexpected behaviour
        img_add.setScaleY(1f);
        img_add.setAnimation(AnimationUtils.loadAnimation(this, R.anim.heart_scale));
        img_add.startAnimation(AnimationUtils.loadAnimation(this, R.anim.heart_scale));
    }


    private void startPulseAnimation() {
        PulsatorLayout pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.start();
        pulsator.setCount(4);
    }


}