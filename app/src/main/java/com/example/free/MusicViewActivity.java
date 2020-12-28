package com.example.free;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import com.example.free.Classes.BaseActivity;
import com.example.free.Classes.Music;
import com.example.free.Classes.MusicAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicViewActivity extends BaseActivity {

    List<Music> musicList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_view);

        setTitle("选择歌曲");

        initMusics();

        RecyclerView recyclerView=findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);//线性布局管理器，可自行设置布局
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        MusicAdapter musicAdapter=new MusicAdapter(musicList);
        recyclerView.setAdapter(musicAdapter);



    }
    public void initMusics() {
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED){
//            //申请权限，名字与manifest里面的一致，1为请求码;需要重写onRequestPermissionsResult的得到申请权限的回调函数
//            ActivityCompat.requestPermissions(this,new String[]{
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1
//            );
//            //这里是需要先把文件读出来，再处理
//        }
        Cursor cursor = null;
        try {
            //这里是利用了数据库，不用从sd卡里过滤目录
            cursor = getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    , null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC);//这是查外存的;

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    System.out.println(cursor);
                    String name=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    //String MediaStore.Audio.Media.ARTIST;String xx.DATA;Int xx.DURATION;Long xx.SIZE
                    String path=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    long size=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    long time=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    System.out.println("#############################"+name);
                    System.out.println("#############################"+path);
                    if(name!=null)
                        if(name.contains(".wav")||name.contains(".mp3"))
                            musicList.add(new Music(name,path,size,time));
//                    if(name.contains("Lov")||name.contains("克罗")||name.contains("万神纪"))
//                        System.out.println("name:"+name+" size:"+size+" time:"+time);
                }
            }
            else
                System.out.println("********************cursor is null");
            Collections.reverse(musicList);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }



    /*
    public void initMusics(){
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED){
//            //申请权限，名字与manifest里面的一致，1为请求码;需要重写onRequestPermissionsResult的得到申请权限的回调函数
//            ActivityCompat.requestPermissions(this,new String[]{
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1
//            );
//            //这里是需要先把文件读出来，再处理
//        }
        //Cursor cursor=null;
        try{
            //cursor.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
            //File file = new File(Environment.getExternalStorageDirectory(),"*.mp3");

//            List<File> mFiles=new ArrayList<>();
//            File root = Environment.getExternalStorageDirectory().getAbsoluteFile();
//            Toast.makeText(getApplicationContext(),root.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//            File files[] = root.listFiles();
//            if (files != null) {
//                for (File f : files) {
//                    if (f.isDirectory()) {
//                        // getAllFiles(f);
//                    } else {
//                        if (f.getAbsolutePath().contains(".wav")) {
//                            mFiles.add(f);
//                        }else if (f.getAbsolutePath().contains(".ogg")){
//                            mFiles.add(f);
//                        }else if (f.getAbsolutePath().contains(".mp3")){
//                            mFiles.add(f);
//                        }
//                    }
//                }
//            }
            List<File> mFiles=getMusicFiles();
            for(int i=0;i<mFiles.size();i++){
                File file=mFiles.get(i);
                musicList.add(new Music(file.getName(),file.getAbsolutePath()));
            }
            musicList.add(new Music("name","path"));

//            String fileName = "file://" + mFiles.get(mIndex).getAbsolutePath();
//            mPlayer.setDataSource(String.valueOf(Uri.parse(fileName)));

        }catch(Exception e){
            e.printStackTrace();
        }finally {
//            if(cursor!=null)
//                cursor.close();
        }

    }

    public List<File> getMusicFiles(){
        List<File> mFiles=new ArrayList<>();
        File root=new File("/mnt/sdcard");
//        File root = Environment.getExternalStorageDirectory().getAbsoluteFile();
//        Toast.makeText(getApplicationContext(),root.getAbsolutePath(), Toast.LENGTH_SHORT).show();//"/storage/emulated/0/:
        //getChildFiles(mFiles,root);
        //return mFiles;
        return getChildFiles(mFiles,root);
    }
    public List<File> getChildFiles(List<File> mFiles,File file){
        File files[] = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    //if(mFiles.size()<10)
                    getChildFiles(mFiles,f);
                } else {
//                    if (f.getAbsolutePath().contains(".wav")) {
//                        mFiles.add(f);
//                    }else if (f.getAbsolutePath().contains(".ogg")){
//                        mFiles.add(f);
//                    }else
                        if (f.getAbsolutePath().contains(".mp3")){
                        mFiles.add(f);
                    }
                   // mFiles.add(new File(mFiles.size()+""));
                }
            }
        }
        Toast.makeText(getApplicationContext(),mFiles.size()+"",Toast.LENGTH_SHORT).show();
        return mFiles;
    }
*/


}
