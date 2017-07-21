package co.astrnt.medrec.medrec.tesst.rangebar.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by hill on 7/21/17.
 */

public class BackgroundBar {
    Paint mPaint;


    public BackgroundBar() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.argb(100, 0, 0, 0));
    }



    public void draw(Canvas mCanvas, float left,float top,float right,float bottom) {
        mCanvas.drawRect(left,top,right,bottom,mPaint);
    }
}
