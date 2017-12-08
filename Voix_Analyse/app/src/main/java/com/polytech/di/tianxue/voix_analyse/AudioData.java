package com.polytech.di.tianxue.voix_analyse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 08/12/2017.
 */

public class AudioData {
    public static short maxAmplitude = 0;
    public static short minAmplitude = 0;
    public static long length;
    public static List<Short> data = new ArrayList<Short>();

    /*
    public static short getMaxAmplitude() {
        return maxAmplitude;
    }

    public static void setMaxAmplitude(short maxAmplitude) {
        AudioData.maxAmplitude = maxAmplitude;
    }

    public static short getMinAmplitude() {
        return minAmplitude;
    }

    public static void setMinAmplitude(short minAmplitude) {
        AudioData.minAmplitude = minAmplitude;
    }

    public static long getLength() {
        return length;
    }

    public static void setLength(long length) {
        AudioData.length = length;
    }

    public static List<Short> getData() {
        return data;
    }

    public static void setData(List<Short> data) {
        AudioData.data = data;
    }
    */
}
