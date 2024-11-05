package com.servicea.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.servicea.adapter.AdapterJobCategory;
import com.servicea.adapter.GridSpacingItemDecoration;
import com.servicea.app.Constants;
import com.servicea.app.G;
import com.servicea.app.RecyclerItemClickListener;
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
import java.util.List;

import ir.servicea.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


public class JobCategoriesActivity extends AppCompatActivity {

    private List<String> listJobs = new ArrayList<>();
    private List<Integer> listJobsIds = new ArrayList<>();
    private RecyclerView recycle_done_service_type;
    TextView txt_tile_action_bar, txt_title_choose_category;
    TextView add_text;
    ImageView img_back;

    private ModelCustomer customer;
    private Boolean fromMain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_categories);
        G.Activity = this;
        G.context = this;
        FindView();
        onCLick();
        txt_tile_action_bar.setText("لیست خدمات خودرو");
        txt_tile_action_bar.setTypeface(G.ExtraBold);
        txt_title_choose_category.setTypeface(G.Normal);
        getCustomerInformationFromExtra();
        getJob_categories();

    }

    private void FindView() {
        recycle_done_service_type = findViewById(R.id.recycle_done_service_type);
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        img_back = findViewById(R.id.img_back);
        recycle_done_service_type = findViewById(R.id.recycle_done_service_type);
        txt_title_choose_category = findViewById(R.id.txt_title_choose_category);

    }

    private void onCLick() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getCustomerInformationFromExtra() {
        Intent intentFromCustomerActivity = getIntent();
        customer = (ModelCustomer) intentFromCustomerActivity.getSerializableExtra("customer_model");
        fromMain = intentFromCustomerActivity.getBooleanExtra("fromMain", false);
    }

    public void getJob_categories() {
        //  marakez_khadamat_khodro.setVisibility(View.GONE);

        listJobs.clear();
        listJobsIds.clear();
        listJobs.add("دسته شغلی");
        listJobsIds.add(0);

  /*      spinnerAdapter = new ArrayAdapter(getContext(), R.layout.item_spiner, listJobs);
        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

*/
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
                        List<ModelJobCategory> listJobCategory = new ArrayList<>();
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
                            listJobs.add(title);
                            listJobsIds.add(id);
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

                        recycle_done_service_type.setLayoutManager(new GridLayoutManager(JobCategoriesActivity.this, 3));
                        int spacing = 40; // 50px
                        boolean includeEdge = false;
                        recycle_done_service_type.setHasFixedSize(true);
                        recycle_done_service_type.addItemDecoration(new GridSpacingItemDecoration(3, spacing, includeEdge));
                        AdapterJobCategory adapterJobCategory = new AdapterJobCategory(JobCategoriesActivity.this, listJobCategory);
                        recycle_done_service_type.setAdapter(adapterJobCategory);
                        recycle_done_service_type.addOnItemTouchListener(
                                new RecyclerItemClickListener(G.context, recycle_done_service_type, new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        ModelJobCategory jobCategory = listJobCategory.get(position);
                                        if (jobCategory.getStatus() == 1) {
                                            Intent intentThatStartsAddServiceActivity = new Intent(JobCategoriesActivity.this, AddServicesActivity.class);
                                            intentThatStartsAddServiceActivity.putExtra("job_id", jobCategory.getId());
                                            intentThatStartsAddServiceActivity.putExtra("idCustomer", customer.getId() + "");
                                            intentThatStartsAddServiceActivity.putExtra(Constants.CAR_ID, customer.getCar_id() + "");
                                            intentThatStartsAddServiceActivity.putExtra("firstName", customer.getFirst_name());
                                            intentThatStartsAddServiceActivity.putExtra("lastName", customer.getLast_name());
                                            intentThatStartsAddServiceActivity.putExtra("phone", customer.getPhone());
                                            intentThatStartsAddServiceActivity.putExtra("nameCar", customer.getName_car());
                                            intentThatStartsAddServiceActivity.putExtra("plak", customer.getPlak());
                                            intentThatStartsAddServiceActivity.putExtra(Constants.CAR_PLATE_TYPE, customer.getPlak_type());
                                            intentThatStartsAddServiceActivity.putExtra("id_customer", customer.getId());
                                            intentThatStartsAddServiceActivity.putExtra("description", "");
                                            intentThatStartsAddServiceActivity.putExtra("gender", customer.getGender());
                                            intentThatStartsAddServiceActivity.putExtra("date_birthday", customer.getDate_birthday());
                                            intentThatStartsAddServiceActivity.putExtra("type_fule", customer.getType_fuel());
                                            intentThatStartsAddServiceActivity.putExtra("date_save", customer.getDate_save_customer());
                                            intentThatStartsAddServiceActivity.putExtra("type_car", customer.getType_car());
                                            intentThatStartsAddServiceActivity.putExtra("car_name_id", customer.getCar_name_id());
                                            intentThatStartsAddServiceActivity.putExtra("car_tip_id", customer.getCar_tip_id());
                                            intentThatStartsAddServiceActivity.putExtra("car_model_id", customer.getCar_model_id());
                                            intentThatStartsAddServiceActivity.putExtra("car_company_id", customer.getCar_company_id());
                                            intentThatStartsAddServiceActivity.putExtra("fuel_type_id", customer.getFuel_type_id());
                                            intentThatStartsAddServiceActivity.putExtra("finish", "finish");
                                            intentThatStartsAddServiceActivity.putExtra("fromMain", fromMain);
                                            startActivity(intentThatStartsAddServiceActivity);
                                        } else {
                                            Toast.makeText(JobCategoriesActivity.this, "به زودی", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onLongItemClick(View view, int position) {
                                        // do whatever
                                    }
                                })
                        );
                        //  spinnerAdapter.notifyDataSetChanged();


                    } else {
                        G.toast("هیچ دسته شغلی یافت نشد!");
                    }
                } catch (JSONException | IOException e) {
                    G.toast("مشکل در تجزیه اطلاعات");
                    e.printStackTrace();
                }
                //    getServiceCenters("", 0);

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
                // getServiceCenters("", 0);
            }
        });


    }

}





