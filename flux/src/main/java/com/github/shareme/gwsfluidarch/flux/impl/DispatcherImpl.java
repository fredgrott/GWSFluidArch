/*
  The MIT License (MIT)

Copyright (c) 2016 Froelich Stefan
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
package com.github.shareme.gwsfluidarch.flux.impl;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.github.shareme.gwsfluidarch.flux.Action;
import com.github.shareme.gwsfluidarch.flux.DispatchListener;
import com.github.shareme.gwsfluidarch.flux.DispatchResult;
import com.github.shareme.gwsfluidarch.flux.Dispatcher;
import com.github.shareme.gwsfluidarch.flux.Reducer;
import com.github.shareme.gwsfluidarch.flux.StateListener;
import com.github.shareme.gwsfluidarch.flux.WaitCallback;
import com.github.shareme.gwsfluidarch.flux.util.CollectionUtils;
import com.github.shareme.gwsfluidarch.flux.util.ThreadUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

/**
 * Created by fgrott on 9/18/2016.
 */

public class DispatcherImpl<State> implements Dispatcher<State> {

  private static final String TAG = "Dispatcher";

  protected State mState;
  protected final LinkedBlockingQueue<Action> mDispatchQueue = new LinkedBlockingQueue<>();
  protected final ConcurrentHashMap<String, Reducer<State>> mReducers = new ConcurrentHashMap<>();
  protected AtomicBoolean mIsDispatching = new AtomicBoolean(false);
  protected String mCurrentActionType = null;
  protected Collection<String> mWaitingToDispatch;
  protected final List<StateListener<State>> mStateListeners;
  protected final List<DispatchListener<State>> mDispatchListeners;


  protected boolean isStarted;
  private Thread mDispatchThread;
  private int mThreadId;

  /**
   * Your initial state tree
   *
   * @param state
   */
  public DispatcherImpl(State state) {
    mState = state;
    mStateListeners = Collections.synchronizedList(new ArrayList<StateListener<State>>());
    mDispatchListeners = Collections.synchronizedList(new ArrayList<DispatchListener<State>>());
  }

  protected void _dispatch(@NonNull final Action action) {
    ThreadUtils.ensureNotOnMain();

    String[] names = mReducers.keySet().toArray(new String[mReducers.size()]);
    for (String name : names) {
      Reducer reducer = mReducers.get(name);
      reducer.reset();
    }

    mCurrentActionType = action.Type;
    mWaitingToDispatch = new HashSet<>(mReducers.keySet());

    mIsDispatching.set(true);

    RuntimeException ex = null;
    try {
      Timber.i("Dispatcher", String.format("[STARTED] dispatch of action [%s]", action.Type));
      doDispatchLoop(action);
      Timber.i("Dispatcher", String.format("[COMPLETED] dispatch of action [%s]", action.Type));
    }
    catch (Exception e) {
      Timber.e("Dispatcher", String.format("[FAILED] dispatch of action [%s]", action.Type), e);
      ex = new RuntimeException(e);

      notifyDispatchListenersOnException(action, e);
    }
    finally {
      mCurrentActionType = null;
      mIsDispatching.set(false);
    }

    //we need to propagate the exception
    if(ex != null) throw ex;
  }

  protected synchronized void doDispatchLoop(Action action) throws Exception {
    ThreadUtils.ensureNotOnMain();

    notifyDispatchListenersBefore(action, mState);

    Reducer<State> dispatch;
    Boolean canBeDispatchedTo;
    Boolean wasHandled = false;
    List<String> removeFromDispatchQueue = new ArrayList<>();
    List<String> dispatchedThisLoop = new ArrayList<>();
    State dispatchState = mState;

    for (String key : mWaitingToDispatch) {
      dispatch = mReducers.get(key);
      canBeDispatchedTo = (dispatch.getWaitingOnList().size() == 0) || (CollectionUtils.intersection(dispatch.getWaitingOnList(), new ArrayList<>(mWaitingToDispatch)).size() == 0);

      if (canBeDispatchedTo) {
        if (dispatch.getWaitCallback() != null) {
          WaitCallback fn = dispatch.getWaitCallback();
          dispatch.reset();
          dispatch.setResolved(true);
          fn.call();
          wasHandled = true;
        } else {
          dispatch.setResolved(true);

          DispatchResult<State> result = mReducers.get(key).reduce(dispatchState, action);
          dispatchState = result.state;

          if (result.handled) {
            wasHandled = true;
          }
        }

        dispatchedThisLoop.add(key);
        if (dispatch.isResolved()) {
          removeFromDispatchQueue.add(key);
        }
      }
    }

    if (mWaitingToDispatch.size() > 0 && dispatchedThisLoop.size() == 0) {
      String reducersWithCircularWaits = CollectionUtils.implode(mWaitingToDispatch.iterator());
      throw new Exception("Indirect circular wait detected among: " + reducersWithCircularWaits);
    }

    for(int i = 0; i < removeFromDispatchQueue.size(); i++)
      mWaitingToDispatch.remove(removeFromDispatchQueue.get(i));

    if (mWaitingToDispatch.size() > 0) {
      this.doDispatchLoop(action);
    }

    boolean stateChanged = hasStateChanged(dispatchState, mState);

    if (!wasHandled) {
      Timber.d(TAG, String.format("An action of type [%s] was dispatched, but no reducer handled it", action.Type));
    }
    else if(stateChanged){
      State oldstate = mState;
      mState = dispatchState;//update state

      notifyStateListeners(dispatchState, oldstate);
    }

    notifyDispatchListenersAfter(action, mState, stateChanged, wasHandled);
  }

  protected void notifyStateListeners(State newState, State oldState) {
    synchronized (mStateListeners) {
      Exception exception = null;
      for (StateListener<State> listener : mStateListeners) {
        try {
          if(listener.hasStateChanged(newState, oldState)) listener.onStateChanged(newState);
        } catch (Exception e) {
          Timber.e(TAG, "Unexpected exception during notifyStateListeners", e);
          exception = e;//let's save this for after. It might just fuck things up terribly
        }
      }

      if(exception != null) throw new RuntimeException(exception);
    }
  }

  protected void notifyDispatchListenersBefore(Action action, State currentState) {
    synchronized (mDispatchListeners) {
      Exception exception = null;
      for (DispatchListener<State> listener : mDispatchListeners) {
        try {
          listener.beforeDispatch(action, currentState);
        } catch (Exception e) {
          Timber.e(TAG, "Unexpected exception during notifyDispatchListenersBefore", e);
          exception = e;//let's save this for after. It might just fuck things up terribly
        }
      }

      if(exception != null) throw new RuntimeException(exception);
    }
  }

  protected void notifyDispatchListenersOnException(Action action, Exception ex) {
    synchronized (mDispatchListeners) {
      Exception exception = null;
      for (DispatchListener<State> listener : mDispatchListeners) {
        try {
          listener.onDispatchException(action, ex);
        } catch (Exception e) {
          Timber.e(TAG, "Unexpected exception during notifyDispatchListenersOnException", e);
          exception = e;//let's save this for after. It might just fuck things up terribly
        }
      }

      if(exception != null) throw new RuntimeException(exception);
    }
  }

  protected void notifyDispatchListenersAfter(Action action, State currentState, Boolean stateChanged, Boolean
          wasHandled) {
    synchronized (mDispatchListeners) {
      Exception exception = null;
      for (DispatchListener<State> listener : mDispatchListeners) {
        try {
          listener.afterDispatch(action, currentState, stateChanged, wasHandled);
        } catch (Exception e) {
          Timber.e(TAG, "Unexpected exception during notifyDispatchListenersAfter", e);
          exception = e;//let's save this for after. It might just fuck things up terribly
        }
      }

      if(exception != null) throw new RuntimeException(exception);
    }
  }

  //------ public api

  /**
   * {@inheritDoc}
   * This must be called before any dispatch events else a runtime exception will be thrown
   */
  public void start() {
    if(isStarted) return;

    mDispatchThread = new Thread(new DispatchThread());
    mDispatchThread.start();
    isStarted = true;
  }

  /**
   * {@inheritDoc}
   * This should be called during cleanup to release any resources or when dispatching is no longer required
   */
  public void stop() {
    isStarted = false;
    mDispatchThread.interrupt();
    try {
      mDispatchThread.join(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    mDispatchThread = null;
    mThreadId = 0;
  }

  /**
   * {@inheritDoc}
   * Dispatching occurs on a dedicated background thread and are processed sequentially.
   *
   * @throws IllegalStateException if {@link DispatcherImpl#start} has not been called or it's called from within a dispatch cycle
   * @throws IllegalArgumentException if action type is empty. {@link TextUtils#isEmpty}
   */
  public void dispatch(@NonNull Action action) {
    if(!isStarted) {
      throw new IllegalStateException("Dispatcher not started!");
    }

    if(mThreadId == ThreadUtils.getId() && isDispatching()) {
      throw new IllegalStateException("Cannot call dispatch while dispatching!");
    }

    if (TextUtils.isEmpty(action.Type)) {
      throw new IllegalArgumentException("Can only dispatch actions with a valid 'Type' property");
    }

    mDispatchQueue.offer(action);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Reducer<State>> T getReducer(Class<T> reducerClass) {
    return (T) mReducers.get(reducerClass.getName());
  }

  @Override
  public Collection<Reducer<State>> getReducers() {
    return mReducers.values();
  }

  /**
   * {@inheritDoc}
   * If a reducer of the same type is already registered, it will be replaced with the new one
   */
  @Override
  public Reducer<State> registerReducer(@NonNull Reducer<State> reducer) {
    reducer.setDispatcher(this);

    return mReducers.put(reducer.getClass().getName(), reducer);
  }

  /**
   * {@inheritDoc}
   * Uses {@link #registerReducer} internally
   */
  @Override
  public Collection<Reducer<State>> registerReducers(@NonNull List<Reducer<State>> reducers) {
    for (Reducer<State> reducer : reducers) {
      registerReducer(reducer);
    }

    return mReducers.values();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Reducer<State>> T unregisterReducer(Class<T> reducer) {
    return (T) mReducers.remove(reducer.getName());
  }

  @Override
  public boolean addListener(StateListener<State> stateListener) {
    removeListener(stateListener);
    return mStateListeners.add(stateListener);
  }

  @Override
  public boolean removeListener(StateListener<State> stateListener) {
    return mStateListeners.remove(stateListener);
  }

  public boolean addDispatchListener(DispatchListener<State> dispatchListener) {
    removeDispatchListener(dispatchListener);
    return mDispatchListeners.add(dispatchListener);
  }

  public boolean removeDispatchListener(DispatchListener<State> dispatchListener) {
    return mDispatchListeners.remove(dispatchListener);
  }

  /**
   * This will throw an IllegalStateException If a dispatch is not currently running or the reducer is already waiting (nested call possibly)
   * or a circular wait is detected.
   * It will also throw an IllegalArgumentException If reducer is in the Set of reducers to wait on or a specified reducer has not been registered.
   */
  @Override
  public void waitFor(Class waitingReducer, Set<Class> reducerNames, WaitCallback callback) {
    ThreadUtils.ensureNotOnMain();

    String waitingReducerName = waitingReducer.getName();

    if (!isDispatching()) {
      throw new IllegalStateException("Cannot wait unless an action is being dispatched");
    }

    if (reducerNames.contains(waitingReducer)) {
      throw new IllegalArgumentException("A reducer cannot wait on itself");
    }

    Reducer dispatch = mReducers.get(waitingReducerName);

    if (dispatch.getWaitingOnList().size() > 0) {
      throw new IllegalStateException(waitingReducerName + " is already waiting on reducers");
    }

    for (Class reducerName1 : reducerNames) {
      String reducerName = reducerName1.getName();

      if (!mReducers.containsKey(reducerName)) {
        throw new IllegalArgumentException("Cannot wait for non-existent reducer " + reducerName);
      }

      Reducer reducerDispatch = mReducers.get(reducerName);
      if (reducerDispatch.getWaitingOnList().contains(waitingReducerName)) {
        throw new IllegalStateException("Circular wait detected between " + waitingReducerName + " and " + reducerName);
      }
    }

    dispatch.reset();

    dispatch.setWaitCallback(callback);
    dispatch.addToWaitingOnList(reducerNames);
  }

  @Override
  public boolean isDispatching() {
    return mIsDispatching.get();
  }

  public State getState() {
    return mState;
  }


  /**
   * {@inheritDoc}
   * By default, it always returns true
   */
  @Override
  public boolean hasStateChanged(State newState, State oldState) {
    return newState != oldState;
  }

  //------ inner classes

  private final class DispatchThread implements Runnable {
    @Override
    public void run() {
      mThreadId = ThreadUtils.getId();
      boolean run = true;
      Action action;

      while (run) {
        if(mDispatchThread.isInterrupted()) return;

        try {
          action = mDispatchQueue.take();
          if(action != null) {
            _dispatch(action);
          }
        } catch (InterruptedException e) {
          run = false;
        }
      }
    }
  }
}
