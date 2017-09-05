package com.wahyudieko.bakingapp.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.wahyudieko.bakingapp.R;
import com.wahyudieko.bakingapp.entities.Step;

import java.util.ArrayList;

public class StepActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private ArrayList<Step> stepArrayList = new ArrayList<Step>();

    private int numberStepInt = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String title = getIntent().getStringExtra("title");

        stepArrayList = getIntent().getParcelableArrayListExtra("step_list");
        String idStepStr = getIntent().getStringExtra("id_step");

        numberStepInt = stepArrayList.size();

        getSupportActionBar().setTitle(title);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionsPagerAdapter.notifyDataSetChanged();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        int num = Integer.valueOf(idStepStr) ;
        TabLayout.Tab tab = tabLayout.getTabAt(num);
        if (tab != null) {
            tab.select();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Step step = stepArrayList.get(position);
            return StepFragment.newInstance(position,stepArrayList);
        }

        @Override
        public int getCount() {
            return numberStepInt;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int pos= position +1;
            return ""+pos;

        }
    }
}
