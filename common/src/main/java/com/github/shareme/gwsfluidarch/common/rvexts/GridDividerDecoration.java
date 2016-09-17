/*
 The MIT License (MIT)

Copyright (c) 2014 Wireless Designs, LLC
Modifications Copyright(C) 2016 Fred Grott(GrottWorkShop)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */
package com.github.shareme.gwsfluidarch.common.rvexts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.shareme.gwsfluidarch.common.R;


/**
 * ItemDecoration implementation that applies and inset margin
 * around each child of the RecyclerView. It also draws item dividers
 * that are expected from a vertical list implementation, such as
 * ListView.
 *
 * Created by fgrott on 9/15/2016.
 */
@SuppressWarnings("unused")
public class GridDividerDecoration extends RecyclerView.ItemDecoration {

  private static final int[] ATTRS = { android.R.attr.listDivider };

  private Drawable mDivider;
  private int mInsets;

  public GridDividerDecoration(Context context) {
    TypedArray a = context.obtainStyledAttributes(ATTRS);
    mDivider = a.getDrawable(0);
    a.recycle();

    mInsets = context.getResources().getDimensionPixelSize(R.dimen.card_insets);
  }

  @Override
  public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
    drawVertical(c, parent);
    drawHorizontal(c, parent);
  }

  /** Draw dividers at each expected grid interval */
  public void drawVertical(Canvas c, RecyclerView parent) {
    if (parent.getChildCount() == 0) return;

    final int childCount = parent.getChildCount();

    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params =
              (RecyclerView.LayoutParams) child.getLayoutParams();

      final int left = child.getLeft() - params.leftMargin - mInsets;
      final int right = child.getRight() + params.rightMargin + mInsets;
      final int top = child.getBottom() + params.bottomMargin + mInsets;
      final int bottom = top + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  /** Draw dividers to the right of each child view */
  public void drawHorizontal(Canvas c, RecyclerView parent) {
    final int childCount = parent.getChildCount();

    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params =
              (RecyclerView.LayoutParams) child.getLayoutParams();

      final int left = child.getRight() + params.rightMargin + mInsets;
      final int right = left + mDivider.getIntrinsicWidth();
      final int top = child.getTop() - params.topMargin - mInsets;
      final int bottom = child.getBottom() + params.bottomMargin + mInsets;
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    //We can supply forced insets for each item view here in the Rect
    outRect.set(mInsets, mInsets, mInsets, mInsets);
  }
}
