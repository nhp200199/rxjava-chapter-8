package com.tehmou.book.androidtictactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;

public class GameGridView extends View {
    private static final String TAG = GameGridView.class.getSimpleName();
    private GameGrid gameGrid;
    private int width;
    private int height;
    private final Paint linePaint;
    private final Paint bitmapPaint;
    private final Bitmap redCircleBitmap;
    private final Bitmap blackCircleBitmap;
    private final Rect bitmapSrcRect;

    public GameGridView(Context context) {
        this(context, null);
    }

    public GameGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(8f);

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        redCircleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_red_circle);
        blackCircleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_black_circle);
        bitmapSrcRect = new Rect(0, 0, redCircleBitmap.getWidth(), redCircleBitmap.getHeight());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        width = right - left;
        height = bottom - top;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);

        Log.d(TAG, "width: " + width + ", height: " + height);

        final float gridWidth = getGridWidth();
        final float gridHeight = getGridHeight();

        final float tileWidth = width / gridWidth;
        final float tileHeight = height / gridHeight;

        drawSymbols(canvas, gridWidth, gridHeight, tileWidth, tileHeight);
        drawGridLines(canvas, gridWidth, gridHeight, tileWidth, tileHeight);
    }

    private void drawSymbols(Canvas canvas,
                             float gridWidth, float gridHeight,
                             float tileWidth, float tileHeight) {
        if (gameGrid == null) {
            return;
        }
        
        for (int i = 0; i < gridWidth; i++) {
            for (int n = 0; n < gridHeight; n++) {
                GameSymbol symbol = gameGrid.getSymbolAt(i, n);
                RectF dst = new RectF(i * tileWidth, n * tileHeight,
                        (i + 1) * tileWidth, (n + 1) * tileHeight);
                if (symbol == GameSymbol.RED) {
                    canvas.drawBitmap(
                            redCircleBitmap,
                            bitmapSrcRect, dst,
                            bitmapPaint);
                } else if (symbol == GameSymbol.BLACK) {
                    canvas.drawBitmap(
                            blackCircleBitmap,
                            bitmapSrcRect, dst,
                            bitmapPaint);
                }
            }
        }
    }

    private void drawGridLines(Canvas canvas,
                               float gridWidth, float gridHeight,
                               float tileWidth, float tileHeight) {
        for (int i = 0; i <= gridWidth; i++) {
            Log.d(TAG, "line " + i);
            canvas.drawLine(i * tileWidth, 0, i * tileWidth, height, linePaint);
        }

        for (int n = 0; n <= gridHeight; n++) {
            canvas.drawLine(0, n * tileHeight, width, n * tileHeight, linePaint);
        }
    }

    public void setData(GameGrid gameGrid) {
        this.gameGrid = gameGrid;
        invalidate();
    }

    public int getGridWidth() {
        return this.gameGrid.getWidth();
    }

    public int getGridHeight() {
        return this.gameGrid.getHeight();
    }
}
