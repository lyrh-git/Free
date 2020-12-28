package com.example.free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.free.Classes.BaseActivity;

public class MainActivity extends AppCompatActivity {

    Button location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Button button=findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //以自身为坐标点 （x轴的起点(与现在位置的差值），结束点，y轴的起点，结束点）
//                TranslateAnimation ta=new TranslateAnimation(0,200,0,300);
//                ta.setDuration(2000);//时长 ms
//                v.startAnimation(ta);//自动启动动画
//            }
//        });


//        Button button_startAnim=findViewById(R.id.button_startAnim);
//        button_startAnim.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),+R.anim.anim));
//
//            }
//        });
//
//        Button remove=findViewById(R.id.remove);
//        remove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LinearLayout ll=findViewById(R.id.content);
//                ll.removeView(findViewById(R.id.textView));//或者是int 即LinearLayout容器里面的第几个（index）
//            }
//        });

//        Button button_Activity1=findViewById(R.id.Activity1);
//        button_Activity1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//               Intent intent=new Intent(MainActivity.this, Activity1.class);
//               startActivity(intent);
//            }
//        });

///*
//        Timer timer=new Timer();
//        TextView textView=findViewById(R.id.textView);
//        timer.schedule(new Task((View)textView),2000);*/
//        new Handler(new Handler.Callback() {
//            @Override
//            public boolean handleMessage(@NonNull Message msg) {
//                findViewById(R.id.textView).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),+R.anim.anim));
//                return false;
//            }
//        }).sendEmptyMessageDelayed(0,2000);
///*
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                findViewById(R.id.textView).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),+R.anim.anim));
//            }
//        },2000);*/
//
//        location=findViewById(R.id.location);
//
//        location.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int[] xy=new int[2];
//                location.getLocationOnScreen(xy);
//                Toast.makeText(MainActivity.this,xy[1]+"",Toast.LENGTH_SHORT).show();
//            }
//        });
//
        Button music=findViewById(R.id.music);
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    //申请权限，名字与manifest里面的一致，1为请求码;需要重写onRequestPermissionsResult的得到申请权限的回调函数
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},1
                    );
                    //这里是需要先把文件读出来，再处理
                }
                else{
//                Intent intent=new Intent(MainActivity.this,MusicActivity.class);
                    Intent intent=new Intent(MainActivity.this,MusicViewActivity.class);
                    startActivity(intent);
                }

            }
        });

        Button exit=findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

//
//        Button force=findViewById(R.id.force);
//        force.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(MainActivity.this,ResultActivity.class);
//                startActivity(intent);
//            }
//        });
//
//
//
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //grantResults是用户授权结果，只是接收结果给出反应，不是给出结果的过程
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1://请求码，一个活动里可能处理多个权限请求，对应不同的请求码
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Intent intent=new Intent(MainActivity.this,MusicViewActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this,"you denied the permission",Toast.LENGTH_SHORT).show();
                    finish();//拒绝时回到上一页面
                }
                break;
            default:
        }
    }

//
//    /*
//    public void startAnimation(View v){
//        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),+R.anim.anim));
//    }*/
//
//    /*
//    class Task extends TimerTask{
//        View v;
//        Task(View v){
//            this.v=v;
//        }
//        @Override
//        public void run() {
//            v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),+R.anim.anim));
//        }
//        //Only the original thread that created a view hierarchy can touch its views.
//        //只有创建这个view的线程才能操作这个view，普通会认为是将view创建在非UI线程中才会出现这个错误。
//    }
//*/
}
