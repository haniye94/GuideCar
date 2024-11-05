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

import com.servicea.adapter.AdapterComment;
import com.servicea.adapter.AdapterTiming;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.Comment;
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

public class ListCommentActivity extends AppCompatActivity {
    private TextView txt_tile_action_bar,txt_category_name,txt_shop_name,txt_score,txt_count_comment;
    private ImageView img_back;
    public static List<Comment> comments = new ArrayList<>();
    private AdapterComment adapterComment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recycle_produce_group;

    public void onclickAlamrs(View v) {
        startActivity(new Intent(ListCommentActivity.this, AlarmsActivity.class));
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
        setContentView(R.layout.activity_list_comment);
        G.Activity = this;
        G.context = this;
        FindView();
        onClick();
        txt_tile_action_bar.setText("نظرات مشتریان");
        txt_tile_action_bar.setTypeface(G.Bold);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("center_id")) {
            center_id = intent.getStringExtra("center_id");
            String category_name = intent.getStringExtra("category_name");
            txt_category_name.setText(category_name);
            String center_name = intent.getStringExtra("center_name");
            txt_shop_name.setText(center_name);
            String center_score = intent.getStringExtra("center_score");
            txt_score.setText(center_score);



            getComment(center_id);
        } else {
            G.toast("کد خالی");
            finish();
        }

    }
    public String center_id = "";


    private void FindView() {
        txt_category_name = findViewById(R.id.txt_category_name);
        txt_category_name.setTypeface(G.ExtraBold);
        txt_shop_name = findViewById(R.id.txt_shop_name);
        txt_shop_name.setTypeface(G.ExtraBold);
        txt_count_comment = findViewById(R.id.txt_count_comment);
        txt_count_comment.setTypeface(G.ExtraBold);
        txt_score = findViewById(R.id.txt_score);
        txt_score.setTypeface(G.ExtraBold);
        TextView head_score = findViewById(R.id.head_score);
        head_score.setTypeface(G.ExtraBold);
        TextView head_count = findViewById(R.id.head_count);
        head_count.setTypeface(G.ExtraBold);
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        img_back = findViewById(R.id.img_back);
        recycle_produce_group = findViewById(R.id.recycle_produce_group);
        swipeRefreshLayout = findViewById(R.id.swipe);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.button));
//        TextView count = findViewById(R.id.count);
//        count.setTypeface(G.ExtraBold);
//        TextView title = findViewById(R.id.title);
//        title.setTypeface(G.ExtraBold);
    }

    private void onClick() {
        img_back.setOnClickListener(view -> finish());
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            new Handler().postDelayed(() -> getComment(center_id), 250);
        });

    }

    public void getComment(String center_id) {

        swipeRefreshLayout.setRefreshing(true);
        comments.clear();
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getComments(  "service_center_id,eq,"+center_id,9999+"");
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
                        txt_count_comment.setText(records.length()+"");
                        if (records.length() > 0) {

                            for (int i = 0; i < records.length(); i++) {
                                Comment comment = new Comment();
                                JSONObject obj = records.getJSONObject(i);
                                int sc_id = obj.getInt("id");
                                comment.setId(sc_id);
                                if(obj.has("user_id")){
                                    JSONObject user = obj.getJSONObject("user_id");
                                    String user_id = user.getString("id");
                                    String f_name = user.getString("f_name");
                                    String l_name = user.getString("l_name");
                                    String profile_photo = user.getString("profile_photo");
                                    comment.setUser_id(user_id);
                                    comment.setUser_name(f_name+" "+l_name);
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
                            G.toast("موردی یافت نشد!");
                        }
                    } catch (JSONException | IOException e) {
                        G.toast("مشکل در تجزیه اطلاعات");
                        e.printStackTrace();
                    }
                }
                G.stop_loading();
                recycle_produce_group.setLayoutManager(new LinearLayoutManager(ListCommentActivity.this, RecyclerView.VERTICAL, false));
                adapterComment = new AdapterComment(ListCommentActivity.this, comments);
                recycle_produce_group.setAdapter(adapterComment);
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