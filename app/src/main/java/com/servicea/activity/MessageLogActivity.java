package com.servicea.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.servicea.activities.AlarmsActivity;
import com.servicea.activities.MainActivity;
import com.servicea.adapter.AdapterLogMessage;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.ModelML;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.servicea.R;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class MessageLogActivity extends AppCompatActivity {

    TextView txt_tile_action_bar;
    RecyclerView recycle;
    ImageView img_add_message, img_back;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdapterLogMessage adapter;
    private Handler handler;
    @Override
    public void onResume(){
        super.onResume();
        G.Activity = this;
        G.context = this;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_message);
        G.Activity = this;
        G.context = this;
        FindView();
        onClick();
        EditText search = findViewById(R.id.search);
        txt_tile_action_bar.setText("گزارش پیامک\u200Cها");
        txt_tile_action_bar.setTypeface(G.Bold);
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.button));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        search.setText("");
                        getManageMessage("");
                    }
                }, 250);
            }
        });
        getManageMessage("");

        handler = new Handler();
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() > 0) {


                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getManageMessage(search.getText().toString());
                        }
                    }, 750);

                } else if (s.length() == 0) {
                    getManageMessage("");
                }

            }
        });
        recycle.setLayoutManager(new LinearLayoutManager(MessageLogActivity.this, RecyclerView.VERTICAL, false));
        adapter = new AdapterLogMessage(MessageLogActivity.this, modelMLS);
        recycle.setAdapter(adapter);
    }

    public List<ModelML> modelMLS = new ArrayList<>();

    public void getManageMessage(String key) {

        swipeRefreshLayout.setRefreshing(true);

        String d_id = PreferenceUtil.getD_id();
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.msg_log("service_center_id,eq," + d_id, "user_fullname,cs," + key, "user_mobile,cs," + key,"id,desc");
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.e("sdsdsd",call.request().toString());
                assert response.body() != null;

                try {
                    String result = response.body().string();
                    JSONObject object = G.StringtoJSONObject(result);
                    JSONArray records = object.getJSONArray("records");
                    modelMLS.clear();
                    if (records.length() > 0) {


                        for (int i = 0; i < records.length(); i++) {
                            JSONObject obj = records.getJSONObject(i);
                            int id = obj.getInt("id");
                            int user_id = obj.getInt("user_id");
                            String content = obj.getString("content");
                            String create_at = obj.getString("send_at");
                            String send_at = obj.getString("send_at");
                            int char_count = obj.getInt("char_count");
                            int total_price = obj.getInt("total_price");

                            String user_fullname = "";
                            String user_phone = "";
                            if (obj.has("user_fullname")) {
                                user_fullname = obj.getString("user_fullname");
                            }
                            if (obj.has("user_mobile")) {
                                user_phone = obj.getString("user_mobile");
                            }
                            modelMLS.add(new ModelML(id, user_id, content, create_at, send_at, char_count, total_price, user_fullname, user_phone));

                        }



                    } else {
                        G.toast("هیچ موردی یافت نشد!");
                    }
                } catch (JSONException | IOException e) {
                    G.toast("مشکل در تجزیه اطلاعات");
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }

    private void FindView() {
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        recycle = findViewById(R.id.recycle_done_service_type);
        img_add_message = findViewById(R.id.img_add_message);
        img_back = findViewById(R.id.img_back);
    }

    private void onClick() {

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onclickAlamrs(View v) {
        startActivity(new Intent(MessageLogActivity.this, AlarmsActivity.class));
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context));
    }
}