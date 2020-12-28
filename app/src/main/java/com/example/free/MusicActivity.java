package com.example.free;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.free.Classes.BaseActivity;

import java.io.File;

public class MusicActivity extends BaseActivity implements View.OnClickListener{

    //继承View.OnClickListener方法，重写onClick方法

    private MediaPlayer mediaPlayer=new MediaPlayer();
    private String musicPath="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Button musicView=findViewById(R.id.musicView);
        musicView.setOnClickListener(this);

        Button play=findViewById(R.id.play);
        Button pause=findViewById(R.id.pause);
        Button stop=findViewById(R.id.stop);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);

        Button result=findViewById(R.id.result);
        result.setOnClickListener(this);

//        Intent intent=getIntent();
//        musicPath=intent.getStringExtra("musicPath");

        //运行时申请权限（网络状态/开机状态等普通权限可以直接在manifest里面添加，
        // 访问sd卡等危险权限不仅要在manifest里添加，还要运行时申请，不然会报错危险
        //if语句检查是否已经授权
        if(ContextCompat.checkSelfPermission(MusicActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            //申请权限，名字与manifest里面的一致，1为请求码;需要重写onRequestPermissionsResult的得到申请权限的回调函数
            ActivityCompat.requestPermissions(MusicActivity.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1
            );
            //这里是需要先把文件读出来，再处理
        }
        else{
            initMediaPlayer();
        }



    }

    private void initMediaPlayer(){
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "music.mp3");//sd卡目录下的文件
            mediaPlayer.setDataSource(file.getPath());
            //mediaPlayer.setDataSource(musicPath);
            Toast.makeText(getApplicationContext(),file.getPath(),Toast.LENGTH_SHORT).show();
            mediaPlayer.prepare();//让mediaPlayer进入到准备状态
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //grantResults是用户授权结果，只是接收结果给出反应，不是给出结果的过程
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1://请求码，一个活动里可能处理多个权限请求，对应不同的请求码
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    initMediaPlayer();
                }else{
                    Toast.makeText(this,"you denied the permission",Toast.LENGTH_SHORT).show();
                    finish();//拒绝时回到上一页面
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.musicView:
                Intent intent=new Intent(MusicActivity.this,MusicViewActivity.class);
                startActivity(intent);
                break;

            case R.id.play:
                if(!mediaPlayer.isPlaying())
                    mediaPlayer.start();
                break;
            case R.id.pause:
                if(mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                break;
            case R.id.stop:
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();//停止播放
                    initMediaPlayer();//停止之后要再准备一次   //prepare start; pause start; reset prepare start,
                }
                break;
            case R.id.result:
                Toast.makeText(getApplicationContext(),"click result activity",Toast.LENGTH_SHORT).show();
                Intent intent1=new Intent(MusicActivity.this,ResultActivity.class);
                getApplicationContext().startActivity(intent1);
                break;
             default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();//中间强制退出时，要关掉播放器
            mediaPlayer.release();//释放播放器所占资源

        }
    }
}
