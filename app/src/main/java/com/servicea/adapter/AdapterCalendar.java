package com.servicea.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servicea.app.G;
import com.servicea.model.ModelJobCategory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ir.servicea.R;
import saman.zamani.persiandate.PersianDate;

public class AdapterCalendar extends RecyclerView.Adapter<AdapterCalendar.ViewHolder> {

    private OnItemClicked onItemClicked;
    private int daysList;
    private int daynum;
    private boolean start = false;
    private int num = 0;
    private int year;
    private int month;
    private int today;
    private PersianDate persianDate;
    private Context context;
    private Boolean today_is_off;

    public AdapterCalendar(Context context, int daysList, int daynum, int year, int month, int today, OnItemClicked onItemClicked) {
        this.context = context;
        this.persianDate = persianDate;
        this.daysList = daysList;
        this.onItemClicked = onItemClicked;
        this.daynum = daynum;
        this.year = year;
        this.month = month;
        this.today = today;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar, parent, false));
    }

    @Override
    public int getItemCount() {
        return daysList+daynum;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == daynum) {
            start = true;
        } else if (position < daynum) {
            num++;
        }
        Log.d("AdapterCalendar", "onBindViewHolder: start::" + start);

        if (start) {
            holder.bind((position - num) + 1);
            Log.d("AdapterCalendar", "onBindViewHolder: position::" + (position));
            Log.d("AdapterCalendar", "onBindViewHolder: num::" + (num));
            Log.d("AdapterCalendar", "onBindViewHolder: position - num::" + (position - num));

        } else {
            holder.bind(-1);
            Log.d("AdapterCalendar", "onBindViewHolder: -1");
        }
    }

    public interface OnItemClicked {
        void onDayClicked(String date, int day);
    }

    // Our view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_calendar_day;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_calendar_day = (TextView) itemView.findViewById(R.id.calendar_day);
            tv_calendar_day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() > num) {
                        int selectedDay = Integer.parseInt(tv_calendar_day.getText().toString());
                        Log.d("AdapterCalendar", "onBindViewHolder: today" + today);
                        Log.d("AdapterCalendar", "onBindViewHolder: selectedDay" + selectedDay);

                        if (today <= selectedDay) {
                            String monthString = String.valueOf(month);
                            String dayString = String.valueOf( tv_calendar_day.getText());
                            if(monthString.length()<2) monthString = "0" + month;
                            if(dayString.length()<2) dayString = "0" +  dayString;
                            String date = year + "/" + monthString + "/" + dayString;
                            Log.d("AdapterCalendar", "onBindViewHolder: date:" + date);
                            onItemClicked.onDayClicked(date, selectedDay);
                        }
                    }
                }
            });
        }

        void bind(int item) {
            if (item == -1) {
                tv_calendar_day.setText("");
            } else {
                if (today > item) {
                    tv_calendar_day.setTextColor(context.getResources().getColor(R.color.gray_button_color));
                } else {
                    tv_calendar_day.setTextColor(context.getResources().getColor(R.color.white));
                }
                tv_calendar_day.setText(item + "");
                tv_calendar_day.setTypeface(G.Bold);
            }
        }
    }


}
