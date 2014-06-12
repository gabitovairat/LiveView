package com.gabitovairat.liveview;

import com.gabitovairat.components.ZoomView;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
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
    
    
    Display display = getWindowManager().getDefaultDisplay();
    
    //Point dispSize = new Point();
    DisplayMetrics outMetrics = new DisplayMetrics();
    display.getMetrics(outMetrics);
    
    createWeekElements(outMetrics.widthPixels, outMetrics.heightPixels);
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
    return ladderFL;
  }
  
  Bitmap createCacheBitmap(int width, int heigh, int elementCount, int resForDraw)
  {
    Drawable strokeDrawable = getResources().getDrawable(R.drawable.week_draw);
    //Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.bmp1);
    //Canvas canvas = new Canvas();
    //strokeDrawable.draw(canvas);
    //int width = 100;//bmp1.getWidth() + STROKE_WIDTH * 2;
    //int heigh = 100;//bmp1.getHeight() + STROKE_WIDTH * 2;
    
    final int STROKE_WIDTH = 3;
    Bitmap copy = Bitmap.createBitmap(width, heigh, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(copy);
    strokeDrawable.setBounds(0, 0, copy.getWidth(), copy.getHeight());
    strokeDrawable.draw(canvas);
    //canvas.drawBitmap(bmp1, STROKE_WIDTH, STROKE_WIDTH, null);
    //bmp1.recycle();
    //bmp1 = copy;
    return copy;
  }
  
  
  void createWeekElements(int sceenSizeX, int sceenSizeY)
  {
    //day in year 365-366
    float originalWeekWidth = 2;
    float originalWeekHeigh = 4;
    //row 52
    int rowCount = 90;//ear in live
    //colomn
    int colomnCount = 52;//366/7;//week in year
    
    int currentYear = 33;
    int currentWeek = 20;
    
    mainView.setWeightSum(rowCount);
    
    Bitmap beforeCurrentRow = createCacheBitmap(sceenSizeX, sceenSizeY/rowCount, colomnCount, R.drawable.past_week_draw);
    Bitmap afterCurrentRow = createCacheBitmap(sceenSizeX, sceenSizeY/rowCount, colomnCount, R.drawable.feature_week_draw);
    
    for (int iYear = 0; iYear != rowCount; ++iYear)
    {
      if (iYear < currentYear)
      {
        FrameLayout ladderFL = new FrameLayout(this);
        LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        ladderFLParams.weight = 1f;
        ladderFL.setLayoutParams(ladderFLParams);
        
        LinearLayout newRow = createLinearLayout((float)colomnCount,R.drawable.past_week_draw);
        ladderFL.addView(newRow);
        mainView.addView(ladderFL);
      }
      else if (iYear > currentYear)
      {
        FrameLayout ladderFL = new FrameLayout(this);
        LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        ladderFLParams.weight = 1f;
        ladderFL.setLayoutParams(ladderFLParams);
        
        LinearLayout newRow = createLinearLayout((float)colomnCount,R.drawable.feature_week_draw);
        ladderFL.addView(newRow);
        mainView.addView(ladderFL);
      }
      else
      {
        FrameLayout ladderFL = new FrameLayout(this);
        LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        ladderFLParams.weight = 1f;
        ladderFL.setLayoutParams(ladderFLParams);
        
        LinearLayout newRow = createLinearLayout((float)colomnCount,R.drawable.week_draw);
        for (int iWeek = 0; iWeek != colomnCount; ++iWeek)
        {
          int WeekDrawable = 0;
          if (iWeek < currentWeek)
          {
            WeekDrawable = R.drawable.past_week_draw;
          }
          else if (iWeek > currentWeek)
          {
            WeekDrawable = R.drawable.feature_week_draw;
          }
          else
          {
            WeekDrawable = R.drawable.week_draw;
          }
          
          FrameLayout weekL = createLinearLayout(originalWeekWidth, originalWeekHeigh, WeekDrawable);
          newRow.addView(weekL);
        }
        ladderFL.addView(newRow);
        mainView.addView(ladderFL);
      }
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
