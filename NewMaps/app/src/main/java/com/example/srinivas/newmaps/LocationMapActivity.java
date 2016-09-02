package com.example.srinivas.newmaps;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

/**
 * Created by Srinivas on 02-07-2016.
 */
public class LocationMapActivity extends AppCompatActivity {

    public static final String EXTRA_PLACES = "extra.places";
    private TabLayout mTabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        FragmentManager fm=getSupportFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.content);
        if(fragment==null) {
            LocationMapFragment lf;
            if(getIntent().getSerializableExtra(EXTRA_PLACES)!=null) {
                lf=LocationMapFragment.newInstance((ArrayList<LocationHandler>)getIntent().getSerializableExtra(EXTRA_PLACES));
                getIntent().removeExtra(EXTRA_PLACES);
            } else {
                lf=new LocationMapFragment();
            }
            fm.beginTransaction().replace(R.id.content,lf).commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpTabs();
    }

    private void setUpTabs() {
        mTabLayout=(TabLayout)findViewById(R.id.tabLayout);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.addTab(mTabLayout.newTab().setText("Current Location").setTag("Tab1"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Location List").setTag("Tab2"));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentManager fm=getSupportFragmentManager();
                Fragment fragment;
                switch ((String)tab.getTag()) {
                    case "Tab1":
                        fragment=fm.findFragmentById(R.id.content);
                        LocationMapFragment lf;
                        if(getIntent().getSerializableExtra(EXTRA_PLACES)!=null) {
                            lf=LocationMapFragment.newInstance((ArrayList<LocationHandler>)getIntent().getSerializableExtra(EXTRA_PLACES));
                            getIntent().removeExtra(EXTRA_PLACES);
                        } else {
                            lf=new LocationMapFragment();
                        }
                        fm.beginTransaction().replace(R.id.content,lf).commit();
                        break;
                    case "Tab2":
                        fragment=fm.findFragmentById(R.id.content);
                        fm.beginTransaction().replace(R.id.content,new LocationListFragment()).commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void onResume() {
        super.onResume();
    }
}
