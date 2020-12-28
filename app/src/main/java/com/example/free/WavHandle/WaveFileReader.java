package com.example.free.WavHandle;

import java.io.*;

public class WaveFileReader {
    private String filename = null;
    private int[][] data = null;

    private int len = 0;

    private String chunkdescriptor = null;
    static private int lenchunkdescriptor = 4;

    private long chunksize = 0;
    static private int lenchunksize = 4;

    private String waveflag = null;
    static private int lenwaveflag = 4;


    private String fmtubchunk = null;
    static private int lenfmtubchunk = 4;

    private long subchunk1size = 0;
    static private int lensubchunk1size = 4;

    private int audioformat = 0;
    static private int lenaudioformat = 2;

    private int numchannels = 0;
    static private int lennumchannels = 2;

    private long samplerate = 0;
    static private int lensamplerate = 2;

    private long byterate = 0;
    static private int lenbyterate = 4;

    private int blockalign = 0;
    static private int lenblockling = 2;

    private int bitspersample = 0;
    static private int lenbitspersample = 2;

    private String datasubchunk = null;
    static private int lendatasubchunk = 4;

    private long subchunk2size = 0;
    static private int lensubchunk2size = 4;


    private FileInputStream fis = null;
    private BufferedInputStream bis = null;

    private boolean issuccess = false;

    public WaveFileReader(String filename) {

        this.initReader(filename);
    }

    // 判断是否创建wav读取器成功
    public boolean isSuccess() {
        return issuccess;
    }

    // 获取每个采样的编码长度，8bit或者16bit
    public int getBitPerSample(){
        return this.bitspersample;
    }

    // 获取采样率
    public long getSampleRate(){
        return this.samplerate;
    }

    // 获取声道个数，1代表单声道 2代表立体声
    public int getNumChannels(){
        return this.numchannels;
    }

    // 获取数据长度，也就是一共采样多少个
    public int getDataLen(){
        return this.len;
    }

    // 获取数据
    // 数据是一个二维数组，[n][m]代表第n个声道的第m个采样值
    public int[][] getData(){
        return this.data;
    }

    private void initReader(String filename){
        this.filename = filename;

        try {
            fis = new FileInputStream(this.filename);//读取文件
            bis = new BufferedInputStream(fis);//转为二进制流

            //块描述符，前四个字节，判断是不是wave文件，wave数据块chunk由两个子数据块subchunk组成
            this.chunkdescriptor = readString(lenchunkdescriptor);//lenchunkdescriptor=4;
            //System.out.print(chunkdescriptor+" ");//"RIFF"
            if(!chunkdescriptor.endsWith("RIFF"))
                throw new IllegalArgumentException("RIFF miss, " + filename + " is not a wave file.");

            //readLong()都是四字节四字节读的，readInt()都是两字节两字节读的，，这与wave文件格式有关
            this.chunksize = readLong();//总长度
            //System.out.print(chunksize+" ");//8398
            this.waveflag = readString(lenwaveflag);//lenwaveflag=4;
            //System.out.print(waveflag+" ");//"WAVE"
            if(!waveflag.endsWith("WAVE"))
                throw new IllegalArgumentException("WAVE miss, " + filename + " is not a wave file.");


            this.fmtubchunk = readString(lenfmtubchunk);
            //System.out.print(fmtubchunk+" ");//"fmt "
            if(!fmtubchunk.endsWith("fmt "))
                throw new IllegalArgumentException("fmt miss, " + filename + " is not a wave file.");

            this.subchunk1size = readLong();//第一个子数据块说明数据的结构，子数据块长度为16字节
            //System.out.print(subchunk1size+" ");//16

            /**fmt-subchunk*/
            this.audioformat = readInt();//格式种类,值为1时为线性PCM编码
            //System.out.print(audioformat+" ");//1
            this.numchannels = readInt();//声道数，1为单声道，2为双声道
            //System.out.print(numchannels+" ");//1
            this.samplerate = readLong();//采样率，一秒钟采样次数
            //System.out.print(samplerate+" ");//16000
            this.byterate = readLong();//字节率，波形数据传输速率
            //System.out.print(byterate+" ");//32000
            this.blockalign = readInt();//data数据块长度（声道数*采样精度/8），这里为2，是16位单声道，数据块为 左声道低，左声道高
            //System.out.print(blockalign+" ");//2
            this.bitspersample = readInt();//采样精度，一次采样的位数，16bits，2bytes，PCM位宽
            //System.out.print(bitspersample+" ");//16

            /**data-subchunk*/
            this.datasubchunk = readString(lendatasubchunk);
            //System.out.print(datasubchunk+" ");//"data"
            if(!datasubchunk.endsWith("data"))
                throw new IllegalArgumentException("data miss, " + filename + " is not a wave file.");
            this.subchunk2size = readLong();
            //System.out.println(subchunk2size+" ");//8362字节

            this.len = (int)(this.subchunk2size/(this.bitspersample/8)/this.numchannels);//这里去掉了bitspersample，所以在采集的时候要按照8 16选择不同的读取长度。

            this.data = new int[this.numchannels][this.len];
			/*音频数据传输编码格式是 8位双声道时 左声道 右声道 左声道 右声道，16位双声道时 左声道低 左声道高 右声道低 右声道高  （左右声道交叉）
			  8位单声道 左声道 左声道  16位单声道 左声道低 左声道高 左声道低 左声道高 */


            for(int i=0; i<this.len; ++i){
                for(int n=0; n<this.numchannels; ++n){
                    if(this.bitspersample == 8){
                        this.data[n][i] = bis.read();
                        //System.out.print(" "+this.data[n][i]);
                    }
                    else if(this.bitspersample == 16){//这里是16位单声道，所以每次采两个数据块（左声道低、左声道高）
                        this.data[n][i] = this.readInt();//两位
                        //System.out.print(this.data[n][i]+" ");
                    }
                }
            }
            //System.out.println();
            issuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            try{
                if(bis != null)
                    bis.close();
                if(fis != null)
                    fis.close();
            }
            catch(Exception e1){
                e1.printStackTrace();
            }
        }
    }

    private String readString(int len){
        byte[] buf = new byte[len];
        try {
            //bis.read(buf)是把bis一字节一字节读取，存入buf中，buf从空转为有内容，return len
            if(bis.read(buf)!=len)
                throw new IOException("no more data!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buf);//Stirng构造函数内部把ascii码转为字符再合为字符串
    }

    private int readInt(){
        byte[] buf = new byte[2];
        int res = 0;
        try {
            if(bis.read(buf)!=2)//一般bis.read(byte[])返回的是length长度（有字节的非空的长度）,确认读了两个字节
                throw new IOException("no more data!!!");
            res = (buf[0]&0x000000FF) | (((int)buf[1])<<8);//buf[0]&0x000000FF(FF 11111111)后八位，没作用啊？等于buf[0]它本身，，
            //若两位的 左声道低音 左声道高音，高音进位之和。。。所以按两个字节或者四个字节传输，后面的字节都要陆续进位？？
            //System.out.println("\nhere"+buf[0]+" "+buf[1]+" "+res);//94 -1 -162
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /* &与	10&01=00
     * |或	10&01=11
     * ^亦或	10^11=01 （相同为0不相同为1）
     * ~非
     * <<左移  11<<2=1100 相当于十进制中的 *2^n
     * >>右移  11>>1=1
     * */

    private long readLong(){
        long res = 0;
        try {
            long[] l = new long[4];
            for(int i=0; i<4; ++i){
                l[i] = bis.read();
                //System.out.print("'"+l[i]);//206 32 0 0
                if(l[i]==-1){
                    throw new IOException("no more data!!!");
                }
            }
            res = l[0] | (l[1]<<8) | (l[2]<<16) | (l[3]<<24);//8398    206+32*2^8
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private byte[] readBytes(int len){
        byte[] buf = new byte[len];
        try {
            if(bis.read(buf)!=len)
                throw new IOException("no more data!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf;
    }
}
