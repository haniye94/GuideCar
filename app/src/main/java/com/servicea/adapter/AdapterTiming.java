package com.servicea.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servicea.activities.ServiceCenterActivity;
import com.servicea.app.CircleTransform;
import com.servicea.app.G;
import com.servicea.model.ServiceCenter;
import com.servicea.model.ServiceTiming;
import com.squareup.picasso.Picasso;

import java.util.List;

import ir.servicea.R;

public class AdapterTiming extends RecyclerView.Adapter<AdapterTiming.ViewHolder> {
    Context context;
    LayoutInflater layoutInflater;
    List<ServiceTiming> models;
    public AdapterTiming(Context context, List<ServiceTiming> models) {
        this.context = context;
        this.models = models;
        layoutInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.adapter_timing, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ServiceTiming ST = models.get(position);
        String type = "بازدید";
        if(ST.isChanged()){
            type = "تعویض";
        }
        holder.txt_product_name.setText(type+" "+ST.getProduct_group_name());
        holder.txt_car_name.setText("خودروی "+ST.getCar_name());
        holder.txt_date.setText(G.toShamsi(ST.getService_date_time()+""));
        holder.txt_center_name.setText("نام مرکز: "+ST.getCenter_name());
        holder.txt_km_next.setText("کیلومتر سر رسید: "+G.getDecimalFormattedString(ST.getKm_next()+""));

//        holder.left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_product_name, txt_car_name, txt_date, txt_center_name,txt_km_next;
       // ImageView left;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_car_name = itemView.findViewById(R.id.txt_car_name);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_center_name = itemView.findViewById(R.id.txt_center_name);
            txt_km_next = itemView.findViewById(R.id.txt_km_next);
          //  left = itemView.findViewById(R.id.left);
        }
    }
}