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
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Just a simple AsyncTaskLoader that broadcasts
 * Created by fgrott on 9/1/2016.
 */
@SuppressWarnings("unused")
public abstract class BroadcastingAsyncTaskLoader<T> extends AsyncTaskLoader<T> {

  private T mData;


  public BroadcastingAsyncTaskLoader(Context context) {
    super(context);
  }

  /**
   * Get the Intent Action string used to listen to Broadcast intents
   * sent when the Loader starts working. NOTE: Implementors must return a
   * string that is unique to their Loader to ensure that only the proper
   * listeners receive the broadcasts.
   *
   * @return A String that is unique to this Loader and action
   */
  public abstract String getLoadingStartedIntentAction();

  /**
   * Get the Intent Action string used to listen to Broadcast intents
   * sent when the Loader finishes working. NOTE: Implementors must return a
   * string that is unique to their Loader to ensure that only the proper
   * listeners receive the broadcasts.
   *
   * @return A String that is unique to this Loader and action
   */
  public abstract String getLoadingFinishedIntentAction();

  @Override
  public final T loadInBackground()
  {
    Intent intent = new Intent(getLoadingStartedIntentAction());
    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

    T data = loadInBackgroundCore();

    intent = new Intent(getLoadingFinishedIntentAction());
    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

    return data;
  }

  public abstract T loadInBackgroundCore();

  @Override
  public void deliverResult(T data)
  {
    // the Loader has been reset; ignore the result and invalidate the data
    if (isReset())
    {
      releaseResources(data);
      return;
    }

    // hold a reference to the old data so we can release it after delivering
    // the new data to the client
    T oldData = mData;
    mData = data;

    // if the Loader is in a started state, deliver the results to the client
    if (isStarted())
      super.deliverResult(data);

    if (oldData != null && oldData != data)
      releaseResources(data);
  }

  @Override
  protected void onStartLoading()
  {
    // deliver any previously loaded data immediately
    if (mData != null)
      deliverResult(mData);

    registerObserver();

    // if the data has changed since the last time it was loaded
    // or is not currently available, start a load
    if (mData == null || takeContentChanged())
      forceLoad();
  }

  @Override
  protected void onStopLoading()
  {
    // attempt to cancel the current load task if possible
    cancelLoad();

    // note that we leave the observer as is. Loaders in a stopped state
    // should still monitor the data source for changes so that the Loader
    // will know to force a new load if it is ever started again
  }

  @Override
  protected void onReset()
  {
    // ensure the Loader has been stopped
    onStopLoading();

    // at this point we can release the resources associated with mData
    if (mData != null)
    {
      releaseResources(mData);
      mData = null;
    }

    // the Loader is being reset, so we should stop monitoring for changes
    unregisterObserver();
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

  /**
   * Called at the appropriate time for registering observers on the content
   * being loaded. The observer could be anything so long as it is able to
   * detect content changes and report them to the Loader with a call to
   * onContentChanged(). For example, if you were writing a Loader which loads a
   * list of all installed applications on the device, the observer could be a
   * BroadcastReceiver that listens for the ACTION_PACKAGE_ADDED intent, and
   * calls onContentChanged() on the particular Loader whenever the receiver
   * detects that a new application has been installed
   * NOTE: This method may be called multiple times without a call to
   * unregisterObserver, so your implementation should be idempotent
   */
  protected void registerObserver()
  {
  }

  /**
   * Called at the appropriate time for unregistering observers on the content
   * being loaded.
   */
  protected void unregisterObserver()
  {
  }

}
