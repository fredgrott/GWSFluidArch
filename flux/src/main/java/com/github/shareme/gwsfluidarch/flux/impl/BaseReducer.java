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

import com.github.shareme.gwsfluidarch.flux.Dispatcher;
import com.github.shareme.gwsfluidarch.flux.Reducer;
import com.github.shareme.gwsfluidarch.flux.WaitCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An abstract implementation of {@link Reducer}
 * This is a good starting point for your reducers
 *
 * @param <State>
 *
 * Created by fgrott on 9/18/2016.
 */

public abstract class BaseReducer<State> implements Reducer<State> {

  protected Dispatcher mDispatcher;
  protected boolean mIsResolved = false;
  protected WaitCallback mWaitCallback = null;
  protected final List<String> mWaitingOnList;

  //ensure you call super else!!!
  public BaseReducer() {
    mWaitingOnList = Collections.synchronizedList(new ArrayList<String>());
  }

  @Override
  public BaseReducer<State> setDispatcher(Dispatcher dispatcher) {
    mDispatcher = dispatcher;

    return this;
  }


  @Override
  public BaseReducer<State> reset() {
    mIsResolved = false;
    mWaitingOnList.clear();
    mWaitCallback = null;

    return this;
  }

  @Override
  public List<String> getWaitingOnList() {
    return mWaitingOnList;
  }

  @Override
  public WaitCallback getWaitCallback() {
    return mWaitCallback;
  }

  @Override
  public BaseReducer<State> setWaitCallback(WaitCallback callback) {
    mWaitCallback = callback;

    return this;
  }


  @Override
  public boolean isResolved() {
    return mIsResolved;
  }

  @Override
  public BaseReducer<State> setResolved(boolean resolved) {
    mIsResolved = resolved;

    return this;
  }

  @Override
  public BaseReducer<State> addToWaitingOnList(Collection<String> reducerNames) {
    mWaitingOnList.addAll(reducerNames);

    return this;
  }


  @Override
  public void waitFor(Class[] reducers, WaitCallback callback) {
    Set<Class> _reducers = new HashSet<>(Arrays.asList(reducers));

    mDispatcher.waitFor(this.getClass(), _reducers, callback);
  }

  @Override
  public void waitFor(Class reducer, WaitCallback callback) {
    waitFor(new Class[]{reducer}, callback);
  }

}
