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
package com.github.shareme.gwsfluidarch.mvc.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.github.shareme.gwsfluidarch.mvc.viewmodel.AbstractViewModel;
import com.github.shareme.gwsfluidarch.mvc.viewmodel.MyView;
import com.github.shareme.gwsfluidarch.mvc.viewmodel.ViewModelHelper;

/**
 * The base fragment you will subclass for your android application.
 * Created by fgrott on 9/17/2016.
 */
@SuppressWarnings("unused")
public abstract class ViewModelBaseFragment<T extends MyView, R extends AbstractViewModel<T>> extends Fragment implements MyView {

  private final ViewModelHelper<T, R> mViewModeHelper = new ViewModelHelper<>();

  @Override
  public void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mViewModeHelper.onCreate(getActivity(), savedInstanceState, getViewModelClass(), getArguments());
  }

  @Nullable
  public abstract Class<R> getViewModelClass();

  /**
   * Call this after your view is ready - usually on the end of {@link Fragment#onViewCreated(View, Bundle)}
   * @param view view
   */
  protected void setModelView(@NonNull final T view) {
    mViewModeHelper.setView(view);
  }

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
  public void onDestroyView() {
    mViewModeHelper.onDestroyView(this);
    super.onDestroyView();
  }

  @Override
  public void onDestroy() {
    mViewModeHelper.onDestroy(this);
    super.onDestroy();
  }

  /**
   * @see ViewModelHelper#getViewModel()
   */
  @NonNull
  @SuppressWarnings("unused")
  public R getViewModel() {
    return mViewModeHelper.getViewModel();
  }
}
