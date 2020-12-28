//package com.example.free;
//
//
//
//import java.io.File;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import javax.sound.sampled.AudioFileFormat;
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioInputStream;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.UnsupportedAudioFileException;
//
//
//
///*import org.json.JSONObject;
//
//import com.baidu.aip.speech.AipSpeech;*/
//
//public class MP3ToWav {
//
//    public static File byteToWav(URL sourceURL, String targetPath) {
//
//        File file=new File(targetPath);
//
//        try (final AudioInputStream sourceAIS = AudioSystem.getAudioInputStream(sourceURL)) {
//            //得到输入流
//
//            AudioFormat sourceFormat = sourceAIS.getFormat();//原格式
//
//            //需要再从mp3转mp3一次，再从mp3转wav，，直接从mp3转wav报错。。
//            // 设置MP3的语音格式,并设置16bit
//            AudioFormat mp3tFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sourceFormat.getSampleRate(), 16, sourceFormat.getChannels(), sourceFormat.getChannels() * 2, sourceFormat.getSampleRate(), false);
//            //sourceFormat.getSampleRate()每秒的样本数，16每个样本中的位数（两个字节），sourceFormat.getChannels()声道数，每帧的字节数，每秒的帧数，false是否以 big-endian 字节顺序存储单个样本中的数据
//
//            // 设置wav的音频格式
//            AudioFormat pcmFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, false);
//            try (
//                    // 先通过MP3转一次，使音频流能的格式完整，，mp3格式转mp3格式，得到完整mp3格式的输入流
//                    final AudioInputStream mp3AIS = AudioSystem.getAudioInputStream(mp3tFormat, sourceAIS);
//
//                    // 转成需要的流，mp3格式转wav格式，得到输入流
//                    final AudioInputStream pcmAIS = AudioSystem.getAudioInputStream(pcmFormat, mp3AIS);
//            ) {
//                // 根据路径生成wav文件，输入流转文件。
//                AudioSystem.write(pcmAIS, AudioFileFormat.Type.WAVE, file);
//            }
//
//            return file;
//        } catch (IOException e) {
//            //System.out.println("文件转换异常：" + e.getMessage());
//            return null;
//        } catch (UnsupportedAudioFileException e) {
//            //System.out.println("文件转换异常：" + e.getMessage());//could not get audio input stream from input stream
//
//            return null;
//        }
//    }
//
//
//    public static File getWav(String filePath) {
//        String fileURL = "file://"+filePath;//url为"file://"开头
//        String targetPath = "1.wav";
//        try {
//            return byteToWav(new URL(fileURL),targetPath);
//        } catch (MalformedURLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return null;
//
//    }
//}
//
