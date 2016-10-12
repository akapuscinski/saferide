/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class TilesView extends View {

    public static final int GRID_PAINT_COLOR = 0x30000000;
    public static final int DEFAULT_TILES_IN_ROW = 5;

    private Paint gridPaint;
    private int tilesInRow=DEFAULT_TILES_IN_ROW;

    public TilesView(Context context) {
        super(context);
        init();
    }

    public TilesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TilesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TilesView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawGrid(canvas);
    }

    private void init() {
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(2);
        gridPaint.setColor(GRID_PAINT_COLOR);
    }

    private void drawGrid(Canvas canvas) {
        int size = getMeasuredHeight() < getMeasuredWidth() ?
                getMeasuredHeight() / tilesInRow :
                getMeasuredWidth() / tilesInRow;

        for (int i = 0; i < getMeasuredHeight(); i += size) {
            for (int k = 0; k < getMeasuredWidth(); k += size) {
                Rect r = new Rect(k, i, k + size, i + size);
                canvas.drawRect(r, gridPaint);
            }
        }
    }

    public int getTilesInRow() {
        return tilesInRow;
    }

    public void setTilesInRow(int tilesInRow) {
        this.tilesInRow = tilesInRow;
        invalidate();
    }
}
