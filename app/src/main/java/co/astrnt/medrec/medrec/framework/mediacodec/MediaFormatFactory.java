package co.astrnt.medrec.medrec.framework.mediacodec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

/**
 * Created by hill on 7/10/17.
 */

public class MediaFormatFactory {
    public static MediaFormat createStandardFormat(int width,int height){
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", width,height);
        format.setInteger("color-format", 2130708361);
        format.setInteger("bitrate", calcBitRate(width,height));
        format.setInteger("frame-rate", 15);
        format.setInteger("rotation-degrees", 270);
        format.setInteger("i-frame-interval", 10);
        return format;
    }
    public static  int calcBitRate(int mWidth,int mHeight) {
        int bitrate = (int) (((BPP * 15.0f) * ((float) mWidth)) * ((float) mHeight));
        Log.e("FRAMERX"," "+bitrate);
        return bitrate;
    }
    static private float BPP = 0.25f;
}
