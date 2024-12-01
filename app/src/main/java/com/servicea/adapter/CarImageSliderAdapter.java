package com.servicea.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.servicea.model.dbModel.ModelCustomer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ir.servicea.R;

/**
 * @author haniye94 .
 * @since on 11/13/2024.
 */
public class CarImageSliderAdapter extends PagerAdapter {
    private Context context;
    private List<ModelCustomer> productList;

    public CarImageSliderAdapter(Context context, List<ModelCustomer> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);

        ImageView imageView;
        View itemView = inflater.inflate(R.layout.item_car_image_viewpager, container, false);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
//        if (productList.get(position).getCar_image().equals(""))
//            imageView.setImageResource(R.drawable.car2);
//        else {
            Picasso.get()
                    .load("https://autoservicea.ir/public/image/cars/" + productList.get(position).getCar_image())
                    .placeholder(R.drawable.car2) // optional placeholder image
                    .error(R.drawable.car2) // optional error image
                    .into(imageView);

//        }

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}