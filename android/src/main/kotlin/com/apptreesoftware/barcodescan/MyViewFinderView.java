package com.apptreesoftware.barcodescan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import me.dm7.barcodescanner.core.ViewFinderView;

/**
 * Description:
 * Created by yangzhengwei on 2019/3/31.
 * Email:yzw5535@gmail.com
 */
public class MyViewFinderView extends ViewFinderView {
    private Paint mTextPaint;

    public MyViewFinderView(Context context) {
        super(context);
        mTextPaint = new Paint();
        mTextPaint.setColor(0xFFf2f9ff);
        mTextPaint.setTextSize(39);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        int bottom = getFramingRect().bottom;
        int left = getFramingRect().left;
        int right = getFramingRect().right;
        canvas.drawText("将二维码放入框内，即可自动扫描", (left + right) / 2, bottom + 60, mTextPaint);
    }
}
