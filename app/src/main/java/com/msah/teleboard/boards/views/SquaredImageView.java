package com.msah.teleboard.boards.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/** An image view which always remains square with respect to its width. */
class SquaredImageView extends androidx.appcompat.widget.AppCompatImageView {
  public SquaredImageView(Context context) {
    super(context);
  }

  public SquaredImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
  }
}
