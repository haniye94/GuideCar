package com.servicea.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.servicea.activities.ListAddressesActivity;
import com.servicea.activities.MapxActivity;
import com.servicea.app.G;
import com.servicea.app.PreferenceUtil;
import com.servicea.model.Address;
import com.servicea.retrofit.Api;
import com.servicea.retrofit.RetrofitClient;

import java.util.List;

import ir.servicea.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class AdapterAddress extends RecyclerView.Adapter<AdapterAddress.ViewHolder> {
    AppCompatActivity context;
    LayoutInflater layoutInflater;
    List<Address> models;

    int selectedPosition = -1;

    public AdapterAddress(AppCompatActivity context, List<Address> models) {
        this.context = context;
        this.models = models;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.adapter_address, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Address address = models.get(position);
        holder.address.setText(address.getTitle() + "\n" + address.getAddress());
        holder.address.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        if(selectedPosition == position){
            holder.check.setImageResource(R.drawable.checkbox_pressed);
        } else{
            holder.check.setImageResource(R.drawable.checkbox_unpress);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (G.preference.getBoolean("FromListAddress", false)) {
                    if (address.getStatus().equals("0")) {
                        address.setStatus("1");
                        selectedPosition = position;
                       /* if (selectedPosition!= -1){
                            models.get(selectedPosition).setStatus("0");
                            notifyItemChanged(selectedPosition);
                        }*/
                        selectedPosition = holder.getAdapterPosition();
                        G.preference.edit().putInt("SelectedAddressID", address.getId()).apply();
                        G.preference.edit().putString("SelectedAddressTitle", address.getTitle()).apply();
                        G.preference.edit().putString("SelectedAddressLat", address.getLatitude()).apply();
                        G.preference.edit().putString("SelectedAddressLng", address.getLongitude()).apply();
                        G.preference.edit().putString("SelectedCityId", address.getCity_id()).apply();
                        G.preference.edit().putBoolean(G.KEY_CITY_ID_CHANGED, true).apply();
                        G.Log("cityId"+ address.getCity_id());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                context.finish();
                            }
                        }, 500);
                        holder.check.setImageResource(R.drawable.checkbox_pressed);
                    } else {
                        selectedPosition = -1;
                        holder.check.setImageResource(R.drawable.checkbox_unpress);
                        address.setStatus("0");
                        G.preference.edit().putInt("SelectedAddressID", 0).apply();
                        G.preference.edit().putString("SelectedAddressTitle", "").apply();
                        G.preference.edit().putString("SelectedAddressLat", "0").apply();
                        G.preference.edit().putString("SelectedAddressLng", "0").apply();
                        G.preference.edit().putString("SelectedCityId","0").apply();

                        if (G.preference.getInt("SelectedAddressID", 0) == 0 && models.size() > 0) {
                            Address defaultAddress = models.get(0);
                            defaultAddress.setStatus("1");
                            G.preference.edit().putInt("SelectedAddressID", defaultAddress.getId()).apply();
                            G.preference.edit().putString("SelectedAddressTitle", defaultAddress.getTitle()).apply();
                            G.preference.edit().putString("SelectedAddressLat", defaultAddress.getLatitude()).apply();
                            G.preference.edit().putString("SelectedAddressLng", defaultAddress.getLongitude()).apply();
                            G.preference.edit().putString("SelectedCityId",defaultAddress.getCity_id()).apply();
                            notifyItemChanged(0);
                        }
                    }
                }
                notifyItemChanged(position);
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, MapxActivity.class)
                        .putExtra("id", address.getId() + "")
                        .putExtra("lat", address.getLatitude() + "")
                        .putExtra("lng", address.getLongitude() + ""));
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete(address.getId() + "");
            }
        });

        if (address.getStatus().equals("1")) {
            holder.check.setImageResource(R.drawable.checkbox_pressed);
        } else {
            holder.check.setImageResource(R.drawable.checkbox_unpress);
        }

        if (!G.preference.getBoolean("FromListAddress", false)) {
            holder.check.setVisibility(View.GONE);
        } else {
            holder.check.setVisibility(View.VISIBLE);
        }

    }

    public void confirmDelete(String id) {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("حذف آدرس")
                .setContentText("آیا می\u200Cخواهید آدرس حذف شود؟")
                .setCancelText("خیر")
                .setConfirmText("بله")
                .showCancelButton(true)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismiss();
                    deleteAddress(id);
                })
                .setCancelClickListener(sDialog -> {
                    sDialog.dismiss();
                })
                .show();
    }

    public void deleteAddress(String id) {
        G.loading(context);
        Api api = RetrofitClient.createService(Api.class, G.api_username, G.api_password);
        Call<ResponseBody> request = api.deleteAddress(id);
        request.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                G.stop_loading();
                String result = G.getResult(response);

                if (result.length() > 0 && result.length() < 10) {
                    G.toast("حذف شد.");
                }
                context.finish();
                context.startActivity(new Intent(context, ListAddressesActivity.class));
                context.overridePendingTransition(0, 0);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                G.stop_loading();
                G.toast("مشکل در برقراری ارتباط");
            }
        });


    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView address;
        ImageView check, edit, delete;
        ViewGroup root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            check = itemView.findViewById(R.id.check);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            root = itemView.findViewById(R.id.root);

        }
    }
}

