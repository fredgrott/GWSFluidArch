/*
  Copyright 2014 Inloop, s.r.o.
  Modifications Copyright(c) 2016 Fred Grott

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.github.shareme.gwsfluidarch.mvp.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.shareme.gwsfluidarch.mvp.viewmodel.AbstractViewModel;
import com.github.shareme.gwsfluidarch.mvp.viewmodel.MyView;
import com.github.shareme.gwsfluidarch.mvp.viewmodel.ViewModelHelper;

/**
 * Created by fgrott on 9/17/2016.
 */
@SuppressWarnings("unused")
public abstract class ViewModelBaseActivity<T extends MyView, R extends AbstractViewModel<T>> extends ViewModelBaseEmptyActivity implements MyView {

  private final ViewModelHelper<T, R> mViewModeHelper = new ViewModelHelper<>();

  @Override
  protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mViewModeHelper.onCreate(this, savedInstanceState, getViewModelClass(), getIntent().getExtras());
  }

  /**
   * Call this after your view is ready - usually on the end of {@link android.app.Activity#onCreate(Bundle)}
   * @param view view
   */
  @SuppressWarnings("unused")
  public void setModelView(@NonNull final T view) {
    mViewModeHelper.setView(view);
  }

  public abstract Class<R> getViewModelClass();

  @Override
  public void onSaveInstanceState(@NonNull final Bundle outState) {
    super.onSaveInstanceState(outState);
    mViewModeHelper.onSaveInstanceState(outState);
  }

  @Override
  public void onStart() {
    super.onStart();
    mViewModeHelper.onStart();
  }

  @Override
  public void onStop() {
    super.onStop();
    mViewModeHelper.onStop();
  }

  @Override
  public void onDestroy() {
    mViewModeHelper.onDestroy(this);
    super.onDestroy();
  }

  /**
   * @see ViewModelHelper#getViewModel()
   */
  @SuppressWarnings("unused")
  @NonNull
  public R getViewModel() {
    return mViewModeHelper.getViewModel();
  }

}
