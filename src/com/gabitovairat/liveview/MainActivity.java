package com.gabitovairat.liveview;

import com.gabitovairat.components.ZoomView;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.res.Configuration;
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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity
{
  private static float         MIN_ZOOM    = 1.0f;
  private static float         MAX_ZOOM    = 35f;
  float                        OriginalX;
  float                        OriginalY;

  private static float         scaleFactor = 1.f;
  private ScaleGestureDetector detector;

  int                          yearCount   = 90;  // ear in live
  int                          colomnCount = 52;  // 366/7;//week in year

  int                          currentYear = 33; // already lived years
  int                          currentWeek = 4;

  LinearLayout                 mainView;
  RelativeLayout               hostView;
  
  LinearLayout                 weekContainerView;
  FrameLayout                  beforeYearFrame;
  FrameLayout                  beforeWeekFrame;
  
  LinearLayout                 beforeWeekConteiner;
  RelativeLayout               weekFrame;
  
  LinearLayout                 weekLayout;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    detector = new ScaleGestureDetector(this, new ScaleListener());
    setContentView(R.layout.activity_main);

    mainView = (LinearLayout) findViewById(R.id.mainView);
    hostView = (RelativeLayout) findViewById(R.id.hostView);
    weekContainerView = (LinearLayout) findViewById(R.id.weekContainer);
    beforeYearFrame = (FrameLayout) findViewById(R.id.beforeYearFrame);
    beforeWeekFrame = (FrameLayout) findViewById(R.id.beforeWeekFrame);
    
    beforeWeekConteiner = (LinearLayout) findViewById(R.id.beforeWeekConteiner);
    weekFrame = (RelativeLayout) findViewById(R.id.weekFrame);
    
    weekLayout = (LinearLayout) findViewById(R.id.weekLayout);

    Display display = getWindowManager().getDefaultDisplay();

    // Point dispSize = new Point();
    DisplayMetrics outMetrics = new DisplayMetrics();
    display.getMetrics(outMetrics);

    createWeekElements(outMetrics.widthPixels, outMetrics.heightPixels);
    placeWeekContainer();
    createDayHourElements();
    // setContentView(new ZoomView(this));
  }

  @Override
  protected void onResume()
  {
    super.onResume();
  }

  int sizeInDim(int original)
  {
    float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        (original), getResources().getDisplayMetrics());
    return (int) pixels;
  }

  LinearLayout createLinearLayout(float weighSum, int ResourceForDraw)
  {
    LinearLayout LL = new LinearLayout(this);
    // LL.setBackgroundColor(Color.CYAN);
    LL.setBackgroundResource(ResourceForDraw);
    LL.setOrientation(LinearLayout.HORIZONTAL);

    LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT);
    LL.setWeightSum(weighSum);
    LL.setLayoutParams(LLParams);
    return LL;
  }

  FrameLayout createLinearLayout(float width, float heigh, int ResourceForDraw)
  {
    FrameLayout ladderFL = new FrameLayout(this);
    // ladderFL.setBackgroundColor(Color.GREEN);
    ladderFL.setBackgroundResource(ResourceForDraw);

    LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(0,
        LinearLayout.LayoutParams.MATCH_PARENT);
    ladderFLParams.weight = 1f;
    ladderFL.setLayoutParams(ladderFLParams);
    return ladderFL;
  }

  Bitmap createCacheBitmapForRow(int width, int heigh, int elementCount,
      int resForDrawBefore, int resForDrawAfter, int resForDraw,
      int currentElement)
  {
    int oneElementWidth = (int) ((float) width / (float) elementCount);
    Bitmap oneElementWidthBitmapBefore = Bitmap.createBitmap(
        (int) oneElementWidth, heigh, Bitmap.Config.ARGB_8888);
    Canvas oneElementWidthCanvasBefore = new Canvas(oneElementWidthBitmapBefore);
    Bitmap oneElementWidthBitmapAfter = Bitmap.createBitmap(
        (int) oneElementWidth, heigh, Bitmap.Config.ARGB_8888);
    Canvas oneElementWidthCanvasAfter = new Canvas(oneElementWidthBitmapAfter);
    Bitmap oneElementWidthBitmap = Bitmap.createBitmap((int) oneElementWidth,
        heigh, Bitmap.Config.ARGB_8888);
    Canvas oneElementWidthCanvas = new Canvas(oneElementWidthBitmap);

    Drawable strokeDrawableBefore = getResources()
        .getDrawable(resForDrawBefore);
    strokeDrawableBefore.setBounds(0, 0,
        oneElementWidthBitmapBefore.getWidth(),
        oneElementWidthBitmapBefore.getHeight());
    strokeDrawableBefore.draw(oneElementWidthCanvasBefore);

    Drawable strokeDrawableAfter = getResources().getDrawable(resForDrawAfter);
    strokeDrawableAfter.setBounds(0, 0, oneElementWidthCanvasAfter.getWidth(),
        oneElementWidthCanvasAfter.getHeight());
    strokeDrawableAfter.draw(oneElementWidthCanvasAfter);

    Drawable strokeDrawable = getResources().getDrawable(resForDraw);
    strokeDrawable.setBounds(0, 0, oneElementWidthCanvas.getWidth(),
        oneElementWidthCanvas.getHeight());
    strokeDrawable.draw(oneElementWidthCanvas);

    Bitmap copy = Bitmap.createBitmap((oneElementWidth + 1) * elementCount,
        heigh + 2, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(copy);

    for (int i = 0; i != elementCount; ++i)
    {
      if (i < currentElement)
      {
        canvas.drawBitmap(oneElementWidthBitmapBefore, (oneElementWidth + 1)
            * i, 1, null);
      }
      else if (i > currentElement)
      {
        canvas.drawBitmap(oneElementWidthBitmapAfter,
            (oneElementWidth + 1) * i, 1, null);
      }
      else
      {
        canvas.drawBitmap(oneElementWidthBitmap, (oneElementWidth + 1) * i, 1,
            null);
      }
    }

    return copy;
  }
  
  void placeWeekContainer()
  {
    weekContainerView.setWeightSum(yearCount);
    {
      FrameLayout ladderFL = new FrameLayout(this);
      LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          0);
      ladderFLParams.weight = currentYear-1;
      
      beforeYearFrame.setLayoutParams(ladderFLParams);
    }
    
    {
      FrameLayout ladderFL = new FrameLayout(this);
      LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          0);
      ladderFLParams.weight = 1;
      
      beforeWeekConteiner.setLayoutParams(ladderFLParams);
    }
    
    beforeWeekConteiner.setWeightSum(colomnCount);
    {
      FrameLayout ladderFL = new FrameLayout(this);
      LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
          0,
          LinearLayout.LayoutParams.MATCH_PARENT);
      ladderFLParams.weight = currentWeek;
      
      beforeWeekFrame.setLayoutParams(ladderFLParams);
    }

    {
      FrameLayout ladderFL = new FrameLayout(this);
      LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
          0,
          LinearLayout.LayoutParams.MATCH_PARENT);
      ladderFLParams.weight = 1;
      
      weekFrame.setLayoutParams(ladderFLParams);
    }
  }

  void createWeekElements(int sceenSizeX, int sceenSizeY)
  {
    // day in year 365-366
    float originalWeekWidth = 2;
    float originalWeekHeigh = 4;

    mainView.setWeightSum(yearCount);

    Bitmap beforeCurrentRow = createCacheBitmapForRow(sceenSizeX, sceenSizeY
        / yearCount, colomnCount, R.drawable.past_week_draw,
        R.drawable.feature_week_draw, R.drawable.week_draw, 10000);
    Bitmap afterCurrentRow = createCacheBitmapForRow(sceenSizeX, sceenSizeY
        / yearCount, colomnCount, R.drawable.past_week_draw,
        R.drawable.feature_week_draw, R.drawable.week_draw, -10000);
    Bitmap CurrentRow = createCacheBitmapForRow(sceenSizeX, sceenSizeY
        / yearCount, colomnCount, R.drawable.past_week_draw,
        R.drawable.feature_week_draw, R.drawable.week_draw, currentWeek);
    
    LinearLayout topFrame = new LinearLayout(this);
    topFrame.setOrientation(LinearLayout.VERTICAL);
    float topWeigthSum = currentYear-1;
    {
      FrameLayout ladderFL = new FrameLayout(this);
      LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          0);
      ladderFLParams.weight = topWeigthSum;
      topFrame.setLayoutParams(ladderFLParams);
    }

    LinearLayout middleFrame = new LinearLayout(this);
    middleFrame.setOrientation(LinearLayout.VERTICAL);
    float middleWeigthSum = 1;
    {
      FrameLayout ladderFL = new FrameLayout(this);
      LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          0);
      ladderFLParams.weight = middleWeigthSum;
      middleFrame.setLayoutParams(ladderFLParams);
    }

    LinearLayout bottomFrame = new LinearLayout(this);
    bottomFrame.setOrientation(LinearLayout.VERTICAL);
    float bottomWeigthSum = yearCount-currentYear;
    {
      FrameLayout ladderFL = new FrameLayout(this);
      LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          0);
      ladderFLParams.weight = bottomWeigthSum;
      bottomFrame.setLayoutParams(ladderFLParams);
    }
    
    for (int iYear = 0; iYear != yearCount; ++iYear)
    {
      if (iYear < currentYear)
      {
        FrameLayout ladderFL = new FrameLayout(this);
        LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
        ladderFLParams.weight = 1f;
        ladderFL.setLayoutParams(ladderFLParams);

        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ScaleType.FIT_XY);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageParams);
        imageView.setImageBitmap(beforeCurrentRow);

        ladderFL.addView(imageView);
        topFrame.addView(ladderFL);
      }
      else if (iYear > currentYear)
      {
        FrameLayout ladderFL = new FrameLayout(this);
        LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
        ladderFLParams.weight = 1f;
        ladderFL.setLayoutParams(ladderFLParams);

        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ScaleType.FIT_XY);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageParams);
        imageView.setImageBitmap(afterCurrentRow);

        ladderFL.addView(imageView);
        bottomFrame.addView(ladderFL);
      }
      else if (iYear == currentYear)
      {
        FrameLayout ladderFL = new FrameLayout(this);
        LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
        ladderFLParams.weight = 1f;
        ladderFL.setLayoutParams(ladderFLParams);

        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ScaleType.FIT_XY);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageParams);
        imageView.setImageBitmap(CurrentRow);

        ladderFL.addView(imageView);
        middleFrame.addView(ladderFL);
      }
      else
      {
        FrameLayout ladderFL = new FrameLayout(this);
        LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0);
        ladderFLParams.weight = 1f;
        ladderFL.setLayoutParams(ladderFLParams);

        LinearLayout newRow = createLinearLayout((float) colomnCount,
            R.drawable.week_draw);
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

          FrameLayout weekL = createLinearLayout(originalWeekWidth,
              originalWeekHeigh, WeekDrawable);
          newRow.addView(weekL);
        }
        ladderFL.addView(newRow);
        mainView.addView(ladderFL);
      }
    }

    {
      mainView.addView(topFrame);
    }
    {
      mainView.addView(middleFrame);
    }
    {
      mainView.addView(bottomFrame);
    }
  }

  protected void invalidate()
  {
    if (OriginalX == 0)
    {
      OriginalX = mainView.getWidth();
      OriginalY = mainView.getHeight();

      float pixelsY = sizeInDim((int) (OriginalY * MAX_ZOOM));// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                              // (OriginalY*5),
                                                              // getResources().getDisplayMetrics());
      float pixelsX = sizeInDim((int) (OriginalX * MAX_ZOOM));// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                              // (OriginalX*5),
                                                              // getResources().getDisplayMetrics());
      android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) hostView
          .getLayoutParams();
      params.height = (int) pixelsY;
      params.width = (int) pixelsX;
      hostView.setLayoutParams(params);
    }

    float pixelsY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        (OriginalY * scaleFactor), getResources().getDisplayMetrics());
    float pixelsX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        (OriginalX * scaleFactor), getResources().getDisplayMetrics());

    android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mainView
        .getLayoutParams();
    params.height = (int) pixelsY;
    params.width = (int) pixelsX;

    int xOffset = -(int) ((OriginalX - OriginalX * scaleFactor) * getViewPartOffsetX());
    int yOffset = -(int) ((OriginalY - OriginalY * scaleFactor) * getViewPartOffsetY());

    // int xOffset = -(int) (OriginalX*(1-scaleFactor)*(getViewPartOffsetX()));
    // int yOffset = -(int) (OriginalY*(1-scaleFactor)*(getViewPartOffsetY()));

    params.setMargins(-1 * xOffset, -1 * yOffset, xOffset, yOffset);
    mainView.setLayoutParams(params);
    mainView.invalidate();
    weekContainerView.setLayoutParams(params);
  }

  float getViewPartOffsetX()
  {
    return ((float) currentWeek) / (float) (colomnCount - 1);
  }

  float getViewPartOffsetY()
  {
    return ((float) currentYear-1.f) / (float) (yearCount-1.f);
  }
  
  //week inside draw
  void createDayHourElements()
  {
    int currentDay = 3;
    int currentHour = 14;
    
    weekLayout.setWeightSum(7);
    for (int dayI = 0; dayI != 7; ++dayI)
    {
      FrameLayout ladderFL = new FrameLayout(this);
      LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
          0, LinearLayout.LayoutParams.MATCH_PARENT);
      ladderFLParams.weight = 1f;
      ladderFL.setLayoutParams(ladderFLParams);

      LinearLayout newDay = new LinearLayout(this);
      newDay.setOrientation(LinearLayout.VERTICAL);
      newDay.setBackgroundResource(R.drawable.feature_week_draw);
      
      newDay.setWeightSum(24);
      
      for (int hourI = 0; hourI != 24; ++hourI)
      {
        int hourDrawable = 0;
        if (hourI < currentHour)
        {
          hourDrawable = R.drawable.past_week_draw;
        }
        else if (hourI > currentHour)
        {
          hourDrawable = R.drawable.feature_week_draw;
        }
        else
        {
          hourDrawable = R.drawable.week_draw;
        }

        FrameLayout ladderFL_ = new FrameLayout(this);
        LinearLayout.LayoutParams ladderFLParams_ = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0);
        ladderFLParams_.weight = 1f;
        ladderFL_.setLayoutParams(ladderFLParams_);

        LinearLayout newHour = new LinearLayout(this);
        newHour.setOrientation(LinearLayout.VERTICAL);
        newHour.setBackgroundResource(hourDrawable);

        ladderFL_.addView(newHour);
        
        newDay.addView(ladderFL_);
      }
      
      ladderFL.addView(newDay);
      weekLayout.addView(ladderFL);
    }
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
