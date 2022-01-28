package com.example.pintura.DrawingView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.pintura.R;
import com.example.pintura.Stroke.Brush;

import java.util.ArrayList;

public class DrawingView extends View {

    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;

    // Paint Class
    private Paint mPaint;

    // ArrayList to store drawings by the user
    private ArrayList<Brush> paths = new ArrayList<>();
    private int currentColor;
    private int brushWidth;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);



    // Constructors to initialise all the attributes
    public DrawingView(Context context) {
        this(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();

        // the below methods smoothens
        // the drawings of the user
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        // 0xff=255 in decimal
        mPaint.setAlpha(0xff);

    }

    // this method instantiates the bitmap and object
    public void init(int height, int width) {

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        // initial color of the brush
        currentColor = Color.BLACK;

        // initial brush size
        brushWidth = 20;
    }

    // sets the current color of brush
    public void setColor(int color) {
        currentColor = color;
    }

    // sets the stroke width
    public void setBrushWidth(int width) {
        brushWidth = width;
    }

    public void undo() {
        // Before undoing check if there is anything to undo
        //If not then an error will be thrown
        if (paths.size() != 0) {
            paths.remove(paths.size() - 1);
            invalidate();
        }
    }

    // this methods returns the current bitmap
    public Bitmap save() {
        return mBitmap;
    }

    // This is the method where the drawing occurs
    @Override
    protected void onDraw(Canvas canvas) {
        // save the current state of the canvas before,
        // to draw the background of the canvas
        canvas.save();

        // DEFAULT color of the canvas
        int backgroundColor = Color.WHITE;
        mCanvas.drawColor(backgroundColor);

        // now, we iterate over the list of paths
        // and draw each path on the canvas
        for (Brush fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.brushWidth);
            mCanvas.drawPath(fp.path, mPaint);
        }
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    ///manage touch response method

    private void touchStart(float x, float y) {

        //New stroke created and added to path list
        mPath = new Path();
        Brush fp = new Brush(currentColor, brushWidth, mPath);
        paths.add(fp);

        // removes any drawing on the canvas
        mPath.reset();

        // wherever you start drawing a new starting point is set
        mPath.moveTo(x, y);

        // save the current location of your touch
        mX = x;
        mY = y;
    }


    private void touchMove(float x, float y) {
        //check if finger movement is greater than tolerance previously defined
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        //call quadTo() to smooth the turns
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }


    private void touchUp() {
        //this draws line until the end
        mPath.lineTo(mX, mY);
    }

    //onTouchEvent() shows the touch movement that is happening
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }


}





