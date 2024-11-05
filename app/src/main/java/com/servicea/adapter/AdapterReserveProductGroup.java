package com.servicea.adapter;

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
import com.servicea.model.ModelProduct;
import com.servicea.model.dbModel.ModelProduceGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ir.servicea.R;

public class AdapterReserveProductGroup extends RecyclerView.Adapter<AdapterReserveProductGroup.ViewHolder> {

    private OnItemClicked onItemClicked;
    private List<ModelProduceGroup> produceGroups = new ArrayList<>();
    private LinkedHashMap<Integer, String> selectedJobCategories = new LinkedHashMap<>();

    public AdapterReserveProductGroup(List<ModelProduceGroup> produceGroups, OnItemClicked onItemClicked){
        this.produceGroups = produceGroups;
        this.onItemClicked = onItemClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserve_product_group, parent, false));
    }

    @Override
    public int getItemCount() {
        return produceGroups.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(produceGroups.get(position));
    }

    public interface OnItemClicked {
        void onCheckedChanged(ModelProduceGroup jobCategory, boolean isChecked);
    }

    // Our view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;


        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);

          /*  ly_job_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/
        }

        void bind(ModelProduceGroup product) {
            checkBox.setText(product.getTitle());
            checkBox.setTypeface(G.Bold);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onItemClicked.onCheckedChanged(product, isChecked);
                }
            });

        }
    }
}
