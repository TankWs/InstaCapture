package com.tarek360.instacapture.screenshot.nonMaps;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.WindowManager;
import com.tarek360.instacapture.exception.ScreenCapturingFailedException;
import java.util.List;

/**
 * Created by tarek on 5/17/16.
 */
public final class NonMapScreenshotTaker {

  private NonMapScreenshotTaker() {
  }

  /**
   * Capture screenshot for the current activity and return bitmap of it.
   *
   * @param activity current activity.
   * @param ignoredViews from the screenshot.
   * @return Bitmap of screenshot.
   * @throws ScreenCapturingFailedException if unexpected error is occurred during capturing
   * screenshot
   */
  public static Bitmap getScreenshotBitmap(Activity activity, View[] ignoredViews) {
    if (activity == null) {
      throw new IllegalArgumentException("Parameter activity cannot be null.");
    }

    final List<RootViewInfo> viewRoots = FieldHelper.getRootViews(activity);
    View main = activity.getWindow().getDecorView();

    final Bitmap bitmap;
    try {
      bitmap = Bitmap.createBitmap(main.getWidth(), main.getHeight(), Bitmap.Config.ARGB_8888);
    } catch (final IllegalArgumentException e) {
      return null;
    }

    drawRootsToBitmap(viewRoots, bitmap, ignoredViews);

    return bitmap;
  }

  private static void drawRootsToBitmap(List<RootViewInfo> viewRoots, Bitmap bitmap,
      View[] ignoredViews) {
    for (RootViewInfo rootData : viewRoots) {
      drawRootToBitmap(rootData, bitmap, ignoredViews);
    }
  }

  private static void drawRootToBitmap(final RootViewInfo rootViewInfo, Bitmap bitmap,
      View[] ignoredViews) {
    // support dim screen
    if ((rootViewInfo.getLayoutParams().flags & WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        == WindowManager.LayoutParams.FLAG_DIM_BEHIND) {
      Canvas dimCanvas = new Canvas(bitmap);

      int alpha = (int) (255 * rootViewInfo.getLayoutParams().dimAmount);
      dimCanvas.drawARGB(alpha, 0, 0, 0);
    }

    final Canvas canvas = new Canvas(bitmap);
    canvas.translate(rootViewInfo.getRect().left, rootViewInfo.getRect().top);

    int[] ignoredViewsVisibility = null;
    if (ignoredViews != null) {
      ignoredViewsVisibility = new int[ignoredViews.length];
    }

    if (ignoredViews != null) {
      for (int i = 0; i < ignoredViews.length; i++) {
        if (ignoredViews[i] != null) {
          ignoredViewsVisibility[i] = ignoredViews[i].getVisibility();
          ignoredViews[i].setVisibility(View.INVISIBLE);
        }
      }
    }

    rootViewInfo.getView().draw(canvas);

    if (ignoredViews != null) {
      for (int i = 0; i < ignoredViews.length; i++) {
        if (ignoredViews[i] != null) {
          ignoredViews[i].setVisibility(ignoredViewsVisibility[i]);
        }
      }
    }
  }
}




