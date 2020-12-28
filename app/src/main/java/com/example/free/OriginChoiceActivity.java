package com.example.free;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.free.Classes.BaseActivity;

public class OriginChoiceActivity extends BaseActivity {

    private RadioGroup roadCounts,blockCounts,rateCounts;
//    private RadioButton two,four,less,more,slow,middle,fast;

    private int roads=2;//2 4
    private int blocks=1;//1 2
    private int rates=2;//1 2 3

    String musicPath;
    long musicSize;
    long musicTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_origin_choice);

        setTitle("选择模式");

        roadCounts=findViewById(R.id.roadCounts);
        blockCounts=findViewById(R.id.blockCounts);
        rateCounts=findViewById(R.id.rateCounts);
//        two=findViewById(R.id.two);
//        four=findViewById(R.id.four);
//        less=findViewById(R.id.less);
//        more=findViewById(R.id.more);
//        slow=findViewById(R.id.slow);
//        middle=findViewById(R.id.middle);
//        fast=findViewById(R.id.fast);

        Intent intentFrom=getIntent();
        musicPath=intentFrom.getStringExtra("musicPath");
        musicSize=intentFrom.getLongExtra("musicSize",0);
        musicTime=intentFrom.getLongExtra("musicTime",musicTime);


        RadioGroupListener radioGroupListener=new RadioGroupListener();
        roadCounts.setOnCheckedChangeListener(radioGroupListener);
        blockCounts.setOnCheckedChangeListener(radioGroupListener);
        rateCounts.setOnCheckedChangeListener(radioGroupListener);


        Button start=findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ProgressBar progressBar=findViewById(R.id.progressBar);
//                progressBar.setVisibility(View.VISIBLE);

                switch(roads){
                    case 2:{
                        Intent intent=new Intent(getApplicationContext(),GameActivity.class);
                        intent.putExtra("className","OriginChoiceActivity");
//                        Toast.makeText(getApplicationContext(),roads+"",Toast.LENGTH_SHORT).show();
//                        System.out.println("******************"+ roads);
                        intent.putExtra("blocks",blocks);
                        intent.putExtra("rates",rates);
                        intent.putExtra("musicPath",musicPath);
                        intent.putExtra("musicSize", musicSize);
                        intent.putExtra("musicTime",musicTime);
                        startActivity(intent);
                        break;
                    }
                    case 4:{
                        Intent intent=new Intent(getApplicationContext(),FourGameActivity.class);
                        intent.putExtra("className","OriginChoiceActivity");
//                        Toast.makeText(getApplicationContext(),roads+"",Toast.LENGTH_SHORT).show();
//                        System.out.println("******************"+ roads);
                        intent.putExtra("blocks",blocks);
                        intent.putExtra("rates",rates);
                        intent.putExtra("musicPath",musicPath);
                        intent.putExtra("musicSize", musicSize);
                        intent.putExtra("musicTime",musicTime);
                        startActivity(intent);
                        break;
                    }
                    default:
                }

            }
        });




        Button advancedChoice=findViewById(R.id.advancedChoice);
        advancedChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ChoiceActivity.class);
                intent.putExtra("musicPath",musicPath);
                intent.putExtra("musicSize", musicSize);
                intent.putExtra("musicTime",musicTime);
                startActivity(intent);
            }
        });

    }

    class RadioGroupListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(group==roadCounts){
                switch (checkedId){
                    case R.id.two:
                        roads=2;break;
                    case R.id.four:
                        roads=4;break;
                    default:
                }
            }
            else{
                if(group==blockCounts){
                    switch (checkedId){
                        case R.id.less:
                            blocks=1;break;
                        case R.id.more:
                            blocks=2;break;
                        default:
                    }
                }
                else{//rateCounts
                    switch (checkedId){
                        case R.id.slow:
                            rates=1;break;
                        case R.id.middle:
                            rates=2;break;
                        case R.id.fast:
                            rates=3;break;
                        default:
                    }
                }
            }
        }
    }
}
