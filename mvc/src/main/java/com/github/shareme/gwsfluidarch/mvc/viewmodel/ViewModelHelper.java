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
package com.github.shareme.gwsfluidarch.mvc.viewmodel;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

import timber.log.Timber;

/**
 * Created by fgrott on 9/17/2016.
 */
@SuppressWarnings("unused")
public class ViewModelHelper<T extends MyView, R extends AbstractViewModel<T>> {

  @Nullable
  private String mScreenId;
  @Nullable
  private R mViewModel;
  private boolean mModelRemoved;
  private boolean mOnSaveInstanceCalled;

  /**
   * Call from {@link android.app.Activity#onCreate(android.os.Bundle)} or
   * {@link android.support.v4.app.Fragment#onCreate(android.os.Bundle)}
   * @param activity parent activity
   * @param savedInstanceState savedInstance state from {@link Activity#onCreate(Bundle)} or
   *                           {@link Fragment#onCreate(Bundle)}
   * @param viewModelClass the {@link Class} of your ViewModel
   * @param arguments pass {@link Fragment#getArguments()}  or
   *                  {@link Activity#getIntent()}.{@link Intent#getExtras() getExtras()}
   */
  public void onCreate(@NonNull Activity activity,
                       @Nullable Bundle savedInstanceState,
                       @Nullable Class<? extends AbstractViewModel<T>> viewModelClass,
                       @Nullable Bundle arguments) {
    // no viewmodel for this fragment
    if (viewModelClass == null) {
      mViewModel = null;
      return;
    }

    // screen (activity/fragment) created for first time, attach unique ID
    if (savedInstanceState == null) {
      mScreenId = UUID.randomUUID().toString();
    } else {
      mScreenId = savedInstanceState.getString("identifier");
      mOnSaveInstanceCalled = false;
    }

    // get model instance for this screen
    final ViewModelProvider.ViewModelWrapper<T> viewModelWrapper = getViewModelProvider(activity).getViewModelProvider().getViewModel(mScreenId, viewModelClass);
    //noinspection unchecked
    mViewModel = (R) viewModelWrapper.viewModel;

    if (viewModelWrapper.wasCreated) {
      // detect that the system has killed the app - saved instance is not null, but the model was recreated
      if (savedInstanceState != null) {
        Timber.d("model", "Fragment recreated by system - restoring viewmodel");
      }
      mViewModel.onCreate(arguments, savedInstanceState);
    }
  }

  /**
   * Call from {@link android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)}
   * or {@link android.app.Activity#onCreate(android.os.Bundle)}
   * @param view view
   */
  public void setView(@NonNull final T view) {
    if (mViewModel == null) {
      //no viewmodel for this fragment
      return;
    }
    mViewModel.onBindView(view);
  }

  /**
   * Use in case this model is associated with an {@link android.support.v4.app.Fragment}
   * Call from {@link android.support.v4.app.Fragment#onDestroyView()}. Use in case model is associated
   * with Fragment
   * @param fragment fragment
   */
  public void onDestroyView(@NonNull Fragment fragment) {
    if (mViewModel == null) {
      //no viewmodel for this fragment
      return;
    }
    mViewModel.clearView();
    if (fragment.getActivity() != null && fragment.getActivity().isFinishing()) {
      removeViewModel(fragment.getActivity());
    }
  }

  /**
   * Use in case this model is associated with an {@link android.support.v4.app.Fragment}
   * Call from {@link android.support.v4.app.Fragment#onDestroy()}
   * @param fragment fragment
   */
  public void onDestroy(@NonNull final Fragment fragment) {
    if (mViewModel == null) {
      //no viewmodel for this fragment
      return;
    }
    if (fragment.getActivity().isFinishing()) {
      removeViewModel(fragment.getActivity());
    } else if (fragment.isRemoving() && !mOnSaveInstanceCalled) {
      // The fragment can be still in backstack even if isRemoving() is true.
      // We check mOnSaveInstanceCalled - if this was not called then the fragment is totally removed.

      Timber.d("mode", "Removing viewmodel - fragment replaced"); //NON-NLS

      removeViewModel(fragment.getActivity());
    }
  }

  /**
   * Use in case this model is associated with an {@link android.app.Activity}
   * Call from {@link android.app.Activity#onDestroy()}
   * @param activity activity
   */
  public void onDestroy(@NonNull final Activity activity) {
    if (mViewModel == null) {
      //no viewmodel for this fragment
      return;
    }
    mViewModel.clearView();
    if (activity.isFinishing()) {
      removeViewModel(activity);
    }
  }

  /**
   * Call from {@link android.app.Activity#onStop()} or {@link android.support.v4.app.Fragment#onStop()}
   */
  public void onStop() {
    if (mViewModel == null) {
      //no viewmodel for this fragment
      return;
    }
    mViewModel.onStop();
  }

  /**
   * Call from {@link android.app.Activity#onStart()} ()} or {@link android.support.v4.app.Fragment#onStart()} ()}
   */
  public void onStart() {
    if (mViewModel == null) {
      //no viewmodel for this fragment
      return;
    }
    mViewModel.onStart();
  }


  /**
   * Returns the current ViewModel instance associated with the Fragment or Activity.
   * Throws an {@link IllegalStateException} in case the ViewModel is null. This can happen
   * if you call this method too soon - before {@link Activity#onCreate(Bundle)} or {@link Fragment#onCreate(Bundle)}
   * or this {@link ViewModelHelper} is not properly setup.
   * @return {@link R}
   */
  @NonNull
  public R getViewModel() {
    if (null == mViewModel) {
      throw new IllegalStateException("ViewModel is not ready. Are you calling this method before Activity/Fragment onCreate?"); //NON-NLS
    }
    return mViewModel;
  }

  /**
   * Call from {@link android.app.Activity#onSaveInstanceState(android.os.Bundle)}
   * or {@link android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)}.
   * This allows the model to save its state.
   * @param bundle bundle
   */
  public void onSaveInstanceState(@NonNull Bundle bundle) {
    bundle.putString("identifier", mScreenId);
    if (mViewModel != null) {
      mViewModel.onSaveInstanceState(bundle);
      mOnSaveInstanceCalled = true;
    }
  }

  private void removeViewModel(@NonNull final Activity activity) {
    if (mViewModel != null && !mModelRemoved) {
      getViewModelProvider(activity).getViewModelProvider().remove(mScreenId);
      mViewModel.onDestroy();
      mModelRemoved = true;
    }
  }

  private MyViewModelProvider getViewModelProvider(@NonNull Activity activity) {
    if (!(activity instanceof MyViewModelProvider)) {
      throw new IllegalStateException("Your activity must implement IViewModelProvider"); //NON-NLS
    }
    return ((MyViewModelProvider) activity);
  }
}
