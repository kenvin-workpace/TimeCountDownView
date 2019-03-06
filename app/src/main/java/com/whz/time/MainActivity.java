package com.whz.time;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.whz.time.view.TimeCountDownView;

public class MainActivity extends AppCompatActivity {

    private TimeCountDownView mTimeCountDownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mTimeCountDownView.setOnTimeCountDownListener(new TimeCountDownView.OnTimeCountDownListener() {
            @Override
            public void countDown(int time) {
                Log.e(getLocalClassName(), String.valueOf(time));
            }
        });
    }

    /**
     * 初始化View数据
     */
    private void initData() {
        mTimeCountDownView.setTimeCountDown(15);
    }

    /**
     * 初始化View资源
     */
    private void initView() {
        mTimeCountDownView = findViewById(R.id.time_count_down);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            mTimeCountDownView.startCountDown(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
