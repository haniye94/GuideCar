package com.servicea.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servicea.app.CalendarTool;
import com.servicea.app.G;
import com.servicea.model.ModelML;

import java.util.List;

import ir.servicea.R;

public class AdapterLogMessage extends RecyclerView.Adapter<AdapterLogMessage.ViewHolder> {
    Context context;
    LayoutInflater layoutInflater;
    List<ModelML> models;


    public AdapterLogMessage(Context context, List<ModelML> models) {
        this.context = context;
        this.models = models;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.adapter_log_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.user.setTypeface(G.Bold);
        holder.user.setText(models.get(position).getUser_fullname());

        holder.user_phone.setTypeface(G.Bold);
        holder.user_phone.setText(models.get(position).getUser_phone());

        holder.char_count.setTypeface(G.Bold);
        holder.char_count.setText(G.getDecimalFormattedString(String.valueOf(models.get(position).getChar_count())));

        holder.price.setTypeface(G.Bold);
        holder.price.setText(G.getDecimalFormattedString(String.valueOf(models.get(position).getTotal_price())));

        holder.content.setTypeface(G.Bold);
        holder.content.setText(models.get(position).getContent());

        String date = models.get(position).getSend_at();
        holder.date.setTypeface(G.Bold);

        holder.date.setTextColor(context.getResources().getColor(R.color.button));
        if (date.contains("-") && date.contains(":") && date.contains(" ")) {
            CalendarTool calendarTool = new CalendarTool();
            String[] date_time = date.split(" ");
            String[] dates = date_time[0].split("-");
            calendarTool.setGregorianDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
            date = calendarTool.getIranianDate() + " " + date_time[1];
            holder.date.setText(date);
        } else if (date.contains("-")) {
            date = date.replace(" ", "");
            CalendarTool calendarTool = new CalendarTool();
            String[] dates = date.split("-");
            calendarTool.setGregorianDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
            date = calendarTool.getIranianDate();
            holder.date.setText(date);
        }

        if (!models.get(position).isExpanded()) {
//            holder.expandableLayout.collapse();
            holder.expandableLayout.setVisibility(View.GONE);
            holder.toggle.setImageResource(R.drawable.arrow_down_24);
            models.get(position).setExpanded(false);
        } else {
//            holder.expandableLayout.expand();
            holder.expandableLayout.setVisibility(View.VISIBLE);

            holder.toggle.setImageResource(R.drawable.arrow_up_24);
            models.get(position).setExpanded(true);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (/*holder.expandableLayout.isExpanded()*/holder.expandableLayout.getVisibility() == View.VISIBLE) {
//                    holder.expandableLayout.collapse();
                    holder.expandableLayout.setVisibility(View.GONE);
                    holder.toggle.setImageResource(R.drawable.arrow_down_24);
                    models.get(position).setExpanded(false);
                } else {
//                    holder.expandableLayout.expand();
                    holder.expandableLayout.setVisibility(View.VISIBLE);
                    holder.toggle.setImageResource(R.drawable.arrow_up_24);
                    models.get(position).setExpanded(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView char_count, user, user_phone, date, price, content;
        RelativeLayout expandableLayout;
        ImageView toggle;
        Button btn_try_send_again;
        ViewGroup root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.user_id);
            user_phone = itemView.findViewById(R.id.user_phone);
            char_count = itemView.findViewById(R.id.char_count);
            date = itemView.findViewById(R.id.create_at);
            price = itemView.findViewById(R.id.total_price);
            content = itemView.findViewById(R.id.content);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
//            expandableLayout.collapse();
            expandableLayout.setVisibility(View.GONE);
            root = itemView.findViewById(R.id.root);
            toggle = itemView.findViewById(R.id.toggle);
        }
    }
}