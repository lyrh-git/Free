package com.example.free.Classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.free.MainActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("force_offline");
        ForceOffLineReceiver receiver=new ForceOffLineReceiver();
        registerReceiver(receiver,intentFilter);
    }

    class ForceOffLineReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityCollector.finishAll();//把所有活动finish后再打开主页面的活动
//            Intent intent1=new Intent(context, MainActivity.class);
//            context.startActivity(intent1);
        }
    }
}
