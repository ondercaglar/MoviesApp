package com.interview.project.moviesapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Stetho.initializeWithDefaults(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null)
        {
            //actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.mipmap.ic_launcher);
            actionBar.setTitle(getString(R.string.app_name));
        }

        ViewPager viewPager = findViewById(R.id.viewpager);
        if (viewPager != null)
        {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //tabLayout.getTabAt(0).select();

    }



    private void setupViewPager(ViewPager viewPager)
    {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new MainScreenFragment(), getResources().getString(R.string.pref_sort_label_popular));
        adapter.addFragment(new MainScreenFragment(), getResources().getString(R.string.pref_sort_label_top_rated));
        adapter.addFragment(new MainScreenFragment(), getResources().getString(R.string.pref_sort_label_favorites));
        adapter.addFragment(new MainScreenFragment(), getResources().getString(R.string.pref_sort_label_upcoming));
        adapter.addFragment(new MainScreenFragment(), getResources().getString(R.string.pref_sort_label_now_playing));
        viewPager.setAdapter(adapter);
    }


    static class Adapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        Adapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(Fragment fragment, String title)
        {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position)
        {
            return MainScreenFragment.newInstance(position);
            //return mFragments.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitles.get(position);
        }
    }






}