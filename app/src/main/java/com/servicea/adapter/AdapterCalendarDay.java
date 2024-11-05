package com.servicea.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servicea.app.G;
import com.servicea.model.ModelJobCategory;

import ir.servicea.R;

public class AdapterCalendarDay extends RecyclerView.Adapter<AdapterCalendarDay.ViewHolder> {

    private OnItemClicked onItemClicked;
    private int startDayNumber;

    public AdapterCalendarDay(int startDayNumber, OnItemClicked onItemClicked){
        this.startDayNumber = startDayNumber;
        this.onItemClicked = onItemClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        int width = parent.getMeasuredWidth() / 7;
        itemView.setMinimumWidth(width);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    public interface OnItemClicked {
        void onCheckedChanged(ModelJobCategory jobCategory, boolean isChecked);
    }

    // Our view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_calendar_day;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_calendar_day = (TextView) itemView.findViewById(R.id.calendar_day);

        /*    ly_job_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (daysList.get(getAdapterPosition()).getStatus() == 1) {
                        if (checkBox.isChecked()) {
                            checkBox.setVisibility(View.GONE);
                            checkBox.setChecked(false);
                        } else {
                            checkBox.setVisibility(View.VISIBLE);
                            checkBox.setChecked(true);
                        }
                    } else {
                        Toast.makeText(itemView.getContext(), "این گروه شغلی در حال حاضر فعال نمی باشد", Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
        }

        void bind(int item) {
            String dayName = "";
            switch (item){
                case 0 : dayName = "شنبه";
                break;

                case 1 : dayName = "یک شنبه";
                    break;

                case 2 : dayName = "دوشنبه";
                    break;

                case 3 : dayName = "سه شنبه";
                    break;

                case 4 : dayName = "چهارشنبه";
                    break;

                case 5 : dayName = "پنجشنبه";
                    break;

                case 6 : dayName = "جمعه";
                    break;
            }

            tv_calendar_day.setText(dayName);
            tv_calendar_day.setTypeface(G.Normal);
        }
    }
}
