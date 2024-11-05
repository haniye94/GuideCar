package com.servicea.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servicea.app.G;
import com.servicea.model.Model;
import com.servicea.model.ModelReserveTime;

import java.util.List;
import java.util.Map;

import ir.servicea.R;

public class AdapterReserveTime extends RecyclerView.Adapter<AdapterReserveTime.ViewHolder> {

    private OnItemClicked onItemClicked;
    private List<ModelReserveTime> reserveTimeList;
    private boolean start = false;


    public AdapterReserveTime( List<ModelReserveTime> reserveTimeList, OnItemClicked onItemClicked) {
        this.reserveTimeList = reserveTimeList;
        this.onItemClicked = onItemClicked;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserve_time, parent, false));
    }

    @Override
    public int getItemCount() {
        return reserveTimeList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(reserveTimeList.get(position));
        Log.d("AdapterCalendar", "onBindViewHolder: start::" + start);
    }

    public interface OnItemClicked {
        void onDayClicked(ModelReserveTime modelReserveTime);
    }

    // Our view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_reserveDay, tv_reserve_month, tv_reserve_time;
        private Button btn_reserve;
        private ViewGroup ly_root;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_reserveDay = (TextView) itemView.findViewById(R.id.txt_reserve_day);
            tv_reserve_month = (TextView) itemView.findViewById(R.id.txt_reserve_month);
            tv_reserve_time = (TextView) itemView.findViewById(R.id.txt_reserve_time);
            ly_root = (LinearLayout) itemView.findViewById(R.id.root);
            btn_reserve = (Button) itemView.findViewById(R.id.btn_reserve);

        }

        void bind(ModelReserveTime item) {
            tv_reserveDay.setText(item.getDay());
            tv_reserveDay.setTypeface(G.Bold);

            tv_reserve_month.setText(item.getMonthName() + ""+ item.getYear());
            tv_reserve_month.setTypeface(G.Normal);

            tv_reserve_time.setText(formatDateForDisplay(item.getStartTime())+"/" + formatDateForDisplay(item.getEndTime()));
            //tv_reserve_time.setText(String.format("%s/%s", formatDateForDisplay(item.getStartTime()), formatDateForDisplay(item.getEndTime())));
            tv_reserve_time.setTypeface(G.Bold);
            if (item.isIs_reserved()){
                // tv_reserveDay.setText("reserved");
                ly_root.setBackgroundResource(R.drawable.item_full_reserve_time);
                btn_reserve.setText(R.string.reserved);
            }else {
                ly_root.setBackgroundResource(R.drawable.item_reserve_time);
                btn_reserve.setText(R.string.reserve_service);
            }
            btn_reserve.setTypeface(G.Normal);
            btn_reserve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!item.isIs_reserved()){
                        // tv_reserveDay.setText("reserved");
                        onItemClicked.onDayClicked(item);
                    }
                }
            });
        }
    }

    private String formatDateForDisplay(String time){
        return time.substring(0,time.lastIndexOf(':'));
    }
}
