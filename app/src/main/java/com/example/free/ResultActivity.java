package com.example.free;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.free.Classes.BaseActivity;

public class ResultActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        setTitle("得分");

//        Button forceOffline=findViewById(R.id.forceOffline);
//        forceOffline.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent("force_offline");
//                sendBroadcast(intent);
//
//            }
//        });

        Intent intent=getIntent();

        TextView wholeScore=findViewById(R.id.result_wholeScore);
        wholeScore.setText("WholeScore:"+intent.getIntExtra("wholeScore",0));
        TextView score=findViewById(R.id.result_score);
        score.setText("Score:"+intent.getIntExtra("resultScore",0));
        TextView bestTimes=findViewById(R.id.result_bestTimes);
        bestTimes.setText("Best:"+intent.getIntExtra("bestTimes",0));
        TextView goodTimes=findViewById(R.id.result_goodTimes);
        goodTimes.setText("Good:"+intent.getIntExtra("goodTimes",0));
        TextView missTimes=(findViewById(R.id.result_missTimes));
        missTimes.setText("Miss:"+intent.getIntExtra("missTimes",0));



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent=new Intent("force_offline");
        sendBroadcast(intent);
    }
}
