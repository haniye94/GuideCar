package com.servicea.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servicea.app.G;
import com.servicea.model.dbModel.ModelCustomer;

import java.util.List;

import ir.servicea.R;

public class AdapterFilterRadioButton extends RecyclerView.Adapter<AdapterFilterRadioButton.ViewHolder> {

    // Initialize variable
    List<ModelCustomer> arrayList;
    ItemClickListener itemClickListener;
    int selectedCarId;
    int selectedPosition = -1;

    // Create constructor
    public AdapterFilterRadioButton(List<ModelCustomer> arrayList,
                                    int selectedCarId,
                                    ItemClickListener itemClickListener) {
        this.arrayList = arrayList;
        this.selectedCarId = selectedCarId;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType) {
        // Initialize view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_car_item, parent,
                        false);
        // Pass holder view
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 @SuppressLint("RecyclerView") int position)
    {
        holder.radioButton.setText(arrayList.get(position).getName_car());

        String pelak = arrayList.get(position).getPlak();
        if (pelak.length() > 3) {
            String c1 = pelak.substring(0, 2);
            String c2 = pelak.substring(2, pelak.length() - 3);
            String c3 = pelak.substring(pelak.length() - 3, pelak.length() - 1);
            String c4 = pelak.substring(pelak.length() - 1);

            holder.tv_pelak_0.setText(c1);
            holder.tv_pelak_1.setText(c4 + " ");
            holder.tv_pelak_2.setText(c2 + " ");
            holder.tv_pelak_3.setText(c3 + "-");
        }
        holder.radioButton.setTypeface(G.Bold);
        holder.tv_pelak_0.setTypeface(G.Normal);
        holder.tv_pelak_1.setTypeface(G.Normal);
        holder.tv_pelak_2.setTypeface(G.Normal);
        holder.tv_pelak_3.setTypeface(G.Normal);
        holder.radioButton.setChecked(position == selectedPosition);
        if(selectedPosition == -1) holder.radioButton.setChecked(arrayList.get(position).getCar_id() == selectedCarId);

        // set listener on radio button
        holder.radioButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(
                            CompoundButton compoundButton,
                            boolean b) {
                        // check condition
                        if (b) {
                            // When checked
                            // update selected position
                            selectedPosition = holder.getAdapterPosition();
                            // Call listener
                            itemClickListener.onClick(arrayList.get(position));
                        }
                    }
                });
    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder
            extends RecyclerView.ViewHolder {
        // Initialize variable
        RadioButton radioButton;
        TextView tv_pelak_0,tv_pelak_1,tv_pelak_2,tv_pelak_3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_button);
            tv_pelak_0 = itemView.findViewById(R.id.tv_pelak_0);
            tv_pelak_1 = itemView.findViewById(R.id.tv_pelak_1);
            tv_pelak_2 = itemView.findViewById(R.id.tv_pelak_2);
            tv_pelak_3 = itemView.findViewById(R.id.tv_pelak_3);
        }
    }

    public interface ItemClickListener {
        // Create method
        void onClick(ModelCustomer car);
    }

    private String getFormattedPelak(String pelak){
        String c1 = pelak.substring(0,2);
        String c2 = pelak.substring(2,pelak.length()-3);
        String c3 = pelak.substring(pelak.length()-3,pelak.length()-1);
        String c4 = pelak.substring(pelak.length()-1);
        return c1 + " "+ c2 + " " + c3 + "-" + c4;
    }
}
