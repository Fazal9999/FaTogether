package com.kptech.peps.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.kptech.peps.R;

import java.util.Timer;
import java.util.TimerTask;

public class HelpActivity extends AppBaseActivity {
    ViewPager viewPager;
    TabLayout indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setHeaderView(getResources().getString(R.string.help));

        initViews();
    }

    private void initViews() {
        HelpViewPagerAdapter helpViewPagerAdapter = new HelpViewPagerAdapter(this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(helpViewPagerAdapter);

        indicator=(TabLayout)findViewById(R.id.indicator);
        indicator.setupWithViewPager(viewPager, true);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
    }

    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            HelpActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < 2) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }
}
