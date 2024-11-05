package com.servicea.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.servicea.app.G;
import com.servicea.model.SliderItem;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ir.servicea.R;
import com.servicea.activities.WebViewActivity;
import com.squareup.picasso.Target;

public class SliderAdapterExample /*extends PagerAdapter<SliderAdapterExample>*/ {

    private Context context;
    private List<SliderItem> mSliderItems = new ArrayList<>();
    private OnImageSizeLoaded onImageSizeLoaded;

    public SliderAdapterExample(Context context, OnImageSizeLoaded onImageSizeLoaded) {
        this.context = context;
        this.onImageSizeLoaded = onImageSizeLoaded;
    }

    public void renewItems(List<SliderItem> sliderItems) {
        this.mSliderItems = sliderItems;
//        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
//        notifyDataSetChanged();
    }

    public void addItem(SliderItem sliderItem) {
        this.mSliderItems.add(sliderItem);
//        notifyDataSetChanged();
    }

//    @Override
//    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
//        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
//        return new SliderAdapterVH(inflate);
//    }

//    @Override
//    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
//
//        SliderItem sliderItem = mSliderItems.get(position);
//
//        //viewHolder.textViewDescription.setText(sliderItem.getDescription());
//        viewHolder.textViewDescription.setTextSize(16);
//        viewHolder.textViewDescription.setTextColor(Color.WHITE);
//        Picasso.get().load(sliderItem.getImageUrl()).fit().into(viewHolder.imageViewBackground, new Callback() {
//            @Override
//            public void onSuccess() {
//                Picasso.get().load(sliderItem.getImageUrl()).into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        int width = bitmap.getWidth();
//                        int height = bitmap.getHeight();
//                        G.Log("height slider" + height);
//                     /*   viewHolder.imageViewBackground.getLayoutParams().height = height;
//                        viewHolder.imageViewBackground.requestLayout();
//*/
//                        onImageSizeLoaded.onSizeLoaded(height);
//
//                    }
//                    @Override
//                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//                    }
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onError(Exception e) {
//
//            }
//        });
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(sliderItem.getUrl().contains("http") && sliderItem.getUrl().length()>10) {
//                    context.startActivity(new Intent(context, WebViewActivity.class)
//                            .putExtra("LINK", sliderItem.getUrl())
//                            .putExtra("TITLE", sliderItem.getDescription()));
//                }
//            }
//        });
//    }

//    @Override
//    public int getCount() {
//        //slider view count could be dynamic size
//        return mSliderItems.size();
//    }
//
//    @Override
//    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return false;
//    }

//    static class SliderAdapterVH extends PagerAdapter. {
//
//        View itemView;
//        ImageView imageViewBackground;
//        ImageView imageGifContainer;
//        TextView textViewDescription;
//
//        public SliderAdapterVH(View itemView) {
//            super(itemView);
//            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
//            imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
//            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
//            this.itemView = itemView;
//        }
//    }

    public interface OnImageSizeLoaded{
        void onSizeLoaded(int height);
    }

}