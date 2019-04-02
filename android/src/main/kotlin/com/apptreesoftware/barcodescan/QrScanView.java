package com.apptreesoftware.barcodescan;

import android.content.Context;
import android.graphics.Canvas;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Description:
 * Created by yangzhengwei on 2019/3/31.
 * Email:yzw5535@gmail.com
 */
public class QrScanView extends ZXingScannerView {
    public QrScanView(Context context) {
        super(context);
    }

    @Override
    protected IViewFinder createViewFinderView(Context context) {
        ViewFinderView viewFinderView = new MyViewFinderView(context);
        viewFinderView.setBorderColor(0xFFf2f9ff);
        viewFinderView.setLaserEnabled(false);
        viewFinderView.setBorderStrokeWidth(4);
        viewFinderView.setBorderLineLength(60);
        viewFinderView.setMaskColor(0x60000000);
        viewFinderView.setBorderCornerRounded(false);
        viewFinderView.setBorderCornerRadius(0);
        viewFinderView.setSquareViewFinder(true);
        viewFinderView.setViewFinderOffset(0);
        return viewFinderView;
    }

}
