package com.gabitovairat.liveview;

import com.gabitovairat.components.ZoomView;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;
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
    
    createWeekElements();
    // setContentView(new ZoomView(this));
  }
  
  @Override
  protected void onResume()
  {
    super.onResume();
  }
  
  int sizeInDim(int original) 
  {
    float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (original), getResources().getDisplayMetrics());
    return (int)pixels;
  }
  
  LinearLayout createLinearLayout(float weighSum, int ResourceForDraw)
  {
    LinearLayout LL = new LinearLayout(this);
    //LL.setBackgroundColor(Color.CYAN);
    LL.setBackgroundResource(ResourceForDraw);
    LL.setOrientation(LinearLayout.HORIZONTAL);

    LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    LL.setWeightSum(weighSum);
    LL.setLayoutParams(LLParams);
    return LL;
  }
  
  FrameLayout createLinearLayout(float width, float heigh, int ResourceForDraw)
  {
    FrameLayout ladderFL = new FrameLayout(this);
    //ladderFL.setBackgroundColor(Color.GREEN);
    ladderFL.setBackgroundResource(ResourceForDraw);
    
    LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
    ladderFLParams.weight = 1f;
    ladderFL.setLayoutParams(ladderFLParams);
    /*
    LinearLayout LL = new LinearLayout(this);
    LL.setBackgroundColor(Color.CYAN);
    //LL.setBackgroundResource(ResourceForDraw);
    LL.setOrientation(LinearLayout.HORIZONTAL);

    //LayoutParams LLParams = new LayoutParams(sizeInDim((int) (width*scaleFactor)), sizeInDim((int) (heigh*scaleFactor)), 1);
    LayoutParams LLParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
    LL.setLayoutParams(LLParams);
    return LL;
    */
    return ladderFL;
  }
  
  
  void createWeekElements()
  {
    //day in year 365-366
    float originalWeekWidth = 2;
    float originalWeekHeigh = 4;
    //row 52
    int rowCount = 10;//ear in live
    //colomn
    int colomnCount = 366/7;//week in year
    
    for (int iYear = 0; iYear != rowCount; ++iYear)
    {
      LinearLayout newRow = createLinearLayout((float)colomnCount,R.drawable.week_draw);
      for (int iWeek = 0; iWeek != colomnCount; ++iWeek)
      {
        FrameLayout weekL = createLinearLayout(originalWeekWidth, originalWeekHeigh, R.drawable.week_draw);
        newRow.addView(weekL);
      }
      mainView.addView(newRow);
    }
  }
  
  protected void invalidate()
  {
    if (OriginalX == 0)
    {
      OriginalX = mainView.getWidth();
      OriginalY = mainView.getHeight();

      float pixelsY = sizeInDim(OriginalY*5);// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (OriginalY*5), getResources().getDisplayMetrics());
      float pixelsX = sizeInDim(OriginalX*5);// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (OriginalX*5), getResources().getDisplayMetrics());
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
