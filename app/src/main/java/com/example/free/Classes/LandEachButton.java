package com.example.free.Classes;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.free.FourGameActivity;
import com.example.free.R;
import com.example.free.ResultActivity;

import java.util.Timer;
import java.util.TimerTask;


public class LandEachButton extends AppCompatButton {
    int fromX=0;
    int fromY=0;
    int toX=0;
    int toY=0;
    int startTime=0;
    int time=0;

    int width=240;//px
    int height=120;

//    final int endWidth=240;//px
//    final int endHeight=120;

    ConstraintLayout mlayout;
    Context mcontext;

    public ObjectAnimator animator;

    public boolean state=true;//按钮的状态，是否还存在于视图中

    int path=0;//轨道




    public LandEachButton(Context context,int _width,int _height,int fx,int fy,int tox,int toy,int st,int t,int p){
        super(context);

        this.fromX=fx;
        this.fromY=fy;
        this.toX=tox;
        this.toY=toy;
        this.startTime=st;
        this.time=t;

        this.path=p;

        width=_width;
        height=_height;

        mcontext=context;

        this.setBackgroundColor(0xaaF8E097);
//        this.layout(fx,fy,fx+width,fy+height);
        this.setWidth(width);
        this.setHeight(height);

        //this.setWidth(80*441/160);
        //this.setHeight(40*441/160);//40dp*441ppi/160 px //setWidth()和后面getX()得到的也都是像素pixels，所以这里也用像素


        //this.start();


    }

    //继承的已有setVisibility(0)方法,getTranslationX()，getX()方法，

    public void start(ConstraintLayout layout){
//
//        TranslateAnimation ta=new TranslateAnimation(fromX,toX,fromY,toY);
//        ta.setDuration(time);//动画时长 ms
//        ta.setStartOffset(startTime);//延时
//        //ta.setFillBefore(false);//动画结束时停在第一帧
//        //ta.setFillAfter(false);
//      /* final LinearLayout mlayout=layout;
//        EachButton eachButton=this;*/
//        //setStateValue(this,layout);
//        //this.setVisibility(INVISIBLE);
//        LinearInterpolator lin = new LinearInterpolator();//匀速运动
//        ta.setInterpolator(lin);
//
//        layout=mlayout;
//
//
//        //setState(this,ta);
//        setStateValue(this);
//        layout.addView(this);//把addView放到onAnimationStart里面加载不出来
//
//
//        this.startAnimation(ta);//自动启动动画
//        //layout.removeView(this);

        //translateAnimation是表面呈现的移动，但布局没变，animator会随着变，方便获得移动中的位置
        mlayout=layout;
        //layout.addView(this);
        //ObjectAnimator.ofFloat(this, "translationY", fromY, toY).setDuration(time).setStartDelay(startTime);
        Path path=new Path();
        path.moveTo(fromX,fromY);//开始
        path.lineTo(toX,toY);//结束
        animator=ObjectAnimator.ofFloat(this,
                Property.of(LandEachButton.class,Float.class,"translationX"),
                Property.of(LandEachButton.class,Float.class,"translationY"),
                path);
        animator.setDuration(time);
        animator.setStartDelay(startTime);

        //animator.pause();

        LinearInterpolator lin = new LinearInterpolator();//匀速运动
        animator.setInterpolator(lin);

        final LandEachButton eachButton=this;
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mlayout.addView(eachButton);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mlayout.removeView(eachButton);

                if(eachButton.state) {
                    View view = LayoutInflater.from(mcontext).inflate(R.layout.toast_land, null);
                    TextView textView = view.findViewById(R.id.textView);
                    textView.setText("miss");
                    //textView.setTextColor(Color.parseColor("#909090"));
                    //textView.setTextColor(getResources().getColor(R.color.colorGray));
                    //Toast toast=new Toast(mcontext);
                    Toast toast = FourGameActivity.toast;//方便被activity1里面新的点击替代，如果是新的可能替代不了
                    toast.setView(view);
                    toast.setDuration(Toast.LENGTH_SHORT);

                    /*
                    ObjectAnimator textAnimator=ObjectAnimator
                            .ofFloat(textView,"translationY", textView.getY(), textView.getY()+200)
                            .setDuration(1000);*/
                    ObjectAnimator.ofArgb(textView, "textColor",
                            Color.parseColor("#ff333333"),
                            Color.parseColor("#00333333"))
                            .setDuration(1000)
                            .start();
                    toast.show();

                    FourGameActivity.buttons.get(eachButton.path).remove(0);
                    FourGameActivity.missTimes+=1;

                    if((FourGameActivity.buttons.get(0).size()+FourGameActivity.buttons.get(1).size()+
                            FourGameActivity.buttons.get(2).size()+FourGameActivity.buttons.get(3).size()==0)&&
                            FourGameActivity.activityState){
                        final Intent iintent = new Intent(mcontext, ResultActivity.class);
                        iintent.putExtra("wholeScore",FourGameActivity.wholeScore);
                        iintent.putExtra("resultScore",FourGameActivity.resultScore);
                        iintent.putExtra("bestTimes",FourGameActivity.bestTimes);
                        iintent.putExtra("goodTimes",FourGameActivity.goodTimes);
                        iintent.putExtra("missTimes",FourGameActivity.missTimes);
                        TimerTask task = new TimerTask(){
                            public void run(){
                                mcontext.startActivity(iintent);
                            }
                        };
                        Timer timer = new Timer();
                        timer.schedule(task, 2000);

                    }

                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                eachButton.setWidth(width+(int)animation.getCurrentPlayTime()/time*(endWidth-width));
//                eachButton.setWidth(height+(int)animation.getCurrentPlayTime()/time*(endHeight-height));
//            }
//        });
        animator.start();
   /*
        layout=mlayout;
        layout.addView(this);
        final EachButton eachButton=this;
        this.animate().translationY(toY)
                .setDuration(time).setStartDelay(startTime)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        layout.removeView(eachButton);
                    }
                });
*/
    }



    /*
    public void viewRemoveButton(ConstraintLayout mlayout){
        mlayout.removeView(this);
    }
*/

/*
    public void setState(final EachButton eachButton,TranslateAnimation ta){

        ta.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                //layout.addView(eachButton);
                Log.d("state","here");
                //eachButton.setVisibility(VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Log.d("state","there");
                //eachButton.setVisibility(INVISIBLE);
                mlayout.removeView(eachButton);
                eachButton.state=false;

                View view =LayoutInflater.from(mcontext).inflate(R.layout.toast, null);
                TextView textView=view.findViewById(R.id.textView);
                textView.setText("miss");
                //textView.setTextColor(Color.parseColor("#909090"));
                textView.setTextColor(getResources().getColor(R.color.colorGray));
                Toast toast=new Toast(mcontext);
                toast.setView(view);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();

                Activity1.buttons.remove(0);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        this.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Toast.makeText(getContext(),top+"",Toast.LENGTH_SHORT).show();
            }
        });


    }
*/

/*
    public void setStateValue(final EachButton eachButton){


        ValueAnimator valueAnimator= ValueAnimator.ofInt(eachButton.fromY,eachButton.toY);
        valueAnimator.setDuration(eachButton.time);
        valueAnimator.setStartDelay(eachButton.startTime);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (int)animation.getAnimatedValue();
                //layout(left,top,right,bottom)
                eachButton.layout(eachButton.fromX,curValue,eachButton.fromX+150,curValue+100);
                //if(curValue==1)
                    //linearLayout.addView(eachButton);
                    //eachButton.setVisibility(VISIBLE);
                if(curValue==eachButton.toY)
                    //eachButton.setVisibility(INVISIBLE);
                    mlayout.removeView(eachButton);
            }
        });
        valueAnimator.start();

    }
*/




}
