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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.shareme.gwsfluidarch.mvc.viewmodel.MyViewModelProvider;
import com.github.shareme.gwsfluidarch.mvc.viewmodel.ViewModelProvider;

/**
 * All your activities must extend this activity - even in case your activity has no viewmodel. The fragment viewmodels are using the {@link MyViewModelProvider}
 * interface to get the {@link ViewModelProvider} from the current activity.
 * You can copy this implementation in case you don't want to extend this class.
 *
 * Created by fgrott on 9/17/2016.
 */

public abstract class ViewModelBaseEmptyActivity extends AppCompatActivity implements MyViewModelProvider {

  private ViewModelProvider mViewModelProvider;

  @Override
  protected void onCreate(@Nullable final Bundle savedInstanceState) {
    //This code must be execute prior to super.onCreate()
    mViewModelProvider = ViewModelProvider.newInstance(this);
    super.onCreate(savedInstanceState);
  }

  @Override
  @Nullable
  public Object onRetainCustomNonConfigurationInstance() {
    return mViewModelProvider;
  }

  @Override
  public void onStop() {
    super.onStop();
    if (isFinishing()) {
      mViewModelProvider.removeAllViewModels();
    }
  }

  @Override
  public ViewModelProvider getViewModelProvider() {
    return mViewModelProvider;
  }
}
