package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.util.Log;

/**
 * Created by hill on 7/11/17.
 */

public class Utils {
    public static final MediaCodecInfo selectAudioCodec(final String mimeType) {


        MediaCodecInfo result = null;
        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        LOOP:	for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {	// skipp decoder
                continue;
            }
            final String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                 Log.i("CodecPick", "supportedType:" + codecInfo.getName() + ",MIME=" + types[j]);
                if (types[j].equalsIgnoreCase(mimeType)) {
                    if (result == null) {
                        result = codecInfo;
                        break LOOP;
                    }
                }
            }
        }
        return result;
    }
}
