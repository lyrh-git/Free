package com.example.free.Classes;

public class FFT {

    int n, m;

    // Lookup tables. Only need to recompute when size of FFT changes.
    double[] cos;
    double[] sin;

    public FFT(int n) {
        this.n = n;
        this.m = (int) (Math.log(n) / Math.log(2));//2的几次方

        // Make sure n is a power of 2
        if (n != (1 << m))
            throw new RuntimeException("FFT length must be power of 2");//2的幂次方

        // precompute tables
        cos = new double[n / 2];
        sin = new double[n / 2];

        for (int i = 0; i < n / 2; i++) {
            cos[i] = Math.cos(-2 * Math.PI * i / n);
            sin[i] = Math.sin(-2 * Math.PI * i / n);
        }

    }


    //x是实部，y是虚部
    public void fft(double[] x, double[] y) {
        int i, j, k, n1, n2, a;
        double c, s, t1, t2;

        //n=16;
        // Bit-reverse
        j = 0;
        n2 = n / 2;//8
        for (i = 1; i < n - 1; i++) {
            n1 = n2;//8
            while (j >= n1) {
                j = j - n1;//0 4 0 4 0
                n1 = n1 / 2;//4 4 4 4 4
            }
            j = j + n1;//8 4 12 8 4 12 8 4 12

            if (i < j) {// i j，1 8,2 4,3 12,  4 8,5 4,6 12,  7 8,8 4,9 12  ,,10 8,11 4,12 12,  13 8,14 4,
                // i j交换，1 8,2 4,3 12,  4 8, ,6 12,  7 8, ,9 12  ,, , , ,   , , ;     8, 4, 12, 1, 5, 3, 2, 7, 6, 10, 11, 9, 13, 14, 15, 16,
                t1 = x[i];
                x[i] = x[j];
                x[j] = t1;
                t1 = y[i];
                y[i] = y[j];
                y[j] = t1;
            }
        }

        // FFT
        n1 = 0;
        n2 = 1;
        //m=4
        for (i = 0; i < m; i++) {
            n1 = n2;//1
            n2 = n2 + n2;//2
            a = 0;

            for (j = 0; j < n1; j++) {
                c = cos[a];
                s = sin[a];
                a += 1 << (m - i - 1);

                for (k = j; k < n; k = k + n2) {
                    t1 = c * x[k + n1] - s * y[k + n1];
                    t2 = s * x[k + n1] + c * y[k + n1];
                    x[k + n1] = x[k] - t1;
                    y[k + n1] = y[k] - t2;
                    x[k] = x[k] + t1;
                    y[k] = y[k] + t2;
                }
            }
        }
    }

    //采集的信号虚部为0即可
    public static void main(String[] args){
        int N=16;
        FFT fft=new FFT(N);

        double[] re=new double[N];
        double[] im=new double[N];

        // Impulse
        re[0] = 1; im[0] = 0;
        for(int i=1; i<N; i++)
            re[i] = im[i] = 0;
        fft.fft(re, im);

        // Nyquist
        for(int i=0; i<N; i++) {
            re[i] = Math.pow(-1, i);
            im[i] = 0;
        }
        fft.fft(re, im);

        // Single sin
        for(int i=0; i<N; i++) {
            re[i] = Math.cos(2*Math.PI*i / N);
            im[i] = 0;
        }
        fft.fft(re, im);

        // Ramp
        for(int i=0; i<N; i++) {
            re[i] = i;
            im[i] = 0;
        }
        fft.fft(re, im);

        long time = System.currentTimeMillis();
        double iter = 30000;
        for(int i=0; i<iter; i++)
            fft.fft(re,im);
        time = System.currentTimeMillis() - time;
        System.out.println("Averaged " + (time/iter) + "ms per iteration");

        System.out.print("]\nRe: [");
        for(int i=0;i<re.length;i++)
            System.out.print(((int)(re[i]*1000)/1000.0) + " ");
        System.out.print("]\nIm: [");
        for(int i=0;i<im.length;i++)
            System.out.print(((int)(im[i]*1000)/1000.0) + " ");

    }

}
