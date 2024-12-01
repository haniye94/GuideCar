package com.servicea.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.servicea.app.DataBaseHelper;
import com.servicea.app.G;
import com.servicea.model.ModelJobCategory;

import java.util.List;

import ir.servicea.R;

public class AdapterJobCategory extends RecyclerView.Adapter<AdapterJobCategory.ViewHolder> {
    Context context;
    LayoutInflater layoutInflater;
    List<ModelJobCategory> models;
    AlertDialog alertDialogs_detect_type;
    DataBaseHelper mDBHelper;


    public AdapterJobCategory(Context context, List<ModelJobCategory> models) {
        this.context = context;
        this.models = models;
        layoutInflater = LayoutInflater.from(context);
        mDBHelper = new DataBaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_job_category_new, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // holder.job_category.setTypeface(G.ExtraBold);
        if (models.get(position).getStatus() == 0) {
            // holder.job_category.setText(models.get(position).getTitle().toString());
            //holder.rr.setBackgroundResource(R.drawable.item_job_category);
            //  holder.job_category.setTextColor(Color.parseColor("#ffffff"));
        } else {
            //  holder.job_category.setText(models.get(position).getTitle().toString());
            // holder.rr.setBackgroundResource(R.drawable.item_job_category_false);
            //   holder.job_category.setTextColor(Color.parseColor("#5F5F5F"));
        }
//        holder.rr.setBackgroundResource(R.drawable.item_job_category_false);

        holder.tv_a_j_jobTitle.setText(models.get(position).getTitle().toString());
        if (models.get(position).getIcon() > 0) {
            holder.imgal.setImageDrawable(ContextCompat.getDrawable(context, models.get(position).getIcon()));
        }
    }


    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView job_category;
        TextView tv_a_j_jobTitle;
        RelativeLayout rr;
        ImageView imgal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            job_category = itemView.findViewById(R.id.job_category);
            tv_a_j_jobTitle = itemView.findViewById(R.id.tv_a_j_jobTitle);
            tv_a_j_jobTitle.setTypeface(G.ExtraBold);
            imgal = itemView.findViewById(R.id.imagali);
//            rr = itemView.findViewById(R.id.rel_id_ic);

        }
    }


}
