package com.example.free.Classes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.free.ChoiceActivity;
import com.example.free.GameActivity;
import com.example.free.OriginChoiceActivity;
import com.example.free.R;

import java.util.List;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private List<Music> musicList;

    //构造函数别忘了
    public MusicAdapter(List<Music> _musicList) {
        musicList=_musicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item,parent,false);
        //得到每一项的样式view，不继承parent
        final ViewHolder viewHolder=new ViewHolder(view);
        final Context context=parent.getContext();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=viewHolder.getAdapterPosition();
                Music music=musicList.get(position);
                String musicPath=music.getPath();
                long musicSize=music.getSize();
                long musicTime=music.getTime();
                //Intent intent=new Intent(context,MusicActivity.class);
                //Intent intent=new Intent(context, ChoiceActivity.class);
                Intent intent=new Intent(context, OriginChoiceActivity.class);
                intent.putExtra("musicPath",musicPath);
                intent.putExtra("musicSize", musicSize);
                intent.putExtra("musicTime",musicTime);
                context.startActivity(intent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Music music=musicList.get(position);
        viewHolder.textView.setText(music.getName());
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        //ViewHolder在adapter新建时储存控件实例/样式，这样在后面就不用一直findViewById查找

        TextView textView;

        ViewHolder(View view){
            super(view);
            textView=view.findViewById(R.id.textView);
        }
    }

}
