package com.example.free.Classes;

import java.util.ArrayList;
import java.util.List;

public class HandleData {

    public static final int WINDOW_256=256;
    public static final int WINDOW_1024=1024;
    public static final int WINDOW_2048=2048;
    public static final int WINDOW_SIZE_10=10;
    public static final int WINDOW_SIZE_20=20;//
    public static final int WINDOW_SIZE_30=30;
    public static final double MULTIPLIER_1=1f;
    public static final double MULTIPLIER_1_5=1.5f;
    public static final double MULTIPLIER_2=2f;
    public static final double MULTIPLIER_2_5=2.5f;
    public static final double MULTIPLIER_3=3f;//
    public static final double MULTIPLIER_3_5=3.5f;
    public static final double MULTIPLIER_4=4f;

    public static void handleData(int[] data,ArrayList<Integer> allTime,int sampling,long musicTime,int _window,int window_size,double multiplier){

        final int window=_window;//样本窗口越大，，可能超过阈值的越多
        final int THRESHOLD_WINDOW_SIZE=window_size;//阈值左右各取多少个样本窗口的光谱通量！！！！越慢的歌要的越少，size越大，块数越多
        final double MULTIPLIER=multiplier;//阈值的加权值！！！！越慢的歌要的越高（快的歌太高，短时间变得快，块数越少）
//        final double lean=0.95;



        double[] dataFFT;//频谱
        List<Double> spectralFlux = new ArrayList<>();//每个样本窗口的光谱通量
        List<Double> leanRate = new ArrayList<>();//每个样本窗口的光谱通量

        List<Double> threshold = new ArrayList<>( );//每个样本窗口的阈值
        List<Double> leanRateAverage=new ArrayList<>();//附近几个的平均斜率，如果它的斜率大于平均值，则为突然变化值


        int len = data.length;

        int m=len/window;//样本窗口数  //近似。。待定加一

        dataFFT=new double[len];
        double[] dataFFTVariation=new double[len-m];


        FFT fft=new FFT(window);//本地类

        double[] re=new double[window];//实部
        double[] im=new double[window];//虚部，为0

        double max=re[0];
        double min=re[0];

        for(int k=0;k<m;k++){
            //处理每个样本窗口
            for(int i=0;i<window;i++){
                re[i]=data[k*window+i];
                im[i]=0;
            }
            fft.fft(re, im);//每个样本窗口离散傅里叶变换后的fft,赋值到了re实部和im虚部里面

            double flux=0;//flux是样本窗口里本次采样频谱与上个频谱之差，所有频谱的差的正值和为该样本窗口的光谱通量
            for(int i=0;i<window;i++){
                dataFFT[k*window+i]=re[i];//把实部值添加到频谱数组里面

                if(i>0){
                    dataFFTVariation[k*window+i-1-k]=dataFFT[k*window+i]-dataFFT[k*window+i-1];
                    double value=dataFFTVariation[k*window+i-1-k];
                    flux+=value<0?0:value;
                    //flux+=value;
                }
            }
//            if(flux>max)max=flux;
//            if(flux<min)min=flux;

            spectralFlux.add( flux );
        }

//        spectralFlux.set(0,(spectralFlux.get(0)-min)/(max-min));
//        leanRate.add(0.0);//第一项
//        for(int i=1;i<spectralFlux.size();i++){
//            spectralFlux.set(i,(spectralFlux.get(i)-min)/(max-min));
//            leanRate.add(Math.abs(spectralFlux.get(i)-spectralFlux.get(i-1)));
//            System.out.print(leanRate.get(i)+" ");
////            if(leanRate.get(i)>0.01)
////                allTime.add(i*window/sampling*1000);
//        }
//        System.out.println();


        //阈值
        int delete=10;
        for(int i=0;i<spectralFlux.size()-delete;i++){//去除最后结尾异变的几个只
            int start = Math.max( 0, i - THRESHOLD_WINDOW_SIZE );//防止开头、结尾溢出
            int end = Math.min( spectralFlux.size()-delete - 1, i + THRESHOLD_WINDOW_SIZE );
            double mean = 0;
//            double meanLeanRate=0;
            for( int j = start; j <= end; j++ ) {
                mean += spectralFlux.get(j);
//                meanLeanRate+=leanRate.get(j);
            }
            mean /= (end - start);
//            meanLeanRate/=(end-start);
            threshold.add( mean * MULTIPLIER );//每个样本窗口与前后THRESHOLD_WINDOW_SIZE 10个样本窗口光谱通量的均值为该样本窗口的阈值
//            leanRateAverage.add(meanLeanRate);
//            System.out.print(leanRateAverage.get(i)+" ");
            if(mean<spectralFlux.get(i)){
                //allTime.add(i*window/sampling*1000);//音频总长t=data.length/16000,第i*window个采样点时间为i*window/data.length*t*1000(ms)
                int time=(int)(i*window/(len*1.0)*musicTime);
                if((allTime.size()>0&&(time-allTime.get(allTime.size()-1))>100)||allTime.size()<1)
                    allTime.add(time);//不知道为什么只有16的时候效果比较好..平均采样率16000
            }
        }
        for(int i=0;i<5;i++)
            allTime.remove(0);
        //System.out.println();

//        for(int i=1;i<leanRate.get(i);i++){
//            if(Math.abs(leanRate.get(i)-leanRate.get(i-1))>leanRate.get(i-1)*0.05)
//                allTime.add(i*window/sampling*1000);
//        }
//
//        for(int i=1;i<spectralFlux.get(i);i++){
//            if(Math.abs(spectralFlux.get(i)-spectralFlux.get(i-1))>Math.abs(spectralFlux.get(i-1))*0.1)
//                allTime.add(i*window/sampling*1000);
//        }
        //System.out.println("here is allTime length:"+allTime.size());

    }
}
