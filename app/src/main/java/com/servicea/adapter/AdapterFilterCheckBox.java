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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ir.servicea.R;

public class AdapterFilterCheckBox extends RecyclerView.Adapter<AdapterFilterCheckBox.ViewHolder> {

    private OnItemClicked onItemClicked;
    private List<ModelJobCategory> jobCategories = new ArrayList<>();
    private LinkedHashMap<Integer, String> selectedJobCategories = new LinkedHashMap<>();

    public AdapterFilterCheckBox(List<ModelJobCategory> jobCategories, LinkedHashMap<Integer, String> selectedJobCategories, OnItemClicked onItemClicked){
        this.jobCategories = jobCategories;
        this.selectedJobCategories = selectedJobCategories;
        this.onItemClicked = onItemClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.job_category_checkbox_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return jobCategories.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(jobCategories.get(position));
    }

    public interface OnItemClicked {
        void onCheckedChanged(ModelJobCategory jobCategory, boolean isChecked);
    }

    // Our view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private TextView tv_job_title;
        private ImageView im_job_icon;
        private ViewGroup ly_job_item;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            tv_job_title = (TextView) itemView.findViewById(R.id.tv_job_title);
            im_job_icon = (ImageView) itemView.findViewById(R.id.im_job_icon);
            ly_job_item = (RelativeLayout) itemView.findViewById(R.id.ly_job_item);
            ly_job_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (jobCategories.get(getAdapterPosition()).getStatus() == 1) {
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
            });
        }

        void bind(ModelJobCategory jobCategory) {
            if(selectedJobCategories.containsKey(jobCategory.getId())){
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
            } else{
                checkBox.setVisibility(View.GONE);
                checkBox.setChecked(false);
            }
            tv_job_title.setText(jobCategory.getTitle());
            tv_job_title.setTypeface(G.Normal);
            im_job_icon.setImageResource(jobCategory.getIcon());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) checkBox.setVisibility(View.GONE);
                    onItemClicked.onCheckedChanged(jobCategory, isChecked);
                }
            });

        }
    }
}
