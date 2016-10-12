/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;

public class BitmapsProvider {

    //GroundOverlay constants
    public static final int OVERLAY_TEXT_COLOR = Color.WHITE;
    public static final int OVERLAY_CIRCLE_COLOR = 0xFF1565C0;
    public static final float OVERLAY_TEXT_TO_AREA_RATIO = 0.33f;

    //Marker constants
    public static final int MARKER_TEXT_COLOR = Color.WHITE;
    public static final int MARKER_DPI_WIDTH = 30;
    public static final int MARKER_DPI_HEIGHT = 60;
    public static final int MARKER_DPI_TEXT_SIZE = 16;
    public static final int MARKER_DPI_STROKE_WIDTH = 1;

    private TextPaint overlayTextPaint, markerTextPaint;
    private Paint overlayPaint, markerPaint, markerStrokePaint;
    private int areaSize, markerHeight, markerWidth;

    public BitmapsProvider(Context context) {
        float density = context.getResources().getDisplayMetrics().density;

        overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        overlayTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        overlayTextPaint.setColor(OVERLAY_TEXT_COLOR);
        overlayTextPaint.setShadowLayer(3, 2, 2, 0x50000000);

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        markerStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerStrokePaint.setStyle(Paint.Style.STROKE);
        markerStrokePaint.setStrokeWidth(density * MARKER_DPI_STROKE_WIDTH);
        markerStrokePaint.setColor(0x30000000);

        markerTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        markerTextPaint.setTextSize(MARKER_DPI_TEXT_SIZE * density);
        markerTextPaint.setColor(MARKER_TEXT_COLOR);
        markerTextPaint.setShadowLayer(2, 2, 2, 0x30000000);

        markerHeight = (int) (MARKER_DPI_HEIGHT * density);
        markerWidth = (int) (MARKER_DPI_WIDTH * density);
    }

    public Bitmap createGroundOverlayBitmap(int entriesCount) {
        Bitmap bitmap = Bitmap.createBitmap(areaSize, areaSize,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        String text = getBitmapText(entriesCount);

        RectF circleRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect textRect = new Rect();
        overlayTextPaint.getTextBounds(text, 0, text.length(), textRect);
        float x = (bitmap.getWidth() - textRect.width()) / 2;
        float y = (bitmap.getHeight() + textRect.height()) / 2;

        overlayPaint.setColor(getOverlayColor(entriesCount));
        canvas.drawOval(circleRect, overlayPaint);
        canvas.drawText(text, x, y, overlayTextPaint);

        return bitmap;
    }

    public Bitmap createMarkerBitmap(int damageValue, int entriesCount) {
        Bitmap bitmap = Bitmap.createBitmap(markerWidth
                , markerHeight
                , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        String text = getBitmapText(entriesCount);

        markerPaint.setColor(getMarkerColor(damageValue));

        float stroke = markerStrokePaint.getStrokeWidth();
        float r = markerWidth / 2;
        Path p = new Path();
        p.arcTo(new RectF(0+stroke, 0+stroke, 2 * r-stroke, 2 * r-stroke), 0, -180);
        p.lineTo(markerWidth / 2, markerHeight-stroke);
        p.close();
        canvas.drawPath(p, markerPaint);

        Path strokeP = new Path();

        strokeP.arcTo(new RectF(0+stroke/2, 0+stroke/2, 2 * r-stroke/2, 2 * r-stroke/2), 0, -180);
        strokeP.lineTo(markerWidth / 2, markerHeight-stroke/2);
        strokeP.close();
        canvas.drawPath(strokeP, markerStrokePaint);

        float textWidth = markerTextPaint.measureText(text);
        float startX = (markerWidth - textWidth) / 2;
        float startY = r - (markerTextPaint.ascent()) / 2;
        canvas.drawText(text, startX, startY, markerTextPaint);

        return bitmap;
    }

    private int getMarkerColor(int damageValue) {
        if (damageValue == 1)
            return 0xFFFFC107; //orange
        else if (damageValue == 2)
            return 0xFFF44336; //red
        else
            return 0xFF37474F; //black
    }

    private String getBitmapText(int damageCount) {
        if (damageCount < 999)
            return String.valueOf(damageCount);
        else
            return String.valueOf(Math.round(damageCount / 1000)) + "K";
    }

    private int getOverlayColor(int entriesCount){
        if (entriesCount < 50)
            return 0xFF3F51B5;
        else if (entriesCount < 100)
            return 0xFF354498;
        else if (entriesCount < 500)
            return 0xFF2B377B;
        else if (entriesCount < 1000)
            return 0xFF212A5E;
        else
            return 0xFF161D41;
    }

    public int getAreaSize() {
        return areaSize;
    }

    public void setAreaSize(int areaSize) {
        this.areaSize = areaSize;
        this.overlayTextPaint.setTextSize(OVERLAY_TEXT_TO_AREA_RATIO * areaSize);
    }
}
