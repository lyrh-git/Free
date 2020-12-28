package com.example.free.Mp3Handle;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.example.free.Classes.HandleData;
import com.example.free.Mp3Handle.FrameData;

//import iface.PrivateInfo;
//import iface.PublicInfo;

public class Mp3FileReader {
    private static Map<Integer, String> style = new HashMap<Integer, String>();

    private Mp3FileReader.SongInfo songInfo;
    private Mp3FileReader.DataInfo dataInfo;

    private List<FrameData> frameList;//数据内容
    byte[] data = null;
//    int[][] data1 = null;
    int dataLength = 0;
    int sampling = 44100;
    ArrayList<Integer> allTime=new ArrayList<>();
    //ArrayList<Integer> samplings=new ArrayList<>();
    //ArrayList<Integer> initTimes=new ArrayList<>();
    final int EACHHANDLE=1280;//byte，10240bit 1帧120*2byte，1920bit，十帧一处理

    /**
     * 主要分为开头音频信息，标签（标签头 标签内容组），数据（帧头，帧内容组），结尾歌曲信息
     **/

    public Mp3FileReader(String filename) {

        File file = new File(filename);
        byte[] bufLast = new byte[128];// 结尾128字节
        byte[] bufFront = new byte[10];// 开头10字节
        byte[] bufFrame = null;// 中间数据帧
        byte[] headLabel = null;// 标签帧(未知长度和帧数)
        try {
            FileInputStream is = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            try {
                System.out.println("here is label head");
                is.read(bufFront, 0, 10);
                this.dataInfo = new Mp3FileReader.DataInfo(bufFront);//标签头 版本号 标签大小之类的

                System.out.println("here is label inner");
                headLabel = new byte[this.dataInfo.getSize()];
                is.read(headLabel, 0, headLabel.length);//往下读标签长度的字节，得到标签内容，赋值给headLabel
                this.ID3ByHeadList(headLabel);//得到标签信息（标题 作者等）return Map(name,data)

                System.out.println("here is data");
                bufFrame = new byte[(int) (file.length() - 128 - 10 - headLabel.length)];//总长度-结尾128-标签头10-标签,,数据不太符合
                is.read(bufFrame, 0, bufFrame.length);
                this.analyseFrame(bufFrame);//一帧一帧读，赋值给frameList

                System.out.println("here is last");
                is.skip(file.length() - (128 + 10 + headLabel.length + bufFrame.length));//文本流跳过中间可能冗余的其他信息？？？
                is.read(bufLast, 0, 128);//获取结尾歌曲信息
                this.songInfo = new Mp3FileReader.SongInfo(bufLast);
                System.out.println("title:"+songInfo.title+" artist:"+songInfo.artist);

//                int[] dataResult=getData();
//                int n=dataResult.length/EACHHANDLE/8+1;
//                for(int i=0;i<n;i++){
//                    HandleData.handleData(dataResult.);
//                }



            } catch (Exception e) {
                System.out.println("文件读取错误：" + file.getAbsolutePath() + " 文件长度不足128。"+file.length());
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件路径：" + file.getAbsolutePath() + " 不存在。");
            // e.printStackTrace();
        }
    }

    class DataInfo  {//开头，音频信息，版本号 标签大小等
        /*
         * char Header[3]; 字符串 "ID3"
         *
         * char Ver; 版本号ID3V2.3 就记录3
         *
         * char Revision; 副版本号此版本记录为0
         *
         * char Flag; 存放标志的字节，这个版本只定义了三位，很少用到，可以忽略
         *
         * char Size[4]; 标签大小，除了标签头的10 个字节的标签帧的大小
         *
         *
         */
        //mp3格式第一个标签帧里面有封面图片信息
        private String id3;
        private String ver;
        private String revision;
        private String flag;
        private int size;

        public DataInfo(byte[] data) throws Exception {
            byte[] temp = null;
            if (data.length != 10) {
                System.out.println("数据不足10字节或者大于10字节！");
                throw new Exception();
            }
            int pos = 0;
            temp = new byte[3];//header 必须为ID3，否则认为标签不存在
            System.arraycopy(data, pos, temp, 0, 3);//得到data[pos,pos+3] (Object src,  int  srcPos,Object dest, int destPos,int length);
            this.id3 = new String(temp);
            pos = pos + temp.length;//pos=3

            temp = new byte[1];//版本号ID3V2.3 就记录3
            System.arraycopy(data, pos, temp, 0, 1);//得到data[pos,pos+1],即data[2,3]
            this.ver = new String(temp);
            pos = pos + temp.length;

            temp = new byte[1];//副版本号 0
            System.arraycopy(data, pos, temp, 0, 1);
            this.revision = new String(temp);
            pos = pos + temp.length;

            temp = new byte[1];//存放标志的字节
            System.arraycopy(data, pos, temp, 0, 1);
            this.flag = new String(temp);
            pos = pos + temp.length;//pos=6

            temp = new byte[4];//标签大小，包括标签头的10 个字节和所有的标签帧的大小
            System.arraycopy(data, pos, temp, 0, 4);//data[5,9]
            this.size = Mp3FileReader.getSizeByByte(temp) - 10;//去掉标签头的长度为所有标签帧的长度

        }

        public int getSize() {
            return this.size;
        }
    }

    class SongInfo {//结尾，存放歌曲信息，歌名 作者等
        private String tag;

        private String title;
        private String artist;
        private String album;
        private String year;
        private String comment;
        private String genre;

        private String reserve;
        private String track;

        public SongInfo(byte[] data) throws Exception {
            byte[] temp = null;
            if (data.length != 128) {
                System.out.println("数据不足128字节或者大于128字节！");
                throw new Exception();
            }
            int pos = 0;
            temp = new byte[3];//TAG字符
            System.arraycopy(data, pos, temp, 0, 3);
            this.tag = new String(temp);
            pos = pos + temp.length;

            if (!this.tag.equals("TAG")) {
                return;
            }

            temp = new byte[30];//歌名
            System.arraycopy(data, pos, temp, 0, 30);
            this.title = new String(temp);
            pos = pos + temp.length;

            temp = new byte[30];//作者
            System.arraycopy(data, pos, temp, 0, 30);
            this.artist = new String(temp);
            pos = pos + temp.length;

            temp = new byte[30];//专辑名
            System.arraycopy(data, pos, temp, 0, 30);
            this.album = new String(temp);
            pos = pos + temp.length;

            temp = new byte[4];//年份
            System.arraycopy(data, pos, temp, 0, 4);
            this.year = new String(temp);
            pos = pos + temp.length;

            temp = new byte[28];//附注
            System.arraycopy(data, pos, temp, 0, 28);
            this.comment = new String(temp);
            pos = pos + temp.length;

            temp = new byte[1];//保留位
            System.arraycopy(data, pos, temp, 0, 1);
            this.reserve = new String(temp);
            pos = pos + temp.length;

            temp = new byte[1];//音轨号
            System.arraycopy(data, pos, temp, 0, 1);
            this.track = new String(temp);
            pos = pos + temp.length;

            temp = new byte[1];//MP3类型 147种
            System.arraycopy(data, pos, temp, 0, 1);
            this.genre = style.get(new Byte(temp[0]).intValue());

        }

        public String getAlbum() {
            // TODO 自动生成方法存根
            return this.album;
        }

        public String getArtist() {
            // TODO 自动生成方法存根
            return this.artist;
        }

        public String getComment() {
            // TODO 自动生成方法存根
            return this.comment;
        }

        public String getGenre() {
            // TODO 自动生成方法存根
            return this.genre;
        }

        public String getTitle() {
            // TODO 自动生成方法存根
            return this.title;
        }

        public String getYaer() {
            // TODO 自动生成方法存根
            return this.year;
        }

    }

    private static int getSizeByByte(byte[] temp) {
//		int r=(int)(temp[0] & 0x7F) << 21| (int)(temp[1] & 0x7F) << 14|
//				 (int)(temp[2] & 0x7F) << 7| (int)(temp[3] & 0x7F);

//		int r=temp[0]*2^24+temp[1]*2^16+temp[2]*2^8+temp[3];

//		int r = 1;
//		if (temp[0] + temp[1] + temp[2] + temp[3] == 0)
//			return 0;
//		for (int x = 0; x < 4; x++)//
//			if (temp[x] != 0)//{
//				r = r * temp[x];//System.out.println(temp[x]);}//1 86 71（直接作为int运算了）

        int r = temp[3] & 0xFF | ((temp[2] << 8) & 0xFF00) | ((temp[1] << 16) & 0xFF0000) | ((temp[0] << 24) & 0xFF0000);

        //return num;
//		System.out.println(temp[0]+"  "+temp[1]+"  "+temp[2]+"  "+temp[3]+"  "+"   -----");
//		System.out.println(r+"     -----");
        return r;
    }

    private Map<String, String> ID3ByHeadList(byte[] buf) {//得到标签帧内容 标题 作者等
        /*
         * 标签帧： | 数据名 | 数据长度 | 标记 | [真实数据] | .....
         */
        Map<String, String> map = new HashMap<String, String>();
        int pix = 0;
        byte[] head;// 4  标识帧，说明其内容 TIT2表示内容为这首歌的标题，TPE1作者 TALB专辑 TRCK因规格是 TYER年代
        byte[] size;// 4 帧内容大小（只包含内容的大小）
        byte[] flag;// 2 存放标志 （c只读 i压缩 j加密等）
        int dataLeng = 0;
        byte[] dataBuf;// n

        System.out.println("buf.length:" + buf.length);//230
        for (; pix < buf.length; ) {//循环几组 标题组 作者组等
            head = new byte[4];
            size = new byte[4];
            flag = new byte[2];
            System.arraycopy(buf, pix, head, 0, 4);
            pix = pix + 4;
            System.arraycopy(buf, pix, size, 0, 4);
            pix = pix + 4;
            System.arraycopy(buf, pix, flag, 0, 2);
            pix = pix + 2;
            dataLeng = getSizeByByte(size);
            if (dataLeng <= 0)
                return map;
            System.out.println(dataLeng);
            dataBuf = new byte[dataLeng];
            dataLeng = dataLeng < buf.length - pix ? dataLeng : buf.length - pix;
            System.arraycopy(buf, pix, dataBuf, 0, dataLeng);
            pix = pix + dataLeng;
            map.put(new String(head), new String(dataBuf));
//			System.out.println(new String(head) + " \t| " + new String(dataBuf) + " \t| " + pix);

        }

        return map;

    }

    private void analyseFrame(byte[] frameByte) throws Exception {
        data = new byte[frameByte.length];
        int dataIndex = 0;
        frameList = new LinkedList<FrameData>();
        int pix = 0;
        int size = 0;
        byte[] frameHead;// 帧头 (四字节,各位bit包含 同步信息 CRC校验 版权 采样率 声道等信息)
        byte[] frameData;// 数据帧
        int countLength=0;
        int countSampling=0;
        int initTime=0;
        int initTimeAfter=0;
        //initTimes.add(0);

        for (; pix < frameByte.length; ) {//一帧一帧循环
            frameHead = new byte[4];//帧头信息，4字节
            System.arraycopy(frameByte, pix, frameHead, 0, 4);
            pix = pix + 4;
            if (pix >= frameByte.length)
                break;
//if(pix==4){
            FrameData frame = new FrameData(frameHead);//帧信息类  里面只具体解析了帧头的信息，setBitData(byte[] data)数据是要外部传的
            size = frame.getBitSize();//大小 数据长度

            if (pix == 4) this.sampling = frame.getSampling();
            this.dataLength += frame.getBitSize();


            countLength+=size;
            countSampling+=size*sampling;
            initTimeAfter+=(int)(size*8/(sampling/1000.0));

            if(countLength>= EACHHANDLE){
                byte[] countData=new byte[countLength];
                System.arraycopy(frameByte, pix, countData, 0, countLength);
                int[] countDataAfter=changeData(countData);
                HandleData.handleData(countDataAfter,allTime,countSampling/countLength,initTime, HandleData.WINDOW_1024,HandleData.WINDOW_SIZE_20,HandleData.MULTIPLIER_3);
                countLength=0;
                countSampling=0;
                initTime=initTimeAfter;
            }

            if (size <= 0)
                continue;
            // if(size<0) break;//是否注释
            frameData = new byte[size];
//			System.out.println("SIZE:" + size + "  获取源数组开始位置:" + pix + "  新数组大小:" + frameData.length + "  长度： "
//					+ frame.getBitSize() + "  总大小：" + frameByte.length);//SIZE:168  获取源数组开始位置:796001  新数组大小:168  长度： 168  总大小：796124
            //System.arraycopy(frameByte, pix, frameData, 0, frame.getBitSize());
            size = size < frameByte.length - pix ? size : frameByte.length - pix;
//            if(frame.getSampling()<0){
//                for(int f=0;f<size;f++)
//                    frameData[f]=0;
//            }
//            else {
                System.arraycopy(frameByte, pix, frameData, 0, size);
                System.arraycopy(frameByte, pix, data, dataIndex, size);
           // }
//            for(int f=0;f<size;f++)
//                System.out.print(data[f]+" ");
            pix = pix + size;
            dataIndex += size;
//			System.out.println("PIX:" + pix + "   SIZE:" + size);
            if (pix >= frameByte.length)
                break;
            frame.setBitData(frameData);//给frame对象赋值数据
            this.frameList.add(frame);

            System.out.println();
            // if (pix>500) break;
//}
        }
        System.out.println("frameByteSize:"+frameByte.length+" dataSize:"+data.length);
        System.out.println("frameListSize:" + frameList.size());
    }

    public int[] changeData(byte[] mdata){
        int[] dataResult=new int[mdata.length];
        for(int i=0;i<mdata.length;i++)
            dataResult[i]=(int)mdata[i];
        return dataResult;
    }

    public ArrayList<Integer> getAllTime(){
        return allTime;
    }

    public int getSampling(){
        return sampling;
    }

}
