package com.example.free.Mp3Handle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyMp3FileReader {
    private static Map<Integer, String> style = new HashMap<Integer, String>();

    ArrayList<String> labels=new ArrayList<>();

    String filename;

    private FileInputStream is=null;
    private BufferedInputStream bis=null;

    private SongInfo songInfo;
    private DataInfo dataInfo;

    private byte[] data;

    public MyMp3FileReader(String _filename){

        String[] mlabels={"AENC", "APIC", "COMM", "COMR", "ENCR", "EQUA", "ETCO", "GEOB", "GRID", "IPLS", "LINK", "MCDI", "MLLT", "OWNE", "PRIV", "PCNT", "POPM", "POSS", "RBUF", "RVAD", "RVRB", "SYLT", "SYTC", "TALB", "TBPM", "TCOM", "TCON", "TCOP", "TDAT", "TDLY", "TENC", "TEXT", "TFLT", "TIME", "TIT1", "TIT2", "TIT3", "TKEY", "TLAN", "TLEN", "TMED", "TOAL", "TOFN", "TOLY", "TOPE", "TORY", "TOWN", "TPE1", "TPE2", "TPE3", "TPE4", "TPOS", "TPUB", "TRCK", "TRDA", "TRSN", "TRSO", "TSIZ", "TSRC", "TSSE", "TYER", "TXXX", "UFID", "USER", "USLT", "WCOM", "WCOP", "WOAF", "WOAR", "WOAS", "WORS", "WPAY", "WPUB", "WXXX"};
        for(String s:mlabels){
            labels.add(s);
        }

        this.filename=_filename;
        File file=new File(filename);
        try{
            is=new FileInputStream(file);
            //bis=new BufferedInputStream(is);

            //dataInfo
            byte[] bufFront=new byte[10];
            is.read(bufFront, 0, 10);
            this.dataInfo = new DataInfo(bufFront);//标签头 版本号 标签大小之类的

            //headLabel
            byte[] headLabel = new byte[this.dataInfo.getSize()];
            is.read(headLabel, 0, headLabel.length);//往下读标签长度的字节，得到标签内容，赋值给headLabel
            int headLabelLength=this.ID3ByHeadList(headLabel);//得到标签信息（标题 作者等）return Map(name,data)

            //data
            data = new byte[(int) (file.length() - 128 - 10 - headLabelLength)];//总长度-结尾128-标签头10-标签,,数据不太符合
            //System.out.println("----------------------data.length:"+data.length+" headLabelLength:"+headLabelLength+" headLabel:"+headLabel.length);
            System.arraycopy(headLabel, headLabelLength, data, 0, headLabel.length-headLabelLength);
            is.read(data, headLabel.length-headLabelLength, data.length-(headLabel.length-headLabelLength));

            //songInfo
            byte[] bufLast=new byte[128];
            is.read(bufLast, 0, 128);//获取结尾歌曲信息
            this.songInfo = new SongInfo(bufLast);
            //System.out.println(" ********************** song tag:"+(char)bufLast[0]+(char)bufLast[1]+(char)bufLast[2]);


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private int ID3ByHeadList(byte[] buf) {//得到标签帧内容 标题 作者等
        /*TSSE软硬件编码格式
         *APIC图片
         *COMM注解评论 comments
         *TALB专辑
         *TIT2歌名 内容描述
         *TPE1作者
         *TPE2乐队成员
         *0000*/

        /*
         * 标签帧： | 数据名 | 数据长度 | 标记 | [真实数据] | .....
         */
        Map<String, String> map = new HashMap<String, String>();
        int length=0;
        int pix = 0;
        byte[] head;// 4  标识帧，说明其内容 TIT2表示内容为这首歌的标题，TPE1作者 TALB专辑 TRCK因规格是 TYER年代
        byte[] size;// 4 帧内容大小（只包含内容的大小）
        byte[] flag;// 2 存放标志 （c只读 i压缩 j加密等）
        int dataLeng = 0;
        byte[] dataBuf;// n

        int dataLengEauals0=0;

//        System.out.println("buf.length:" + buf.length);//230
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



            //System.out.print("\n 标识帧，说明其内容 TIT2表示内容为这首歌的标题，TPE1作者 TALB专辑 TRCK因规格是 TYER年代:");
//            for(int i=0;i<head.length;i++)
//                System.out.print(head[i]+" ");
            String headString=new String(head);
            if(!labels.contains(headString)){
                return length;
            }
            //System.out.print(new String(head));

//            System.out.print("\n 帧内容大小（只包含内容的大小）:");
//            for(int i=0;i<size.length;i++)
//                System.out.print(size[i]+" ");

//            System.out.print("\n 存放标志 （c只读 i压缩 j加密等）:");
//            for(int i=0;i<flag.length;i++)
//                System.out.print(flag[i]+" ");


            dataLeng = getSizeByByte(size);
//            System.out.println("--------dataLeng:"+dataLeng);

            if (dataLeng <= 0)
                return length;
//            System.out.println(dataLeng);
            dataBuf = new byte[dataLeng];
            dataLeng = dataLeng < buf.length - pix ? dataLeng : buf.length - pix;
            System.arraycopy(buf, pix, dataBuf, 0, dataLeng);

//            System.out.print("\n 标签帧内容:");
//            for(int i=0;i<dataBuf.length;i++)
//            	System.out.print(dataBuf[i]+" ");
//
            pix = pix + dataLeng;
            map.put(new String(head), new String(dataBuf));
//			System.out.println(new String(head) + " \t| " + new String(dataBuf) + " \t| " + pix);

            length=pix;
        }

        return length;

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

//            System.out.print("\n ID3:");
//            for(int i=0;i<temp.length;i++)
//            	System.out.print(temp[i]+" ");
//            System.out.print(new String(temp));

            temp = new byte[1];//版本号ID3V2.3 就记录3
            System.arraycopy(data, pos, temp, 0, 1);//得到data[pos,pos+1],即data[2,3]
            this.ver = new String(temp);
            pos = pos + temp.length;

//            System.out.print("\n 版本号ID3V2.3 就记录3:");
//            for(int i=0;i<temp.length;i++)
//            	System.out.print(temp[i]+" ");

            temp = new byte[1];//副版本号 0
            System.arraycopy(data, pos, temp, 0, 1);
            this.revision = new String(temp);
            pos = pos + temp.length;

//            System.out.print("\n 副版本号 0:");
//            for(int i=0;i<temp.length;i++)
//            	System.out.print(temp[i]+" ");

            temp = new byte[1];//存放标志的字节
            System.arraycopy(data, pos, temp, 0, 1);
            this.flag = new String(temp);
            pos = pos + temp.length;//pos=6

//            System.out.print("\n 存放标志的字节:");
//            for(int i=0;i<temp.length;i++)
//            	System.out.print(temp[i]+" ");

            temp = new byte[4];//标签大小，包括标签头的10 个字节和所有的标签帧的大小
            System.arraycopy(data, pos, temp, 0, 4);//data[5,9]
            this.size = MyMp3FileReader.getSizeByByte(temp) - 10;//去掉标签头的长度为所有标签帧的长度

//            System.out.print("\n 标签大小，包括标签头的10 个字节和所有的标签帧的大小:");
//            for(int i=0;i<temp.length;i++)
//            	System.out.print(temp[i]+" ");

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

//            System.out.print("\n TAG字符:");
//            for(int i=0;i<temp.length;i++)
//                System.out.print(temp[i]+" ");
//            System.out.print(new String(temp));

            if (!this.tag.equals("TAG")) {
                return;
            }

            temp = new byte[30];//歌名
            System.arraycopy(data, pos, temp, 0, 30);
            this.title = new String(temp);
            pos = pos + temp.length;

//            System.out.print("\n 歌名:");
//            for(int i=0;i<temp.length;i++)
//                System.out.print(temp[i]+" ");
//            System.out.print(new String(temp));

            temp = new byte[30];//作者
            System.arraycopy(data, pos, temp, 0, 30);
            this.artist = new String(temp);
            pos = pos + temp.length;

//            System.out.print("\n 作者:");
//            for(int i=0;i<temp.length;i++)
//                System.out.print(temp[i]+" ");
//            System.out.print(new String(temp));

            temp = new byte[30];//专辑名
            System.arraycopy(data, pos, temp, 0, 30);
            this.album = new String(temp);
            pos = pos + temp.length;

//            System.out.print("\n 专辑名:");
//            for(int i=0;i<temp.length;i++)
//                System.out.print(temp[i]+" ");
//            System.out.print(new String(temp));

            temp = new byte[4];//年份
            System.arraycopy(data, pos, temp, 0, 4);
            this.year = new String(temp);
            pos = pos + temp.length;

//            System.out.print("\n 年份:");
//            for(int i=0;i<temp.length;i++)
//                System.out.print(temp[i]+" ");
//            System.out.print(new String(temp));

            temp = new byte[28];//附注
            System.arraycopy(data, pos, temp, 0, 28);
            this.comment = new String(temp);
            pos = pos + temp.length;

//            System.out.print("\n 附注:");
//            for(int i=0;i<temp.length;i++)
//                System.out.print(temp[i]+" ");
//            System.out.print(new String(temp));

            temp = new byte[1];//保留位
            System.arraycopy(data, pos, temp, 0, 1);
            this.reserve = new String(temp);
            pos = pos + temp.length;

//            System.out.print("\n 保留位:");
//            for(int i=0;i<temp.length;i++)
//                System.out.print(temp[i]+" ");

            temp = new byte[1];//音轨号
            System.arraycopy(data, pos, temp, 0, 1);
            this.track = new String(temp);
            pos = pos + temp.length;

//            System.out.print("\n 音轨号:");
//            for(int i=0;i<temp.length;i++)
//                System.out.print(temp[i]+" ");

            temp = new byte[1];//MP3类型 147种
            System.arraycopy(data, pos, temp, 0, 1);
            this.genre = style.get(new Byte(temp[0]).intValue());

//            System.out.print("\n MP3类型 147种:");
//            for(int i=0;i<temp.length;i++)
//                System.out.print(temp[i]+" ");

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
//				r = r * temp[x];//System.out.println(temp[x]);}//1 86 71锛堢洿鎺ヤ綔涓篿nt杩愮畻浜嗭級

        int r = temp[3] & 0xFF | ((temp[2] << 8) & 0xFF00) | ((temp[1] << 16) & 0xFF0000) | ((temp[0] << 24) & 0xFF0000);

        //return num;
//		System.out.println(temp[0]+"  "+temp[1]+"  "+temp[2]+"  "+temp[3]+"  "+"   -----");
//		System.out.println(r+"     -----");
        return r;
    }


    public int[] getData(){
        int[] mdata=new int[data.length];
        for(int i=0;i<data.length;i++)
            mdata[i]=(int)data[i];
        return mdata;
    }



}
