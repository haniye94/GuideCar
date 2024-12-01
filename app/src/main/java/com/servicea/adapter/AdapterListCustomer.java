package com.servicea.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servicea.activities.InformationCustomersActivity;
import com.servicea.activities.LastServiseDoneActivity;
import com.servicea.activities.AddNewCarActivity;
import com.servicea.app.Constants;
import com.servicea.app.DataBaseHelper;
import com.servicea.app.G;
import com.servicea.app.PLakUtils;
import com.servicea.model.dbModel.ModelCustomer;

import java.util.List;

import ir.servicea.R;

public class AdapterListCustomer extends RecyclerView.Adapter<AdapterListCustomer.ViewHolder> {
    Context context;
    LayoutInflater layoutInflater;
    List<ModelCustomer> models;
    DataBaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    private OnItemClickListener listener;
    private OnItemClickListener newServiceClicked;

    public AdapterListCustomer(Context context, List<ModelCustomer> models, OnItemClickListener listener, OnItemClickListener newServiceClicked) {
        this.context = context;
        this.models = models;
        layoutInflater = LayoutInflater.from(context);
        mDBHelper = new DataBaseHelper(context);
        mDatabase = mDBHelper.getReadableDatabase();
        this.listener = listener;
        this.newServiceClicked = newServiceClicked;
    }

    public interface OnItemClickListener {
        void onItemClick(ModelCustomer model, ImageView item, ViewHolder holder, int position);
        void onNewServiceClick(ModelCustomer model);
    }

    @Override
    public int getItemViewType(int position) {
        ModelCustomer customer = models.get(position);
        int viewType = 0;
        switch (customer.getPlak_type()) {
            case PLAK_GENERAL:
                viewType = 1;
                break;

            case PLAK_TAXI:
                viewType = 2;
                break;

            case PLAK_EDARI:
                viewType = 3;
                break;

            case PLAK_ENTEZAMI:
                viewType = 4;
                break;

            case PLAK_MAOLOIN:
                viewType = 5;
                break;

            case PLAK_AZAD_NEW:
                viewType = 6;
                break;

            case PLAK_AZAD_OLD:
                viewType = 7;
                break;
        }
        return viewType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.item_list_customer;
        switch (viewType) {
            case 1:
                layout = R.layout.item_list_customer_azad_new;
                break;

            case 2:
                layout = R.layout.item_list_customer_taxi;
                break;

            case 3:
                layout = R.layout.item_list_customer_edari;
                break;

            case 4:
                layout = R.layout.item_list_customer_entezami;
                break;

            case 5:
                layout = R.layout.item_list_customer_malolin;
                break;

            case 6:
                layout = R.layout.item_list_customer_azad_new;
                break;

            case 7:
                layout = R.layout.item_list_customer_azad_old;
                break;

        }

        return new ViewHolder(layoutInflater.inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ModelCustomer model = models.get(position);
        String plak = (models.get(position).getPlak() + "").replace(" ", "").replace("null", "");
        if (plak.length() > 3) {
            setPlakBasedViewType(plak, holder);
        } else {
            holder.plaks.setVisibility(View.GONE);
        }
        holder.txt_name_customer.setText(models.get(position).getFirst_name().toString() + " " + models.get(position).getLast_name().toString());
        holder.txt_name_car.setText(models.get(position).getName_car());
        holder.txt_phone_customer.setText(models.get(position).getPhone());



        holder.show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, CarListActivity.class);
                Intent intent = new Intent(context, InformationCustomersActivity.class);
                intent.putExtra("idCustomer", model.getId() + "");

                intent.putExtra("id_car", model.getCar_id() + "");
                intent.putExtra("firstName", model.getFirst_name());
                intent.putExtra("lastName", model.getLast_name());
                intent.putExtra("phone", model.getPhone());
                intent.putExtra("nameCar", model.getName_car());
                intent.putExtra("plak", model.getPlak());
                intent.putExtra(Constants.CAR_PLATE_TYPE, model.getPlak_type());
                intent.putExtra("gender", model.getGender());
                intent.putExtra("date_birthday", model.getDate_birthday());
                intent.putExtra("type_fule", model.getType_fuel());
                intent.putExtra("date_save", model.getDate_save_customer());
                intent.putExtra("type_car", model.getType_car());
                intent.putExtra("car_name_id", model.getCar_name_id());
                intent.putExtra("car_tip_id", model.getCar_tip_id());
                intent.putExtra("car_model_id", model.getCar_model_id());
                intent.putExtra("car_company_id", model.getCar_company_id());
                intent.putExtra("fuel_type_id", model.getFuel_type_id());
                intent.putExtra("car_model", model.getCar_model() + "");
                intent.putExtra("car_tip", model.getCar_tip() + "");
                context.startActivity(intent);
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddNewCarActivity.class);
                intent.putExtra("idCustomer", model.getId() + "");
                intent.putExtra("id_car", model.getCar_id() + "");
                intent.putExtra("firstName", model.getFirst_name());
                intent.putExtra("lastName", model.getLast_name());
                intent.putExtra("phone", model.getPhone());
                intent.putExtra("nameCar", model.getName_car());
                intent.putExtra("plak", model.getPlak());
                intent.putExtra(Constants.CAR_PLATE_TYPE, model.getPlak_type());
                intent.putExtra("gender", model.getGender());
                intent.putExtra("date_birthday", model.getDate_birthday());
                intent.putExtra("type_fule", model.getType_fuel());
                intent.putExtra("date_save", model.getDate_save_customer());
                intent.putExtra("type_car", model.getType_car());
                intent.putExtra("car_name_id", model.getCar_name_id());
                intent.putExtra("car_tip_id", model.getCar_tip_id());
                intent.putExtra("car_model_id", model.getCar_model_id());
                intent.putExtra("car_company_id", model.getCar_company_id());
                intent.putExtra("fuel_type_id", model.getFuel_type_id());

                context.startActivity(intent);
            }
        });
        holder.service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LastServiseDoneActivity.class);
                intent.putExtra("car_id", model.getCar_id());

                context.startActivity(intent);
            }
        });
         //fro next update
        holder.newService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  G.toast("بزودی...");
              /*  Intent intent = new Intent(context, JobCategoriesActivity.class);
                intent.putExtra("customer_model", (Parcelable) model);
                context.startActivity(intent);*/
                newServiceClicked.onNewServiceClick(model);

            }
        });
        holder.bind(context, models.get(position), holder, listener);
    }

    private void setPlakBasedViewType(String plak, ViewHolder holder) {
        switch (holder.getItemViewType()) {
            case 1:
            case 2:
            case 3:
            case 4: {
                holder.plaks.setVisibility(View.VISIBLE);
                String c1 = plak.substring(0, 2);
                String c2 = plak.substring(2, plak.length() - 3);
                String c3 = plak.substring(plak.length() - 3, plak.length() - 1);
                String c4 = plak.substring(plak.length() - 1);
                holder.txt_plak_customer1.setText(c1);
                holder.txt_plak_customer2.setText(c4);
                holder.txt_plak_customer3.setText(c2);
                holder.txt_plak_customer4.setText(c3);
                break;
            }
            case 5:
            {
                holder.plaks.setVisibility(View.VISIBLE);
                String c1 = plak.substring(0, 2);
                String c2 = plak.substring(2, plak.length() - 3);
                String c3 = plak.substring(plak.length() - 3, plak.length() - 1);
                holder.txt_plak_customer1.setText(c1);
                holder.txt_plak_customer2.setVisibility(View.GONE);
                holder.txt_plak_customer3.setText(c2);
                holder.txt_plak_customer4.setText(c3);
                break;
            }
            case 6: {
                holder.plaks.setVisibility(View.VISIBLE);
                String c1 = plak.substring(0, 6);
                String c4 = plak.substring(6, plak.length());
                holder.txt_plak_customer1.setText(c1);
                holder.txt_plak_customer2.setText(c4);
                holder.txt_plak_customer3.setText(PLakUtils.convertPersianToEnglish(c1));
                holder.txt_plak_customer4.setText(PLakUtils.convertPersianToEnglish(c4));
                break;
            }
            case 7: {
                holder.plaks.setVisibility(View.VISIBLE);
                String c1 = plak.substring(0, 6);
                String c4 = plak.substring(6, plak.length());
                holder.txt_plak_customer1.setText(c1);
                holder.txt_plak_customer2.setText(PLakUtils.convertPersianToEnglish(c1));
                holder.txt_plak_customer3.setText(c4);
                holder.txt_plak_customer4.setVisibility(View.GONE);
                break;
            }
        }
       /* holder.txt_plak_customer1.setTypeface(G.ExtraBold);
        holder.txt_plak_customer2.setTypeface(G.ExtraBold);
        holder.txt_plak_customer3.setTypeface(G.ExtraBold);
        holder.txt_plak_customer4.setTypeface(G.ExtraBold);*/
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name_customer, txt_name_car, txt_phone_customer;
        ImageView icon_menu;
        TextView txt_plak_customer1, txt_plak_customer2, txt_plak_customer3, txt_plak_customer4;
        ViewGroup plaks, root;
        Button show,edit,service,newService;

        public void bind(Context context, final ModelCustomer item, final ViewHolder holder, final OnItemClickListener listener) {
            icon_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, icon_menu, holder, getAdapterPosition());
                }
            });
/*
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, InformationCustomersActivity.class);
                    intent.putExtra("idCustomer", item.getId() + "");
                    intent.putExtra("id_car", item.getCar_id() + "");
                    intent.putExtra("firstName", item.getFirst_name());
                    intent.putExtra("lastName", item.getLast_name());
                    intent.putExtra("phone", item.getPhone());
                    intent.putExtra("nameCar", item.getName_car());
                    intent.putExtra("plak", item.getPlak());
                    intent.putExtra("gender", item.getGender());
                    intent.putExtra("date_birthday", item.getDate_birthday());
                    intent.putExtra("type_fule", item.getType_fuel());
                    intent.putExtra("date_save", item.getDate_save_customer());
                    intent.putExtra("type_car", item.getType_car());
                    intent.putExtra("car_name_id", item.getCar_name_id());
                    intent.putExtra("car_tip_id", item.getCar_tip_id());

                    intent.putExtra("car_model_id", item.getCar_model_id());
                    intent.putExtra("fuel_type_id", item.getFuel_type_id());

                    intent.putExtra("car_model", item.getCar_model()+"");
                    intent.putExtra("car_tip", item.getCar_tip()+"");

                    context.startActivity(intent);
                }
            });
*/


        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name_customer = itemView.findViewById(R.id.txt_name_customer);
            txt_name_car = itemView.findViewById(R.id.txt_name_car);
            plaks = itemView.findViewById(R.id.plaks);
            txt_phone_customer = itemView.findViewById(R.id.txt_phone_customer);
            txt_plak_customer1 = itemView.findViewById(R.id.txt_plak_customer1);
            txt_plak_customer2 = itemView.findViewById(R.id.txt_plak_customer2);
            txt_plak_customer3 = itemView.findViewById(R.id.txt_plak_customer3);
            txt_plak_customer4 = itemView.findViewById(R.id.txt_plak_customer4);
            show = itemView.findViewById(R.id.show);
            edit = itemView.findViewById(R.id.edit);
            service = itemView.findViewById(R.id.service);
            // for next update
            newService = itemView.findViewById(R.id.newService);
            icon_menu = itemView.findViewById(R.id.icon_menu);
            root = itemView.findViewById(R.id.root);
            txt_name_customer.setTypeface(G.ExtraBold);
            txt_name_car.setTypeface(G.ExtraBold);
            txt_phone_customer.setTypeface(G.Normal);
            txt_plak_customer1.setTypeface(G.ExtraBold);
            txt_plak_customer2.setTypeface(G.ExtraBold);
            txt_plak_customer3.setTypeface(G.ExtraBold);
            txt_plak_customer4.setTypeface(G.ExtraBold);
        }
    }
}
