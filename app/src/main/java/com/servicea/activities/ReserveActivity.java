package com.servicea.activities;

import static com.servicea.app.G.context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.servicea.activity.AddServicesActivity;
import com.servicea.activity.ListCarActivity;
import com.servicea.adapter.AdapterCalendar;
import com.servicea.adapter.AdapterCalendarDay;
import com.servicea.adapter.AdapterReserveTime;
import com.servicea.app.CalendarTool;
import com.servicea.app.Constants;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.ModelJobCategory;
import com.servicea.model.ModelReserveTime;
import com.servicea.model.dbModel.ReserveModel;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import ir.servicea.R;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class ReserveActivity extends AppCompatActivity {

    ShimmerFrameLayout reserve_shimmer_layout;
    RecyclerView rv_calendar, rv_calendar_day, rv_reserve_times;
    AdapterCalendar adapterCalendar;
    AdapterCalendarDay adapterCalendarDay;
    AdapterReserveTime adapterReserveTime;

    List<String> reservedTime = new ArrayList<>();
    private static final String TAG = "ReserveActivity";
    private Boolean update_reserve_time = false;
    private String reserved_service_id;
    private String f_open, f_close, l_open, l_close;
    PersianDate pd;

    private String selectedDate = "";
    private String persianSelectedDate = "";
    private int job_category_id;
    private int service_center_id;

    private TextView txt_title_choose_category, txt_full_reserve_time_message;
    private ImageView iv_calendar_bg;
    private Button btn_cancel;

    int[] da;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);
        getServiceCenterWorkTimeFromIntent();
        FindViews();


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        PersianDate pdate = new PersianDate();
        PersianDateFormat pdformater = new PersianDateFormat();
        pdformater.format(pdate);
        PersianDate pDate = new PersianDate();
        PersianDateFormat pdformater1 = new PersianDateFormat();
        PersianDateFormat pdformater2 = new PersianDateFormat("y F j");
        PersianDateFormat pdformater3 = new PersianDateFormat("y F j", PersianDateFormat.PersianDateNumberCharacter.FARSI); //return Farsi character
        int dayNumbers = pDate.getMonthDays();
        Log.d("Main", "getMonthDays: " + pDate.getMonthDays());

        pDate.dayName();
        List<Integer> daysList = new ArrayList<>();
        for (int i = 1; i <= dayNumbers; i++) {
            daysList.add(i);
        }

        da = pDate.gregorian_to_jalali(pdate.getGrgYear(), pDate.getGrgMonth(), pDate.getGrgDay());
        Log.d("Main", "da: " + da[0] + "/" + da[1] + "/" + da[2]);

        pd = new PersianDate(new Date(da[0], da[1], 1));
        pd.setShYear(da[0]);
        pd.setShMonth(da[1]);
        pd.setShDay(1);
        String dayName = pDate.dayName(pd);
        Log.d("Main", "dayName: " + pd.monthName(1));
        String timeStamp = new SimpleDateFormat("HHmmss").format(pd.getTime());
        Log.d("Main", "getTime: " + timeStamp);


        int dayNumber = pd.dayOfWeek();
    /*    String todayDate = pd.getShYear()+"/"+ pd.getShMonth()+"/"+ pDate.getShDay();
        Log.d("AdapterCalendar", "todayDate::: " + todayDate );
        checkForReserve(pd.getShYear()+"/"+ pd.getShMonth()+"/"+ pDate.getShDay(), pDate.getShDay());
*/
        adapterCalendar = new AdapterCalendar(this, pd.getMonthLength(), dayNumber, pd.getShYear(), pd.getShMonth(), pDate.getShDay(), new AdapterCalendar.OnItemClicked() {
            @Override
            public void onDayClicked(String date, int day) {
                Log.d("AdapterCalendar", "onBindViewHolder:date:: " + date);
                //getServiceCenterTime(date);
                persianSelectedDate = date;
                selectedDate = G.dateConverter(date);
                Toast.makeText(ReserveActivity.this, "لطفا چند لحظه صبر کنید...", Toast.LENGTH_SHORT).show();
                checkForReserve(date, day);

            }
        });


        adapterCalendarDay = new AdapterCalendarDay(dayNumber, new AdapterCalendarDay.OnItemClicked() {
            @Override
            public void onCheckedChanged(ModelJobCategory jobCategory, boolean isChecked) {

            }
        });
        rv_calendar_day.setAdapter(adapterCalendarDay);
        rv_calendar.setAdapter(adapterCalendar);

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rv_calendar.getChildAt(0).performClick();

            }
        },300);*/
    }

    private void FindViews() {
        txt_title_choose_category = findViewById(R.id.txt_title_choose_category);
        txt_full_reserve_time_message = findViewById(R.id.txt_full_reserve_time_message);
        iv_calendar_bg = findViewById(R.id.iv_calendar_bg);
        Picasso.get().load(R.drawable.calendar_bg).transform(new BlurTransformation(this, 20, 1)).into(iv_calendar_bg);
        iv_calendar_bg.setColorFilter(R.color.black);

        txt_title_choose_category.setTypeface(G.Normal);
        txt_full_reserve_time_message.setTypeface(G.Normal);
        rv_calendar = findViewById(R.id.rv_calendar);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setTypeface(G.Bold);
        rv_calendar_day = findViewById(R.id.rv_calendar_day);
        rv_reserve_times = findViewById(R.id.rv_reserve_times);
        rv_calendar_day.setLayoutManager(new GridLayoutManager(this, 7));
        rv_calendar.setLayoutManager(new GridLayoutManager(this, 7));
        rv_reserve_times.setLayoutManager(new GridLayoutManager(this, 4));
        rv_calendar.setHasFixedSize(true);
        rv_calendar_day.setHasFixedSize(true);
        reserve_shimmer_layout = findViewById(R.id.reserve_shimmer_layout);
    }

    private void getServiceCenterWorkTimeFromIntent() {
        /*Intent intent = getIntent();
        f_open = intent.getStringExtra(Constants.f_open);
        f_close = intent.getStringExtra(Constants.f_close);
        l_open = intent.getStringExtra(Constants.l_open);
        l_close = intent.getStringExtra(Constants.l_close);
        job_category_id = intent.getIntExtra(Constants.job_category_id, 1);
        service_center_id = intent.getIntExtra(Constants.service_center_id, 1);*/
        Intent intent = getIntent();
        update_reserve_time = intent.getBooleanExtra(Constants.UPDATE_RESERVE_TIME, false);
        reserved_service_id = intent.getStringExtra(Constants.RESERVED_SERVICE_ID);
        ReserveModel reserveModel = G.getReserveModel();
        f_open = reserveModel.getF_open();
        f_close = reserveModel.getF_close();
        l_open = reserveModel.getL_open();
        l_close = reserveModel.getL_close();
        job_category_id = reserveModel.getJob_category_id();
        service_center_id = reserveModel.getService_center_id();
        G.saveReserveModel(reserveModel);

        Log.d(TAG, "getServiceCenterWorkTimeFromIntent: " + f_open + "*" + f_close + "*" + l_open + "*" + l_close + "*" + job_category_id);
        String firstTime = f_open;

        List<String> times = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        String firstOpenTime = f_open;
        String firstCloseTime = f_close;
        String secondOpenTime = l_open;
        String secondCloseTime = l_close;

    }

    private Long getDateMillis(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date mDate = null;
        try {
            mDate = sdf.parse(time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return mDate.getTime();
    }

/*
    private List<ModelReserveTime> getTimes(int day) {
        long fOpenMillis = getDateMillis(f_open);
        long fCloseMillis = getDateMillis(f_close);
        //System.out.println("Date in milli :: " + fOpenMillis);
        String newDate = f_open;
        String endTime = "";
        long newDataMillis = fOpenMillis;
        long endDateMillis = 0;
        List<ModelReserveTime> times = new ArrayList<>();

        String fOpen = f_open;
        String fClose = f_close;
        Log.d(TAG, "getTimes():: " + newDate + "//" + fClose);
        Log.d(TAG, "getTimes()compareTo:: " + newDataMillis);
        Log.d(TAG, "getTimes()compareTo:: " + fCloseMillis);
        Log.d(TAG, "getTimes()f_open:: " + f_open);
        Log.d(TAG, "getTimes()f_close:: " + f_close);

        String currentTime = millisToDate(Calendar.getInstance().getTime().getTime());
        long currentTimeMillis = convertDateToMillis(currentTime);

        Log.d(TAG, "getTimes()setStartTime:: " + newDataMillis + "##" + currentTime);
        Log.d(TAG, "getTimes()setStartTime:: " + millisToDate(newDataMillis) + "##" + currentTime);

        int c = 0;
        boolean today = (da[2] == day);
        Log.d(TAG, "today:: " + da[2] + "##" + day);

        long reserveMillis = 3600000;
        ModelReserveTime reserveTime = new ModelReserveTime();
        reserveTime.setStartTime(millisToDate(newDataMillis));
        reserveTime.setEndTime(millisToDate(newDataMillis + reserveMillis));
        reserveTime.setMonthName(pd.getMonthName());
        reserveTime.setDay(String.valueOf(day));
        reserveTime.setYear(pd.getShYear() + "");
        reserveTime.setReserveDuration(String.valueOf(reserveMillis));
        Log.d(TAG, "getTimes()setStartTime:: " + newDataMillis + "##" + currentTime);
        Log.d(TAG, "getTimes()setStartTime:: " + millisToDate(newDataMillis) + "##" + currentTime);
        if (today) {
            if (times.size() == 0) {
                if ((newDataMillis > currentTimeMillis) && Math.abs(newDataMillis - currentTimeMillis) >= 1800000) {
                    times.add(reserveTime);
                    // Log.d(TAG, "currentTimeMillis +3600000: " + true);
                }
            } else {
                times.add(reserveTime);
            }
        } else {
            times.add(reserveTime);

        }

        while (newDataMillis < fCloseMillis) {
            if (fCloseMillis - newDataMillis < 3600000) {
                reserveMillis = fCloseMillis - newDataMillis;
                Log.d(TAG, "getTimes()reserveMillis::fCloseMillis " + millisToDate(fCloseMillis));
                Log.d(TAG, "getTimes()reserveMillis::newDataMillis  " + millisToDate(newDataMillis));
                Log.d(TAG, "getTimes()reserveMillis:: " + reserveMillis);
                Log.d(TAG, "getTimes()newDataMillis:: " + newDataMillis);

            } else {
                reserveMillis = 3600000;
            }
            newDataMillis += reserveMillis;
            newDate = millisToDate(newDataMillis);
            Log.d(TAG, "getTimes()newDate:: " + newDate);

            if (newDataMillis < fCloseMillis) {
                endDateMillis = newDataMillis + reserveMillis;

                endTime = millisToDate(endDateMillis);
                Log.d(TAG, "getTimes()setStartTime16:: " + millisToDate(newDataMillis) + "##" + endTime + "##" + reserveMillis);

                reserveTime = new ModelReserveTime();
                reserveTime.setStartTime(newDate);
                reserveTime.setEndTime(endTime);
                reserveTime.setMonthName(pd.getMonthName());
                reserveTime.setDay(String.valueOf(day));
                reserveTime.setYear(pd.getShYear() + "");
                reserveTime.setReserveDuration(String.valueOf(reserveMillis));

                Log.d(TAG, "getTimes()setStartTime:: " + newDataMillis + "##" + currentTime);
                Log.d(TAG, "getTimes()setStartTime:: " + millisToDate(newDataMillis) + "##" + currentTime);

                if (today) {
                    Log.d(TAG, "newDataMillis-currentTimeMillis: " + (newDataMillis - currentTimeMillis));
                   */
/* if(newDataMillis-currentTimeMillis >= 7200000){
                        times.add(reserveTime);
                    }*//*

                    if (times.size() == 0) {
                        if ((newDataMillis > currentTimeMillis) && Math.abs(newDataMillis - currentTimeMillis) >= 1800000) {
                            times.add(reserveTime);
                            // Log.d(TAG, "currentTimeMillis +3600000: " + true);
                        }
                    } else {
                        times.add(reserveTime);
                    }
                } else {
                    times.add(reserveTime);

                }
            }
            if (newDataMillis >= fCloseMillis) {
                if (fClose.equals(l_close)) break;
                reserveMillis = 3600000;
                newDataMillis = getDateMillis(l_open);
                fCloseMillis = getDateMillis(l_close);
                reserveTime = new ModelReserveTime();
                reserveTime.setStartTime(millisToDate(newDataMillis));
                reserveTime.setEndTime(millisToDate(newDataMillis + reserveMillis));
                reserveTime.setMonthName(pd.getMonthName());
                reserveTime.setDay(String.valueOf(day));
                reserveTime.setYear(pd.getShYear() + "");
                Log.d(TAG, "getTimes()setStartTime:: " + newDataMillis + "##" + currentTime);
                Log.d(TAG, "getTimes()setStartTime:: " + millisToDate(newDataMillis) + "##" + currentTime);
                Log.d(TAG, "today: " + today);

                if (today) {
                    if (times.size() == 0) {
                        if ((newDataMillis > currentTimeMillis) && Math.abs(newDataMillis - currentTimeMillis) >= 1800000) {
                            times.add(reserveTime);
                            // Log.d(TAG, "currentTimeMillis +3600000: " + true);
                        }
                    } else {
                        times.add(reserveTime);
                    }
                } else {
                    times.add(reserveTime);
                }
                reserveMillis = 3600000;
                newDate = l_open;
                fClose = l_close;
            }
            reserveMillis = 3600000;

        }
        return times;
    }
*/

    private List<ModelReserveTime> getTimes(int day) {
        long fOpenMillis = getDateMillis(f_open);
        long fCloseMillis = getDateMillis(f_close);
        long newDataMillis = fOpenMillis;
        long currentTimeMillis = convertDateToMillis(millisToDate(Calendar.getInstance().getTime().getTime()));
        boolean today = (da[2] == day);
        List<ModelReserveTime> times = new ArrayList<>();

        while (newDataMillis < fCloseMillis) {
            long reserveMillis = (fCloseMillis - newDataMillis < 3600000) ? fCloseMillis - newDataMillis : 3600000;
            String newDate = millisToDate(newDataMillis);

            long endDateMillis = newDataMillis + reserveMillis;
            String endTime = millisToDate(endDateMillis);

            ModelReserveTime reserveTime = new ModelReserveTime();
            reserveTime.setStartTime(newDate);
            reserveTime.setEndTime(endTime);
            reserveTime.setMonthName(pd.getMonthName());
            reserveTime.setDay(String.valueOf(day));
            reserveTime.setYear(pd.getShYear() + "");
            reserveTime.setReserveDuration(String.valueOf(reserveMillis));

            if (today) {
                if ((newDataMillis > currentTimeMillis) && Math.abs(newDataMillis - currentTimeMillis) >= 1800000) {
                    times.add(reserveTime);
                }
            } else {
                times.add(reserveTime);
            }
            newDataMillis += reserveMillis;

            if (newDataMillis >= fCloseMillis) {
                if (f_close.equals(l_close)) break;
                newDataMillis = getDateMillis(l_open);
                fCloseMillis = getDateMillis(l_close);
                f_close = l_close;
            }
        }
        return times;
    }


    private String millisToDate(Long millis) {
        String DATE_FORMAT = "HH:mm:ss";
        return new SimpleDateFormat(DATE_FORMAT, Locale.US).format(new Date(millis));
    }

    private void getServiceCenterTime(String dateTime) {
        String f_open = "08:00:00";
        String f_close = "13:45:00";

        String l_open = "14:00:00";
        String l_close = "20:00:00";

        String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());

        dateTime = dateTime.replace("/", "-");
        if (dateTime.contains("-")) {
            CalendarTool calendarTool = new CalendarTool();
            String[] dates = dateTime.split("-");
            calendarTool.setIranianDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
            dateTime = calendarTool.getGregorianDate();
        }
        String time = created_at.split(" ")[1];
        String reserveTime = "";
        if (time.compareTo(f_open) > 0) {
            reserveTime = dateTime + " " + f_open;
        }
        Log.d("ReserveActivity", "getServiceCenterTime: " + reserveTime);

    }


    public static int getCurrentMonthDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public void checkForReserve(String reserveTime, int day) {
        startShimmer();
        reservedTime = new ArrayList<>();
        //G.loading(rv_calendar.getContext());
        String d_id = PreferenceUtil.getD_id();
        String detail_serviceS = G.preference.getString("detail_service", "[]");
        G.Log("ReserveActivity::" + detail_serviceS);
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        try {
            String created_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());

            JSONObject objx = new JSONObject();
            objx.put("service_center_id", service_center_id);
            objx.put("reserve_time", G.dateConverter(reserveTime));
            G.Log("ReserveActivity:object:" + objx);


            Call<ResponseBody> request = api.checkForReserve(G.returnBody(objx.toString()));
            G.Log("ReserveActivity: request" + request.request());
            request.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    stopShimmer();
                    G.Log(call.request().toString());
                    String result = G.getResult(response);
                    G.Log("ReserveActivity:result:" + result);
                    Log.d("ReserveActivity", "result: " + result);
                    boolean isReserved = false;
                    String[] reserveTime = new String[2];
                    if (result.length() > 0) {
                        try {
                            JSONArray reserveInf = new JSONArray(result);
                            for (int i = 0; i < reserveInf.length(); i++) {
                                JSONObject reserve = reserveInf.getJSONObject(i);
                                if (reserve.has("reserve_date_time")) {
                                    reservedTime.add(reserve.getString("reserve_date_time").split(" ")[1]);
                                   /* reserveTime = reserve.getString("reserve_date_time").split(" ")[1].split(":");
                                    if(reserveTime[0].equals("08") && reserveTime[1].equals("30")){
                                        Log.d("ReserveActivity", "onResponse:reserve_date_time:time is reserved " );

                                    }*/
                                    Log.d("ReserveActivity", "onResponse: " + isReserved);
                                }
                            }
                            setReserveTimesRecycler(day);

                        } catch (JSONException e) {
                            G.stop_loading();
                            Log.d("ReserveActivity", "JSONException: " + e.getMessage());
                            throw new RuntimeException(e);
                        }

                    } else {
                        G.stop_loading();
                        // finish();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    G.stop_loading();
                    G.toast("مشکل در برقراری ارتباط با سرور");
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startShimmer() {
        rv_reserve_times.setVisibility(View.GONE);
        reserve_shimmer_layout.setVisibility(View.VISIBLE);
        reserve_shimmer_layout.startShimmer();
    }

    private void stopShimmer() {
        reserve_shimmer_layout.stopShimmer();
        reserve_shimmer_layout.setVisibility(View.GONE);
        rv_reserve_times.setVisibility(View.VISIBLE);
    }

    private void setReserveTimesRecycler(int day) {
        G.stop_loading();
        List<ModelReserveTime> times = getTimes(day);

        String reserveTimeString = "";
        ModelReserveTime reserveItem = new ModelReserveTime();
        for (int i = 0; i < times.size(); i++) {
            for (int j = 0; j < reservedTime.size(); j++) {
                String reserveTime = reservedTime.get(j);
                reserveTimeString = reserveTime.substring(0, 8);
                Log.d("ReserveActivity", "reserveTimeString/" + reserveTimeString);
                Log.d("ReserveActivity", "getStartTime/" + times.get(i).getStartTime());
                reserveItem = times.get(i);
                if (reserveTimeString.equals(reserveItem.getStartTime())) {
                    reserveItem.setIs_reserved(true);
                    times.set(i, reserveItem);
                    Log.d("ReserveActivity", "onResponse:getStartTime:time is reserved ");
                    break;
                } else{
                    reserveItem.setIs_reserved(false);
                }
            }
        }

        adapterReserveTime = new AdapterReserveTime(times, new AdapterReserveTime.OnItemClicked() {
            @Override
            public void onDayClicked(ModelReserveTime modelReserveTime) {
                String selectedReserveTime = selectedDate + " " + modelReserveTime.getStartTime() + " " + modelReserveTime.getEndTime();
                Log.d(TAG, "onDayClicked():: " + selectedReserveTime);
                ReserveModel modelReserve = G.getReserveModel();
                modelReserve.setStart_time(modelReserveTime.getStartTime());
                Log.d("addService", "onDayClicked: " + modelReserve.getStart_time());
                modelReserve.setEnd_time(modelReserveTime.getEndTime());
                modelReserve.setDate(modelReserveTime.getDay() + " " + modelReserveTime.getMonthName());
                modelReserve.setSelectedDate(selectedReserveTime);
                modelReserve.setReserveDuration(modelReserveTime.getReserveDuration());
                G.saveReserveModel(modelReserve);
                if (update_reserve_time) {
                    updateReserveTime(selectedReserveTime);
                } else {
                    Intent intentThatStartsReserveProductGroupActivity = new Intent(ReserveActivity.this, ListCarActivity.class);
                    intentThatStartsReserveProductGroupActivity.putExtra(Constants.selected_reserve_time, selectedReserveTime);
                    intentThatStartsReserveProductGroupActivity.putExtra(Constants.job_category_id, job_category_id);
                    intentThatStartsReserveProductGroupActivity.putExtra(Constants.service_center_id, service_center_id);
                    intentThatStartsReserveProductGroupActivity.putExtra(Constants.customer_car_id, 1);
                    startActivity(intentThatStartsReserveProductGroupActivity);
                }
            }
        });
        rv_reserve_times.setAdapter(adapterReserveTime);
        Log.d(TAG, "getTimes():: " + times);
    }

    public void updateReserveTime(String reserve_date_time) {

        G.loading(this);
        String updated_at = G.converToEn(DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString());

        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        JSONObject object = new JSONObject();
        try {
            object.put("reserve_date_time", reserve_date_time);
            object.put("updated_at", updated_at);
            object.put("status", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<ResponseBody> request = api.updateReserve(reserved_service_id, G.returnBody(object.toString()));
        Log.d("GuidCar_ArenaTeam_", "addServiceRequest: " + request.request());
        Log.d("GuidCar_ArenaTeam_", "addServiceRequest: object" + object);
        Log.d("GuidCar_ArenaTeam_", "addServiceRequest: object2" + G.returnBody(object.toString()));

        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d("GuidCar_ArenaTeam_", "addService: response" + response);
                String result = G.getResult(response);
                Log.d("GuidCar_ArenaTeam_", "addService: result" + result);

                if (result.length() > 0 && result.length() < 10) {
                    G.toast("تغییر زمان رزرو با موفقیت انجام شد");
                    Intent intent = new Intent(ReserveActivity.this, ListReserveActivity.class);
                    showSnackBar(btn_cancel, intent, reserve_date_time);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                        }
                    },3000);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    private void showSnackBar(View parentLayout, Intent intent, String date) {
        String persianDate = persianSelectedDate;
        //String persianDate = persianSelectedDate + date.substring(date.indexOf(' '));
        Snackbar.make(parentLayout, "زمان رزرو با موفقیت به "+ persianDate + " تغییر یافت", Snackbar.LENGTH_LONG)
                .setAction("متوجه شدم", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(intent);
                        finish();
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();
    }

    private long convertDateToMillis(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date.getTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reserve_shimmer_layout.startShimmer();
    }


    ////////////////////////////////////////////////////

}