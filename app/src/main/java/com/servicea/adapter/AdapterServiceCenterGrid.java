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

public class AdapterServiceCenterGrid extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    LayoutInflater layoutInflater;
    List<ServiceCenter> models;
    private OnAddMoreClicked onAddMoreClicked;

    private static final int LAYOUT_ONE = 0;
    private static final int LAYOUT_TWO = 1;
    public AdapterServiceCenterGrid(Context context, List<ServiceCenter> models, OnAddMoreClicked onAddMoreClicked) {
        this.context = context;
        this.models = models;
        layoutInflater = LayoutInflater.from(context);
        this.onAddMoreClicked = onAddMoreClicked;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position==models.size())
            return LAYOUT_TWO;
        else
            return LAYOUT_ONE;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = null;
        RecyclerView.ViewHolder viewHolder = null;

        if(viewType==LAYOUT_ONE)
        {
            view = layoutInflater.inflate(R.layout.item_service_center_grid,parent,false);
            viewHolder = new ViewHolderOne(view);
        }
        else if(viewType==LAYOUT_TWO)
        {
            view = layoutInflater.inflate(R.layout.item_add_more_center,parent,false);
            viewHolder= new ViewHolderTwo(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if(holder.getItemViewType() == LAYOUT_TWO)
        {
            ViewHolderTwo holderTwo = (ViewHolderTwo) holder;
            holderTwo.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAddMoreClicked.onAddMoreCentersClicked();
                }
            });
            G.Log("SeviceCenter:models" + models.size());
            if (models.size() % 6 != 0){
                holderTwo.root.setVisibility(View.GONE);
            }else{
                holderTwo.root.setVisibility(View.VISIBLE);
            }
        } else if(holder.getItemViewType()==LAYOUT_ONE){
            ViewHolderOne holderOne = (ViewHolderOne) holder;
            ServiceCenter SC = models.get(position);
            holderOne.title.setText(SC.getTitle());
            holderOne.title.setTypeface(G.ExtraBold);

            G.Log("SeviceCenter" + SC.getTitle());
            holderOne.score.setText(SC.getScore());
            holderOne.percent.setText(SC.getPercent());
            holderOne.txtjob.setText(SC.getCategory());

            String profile_photo = SC.getProfile();
            holderOne.img_profile.setImageResource(0);
            Picasso.get().load(G.PreImagesURL + "profiles/" + profile_photo).error(R.drawable.ic_user).placeholder(R.drawable.ic_user).transform(new CircleTransform()).into(holderOne.img_profile);


            holderOne.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ServiceCenterActivity.class).putExtra("id",SC.getId()+""));
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return models.size()+1;
    }

    public interface OnAddMoreClicked{
        void onAddMoreCentersClicked();
    }

    public static class ViewHolderOne extends RecyclerView.ViewHolder {
        TextView title, percent, score, txtjob;
        ImageView  img_profile;
        ViewGroup root;

        public ViewHolderOne(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            percent = itemView.findViewById(R.id.tv_percent);
            score = itemView.findViewById(R.id.tv_score);
            txtjob=itemView.findViewById(R.id.tv_category);

            img_profile = itemView.findViewById(R.id.img_profile);
            root = itemView.findViewById(R.id.root);

        }
    }

    public static class ViewHolderTwo extends RecyclerView.ViewHolder {
        ViewGroup root;

        public ViewHolderTwo(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
        }
    }
}


