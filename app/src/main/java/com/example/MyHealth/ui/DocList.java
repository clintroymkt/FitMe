package com.example.MyHealth.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;

import com.example.MyHealth.R;
import com.example.MyHealth.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class DocList extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_list);

        tabLayout = findViewById(R.id.tab_doclist);
        viewPager = findViewById(R.id.vp_doclist);
        getTabs();

    }

    public void getTabs() {
       final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        new Handler().post(new Runnable() {
            @Override
            public void run() {


        viewPagerAdapter.addFragment(DocOnlineFrag.getInstance(), "Online");
        viewPagerAdapter.addFragment(DocOfflineFrag.getInstance(), "Offline");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
            }
        });
    }
}