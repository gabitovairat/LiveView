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

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    detector = new ScaleGestureDetector(this, new ScaleListener());
    setContentView(R.layout.activity_main);
    
    mainView = (LinearLayout) findViewById(R.id.mainView);

    // setContentView(new ZoomView(this));
  }
  
  protected void invalidate()
  {
    if (OriginalX == 0)
    {
      OriginalX = mainView.getWidth();
      OriginalY = mainView.getHeight();
    }
    //mainView.setPivotX(0);
    //mainView.setPivotY(0);
    //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainView.getLayoutParams();
    //params.width = (int) (OriginalX*scaleFactor);
    //params.height = (int) (OriginalY*scaleFactor);
   
    //android.view.ViewGroup.LayoutParams params = mainView.getLayoutParams();
    //params.height = (int) (OriginalY*scaleFactor);
    //params.width = (int) (OriginalX*scaleFactor);
    //mainView.requestLayout();

    float pixelsY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (OriginalY*scaleFactor), getResources().getDisplayMetrics());
    float pixelsX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (OriginalX*scaleFactor), getResources().getDisplayMetrics());
    android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mainView.getLayoutParams();
    params.height = (int) pixelsY;
    params.width  = (int) pixelsX;
    mainView.setLayoutParams(params);
    
    //mainView.setLayoutParams(new RelativeLayout.LayoutParams((int) (OriginalX*scaleFactor), (int) (OriginalY*scaleFactor)));
    //mainView.setLayoutParams(params);
    //mainView.setScaleX(scaleFactor);
    //mainView.setScaleY(scaleFactor);
    /*mainView.post(new Runnable()
    {
        @Override
        public void run()
        {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainView.getLayoutParams();
            params.width = (int) (OriginalX*scaleFactor);
            params.height = (int) (OriginalY*scaleFactor);
            mainView.setLayoutParams(params);
        }
    });
    */
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
