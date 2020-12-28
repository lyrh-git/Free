package com.example.free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Queue;

import javax.xml.datatype.Duration;

public class Activity1 extends AppCompatActivity {
//
//    //EachButton eachButton1=null;//每个轨道的按钮list都放在数据域里
//    Button button_bottom;
//    int height;
//    ConstraintLayout constraintLayout;
//    static Toast toast;
//
//    static ArrayList<EachButton> buttons=new ArrayList();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_1);
//
//        int width=getWindowManager().getDefaultDisplay().getWidth();
//        int height=getWindowManager().getDefaultDisplay().getHeight();
//        Button test1=findViewById(R.id.test_bottom1);
//        Button test2=findViewById(R.id.test_bottom2);
//        test1.setText(width+"");
//        test2.setText(height+"");
//    }
//
//        constraintLayout=findViewById(R.id.constraintLayout);
//        EachButton eachButton1=new EachButton(getApplicationContext(),200,0,200,1300,0,2000);
//        eachButton1.setText("here1");
//
//        height=eachButton1.height;
//
//        //Toast.makeText(Activity1.this,"button1",Toast.LENGTH_SHORT).show();
//        //linearLayout.addView(eachButton1);
//        eachButton1.start(constraintLayout);
//
////        eachButton1.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Toast.makeText(Activity1.this,eachButton1.getY()+"",Toast.LENGTH_SHORT).show();
////            }
////        });//button实际的布局位置没变，只是显示变了，点击原位置仍可以有事件响应
//
//        EachButton eachButton2=new EachButton(getApplicationContext(),400,0 ,400,1200,500,2000,0);
//        eachButton2.setText("here2");
//        //linearLayout.addView(eachButton2);
//        eachButton2.start(constraintLayout);
//
//        EachButton eachButton3=new EachButton(getApplicationContext(),600,0,600,1200,700,2000);
//        eachButton3.setText("here3");
//        //linearLayout.addView(eachButton3);
//        eachButton3.start(constraintLayout);
//
//        buttons.add(eachButton1);
//        buttons.add(eachButton2);
//        buttons.add(eachButton3);
//
//        Toast.makeText(getApplicationContext(),buttons.size()+"",Toast.LENGTH_SHORT).show();
//
//        toast=new Toast(getApplicationContext());
//        //toast.show();
//
//
//
//        button_bottom=findViewById(R.id.button_bottom);
//        button_bottom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EachButton button=null;
//                try {
//                    Toast.makeText(Activity1.this,buttons.size()+"",Toast.LENGTH_SHORT).show();
//                    if (buttons.size() > 0)
//                        button = buttons.get(0);
//                }
//                catch(Exception e){
//                    e.printStackTrace();
//                }
//
//                //Toast.makeText(Activity1.this,"here click",Toast.LENGTH_SHORT).show();
//                Log.d("testToast","here");
//                Context context=getApplicationContext();
//                View view =LayoutInflater.from(context).inflate(R.layout.toast, null);
//                //不然如果后面用setView(findViewById())，AppCompatActivity.findViewById()是无法识别的，会出错
//                /*LayoutInflater这个类的作用类似于findViewById()。
//                不同点是LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化；
//                而findViewById()是找xml布局文件下的具体widget控件(如Button、TextView等)。
//                 */
//
//                toast.cancel();//让上一个取消，不然新的toast放不上去
//
//
//
//
//                //float buttonY=getButtonY();
//                //int[] location=new int[2];
//                //eachButton1.getLocationOnScreen(location);
//                //eachButton1.getLocationOnScreen(location);
//                TextView textView=view.findViewById(R.id.textView);
//
//                /*
//                ObjectAnimator textAnimator=ObjectAnimator
//                        .ofFloat(textView,"translationY", textView.getY(), textView.getY()+300)
//                        .setDuration(1000);*/
//
//                int d=(int)(button_bottom.getY()-button.getY());
//                if((d>-height&&d<-height/2)||(d<height&&d>height/2)) {
//                    textView.setText("good");
//                    //textView.setTextColor(getResources().getColor(R.color.colorLightGreen));
//                    constraintLayout.removeView(button);
//                    buttons.remove(0);
//                    button.state=false;
//                    button.animator.cancel();//这里要取消动画，不然虽然视图里的button消失了，但button对象还在，动画还会继续执行
//                    //button.viewRemoveButton(constraintLayout);
//
//                    ObjectAnimator.ofArgb(textView, "textColor",
//                            Color.parseColor("#ff1B8A22"),
//                            Color.parseColor("#001B8A22"))
//                            .setDuration(1000)
//                            .start();
//                }
//                else {
//                    if(d<=height/2&&d>=-height/2){
//                        textView.setText("best");
//                        //textView.setTextColor(getResources().getColor(R.color.colorLightBlue));
//                        constraintLayout.removeView(button);
//                        buttons.remove(0);
//                        button.state=false;
//                        button.animator.cancel();
//                        //button.viewRemoveButton(constraintLayout);
//
//                        ObjectAnimator.ofArgb(textView, "textColor",
//                                Color.parseColor("#ff00bfff"),
//                                Color.parseColor("#0000bfff"))
//                                .setDuration(1000)
//                                .start();
//                    }
//                    else{
//                        textView.setText("");
//                    }
//                }
//
//
//                //textView.setText(location[1]+"  "+eachButton1.getY()+" "+button_bottom.getY());//getTop()得到的是控件距离父级的top的距离
//                toast=new Toast(context);
//                //LinearLayout toastView=(LinearLayout)toast.getView();
//                toast.setView(view);
//                Log.d("testToast","here1");
//                toast.setDuration(Toast.LENGTH_SHORT);
//
//                view.animate().translationY(50)
//                        .setDuration(1000);
//
//
//                toast.show();
//
//            }
//        });
//
//        /*
//        EachButton eachButton4=new EachButton(getApplicationContext(),300,0,300,500,500,3000);
//        eachButton3.setText("here3");
//        //linearLayout.addView(eachButton3);
//        //eachButton3.start(linearLayout);
//
//        new Handler(new Handler.Callback() {
//            //不能动态设置开始时间
//            @Override
//            public boolean handleMessage(@NonNull Message msg) {
//                findViewById(R.id.textView).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),+R.anim.anim));
//                return false;
//            }
//        }).sendEmptyMessageDelayed(0,2000);*/
//    }
//
////    public float getButtonY(){
////        return eachButton1.getTranslationY();
////    }

}
