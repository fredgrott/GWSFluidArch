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
package com.github.shareme.gwsfluidarch.common.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Just some boilerplate for your AsyncTaskLoader. Meant to be used when
 * the retrieve operation is expensive enough not to repeat during a
 * device orientation event such as network operations.
 *
 *
 *
 * Created by fgrott on 9/1/2016.
 */
@SuppressWarnings("unused")
public abstract class SimpleAsyncTaskLoader<T> extends AsyncTaskLoader<T>{

  private T data;


  public SimpleAsyncTaskLoader(Context context) {
    super(context);
  }



  @Override
  public void deliverResult(T data) {
    if (isReset()) {
      // An async query came in while the loader is stopped
      return;
    }
    T oldData = this.data;
    this.data = data;

    // if the Loader is in a started state, deliver the results to the client
    if (isStarted())
      super.deliverResult(data);

    if (oldData != null && oldData != data)
      releaseResources(data);
  }


  @Override
  protected void onStartLoading() {

    //deliver cached data
    if (data != null) {
      deliverResult(data);
    }

    // if the data has changed since the last time it was loaded
    // or is not currently available, start a load
    if (takeContentChanged() || data == null) {
      forceLoad();
    }
  }

  @Override
  protected void onStopLoading() {
    // Attempt to cancel the current load task if possible.
    cancelLoad();
  }

  @Override
  protected void onReset() {
    super.onReset();

    // Ensure the loader is stopped
    onStopLoading();

    data = null;
  }

  @Override
  public void onCanceled(T data)
  {
    // attempt to cancel the current asynchronous load
    super.onCanceled(data);

    // the load has been canceled, so we should release the resources
    // associated with 'data'
    releaseResources(data);
  }


  /**
   * Implementers should override this method and perform any necessary actions
   * to release the given data. For a simple List, there is nothing to do. For
   * something like a Cursor, it should be closed in this method. All resources
   * associated with the Loader should be released here.
   *
   * @param data
   *            The data to be released
   */
  protected void releaseResources(T data)
  {
  }

}
