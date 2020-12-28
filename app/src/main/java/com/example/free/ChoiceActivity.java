package com.example.free;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.free.Classes.BaseActivity;

public class ChoiceActivity extends BaseActivity {


    String musicPath;
    long musicSize;
    long musicTime;


    EditText window;
    EditText window_size;
    EditText multiplier;
    EditText duration;
    EditText roads;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        setTitle("高级设置");

        Intent intent=getIntent();
        musicPath=intent.getStringExtra("musicPath");
        musicSize=intent.getLongExtra("musicSize",0);
        musicTime=intent.getLongExtra("musicTime",0);


        window=findViewById(R.id.window);
        window_size=findViewById(R.id.window_size);
        multiplier=findViewById(R.id.multiplier);
        duration=findViewById(R.id.duration);
        roads=findViewById(R.id.roads);

        Button click=findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ProgressBar progressBar=findViewById(R.id.progressBar1);
//                progressBar.setVisibility(View.VISIBLE);

                int windowInt=1024;
                int windowSizeInt=20;
                double multiplierDouble=1.5;
                int blockDuration=1000;
                int roadsCount=2;

                String s1=(window.getText()+"").trim();
                if(!s1.equals(""))
                    windowInt=Integer.parseInt(s1);

                String s2=(window_size.getText()+"").trim();
                if(!s2.equals(""))
                    windowSizeInt=Integer.parseInt(s2);

                String s3=(multiplier.getText()+"").trim();
                if(!s3.equals(""))
                    multiplierDouble=Double.parseDouble(s3);

                String s4=(duration.getText()+"").trim();
                if(!s4.equals(""))
                    blockDuration=Integer.parseInt(s4);

                String s5=(roads.getText()+"").trim();
                if(!s5.equals(""))
                    roadsCount=Integer.parseInt(s5);

                Intent intent1;
                if(roadsCount==4)
                    intent1=new Intent(getApplicationContext(),FourGameActivity.class);
                else
                    intent1=new Intent(getApplicationContext(),GameActivity.class);

                intent1.putExtra("className","ChoiceActivity");//getClass().getName()带有根底
//                Toast.makeText(getApplicationContext(),getClass().getName(),Toast.LENGTH_SHORT).show();
                intent1.putExtra("window",windowInt);
                intent1.putExtra("windowSize",windowSizeInt);
                intent1.putExtra("multiplier",multiplierDouble);
                intent1.putExtra("duration",blockDuration);
                intent1.putExtra("musicPath",musicPath);
                intent1.putExtra("musicSize", musicSize);
                intent1.putExtra("musicTime",musicTime);
                startActivity(intent1);


            }
        });
    }
}
