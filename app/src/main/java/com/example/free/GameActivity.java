package com.example.free;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.free.Classes.BaseActivity;
import com.example.free.Classes.EachButton;
import com.example.free.Classes.HandleData;
import com.example.free.Mp3Handle.MyMp3FileReader;
import com.example.free.WavHandle.WaveFileReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;



public class GameActivity extends BaseActivity {

    public static boolean activityState=true;//活动状态，方便eachButton在buttons为0时是否要去resultActivity

    boolean stateMove=true;//活动状态，暂停键

    int nowTime=0;//方便暂停恢复时重新设置button的动画delay
    Date dateHelp=null;

    final Context context=GameActivity.this;

    String musicPath="";
    long musicSize=0;
    long musicTime=0;
    int window=1024;
    int windowSize=20;
    double multiplier=1.5;
    MediaPlayer mediaPlayer=new MediaPlayer();

    ArrayList<Integer> allTime=new ArrayList<>();//从音频里得到的节奏点时间

    Button[] bottoms=new Button[2];

    static ConstraintLayout buttonConstraintLayout;//按钮放置的视图
    public static ArrayList<ArrayList<EachButton>> buttons=new ArrayList();//按钮滑块们

    int deviceWidthPx=1080;//1080/3dp
    int deviceWidthDp=360;//dp
    double dp_px=3.0;//1dp=3px;

    //这些都是以dp为指标，需要转化px的变量
    int bottomWidth=120;//dp
    int buttonWidth=80;//dp
    int bottomHeight=60;//底部按钮的高度，也是最后决定good best等级的高度
    int buttonHeight=40;
    int[] X={70,200};//轨道 //button 宽80dp  bottom 120dp
    int startY=0;
    int marginTop=400;
    int endY=marginTop+bottomHeight;//距top400dp

    //这些都是以dp为指标，需要转化px的变量


    int duration=1000;//滑块运动时间
    int blocks=0;//滑块数量 0 代表是默认或者高级设置 1 少  2 多，在getTime()那里用到去配置
    //压缩音频：滑块少：windowSize:15,multiplier:1;滑块多：windowSize:20,multiplier:5;
    //无损音频：滑块少：windowSize:10,multiplier:5;滑块多：windowSize:10,multiplier:2;

    int microChange=20;//判定miss good best调整

    public static Toast toast;

    final int goodScore=15;//5分
    final int bestScore=20;//10分
    public static int goodTimes;
    public static int bestTimes;
    public static int missTimes;
    public static int resultScore;
    public static int wholeScore;


    TextView scoreView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        //这里要重新初始化活动状态，不然第二次选歌后这里默认false，eachButton那里无法访问resultActivity
        activityState=true;

        goodTimes=0;
        bestTimes=0;
        missTimes=0;
        resultScore=0;
        wholeScore=0;

        //初始化视图，dp转化为px，方便eachButton类的加载
        initDpToPx();
        initPxs();


        //得到按钮放置的视图
        buttonConstraintLayout=findViewById(R.id.buttonConstraintLayout);

        scoreView=findViewById(R.id.score);

        //得到音乐文件的路径 模式 滑块数量等信息
        getIntentData();

        //初始化播放器
        initMediaPlayer();

        //得到节奏点时间
        getTime();//节奏点时间储存在了allTime里面

        toast=new Toast(context);

        //初始化底部按钮事件
        initBottom();

//        TimerTask task = new TimerTask(){
//            public void run(){
//                //execute the task
//                mediaPlayer.start();
//            }
//        };
//        Timer timer = new Timer();
//        timer.schedule(task, duration);

        //初始化按钮
//        Date date=new Date();
        initButtons();//733s/329s，生成一个按钮大概2ms，
        //System.out.println("````````````````````time```````````````:"+(new Date().getTime()-date.getTime()));


        //播放音频
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(duration*(marginTop+buttonHeight)/(endY));
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

                mediaPlayer.start();
            }
        }).start();

        dateHelp=new Date();

//        TimerTask task = new TimerTask(){
//            public void run(){
//                //execute the task
//                mediaPlayer.start();
//
//            }
//        };
//        Timer timer = new Timer();
//        timer.schedule(task, duration);
//        //mediaPlayer.start();

        //初始化暂停按钮
        initStateButton();

    }

    public  void initDpToPx(){

//        低密度     ldpi：  240x320    ppi 120    1dp=0.75px
//        中密度     mdpi：  320x480    ppi 160    1dp=1px
//        高密度     hdpi：  480x800    ppi 240    1dp=1.5px
//        超高密度   xhdpi： 720x1280   ppi 320    1dp=2px
//        超超高密度 xxhdpi：1080x1920  ppi 480    1dp=3px
        deviceWidthPx=getWindowManager().getDefaultDisplay().getWidth();
//        int height=getWindowManager().getDefaultDisplay().getHeight();
        Map<Integer,Double> dpis=new HashMap<Integer, Double>();
        dpis.put(240,0.75);
        dpis.put(320,1.0);
        dpis.put(480,1.5);
        dpis.put(720,2.0);
        dpis.put(1080,3.0);

        if(dpis.containsKey(deviceWidthPx)){
            dp_px=dpis.get(deviceWidthPx);
        }

        deviceWidthDp=(int)(deviceWidthPx/dp_px);


    }

    public void initPxs(){
        //需要转为px的指标
//        int bottomWidth=120;//dp
//        int buttonWidth=70;//dp
//        int bottomHeight=60;//底部按钮的高度，也是最后决定good best等级的高度
//        int buttonHeight=35;
//        int[] X={70,200};//轨道 //button 宽80dp  bottom 120dp
//        int startY=0;
//        int marginTop=400;
//        int endY=marginTop+bottomHeight;//距top400dp

        endY=(int)(endY*dp_px);//endY受bottomHeight影响，所以第一个改
        marginTop=(int)(marginTop*dp_px);
        bottomHeight=(int)(bottomHeight*dp_px);
        buttonHeight=(int)(buttonHeight*dp_px);
        buttonWidth=(int)(buttonWidth*dp_px);
        bottomWidth=(int)(bottomWidth*dp_px);
        int temp=bottomWidth-buttonWidth;
//        Toast.makeText(getApplicationContext(),findViewById(R.id.bottom1).getX()+"",Toast.LENGTH_SHORT).show();
        for(int i=0;i<X.length;i++){
            X[i]=deviceWidthPx/X.length*i+(deviceWidthPx/X.length-buttonWidth)/2;//按钮居中对齐
        }


    }

    public void getIntentData(){
        Intent intent=getIntent();
        String className=intent.getStringExtra("className");
        if(className.equals("ChoiceActivity")) {
            musicPath = intent.getStringExtra("musicPath");
            musicSize = intent.getLongExtra("musicSize", 0);
            musicTime = intent.getLongExtra("musicTime", 0);
            window = intent.getIntExtra("window", 1024);
            windowSize = intent.getIntExtra("windowSize", 20);
            multiplier = intent.getDoubleExtra("multiplier", 3);
            duration=intent.getIntExtra("duration",1000);
        }
        else{
            if(className.equals("OriginChoiceActivity")){
                blocks=intent.getIntExtra("blocks",1);
                int rates=intent.getIntExtra("rates",1);

                switch(rates){
                    case 1:duration=1200;break;
                    case 2:duration=1000;break;
                    case 3:duration=800;break;
                    default:
                }

                musicPath = intent.getStringExtra("musicPath");
                musicSize = intent.getLongExtra("musicSize", 0);
                musicTime = intent.getLongExtra("musicTime", 0);


            }


        }
    }

    public void initMediaPlayer(){
        try {
            mediaPlayer.setDataSource(musicPath);
            mediaPlayer.prepare();//让mediaPlayer进入到准备状态

        }catch(Exception e){
            Toast.makeText(context,"no music file found",Toast.LENGTH_SHORT);
        }
    }

    public void getTime(){
        //File file=MP3ToWav.getWav(musicPath);
        //WaveFileReader reader = new WaveFileReader(file.getAbsolutePath());
        int[] data=null;
        if(musicPath.contains(".wav")){
            WaveFileReader reader = new WaveFileReader(musicPath);
            data= reader.getData()[0];

            if(blocks==1){//压缩，滑块少
                windowSize=15;multiplier=2;
            }
            else{//压缩，滑块多
                if(blocks==2){
                    windowSize=20;multiplier=5;
                }
            }

//            HandleData.handleData(data,allTime,16000,0,HandleData.WINDOW_1024,HandleData.WINDOW_SIZE_20,HandleData.MULTIPLIER_1_5);
            HandleData.handleData(data,allTime,16000,musicTime,window,windowSize,multiplier);
        }
        else if(musicPath.contains(".mp3")){
//            Mp3FileReader reader = new Mp3FileReader(musicPath);
//            allTime=reader.getAllTime();


            MyMp3FileReader reader = new MyMp3FileReader(musicPath);
            data=reader.getData();
//            Toast.makeText(context,musicSize+"",Toast.LENGTH_SHORT).show();//大于6MB
//            if(musicSize>6000000) {
            if(musicSize/musicTime>30) {//高音质

                if(blocks==1){//无损，滑块少
                    windowSize=10;multiplier=5;
                }
                else{//无损，滑块多
                    if(blocks==2){
                        windowSize=10;multiplier=4;
                    }
                }
//                HandleData.handleData(data,allTime,44100,0,HandleData.WINDOW_1024,HandleData.WINDOW_SIZE_20,HandleData.MULTIPLIER_1_5);
                HandleData.handleData(data, allTime, 24000, musicTime, window, windowSize,multiplier);
            }
            else {
                if(blocks==1){//压缩，滑块少
                    windowSize=15;multiplier=1;
                }
                else{//压缩，滑块多
                    if(blocks==2){
                        windowSize=20;multiplier=5;
                    }
                }
//                HandleData.handleData(data,allTime,16000,0,HandleData.WINDOW_1024,HandleData.WINDOW_SIZE_30,HandleData.MULTIPLIER_4);
                HandleData.handleData(data, allTime, 16000, musicTime, window, windowSize,multiplier);
            }
            System.out.println("data.length:"+data.length+" allTime.length:"+allTime.size()+" musicSize:"+musicSize);


        }
//        handleData(data);
        //file.delete();
    }
//    public void handleData(int[] data,int sampling){
//        HandleData.handleData(data,allTime,sampling,0);
//    }

    public void initButtons(){
        buttons.add(new ArrayList<EachButton>());//此处需要初始化！！！
        buttons.add(new ArrayList<EachButton>());
        int n=allTime.size();
        for(int i=0;i<n;i++) {
            int p=(int)(Math.random()*2);
            int x=X[p];
            EachButton eachButton = new EachButton(getApplicationContext(),
                    buttonWidth,buttonHeight,
                    x, startY, x, endY+microChange, allTime.get(i)-i*2, duration,p);//allTime.get(i)-i*2，处理一个button的时间大概为2ms
            eachButton.start(buttonConstraintLayout);
            buttons.get(p).add(eachButton);
//            getClass();
        }
        wholeScore=bestScore*n;

    }

    public void initBottom(){
        bottoms[0]=findViewById(R.id.bottom1);
        bottoms[1]=findViewById(R.id.bottom2);

        //for(int i=0;i<bottoms.length;i++){
            bottoms[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(buttons.get(0).size()!=0)
                    initToasts(0);
                }
            });
        bottoms[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttons.get(1).size()!=0)
                initToasts(1);
            }
        });
       // }

    }

    void initToasts(int i){

        ObjectAnimator.ofArgb(bottoms[i], "backgroundColor",
                Color.parseColor("#F3E77C"),
                Color.parseColor("#F8E097"))
                .setDuration(500)
                .start();//点击时滑块颜色变化
        //bottoms[i].setBackgroundColor(getResources().getColor(R.color.colorGoldYellow));
        //bottoms[i].setBackground(getResources().getColor(R.color.colorGoldYellow));

        EachButton button=null;
        try {
//            Toast.makeText(context,buttons.get(i).size()+"",Toast.LENGTH_SHORT).show();
            if (buttons.get(i).size() > 0)
                button = buttons.get(i).get(0);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        toast.cancel();//让上一个取消，不然新的toast覆盖不上去

        View toastView =LayoutInflater.from(context).inflate(R.layout.toast, null);//得到toast样式
        TextView textView=toastView.findViewById(R.id.textView);

        //最近滑块顶部与按钮顶部距离
        //        int d=(int)(bottoms[i].getY()-button.getY());
        int d=(int)(marginTop-button.getY());//不能用bottoms[i].getY(),底部按钮在linearLayout里面，得不到Y
        System.out.println("距离:"+d);

        //good效果，大于滑块一半（三分之一）长度但又触碰到了
        if((d>-buttonHeight-microChange&&d<-buttonHeight/3)||(d<bottomHeight+microChange&&d>bottomHeight-buttonHeight/3)) {
            goodTimes+=1;
            resultScore+=goodScore;
            scoreView.setText("Score:"+resultScore);

            textView.setText("good");
            buttonConstraintLayout.removeView(button);//从视图中移出滑块
            buttons.get(i).remove(0);//从此轨道的滑块数组中移出滑块，（因为每次点击事件取得是第一个滑块）
            button.state=false;
            button.animator.cancel();//这里要取消动画，不然虽然视图里的button消失了，但button对象还在，动画还会继续执行

            ObjectAnimator.ofArgb(textView, "textColor",
                    Color.parseColor("#ffffffff"),
                    Color.parseColor("#00ffffff"))
                    .setDuration(1000)
                    .start();//good的toast动画，下降消失效果
        }
        else {
            //best效果，小于滑块一半长度
            if(d<=bottomHeight-buttonHeight/3&&d>=-buttonHeight/3){
                bestTimes+=1;
                resultScore+=bestScore;
                scoreView.setText("Score:"+resultScore);

                textView.setText("best");
                //textView.setTextColor(getResources().getColor(R.color.colorLightBlue));
                buttonConstraintLayout.removeView(button);
                buttons.get(i).remove(0);
                button.state=false;
                button.animator.cancel();
                //button.viewRemoveButton(constraintLayout);

                ObjectAnimator.ofArgb(textView, "textColor",
                        Color.parseColor("#ffD9D919"),
                        Color.parseColor("#00D9D919"))
                        .setDuration(1000)
                        .start();//best的toast动画，下降消失效果
            }
            else {
                textView.setText("");

            }
        }

        toast=new Toast(context);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);

        toastView.animate().translationY(50)
                .setDuration(1000);
        toast.show();

        if(buttons.get(0).size()+buttons.get(1).size()==0){
            final Intent iintent=new Intent(GameActivity.this,ResultActivity.class);
            iintent.putExtra("wholeScore",wholeScore);
            iintent.putExtra("resultScore",resultScore);
            iintent.putExtra("bestTimes",bestTimes);
            iintent.putExtra("goodTimes",goodTimes);
            iintent.putExtra("missTimes",missTimes);
            TimerTask task = new TimerTask(){
                public void run(){
                    startActivity(iintent);
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 2000);

        }

    }

    void initStateButton(){
        final Button state=findViewById(R.id.state);

        state.setOnClickListener(new View.OnClickListener() {

//            String text=state.getText().toString();
            @Override
            public void onClick(View v) {
                if(stateMove){//true
                    Toast.makeText(getApplicationContext(),"暂停",Toast.LENGTH_SHORT).show();
                    state.setRotation(90);
                    state.setText(" △");
                    stateMove=false;
                    buttonsPause();
                }
                else{
                    if(!stateMove){
                        Toast.makeText(getApplicationContext(),"开始",Toast.LENGTH_SHORT).show();
                        state.setRotation(0);
                        state.setText("| |");
                        stateMove=true;
                        buttonsRestart();
                    }
                }
            }
        });
    }

    void buttonsPause(){
        if(mediaPlayer!=null){
            mediaPlayer.pause();
        }
//        nowTime=(int)Math.min(buttons.get(0).get(0).animator.getStartDelay(),
//                buttons.get(0).get(1).animator.getStartDelay());
        nowTime+=(int)(new Date().getTime()-dateHelp.getTime());//多次暂停时，dateHelp每次开始要更新，然后nowTime是每次的和

        if(buttons.get(0).size()+buttons.get(1).size()!=0){
            for(int i=0;i<2;i++){
                for(int j=0;j<buttons.get(i).size();j++){
                    buttons.get(i).get(j).animator.pause();
                    buttons.get(i).get(j).setVisibility(View.INVISIBLE);
                }
            }
        }
    }
    void buttonsRestart(){
        if(mediaPlayer!=null){
            mediaPlayer.start();
           // int time=mediaPlayer.getDuration();
        }

        if(buttons.get(0).size()+buttons.get(1).size()!=0){
            for(int i=0;i<2;i++){
                for(int j=0;j<buttons.get(i).size();j++){
                    buttons.get(i).get(j).setVisibility(View.VISIBLE);
                    ObjectAnimator animator=buttons.get(i).get(j).animator;
                    if(animator.getStartDelay()-nowTime>=0) {
                        animator.setStartDelay(animator.getStartDelay() - nowTime);
                        animator.start();
                    }
                    else {
                        buttons.get(i).remove(j);
                        animator.cancel();
                    }

                }
            }
        }
        dateHelp=new Date();//更新dateHelp，作为本次计时的起点
    }
    void buttonsDestroy(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();//中间强制退出时，要关掉播放器
            mediaPlayer.release();//释放播放器所占资源
        }

        if(buttons.get(0).size()+buttons.get(1).size()!=0){
            for(int i=0;i<2;i++){
                while(buttons.get(i).size()!=0){
                    buttons.get(i).get(0).animator.cancel();
                    if(buttons.get(i).size()!=0)
                        buttons.get(i).remove(0);
                }
            }
        }
        goodTimes=0;
        bestTimes=0;
        missTimes=0;

        toast.cancel();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        buttonsDestroy();
        activityState=false;
        ////
        Intent intent=new Intent("force_offline");
        sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        buttonsPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        System.out.println("restart");

        buttonsRestart();

    }

    @Override
    protected void onStop(){
        super.onStop();

        activityState=false;
    }

    public static ArrayList<ArrayList<EachButton>> getButtons(){
        return buttons;
    }
}
