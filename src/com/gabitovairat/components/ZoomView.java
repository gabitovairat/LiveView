package com.gabitovairat.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class ZoomView extends View
{

  private static float         MIN_ZOOM    = 1f;
  private static float         MAX_ZOOM    = 5f;

  private float                scaleFactor = 1.f;
  private ScaleGestureDetector detector;
  
  int CenterZoomX = 0;
  int CenterZoomY = 0;

  public ZoomView(Context context)
  {
    super(context);
    detector = new ScaleGestureDetector(getContext(), new ScaleListener());
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    detector.onTouchEvent(event);
    return true;
  }

  @Override
  public void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    
    CenterZoomX = canvas.getWidth()/2;
    CenterZoomY = canvas.getHeight()/2;
    
    //canvas.save();
    //canvas.scale(scaleFactor, scaleFactor, canvas.getWidth()/2, canvas.getHeight()/2);

    // ...
    // your canvas-drawing code
    Paint paint = new Paint();
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(Color.WHITE);
    canvas.drawPaint(paint);
    paint.setColor(Color.parseColor("#CD5C5C"));
    
    DrawElements(canvas,  0, 0, 100, paint);
    DrawElements(canvas,  getWidth()/2, getHeight()/2, 100, paint);

    //DrawElements(canvas,  getWidth(), getHeight(), 100);
    // ...
    canvas.restore();
  }

  void DrawElements(Canvas canvas, float sourceX, float sourceY, float sourceRadius, Paint paint)
  {
    // TODO Auto-generated method stub
    int x = (int) (sourceX*scaleFactor + CenterZoomX - CenterZoomX*scaleFactor);
    int y = (int) (sourceY*scaleFactor + CenterZoomY - CenterZoomY*scaleFactor);
    int radius;
    radius = (int) (sourceRadius*scaleFactor);

    // Use Color.parseColor to define HTML colors
    canvas.drawCircle(x, y, radius, paint);
  }

  private class ScaleListener extends
      ScaleGestureDetector.SimpleOnScaleGestureListener
  {
    @Override
    public boolean onScale(ScaleGestureDetector detector)
    {
      scaleFactor *= detector.getScaleFactor();
      scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
      invalidate();
      return true;
    }
  }
}