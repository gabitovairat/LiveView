package com.gabitovairat.liveview;

import com.gabitovairat.components.ZoomView;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity
{
  private static float         MIN_ZOOM    = 0.1f;
  private static float         MAX_ZOOM    = 5f;
  int OriginalX;
  int OriginalY;

  private float                scaleFactor = 1.f;
  private ScaleGestureDetector detector;
  
  LinearLayout mainView; 
  RelativeLayout hostView;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    detector = new ScaleGestureDetector(this, new ScaleListener());
    setContentView(R.layout.activity_main);
    
    mainView = (LinearLayout) findViewById(R.id.mainView);
    hostView = (RelativeLayout) findViewById(R.id.hostView);
    // setContentView(new ZoomView(this));
  }
  
  protected void invalidate()
  {
    if (OriginalX == 0)
    {
      OriginalX = mainView.getWidth();
      OriginalY = mainView.getHeight();

      float pixelsY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (OriginalY*5), getResources().getDisplayMetrics());
      float pixelsX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (OriginalX*5), getResources().getDisplayMetrics());
      android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) hostView.getLayoutParams();
      params.height = (int) pixelsY;
      params.width  = (int) pixelsX;
      hostView.setLayoutParams(params);
    }

    float pixelsY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (OriginalY*scaleFactor), getResources().getDisplayMetrics());
    float pixelsX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (OriginalX*scaleFactor), getResources().getDisplayMetrics());
    android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mainView.getLayoutParams();
    params.height = (int) pixelsY;
    params.width  = (int) pixelsX;
    mainView.setLayoutParams(params);
  }
  
  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    detector.onTouchEvent(event);
    return true;
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
