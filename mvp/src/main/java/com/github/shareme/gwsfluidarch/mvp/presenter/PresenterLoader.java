/*
   Copyright (C) 2016 Fred Grott(aka shareme GrottWorkShop)

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
package com.github.shareme.gwsfluidarch.mvp.presenter;

import android.content.Context;
import android.content.Loader;

/**
 * Created by fgrott on 9/17/2016.
 */

public class PresenterLoader<T extends Presenter> extends Loader<T> {

  private PresenterFactory<T> factory;
  private T presenter;

  /**
   * Stores away the application context associated with context. Since Loaders can be used across
   * multiple activities it's dangerous to store the context directly; always use {@link
   * #getContext()} to retrieve the Loader's Context, don't use the constructor argument directly. The
   * Context returned by {@link #getContext} is safe to use across Activity instances.
   *
   * @param context used to retrieve the application context.
   */
  public PresenterLoader(Context context) {
    super(context);
  }


  // Constructor...
  @Override
  protected void onStartLoading(){

    if(presenter != null){

      deliverResult(presenter);
      return;
    }

    forceLoad();
  }

  @Override
  protected void onForceLoad(){

    presenter=factory.create();
    deliverResult(presenter);
  }

  @Override
  protected void onReset(){
    presenter.onDestroyed();
    presenter = null;
  }
}
