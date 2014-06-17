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
  int                          OriginalX;
  int                          OriginalY;

  private static float         scaleFactor = 1.f;
  private ScaleGestureDetector detector;
  
  int yearCount   = 90; // ear in live
  int colomnCount = 52; // 366/7;//week in year

  int currentYear = 33;
  int currentWeek = 4;

  LinearLayout                 mainView;
  RelativeLayout               hostView;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    detector = new ScaleGestureDetector(this, new ScaleListener());
    setContentView(R.layout.activity_main);

    mainView = (LinearLayout) findViewById(R.id.mainView);
    hostView = (RelativeLayout) findViewById(R.id.hostView);

    Display display = getWindowManager().getDefaultDisplay();

    // Point dispSize = new Point();
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
    float oneElementWidth = (float) width / (float) elementCount;
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

    Bitmap copy = Bitmap.createBitmap(width, heigh, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(copy);

    for (int i = 0; i != elementCount; ++i)
    {
      if (i < currentElement)
      {
        canvas.drawBitmap(oneElementWidthBitmapBefore, oneElementWidth * i, 0,
            null);
      }
      else if (i > currentElement)
      {
        canvas.drawBitmap(oneElementWidthBitmapAfter, oneElementWidth * i, 0,
            null);
      }
      else
      {
        canvas.drawBitmap(oneElementWidthBitmap, oneElementWidth * i, 0, null);
      }
    }

    return copy;
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

    for (int iYear = 0; iYear != yearCount; ++iYear)
    {
      if (iYear < currentYear)
      {
        FrameLayout ladderFL = new FrameLayout(this);
        LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0);
        ladderFLParams.weight = 1f;
        ladderFL.setLayoutParams(ladderFLParams);

        ImageView imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ScaleType.FIT_XY);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageParams);
        imageView.setImageBitmap(beforeCurrentRow);

        ladderFL.addView(imageView);
        mainView.addView(ladderFL);
      }
      else if (iYear > currentYear)
      {
        FrameLayout ladderFL = new FrameLayout(this);
        LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0);
        ladderFLParams.weight = 1f;
        ladderFL.setLayoutParams(ladderFLParams);

        ImageView imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ScaleType.FIT_XY);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageParams);
        imageView.setImageBitmap(afterCurrentRow);

        ladderFL.addView(imageView);
        mainView.addView(ladderFL);
      }
      else if (iYear == currentYear)
      {
        FrameLayout ladderFL = new FrameLayout(this);
        LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0);
        ladderFLParams.weight = 1f;
        ladderFL.setLayoutParams(ladderFLParams);

        ImageView imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ScaleType.FIT_XY);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageParams);
        imageView.setImageBitmap(CurrentRow);

        ladderFL.addView(imageView);
        mainView.addView(ladderFL);
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
  }

  float getViewPartOffsetX()
  {
    return ((float) currentWeek) / (float) (colomnCount-1);
  }

  float getViewPartOffsetY()
  {
    return ((float) currentYear) / (float) (yearCount-1);
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
