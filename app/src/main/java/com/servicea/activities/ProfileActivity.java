package com.servicea.activities;

import static android.os.Build.VERSION.SDK_INT;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.servicea.adapter.AdapterAddress;
import com.servicea.app.CircleTransform;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.app.StateOpenHelper;
import com.servicea.app.UploadTask;
import com.servicea.model.Address;
import com.servicea.model.State;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;
import ir.servicea.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    /* Global */
    private PreferenceUtil preferenceUtil;
    private int province_id, city_id;
    boolean first_rty = true;
    public int typeid = 1;
    private String imagepath1, NameImage = "", user_id = "0", d_id = "0", gender = "";
    private TextView txt_tile_action_bar, license, txt_loc, txt_date_customer;
    private Button btn_save_info;
    private ImageView profile_iv, profile_iv2, img_loc, edit_loc;
    private Spinner spinner_ostan, spinner_city, spin_gender;
    private ViewGroup location;

    private TextInputEditText edt_first_name, edt_last_name, edt_address;

    private ImagePicker imagePicker;
    private List<String> citiesList, listJobs = new ArrayList<>();
    private List<Integer> citiesListIDS, stateListIDS, listJobsIds = new ArrayList<>();
    private List<String> listgen = new ArrayList<>();
    private List<State> stateList;
    private ArrayAdapter spinnerAdapter;
    private Uri selectedImageUri;
    private boolean tryfirst = false;
    private PersianDatePickerDialog mDatePicker;

    private File croppedImageFile;
    private static final int PICK_IMAGE = 1;


    /* End Global */
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_info);
        G.Activity = this;
        G.context = this;
//        startActivity(new Intent(ProfileActivity.this, ServiceCenterActivity.class));
        preferenceUtil = new PreferenceUtil(this);
        FindView();
        onClick();
        StateOpenHelper openHelper = new StateOpenHelper(this);
        openHelper.openDatabase();
        SQLiteDatabase mDatabase = openHelper.getReadableDatabase();
        if (G.preference.getString("resultUri", "").length() > 3) {
            Uri resultUri = Uri.parse(G.preference.getString("resultUri", ""));
            Picasso.get().load(resultUri).error(R.drawable.ic_user).placeholder(R.drawable.ic_user).transform(new CircleTransform()).into(profile_iv);
        }
        if (G.preference.getString("resultUri2", "").length() > 3) {
            Uri resultUri2 = Uri.parse(G.preference.getString("resultUri2", ""));
            Picasso.get().load(resultUri2).error(R.drawable.backpro).placeholder(R.drawable.backpro).into(profile_iv2);
        }


        if (preferenceUtil != null || PreferenceUtil.getD_id() != null || PreferenceUtil.getD_id().length() <= 0) {
            edt_address.setText("");
            edt_first_name.setText("");
            edt_last_name.setText("");


        } else {
            edt_address.setText((PreferenceUtil.getAddress() + "").replace("null",""));

            edt_first_name.setText(preferenceUtil.getName());
            edt_last_name.setText(preferenceUtil.getFamily());


        }
        getProfile();

//        if (G.preference.getBoolean("ActivateRad", false)) {
//
//        } else {
//
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        G.preference.edit().putBoolean("FromListAddress",false).apply();
        G.Activity = this;
        G.context = this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getAddress("");
            }
        }, 500);

    }

    private void FindView() {
        txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        btn_save_info = findViewById(R.id.btn_save_info);
        license = findViewById(R.id.license);
        spinner_ostan = findViewById(R.id.spinner_ostan);
        spinner_city = findViewById(R.id.spinner_city);

        edt_first_name = findViewById(R.id.edt_first_name);
        edt_last_name = findViewById(R.id.edt_last_name);

        edt_address = findViewById(R.id.edt_address);
        txt_date_customer = findViewById(R.id.txt_date_customer);

        profile_iv = findViewById(R.id.profile_iv);
        profile_iv2 = findViewById(R.id.profile_iv2);
        spin_gender = findViewById(R.id.spin_gender);
        location = findViewById(R.id.location);
        img_loc = findViewById(R.id.img_loc);
        txt_loc = findViewById(R.id.txt_loc);
        edit_loc = findViewById(R.id.edit_loc);
        btn_save_info.setTypeface(G.Bold);
        license.setTypeface(G.Bold);
        txt_tile_action_bar.setText("ثبت اطلاعات فردی");
        txt_tile_action_bar.setTypeface(G.Bold);

        listgen.add("جنسیت");
        listgen.add("مرد");
        listgen.add("زن");

        SpinnerAdapter spinnerAdapter = new ArrayAdapter(ProfileActivity.this, R.layout.item_spiner, listgen);
        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_gender.setAdapter(spinnerAdapter);


        spin_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = listgen.get(i);
                if (tryfirst) {
                    if (txt_date_customer.getText().toString().contains("تاریخ")) {
                        txt_date_customer.performClick();

                        G.toast("تاریخ تولد را وارد کنید");
                    }
                } else {
                    tryfirst = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void onClick() {
        btn_save_info.setText("ذخیره تغییرات");

        btn_save_info.setOnClickListener(view -> {
            String phone = PreferenceUtil.getPhone();
            String first_name = edt_first_name.getText().toString();
            String last_name = edt_last_name.getText().toString();
            String birthdate = txt_date_customer.getText().toString();
            String sex = gender;
            String address = edt_address.getText().toString();


            boolean checkvalid = true;
            if (first_name.equals("")) {
                edt_first_name.setError(" نام را به درستی وارد کنید");
                checkvalid = false;
            }
            if (last_name.equals("")) {
                edt_last_name.setError("نام خانوادگی را به درستی وارد کنید");
                checkvalid = false;
            }
            if (sex.length() <= 0) {
                G.toast("جنسیت را به درستی وارد کنید.");
                checkvalid = false;
            }
            if (!birthdate.contains("/")) {
                G.toast("تاریخ تولد را به درستی وارد کنید.");
                checkvalid = false;
            }
//            if (address.equals("")) {
//                edt_address.setError("آدرس را به درستی وارد کنید");
//                checkvalid = false;
//            }
            if (checkvalid) {

                if (sex.contains("مرد")) {
                    sex = "M";
                } else {
                    sex = "F";
                }
                sendProfileInfo(first_name + "", last_name + "", phone + "",
                        "" + sex, G.toMiladi(birthdate) + "", province_id, city_id, address);
                PreferenceUtil.cashInfo(first_name, last_name, address, "");
            }

        });


        profile_iv.setOnClickListener(view -> selectTypeImage(1));
        profile_iv2.setOnClickListener(view -> selectTypeImage(2));
        spinner_ostan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getCitiesOfState(stateList.get(position).getCityId());
                String provinces = stateList.get(position).getCityName();
                province_id = stateListIDS.get(position);
                preferenceUtil.cashOstan(position, parent.getSelectedItem().toString());
                if (!first_rty) {
                    closeKeyboard();
                    spinner_city.performClick();
                } else {
                    new Handler().postDelayed(() -> first_rty = false, 1000);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String citys = citiesList.get(position);
                city_id = citiesListIDS.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        edt_first_name.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                edt_last_name.requestFocus();
                return true;
            }
            return false;
        });
        edt_last_name.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                closeKeyboard();
                spin_gender.performClick();
                return true;
            }
            return false;
        });

        edt_address.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                closeKeyboard();

                return true;
            }
            return false;
        });
        txt_date_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeDatePicker();
            }
        });
    }

    public void onclickAlamrs(View v) {
        startActivity(new Intent(ProfileActivity.this, AlarmsActivity.class));
    }

    public void closeKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void getProfile() {
        String user_id = PreferenceUtil.getUser_id();
        if (user_id.length() > 0) {
            G.loading(this);
            Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
            Call<ResponseBody> request = api.getProfile("id,eq," + user_id);
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
                                        Picasso.get().load(G.PreImagesURL + "profiles/" + profile_photo).error(R.drawable.ic_user).placeholder(R.drawable.ic_user).transform(new CircleTransform()).into(profile_iv);
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

                                    if (birthdate.contains("-")) {
                                        txt_date_customer.setText(G.toShamsi(birthdate));
                                    }
                                    edt_address.setText((PreferenceUtil.getAddress() + "").replace("null",""));

                                    if (sex.contains("آقا") || sex.contains("مرد") || sex.contains("M") || sex.contains("m")) {
                                        spin_gender.post(new Runnable() {
                                            public void run() {
                                                spin_gender.setSelection(1);
                                            }
                                        });
                                    } else if (sex.equals("خانم") || sex.equals("زن") || sex.contains("F") || sex.contains("f")) {
                                        spin_gender.post(new Runnable() {
                                            public void run() {
                                                spin_gender.setSelection(2);
                                            }
                                        });
                                    }

                                    edt_first_name.setText(f_name);
                                    edt_last_name.setText(l_name);
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

                    }
                    G.stop_loading();
                    getStatesList();
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    G.stop_loading();
                    G.toast("مشکل در برقراری ارتباط");
                }
            });

        } else {
            getStatesList();
        }


    }

    public void sendProfileInfo(String name, String lastName, String phone, String sex,
                                String b_date,
                                int province, int city, String address) {
        G.loading(ProfileActivity.this);
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        JSONObject object = new JSONObject();
        try {

            String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());
            created_at = G.converToEn(created_at);
            object.put("username", "" + phone);
            object.put("f_name", name);
            object.put("l_name", lastName);
            object.put("mobile", phone);
            object.put("national_code", "");
            object.put("user_type", G.UserType);
            object.put("role_id", G.RoleId);
            object.put("profile_photo", G.preference.getString("profile_photo", ""));
            object.put("header_photo", G.preference.getString("header_photo", ""));
            object.put("password", G.preference.getString("PASSWORD", ""));
            object.put("sex", sex);
            object.put("birthdate", b_date);
            object.put("province_id", province);
            object.put("city_id", city);
            if (user_id.length() > 0) {
                object.put("updated_at", created_at + "");
            } else {
                object.put("created_at", created_at + "");
                object.put("updated_at", created_at + "");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        G.Log(object.toString());
        d_id = PreferenceUtil.getD_id();
        user_id = PreferenceUtil.getUser_id();
        Call<ResponseBody> request = api.newOperator(G.returnBody(object.toString()));
        if (user_id.length() > 0) {
            request = api.editOperator(user_id, G.returnBody(object.toString()));
        }
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                G.Log(call.request().toString());
                String result = G.getResult(response);
                G.Log(result);
                if (result.length() > 0 && result.length() < 10) {
                    PreferenceUtil.cachLogin();
                    PreferenceUtil.cashAddress(address);
                    G.preference.edit().putBoolean("ActivateRad", true).apply();
                    if (user_id.length() <= 0) {
                        user_id = result;
                    }
                    PreferenceUtil.cashUser_id(user_id);
                    JSONObject object = new JSONObject();

                    Intent mainIntent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {

                    G.toast("مشکل در ثبت اطلاعات کاربر");
                }
                G.stop_loading();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.Log(t.getMessage());
                G.stop_loading();
                Toast.makeText(ProfileActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeDatePicker() {
        PersianCalendar initDate = new PersianCalendar();
        initDate.setPersianDate(1370, 1, 1);

        mDatePicker = new PersianDatePickerDialog(ProfileActivity.this)
                .setPositiveButtonString("تأیید")
                .setNegativeButton("بستن")
                .setTodayButton("امروز")
                .setTodayButtonVisible(true)
                .setMinYear(1340)
                .setMaxYear(1410)
                .setInitDate(initDate)
                .setTitleColor(getResources().getColor(R.color.text_dark))
                .setActionTextColor(Color.GRAY)
                //.setPickerBackgroundColor(Color.DKGRAY)
                //.setBackgroundColor(Color.DKGRAY)
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)

                .setListener(new Listener() {
                    @Override
                    public void onDateSelected(PersianCalendar persianCalendar) {

                        txt_date_customer.setText(persianCalendar.getPersianYear() + "/" + persianCalendar.getPersianMonth() + "/" + persianCalendar.getPersianDay());
                        txt_date_customer.setTextColor(getResources().getColor(R.color.text_dark));

                    }

                    @Override
                    public void onDismissed() {
                    }
                });
        mDatePicker.show();
    }

    private void getStatesList() {
        G.loading(ProfileActivity.this);

        stateList = new ArrayList<>();
        stateListIDS = new ArrayList<>();
        List<String> stateName = new ArrayList<>();
        SpinnerAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.item_spiner, stateName);
        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_ostan.setAdapter(spinnerAdapter);


        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getProvince();
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                assert response.body() != null;
                try {
                    String result = response.body().string();
                    JSONObject object = G.StringtoJSONObject(result);
                    JSONArray records = object.getJSONArray("records");
                    int position = 0;

                    if (records.length() > 0) {

                        for (int i = 0; i < records.length(); i++) {
                            State state = new State();
                            JSONObject obj = records.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("name");
                            state.setCityName(name);
                            state.setCityId(id);
                            if ((id + "").equals(G.preference.getString("province_id", "0"))) {
                                position = i;
                            }
                            stateList.add(state);
                            stateListIDS.add(state.getCityId());
                            stateName.add(state.getCityName());
                        }
                        ((ArrayAdapter<?>) spinnerAdapter).notifyDataSetChanged();
                        spinner_ostan.setSelection(position);
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

    private void getCitiesOfState(int stateId) {
        if (stateId > 0) {

            citiesList = new ArrayList<>();
            citiesListIDS = new ArrayList<>();


            SpinnerAdapter spinnerAdapter = new ArrayAdapter(ProfileActivity.this, R.layout.item_spiner, citiesList);
            ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_city.setAdapter(spinnerAdapter);

            Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
            Call<ResponseBody> request = api.getCity("province_id,eq," + stateId);
            request.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    assert response.body() != null;
                    try {
                        String result = response.body().string();
                        JSONObject object = G.StringtoJSONObject(result);
                        JSONArray records = object.getJSONArray("records");
                        int position = 0;

                        if (records.length() > 0) {

                            for (int i = 0; i < records.length(); i++) {
                                State state = new State();
                                JSONObject obj = records.getJSONObject(i);
                                int id = obj.getInt("id");
                                String name = obj.getString("name");
                                state.setCityName(name);
                                state.setCityId(id);
                                if ((id + "").equals(G.preference.getString("city_id", "0"))) {
                                    position = i;
                                }
                                citiesList.add(state.getCityName());
                                citiesListIDS.add(state.getCityId());

                            }
                            ((ArrayAdapter<?>) spinnerAdapter).notifyDataSetChanged();
                            spinner_city.setSelection(position);
                            G.Log(G.preference.getString("city_id", "0"));
                            G.Log(position + "");
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

    }

    /* Start Select Image */
    public void selectTypeImage(int type) {
        typeid = type;
        G.preference.edit().putInt("imagetype", type).apply();
        if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            selImage();
            openGallery();
        } else {
            checkRunTimePermission();
        }
    }

    private void checkRunTimePermission() {
        String[] permissionArrays = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 11111);
        }

    }

    @SuppressLint("SimpleDateFormat")
    private String getFilename() {
        File file = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if (!file.exists()) {
            file.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        Random r = new Random();
        int i1 = r.nextInt(9999999 - 1000000 + 1) + 1000000;
        currentDateandTime = currentDateandTime + "_" + i1;
        String uriSting = (file.getAbsolutePath() + "/" + currentDateandTime + ".jpg");
        currentDateandTime = G.converToEn(currentDateandTime);
        NameImage = currentDateandTime;
        if (G.preference.getInt("imagetype", 1) == 1) {
            G.preference.edit().putString("profile_photo", NameImage + ".jpg").apply();
        } else if (G.preference.getInt("imagetype", 1) == 2) {
            G.preference.edit().putString("header_photo", NameImage + ".jpg").apply();
        }
        uriSting = G.converToEn(uriSting);
        uriSting = G.NumToEn(uriSting);
        return uriSting;
    }

/*
    public void selImage() {
        imagePicker = new ImagePicker(this,
                null,
                imageUri -> {
                    UCrop.Options options = new UCrop.Options();
                    options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                    options.setCompressionQuality(75);
                    options.setFreeStyleCropEnabled(true);
                    options.setToolbarColor(ContextCompat.getColor(getApplication(), R.color.white));
                    options.setStatusBarColor(ContextCompat.getColor(getApplication(), R.color.white));
                    options.setToolbarTitle("ویرایش تصویر");
                    Uri destinationUri = Uri.fromFile(new File(getFilename()));
                    imagepath1 = destinationUri.getPath();
                    UCrop.of(imageUri, destinationUri)
                            .withOptions(options)
                            .withMaxResultSize(750, 750)
                            .start(ProfileActivity.this);
                });
        imagePicker.choosePicture(false);
    }
*/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectTypeImage(typeid);
        }
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }
    public void selImage() {
        ImagePicker.with(this)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                goUcrop(uri);
            }
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                croppedImageFile = new File(resultUri.getPath());

                // Handle different image types
                if (G.preference.getInt("imagetype", 1) == 1) {
                    G.preference.edit().putString("resultUri", resultUri.toString()).apply();
                    Picasso.get().load(resultUri).error(R.drawable.guy).placeholder(R.drawable.guy).transform(new CircleTransform()).into(profile_iv);
                    G.preference.edit().putString("upload_pushe", "profiles").apply();
                } else if (G.preference.getInt("imagetype", 1) == 2) {
                    G.preference.edit().putString("resultUri2", resultUri.toString()).apply();
                    Picasso.get().load(resultUri).into(profile_iv2);
                    G.preference.edit().putString("upload_pushe", "headers").apply();
                }

                new UploadTask().execute(croppedImageFile.getPath());
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "خطایی رخ داده است", Toast.LENGTH_SHORT).show();
            final Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                G.Log("ProfileActivity"+"Crop error: " + cropError.getMessage()+cropError);
            }
        }
    }

    private void goUcrop(Uri uri) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(75);
        options.setFreeStyleCropEnabled(true);
        options.setToolbarColor(ContextCompat.getColor(getApplication(), R.color.button));
        options.setStatusBarColor(ContextCompat.getColor(getApplication(), R.color.button));
        options.setRootViewBackgroundColor(ContextCompat.getColor(getApplication(), R.color.white));
        options.setActiveControlsWidgetColor(Color.parseColor("#2489ed"));
        options.setToolbarWidgetColor(Color.parseColor("#ffffff"));
        options.setCropFrameColor(Color.parseColor("#2489ed"));
        options.setToolbarTitle("ویرایش تصویر");

        Uri destinationUri = Uri.fromFile(new File(getFilename()));
        UCrop.of(uri, destinationUri)
                .withOptions(options)
                .withMaxResultSize(750, 750)
                .start(ProfileActivity.this);
    }


    public static List<Address> addresses = new ArrayList<>();
    public static AdapterAddress adapterAddress;
    private Handler handler;
    private EditText search;
    private RecyclerView recycle_produce_group;

    public void getAddress(String key) {
       TextView tozih = findViewById(R.id.tozih);
       tozih.setTypeface(G.ExtraBold);
       search = findViewById(R.id.search);
       search.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(ProfileActivity.this,ListAddressesActivity.class));
               overridePendingTransition(0, 0);
           }
       });
        addresses.clear();

        if (true) {

            Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
            Call<ResponseBody> request = api.getAddresses("user_id,eq,"+ PreferenceUtil.getUser_id(),"address,cs," + key);
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
                           /* JSONObject object = G.StringtoJSONObject(result);
                            JSONArray records = object.getJSONArray("records");*/
                            JSONArray records = G.StringtoJSONArray(result);
                            int SelectedAddressID = G.preference.getInt("SelectedAddressID", 0);
                            if (records.length() > 0) {
                                int max = records.length();
                                /*if(max >1){
                                    max = 1;
                                }*/
                                for (int i = 0; i < max; i++) {
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
                                    if(id == SelectedAddressID){
                                        address.setStatus(1 + "");
                                        selectedPosition = id;
                                    }
                                    String created_at = obj.getString("created_at");
                                    address.setCreated_at(created_at + "");
                                    String updated_at = obj.getString("updated_at");
                                    address.setUpdated_at(updated_at + "");
                                    String deleted_at = obj.getString("deleted_at");
                                    address.setDeleted_at(deleted_at + "");

                                    addresses.add(address);


                                }
                                if(selectedPosition == -1) {
                                    G.preference.edit().putInt("SelectedAddressID", addresses.get(0).getId()).apply();
                                    G.preference.edit().putString("SelectedAddressTitle", addresses.get(0).getTitle()).apply();
                                    G.preference.edit().putString("SelectedAddressLat", addresses.get(0).getLatitude()).apply();
                                    G.preference.edit().putString("SelectedAddressLng", addresses.get(0).getLongitude()).apply();
                                }
                            }

                        } catch (JSONException | IOException e) {
                            G.toast("مشکل در تجزیه اطلاعات");
                            e.printStackTrace();
                        }

                    }
                    G.stop_loading();

                    recycle_produce_group = findViewById(R.id.recycle_produce_group);
                    recycle_produce_group.setLayoutManager(new LinearLayoutManager(ProfileActivity.this, RecyclerView.VERTICAL, false));
                    adapterAddress = new AdapterAddress(ProfileActivity.this, addresses);
                    recycle_produce_group.setAdapter(adapterAddress);

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