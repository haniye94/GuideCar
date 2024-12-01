package com.servicea.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.servicea.app.G;
import com.servicea.app.GPSTrack;
import com.servicea.app.PreferenceUtil;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.servicea.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


public class MapxActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap gMap;
//    MapView mapView;
    private MarkerOptions markerOptions;
    Button registerBtn;
    FloatingActionButton myLocationBtn;
    private LatLng samplePoint = new LatLng(34.798510, 48.514853);
    private LatLng location = new LatLng(34.798510, 48.514853);
    private LatLng selectedLocation = new LatLng(34.798510, 48.514853);

    private Intent intent;
    private EditText search;
    private Handler handler;
   private SupportMapFragment mapFragment;
    private boolean permissionGranted = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapx);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        G.Activity = this;
        G.context = this;
        TextView txt_tile_action_bar = findViewById(R.id.txt_tile_action_bar);
        txt_tile_action_bar.setText("ایجاد آدرس");
        txt_tile_action_bar.setTypeface(G.ExtraBold);
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        intent = getIntent();
        registerBtn = findViewById(R.id.register_btn);
        myLocationBtn = findViewById(R.id.myLocationButton);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DecimalFormat df = new DecimalFormat("#.0000000");
                double lat = Double.parseDouble(G.converToEn(df.format(selectedLocation.latitude)));
                double lon = Double.parseDouble(G.converToEn(df.format(selectedLocation.longitude)));
                getMapAddress(lat + "", lon + "");
            }
        });

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
                            getMapSearch(gMap.getCameraPosition().target.latitude + "", gMap.getCameraPosition().target.latitude + "", s.toString() + search.getText().toString());
                        }
                    }, 1000);

                } else if (s.length() == 0) {

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
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            editId = intent.getStringExtra("id");
            String lat = intent.getStringExtra("lat");
            String lng = intent.getStringExtra("lng");

            if (lat.length() > 5 && lat.contains(".") && lng.length() > 5 && lng.contains(".")) {
                txt_tile_action_bar.setText("ویرایش آدرس");
                selectedLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                markerOptions.position(selectedLocation);
//                changelocation.setLatitude(Double.parseDouble(lat));
//                changelocation.setLongitude(Double.parseDouble(lng));

            } else {
                editId = "";
            }
        } else {
            editId = "";
        }
    }

    public String editId = "";

    public void getMapSearch(String lat, String lng, String key) {
        G.loading(this);
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getMapSearch(lat, lng, key);
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                G.Log(call.request().toString());
                if (response.body() != null) {
                    try {
                        String result = response.body().string();
                        G.Log(result);
                        JSONObject object = G.StringtoJSONObject(result);
                        if (object.has("count")) {
                            int count = object.getInt("count");
                            if (count > 0) {
                                JSONArray items = object.getJSONArray("items");
                                JSONObject item = items.getJSONObject(0);
                                JSONObject location = item.getJSONObject("location");
                                double lng = location.getDouble("x");
                                double lat = location.getDouble("y");
                                LatLng loc = new LatLng(lat, lng);
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16f));
                            } else {
                                G.toast("موردی یافت نشد!");
                            }
                        }
                    } catch (JSONException | IOException e) {
                        G.toast("مشکل در تجزیه اطلاعات");
                        e.printStackTrace();
                    }
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

    public void getMapAddress(String lat, String lng) {
        G.loading(this);
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.getMapAddress(lat, lng);
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                G.Log(call.request().toString());
                G.stop_loading();
                if (response.body() != null) {
                    try {
                        String result = response.body().string();
                        G.Log(result);
                        JSONObject object = G.StringtoJSONObject(result);
                        if (object.has("status")) {
                            String status = object.getString("status");
                            if (status.equals("OK")) {
                                String neighbourhood = (object.getString("neighbourhood") + "").replace("null", "");
                                String formatted_address = (object.getString("formatted_address") + "").replace("null", "");
                                if (neighbourhood.length() == 0 && object.has("formatted_address")) {
                                    String[] addressTitle = object.getString("formatted_address").split("،");
                                    neighbourhood = addressTitle[1] + "،" + addressTitle[2];
                                }
                                String city = object.getString("city").trim();
                                addAddress(neighbourhood, formatted_address, lat, lng, city);
                            } else {
                                G.toast("اطلاعات آدرس دریافت نشد!");
                            }
                        }
                    } catch (JSONException | IOException e) {
                        G.toast("مشکل در تجزیه اطلاعات آدرس");
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }

    public void addAddress(String title, String address, String lat, String lng, String city) {
        G.loading(this);
        String user_id = PreferenceUtil.getUser_id();
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);

        JSONObject object = new JSONObject();
        String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());
        try {
            object.put("user_id", user_id);
            object.put("title", title);
            object.put("address", address);
            object.put("latitude", lat);
            object.put("longitude", lng);
            object.put("status", 1);
            object.put("city", city);
            object.put("created_at", created_at);
            object.put("updated_at", created_at);
            object.put("deleted_at", JSONObject.NULL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<ResponseBody> request = api.addAddress(G.returnBody(object.toString()));
        Log.d("Add Location", "request: " + request.request().url());

        if (editId.length() > 0) {
            request = api.editAddress(editId, G.returnBody(object.toString()));
        }
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                G.stop_loading();
                String result = G.getResult(response);
                /*String status = null;
                try {
                    Log.d("Add Location", "result: " + result);
                    Log.d("Add Location", "call: " + call.toString());
                    JSONObject responseObject = new JSONObject(result);
                     status = responseObject.getString("status");
                    Log.d("Add Location", "status: " + status);
                } catch (JSONException e) {
                    Log.d("Add Location", "onResponse: " + e.getMessage());
                    throw new RuntimeException(e);
                }*/
                if (result.length() > 0 && result.length() < 10) {
                    G.toast("با موفقیت ثبت شد");
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    myLocationBtn.performClick();
                } else {
                    G.toast("دسترسی به لوکیشن داده نشده است!");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public boolean GPS() {

        GPSTrack gps = new GPSTrack(MapxActivity.this);
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            if (longitude != 0 && latitude != 0) {
                location = new LatLng(gps.getLatitude(), gps.getLongitude());
//                location.setLatitude(gps.getLatitude());
//                location.setLongitude(gps.getLongitude());
                new Handler().postDelayed(() -> {
                    location = new LatLng(gps.getLatitude(), gps.getLongitude());

//                    location.setLatitude(gps.getLatitude());
//                    location.setLongitude(gps.getLongitude());
                }, 1000);

                gMap.clear();
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f));
//                MarkerOptions markerOptions = new MarkerOptions().position(location);
                 markerOptions.position(location);
                gMap.addMarker(markerOptions);

            } else {
                new Handler().postDelayed(this::GPS, 1000);
            }
        } else {

            G.toast(" لطفا GPS خود را روشن کنید");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }, 1000);

            return false;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapFragment.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        G.context = this;
        G.Activity = this;
        mapFragment.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapFragment.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapFragment.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapFragment.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapFragment.onSaveInstanceState(outState);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
        {

            gMap = googleMap;

            gMap.getUiSettings().setCompassEnabled(false);
            gMap.getUiSettings().setRotateGesturesEnabled(true);
//            gMap.clear();
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f));
            markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));;
            markerOptions.draggable(true);
            markerOptions.position(location);
            gMap.addMarker(markerOptions);

            gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    selectedLocation =latLng;
                    gMap.clear();
                    markerOptions.position(selectedLocation);
                    gMap.addMarker(markerOptions);

                }
            });

            if (editId.length() == 0) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myLocationBtn.performClick();

                    }
                }, 1000);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 16f));

                    }
                }, 700);
            }
            myLocationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int MyVersion = Build.VERSION.SDK_INT;
                    if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (!checkIfAlreadyhavePermission()) {
                            requestForSpecificPermission();
                        } else {
                            GPS();
                        }
                    } else {
                        GPS();
                    }
                }
            });
        }



}


