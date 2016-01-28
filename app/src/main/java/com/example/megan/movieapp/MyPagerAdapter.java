package com.example.megan.movieapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    static final int NUM_PAGES = 3;

    private final Context mContext;
    private final TabHost mTabHost;
    private final ViewPager mViewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<>();

    static final class TabInfo
    {
        private final String tag;
        private final Class<?> clss;
        private final Bundle args;

        TabInfo(String tag, Class<?> _class, Bundle args)
        {
            this.tag = tag;
            clss = _class;
            this.args = args;
        }
    }

    static class DummyTabFactory implements TabHost.TabContentFactory
    {
        private final Context mContext;

        public DummyTabFactory(Context context)
        {
            mContext = context;
        }

        public View createTabContent(String tag)
        {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    public MyPagerAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager)
    {
        super(activity.getSupportFragmentManager());
        mContext = activity;
        mTabHost = tabHost;
        mViewPager = pager;
        mTabHost.setOnTabChangedListener(this);
        mViewPager.setAdapter(this);
        mViewPager.addOnPageChangeListener(this);
    }

    public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args)
    {
        tabSpec.setContent(new DummyTabFactory(mContext));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, clss, args);
        mTabs.add(info);
        mTabHost.addTab(tabSpec);
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return NUM_PAGES;
    }

    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;
        switch(position){
            case 0: //The "sorted by popularity" tab
                fragment = new MainActivityFragment();
                Bundle args0 = new Bundle();
                args0.putString("sortParam", "pop");
                fragment.setArguments(args0);
                break;
            case 1: //The "sorted by rating" tab
                fragment = new MainActivityFragment();
                Bundle args1 = new Bundle();
                args1.putString("sortParam", "rate");
                fragment.setArguments(args1);
                break;
            case 2: //The "favorites" tab
                fragment = new MainActivityFragment();
                Bundle args2 = new Bundle();
                args2.putString("sortParam", "favs");
                fragment.setArguments(args2);
        }
        return fragment;




    }

    public void onTabChanged(String tabId)
    {
        int position = mTabHost.getCurrentTab();
        mViewPager.setCurrentItem(position);
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
    }



    public void onPageSelected(int position)
    {
        TabWidget widget = mTabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mTabHost.setCurrentTab(position);
        widget.setDescendantFocusability(oldFocusability);
    }


    public void onPageScrollStateChanged(int state) {
    }


}
