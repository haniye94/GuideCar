package com.servicea.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.servicea.FragmentMain;
import com.servicea.Fragmentprofile;

public class AdapterTabLayout extends FragmentPagerAdapter {
    Context context;
    int totalTabs;

    public AdapterTabLayout(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentMain main = new FragmentMain();
                return main;

            case 1:
                Fragmentprofile profile = new Fragmentprofile();
                return profile;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}