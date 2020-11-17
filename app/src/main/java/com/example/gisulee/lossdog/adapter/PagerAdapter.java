package com.example.gisulee.lossdog.adapter;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.gisulee.lossdog.view.fragment.LossListFragment;
import com.example.gisulee.lossdog.view.fragment.NearFinderTDataFragment;
import com.example.gisulee.lossdog.view.fragment.NotificationListFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int pageSize;
    private boolean flag2 = false;
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public PagerAdapter(FragmentManager fragmentManager, int pageSize) {
        super(fragmentManager);
        this.pageSize = pageSize;
    }


    @Override
    public Fragment getItem(int position) {
//        notifyDataSetChanged();
        switch (position) {
            case 0:
                LossListFragment fragment1 = new LossListFragment();
                return fragment1;
            case 1:
                NearFinderTDataFragment fragment2 = new NearFinderTDataFragment();
                return fragment2;
            case 2:
                NotificationListFragment fragment3 = new NotificationListFragment();
                return fragment3;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return pageSize;
    }

    @Override
    public int getItemPosition(Object object) {

            return POSITION_UNCHANGED; // don't force a reload
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }


}