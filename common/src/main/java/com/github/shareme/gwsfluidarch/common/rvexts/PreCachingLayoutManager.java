/*
 Copyright (C) 2014 Ovidiu
 Modifications Copyright(C) 2016 Fred Grott(GrottWorkShop)

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You may
obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language
governing permissions and limitations under License.
 */
package com.github.shareme.gwsfluidarch.common.rvexts;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Allows pre-caching of pages/screens, works by defining
 * extra screen space. Original author's project:
 *
 * https://github.com/ovy9086/recyclerview-playground
 *
 *
 *
 * Usage:
 *
 * <code>
 *    //Setup layout manager
 *    PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(getActivity());
 *    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
 *    layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(getActivity()));
 *    recyclerView.setLayoutManager(layoutManager);
 *    recyclerView.setAdapter(adapter);
 * </code>
 * Created by fgrott on 9/13/2016.
 */
@SuppressWarnings("unused")
public class PreCachingLayoutManager extends LinearLayoutManager {
  private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 600;
  private int extraLayoutSpace = -1;
  private Context context;

  public PreCachingLayoutManager(Context context) {
    super(context);
    this.context = context;
  }

  public PreCachingLayoutManager(Context context, int extraLayoutSpace) {
    super(context);
    this.context = context;
    this.extraLayoutSpace = extraLayoutSpace;
  }

  public PreCachingLayoutManager(Context context, int orientation, boolean reverseLayout) {
    super(context, orientation, reverseLayout);
    this.context = context;
  }

  public void setExtraLayoutSpace(int extraLayoutSpace) {
    this.extraLayoutSpace = extraLayoutSpace;
  }

  @Override
  protected int getExtraLayoutSpace(RecyclerView.State state) {
    if (extraLayoutSpace > 0) {
      return extraLayoutSpace;
    }
    return DEFAULT_EXTRA_LAYOUT_SPACE;
  }
}
