package com.servicea.adapter;


import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servicea.model.FilterModel;

import java.util.List;

import ir.servicea.R;

public class AdapterShowFilterToolBar extends RecyclerView.Adapter<AdapterShowFilterToolBar.ViewHolder> {
    Context context;
    LayoutInflater layoutInflater;
    List<FilterModel> models;
    List<Integer> searchFiltersId;

    private OnCancelFilterClicked onCancelFilterClicked;

    public AdapterShowFilterToolBar(Context context, List<FilterModel> models, OnCancelFilterClicked onCancelFilterClicked) {
        this.context = context;
        this.models = models;
        this.onCancelFilterClicked = onCancelFilterClicked;
        layoutInflater = LayoutInflater.from(context);
    }

    public interface OnCancelFilterClicked{
        void onCancelClicked(FilterModel filterModel);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_layout_show_filter, parent, false));
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        FilterModel filterModel = models.get(position);
        Typeface bold_type = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/IRANSans-Light-web.ttf");
        holder.txt_show_filter.setTypeface(bold_type);
        holder.txt_show_filter.setText(filterModel.getTitle());
        holder.img_cancle_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelFilterClicked.onCancelClicked(filterModel);
                notifyDataSetChanged();
            }
        });

       // searchFiltersId = new ArrayList<>();
      //  searchFiltersId = classG.getSearchFiltersId();
       // switch (position) {


        }


    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_show_filter;
        ImageView img_cancle_filter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_show_filter = itemView.findViewById(R.id.txt_show_filter);
            img_cancle_filter = itemView.findViewById(R.id.img_cancle_filter);
        }
    }

    public void swapList(List<FilterModel> models) {
        this.models = models;
    }
}
