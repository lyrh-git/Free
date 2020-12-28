package com.example.free.Classes;

public class Music {

    private String name="";
    private String path="";
    private long size=0;
    private long time=0;

    public Music(String _name,String _path,long _size,long _time){
        name=_name;
        path=_path;
        size=_size;
        time=_time;
    }

    public String getName(){
        return name;
    }

    public String getPath(){
        return path;
    }
    public long getSize(){
        return size;
    }
    public long getTime(){
        return time;
    }
}
