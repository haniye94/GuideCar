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
import com.squareup.picasso.Picasso;

import java.util.List;

import ir.servicea.R;

public class AdapterServiceCenter extends RecyclerView.Adapter<AdapterServiceCenter.ViewHolder> {
    Context context;
    LayoutInflater layoutInflater;
    List<ServiceCenter> models;
    OnCenterClickListener onCenterClickListener;

    public AdapterServiceCenter(Context context, List<ServiceCenter> models, OnCenterClickListener onCenterClickListener) {
        this.context = context;
        this.models = models;
        layoutInflater = LayoutInflater.from(context);
        this.onCenterClickListener = onCenterClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.adapter_service_center, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ServiceCenter SC = models.get(position);
        holder.title.setText(SC.getTitle());
        holder.desc.setText(SC.getDesc());
        holder.score.setText(SC.getScore());
        holder.percent.setText(SC.getPercent());
        holder.category.setText(SC.getCategory());
        String profile_photo = SC.getProfile();
        holder.img_profile.setImageResource(0);
        Picasso.get().load(G.PreImagesURL + "profiles/" + profile_photo).error(R.drawable.ic_user).placeholder(R.drawable.ic_user).transform(new CircleTransform()).into(holder.img_profile);
        String header_photo = SC.getHeader();
        holder.img_header.setImageResource(0);
        Picasso.get().load(G.PreImagesURL + "headers/" + header_photo).error(R.drawable.backpro).placeholder(R.drawable.backpro).into(holder.img_header);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onCenterClickListener.onCenterClick(SC);
            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, percent, score,category;
        ImageView img_header, img_profile;
        ViewGroup root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            percent = itemView.findViewById(R.id.percent);
            score = itemView.findViewById(R.id.score);
            category = itemView.findViewById(R.id.category);
            img_header = itemView.findViewById(R.id.img_header);
            img_profile = itemView.findViewById(R.id.img_profile);
            root = itemView.findViewById(R.id.root);

        }
    }

   public interface OnCenterClickListener{
        void onCenterClick(ServiceCenter center);
    }
}