package com.servicea;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import ir.servicea.R;

import com.servicea.activities.AlarmsActivity;
import com.servicea.activities.ProfileActivity;
import com.servicea.activities.SplashActivity;
import com.servicea.activities.SupportActivity;
import com.servicea.activities.WebViewActivity;
import com.servicea.app.CircleTransform;
import com.servicea.app.G;
import com.servicea.activity.Config2Activity;
import com.servicea.activity.ConfigActivity;
import com.servicea.activity.passwordActivity;
import com.servicea.app.PreferenceUtil;

public class Fragmentprofile extends Fragment {
    TextView txt_tile_action_bar;
    TextView txt_city_job, txt_name_family, txt_edit_pass, txt_faq, txt_notif, txt_language, txt_suport, txt_exit,txt_privacy,txt_score;
    Button btn_edit_profile;
    PreferenceUtil preferenceUtil;
    private ImageView profile_iv;
    public View view;

    public void onclickAlamrs(View v) {
        startActivity(new Intent(getContext(), AlarmsActivity.class));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile, container, false);
        preferenceUtil = new PreferenceUtil(getContext());
        FindView(view);
        onClick();
        txt_tile_action_bar.setText("تنظیمات حساب کاربری");

        txt_city_job.setTypeface(G.Normal);
        txt_tile_action_bar.setTypeface(G.ExtraBold);
        txt_name_family.setTypeface(G.ExtraBold);
        txt_edit_pass.setTypeface(G.ExtraBold);
        txt_faq.setTypeface(G.ExtraBold);
        txt_notif.setTypeface(G.ExtraBold);
        txt_language.setTypeface(G.ExtraBold);
        txt_suport.setTypeface(G.ExtraBold);
        txt_exit.setTypeface(G.ExtraBold);
        txt_privacy.setTypeface(G.ExtraBold);
        txt_score.setTypeface(G.ExtraBold);

        txt_name_family.setText((preferenceUtil.getName() + "").replace("null", "") + " " + (preferenceUtil.getFamily() + "").replace("null", ""));
        txt_city_job.setText((PreferenceUtil.getPhone() + "").replace("null", "") + "");
        if (txt_city_job.getText().toString().replace(" ", "").length() <= 3) {
            txt_city_job.setText("");
        }
        if(txt_name_family.getText().toString().length()<=1){
            txt_name_family.setText("کاربر مهمان");
        }
        view.findViewById(R.id.faq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), WebViewActivity.class)
                        .putExtra("LINK", G.LINK_Introduction)
                        .putExtra("TITLE", "معرفی و آموزش"));
            }
        });view.findViewById(R.id.elans).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), ConfigActivity.class));
            }
        });
        view.findViewById(R.id.privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Config2Activity.class));
            }
        });
        view.findViewById(R.id.score).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                G.toast("بزودی ...");
            }
        });
        view.findViewById(R.id.language).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              G.toast("بزودی ...");
            }
        });
        view.findViewById(R.id.support).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SupportActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), passwordActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("خروج")
                        .setContentText("آیا میخواهید از برنامه خارج شوید؟")
                        .setCancelText("خیر")
                        .setConfirmText("بله")
                        .showCancelButton(true)
                        .setConfirmClickListener(sDialog -> {
                            sDialog.dismiss();
                            G.preference.edit().clear().apply();
                            PreferenceUtil.preferenceUtil.edit().clear().apply();

                            startActivity(new Intent(getActivity(), SplashActivity.class));
                            getActivity().finishAffinity();
                        })
                        .setCancelClickListener(sDialog -> {
                            sDialog.dismiss();
                        })
                        .show();

            }
        });
        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        if (G.preference.getString("resultUri", "").length() > 3) {
            profile_iv = view.findViewById(R.id.profile_iv);
            Uri resultUri = Uri.parse(G.preference.getString("resultUri", ""));
            Picasso.get().load(resultUri).error(R.drawable.ic_user).placeholder(R.drawable.ic_user).transform(new CircleTransform()).into(profile_iv);
        }
        if (G.preference.getString("profile_photo", "").length() > 3) {
            profile_iv = view.findViewById(R.id.profile_iv);
            String profile_photo = G.preference.getString("profile_photo","");
            Picasso.get().load(G.PreImagesURL+"profiles/" +profile_photo).error(R.drawable.ic_user).placeholder(R.drawable.ic_user).transform(new CircleTransform()).into(profile_iv);
        }
    }

    void FindView(View view) {

        txt_tile_action_bar = view.findViewById(R.id.txt_tile_action_bar);
        txt_name_family = view.findViewById(R.id.txt_name_family);
        txt_city_job = view.findViewById(R.id.txt_city_job);
        txt_edit_pass = view.findViewById(R.id.txt_edit_pass);
        txt_faq = view.findViewById(R.id.txt_faq);
        txt_notif = view.findViewById(R.id.txt_notif);
        txt_language = view.findViewById(R.id.txt_language);
        txt_suport = view.findViewById(R.id.txt_suport);
        txt_exit = view.findViewById(R.id.txt_exit);
        txt_privacy = view.findViewById(R.id.txt_privacy);
        txt_score = view.findViewById(R.id.txt_score);
        btn_edit_profile = view.findViewById(R.id.btn_edit_profile);
    }

    private void onClick() {
        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("editPro", "editPro");
                startActivity(intent);
            }
        });
    }

}