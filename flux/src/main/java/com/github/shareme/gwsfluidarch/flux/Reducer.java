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
package com.github.shareme.gwsfluidarch.flux;

import com.github.shareme.gwsfluidarch.flux.impl.DispatcherImpl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This interface defines a reducer
 * The reducer takes in the State tree and an action {@link Action} and returns a new State tree based on this
 * Ideally, the reducer should not mutate the state but return a new instance of it if needs to be changed
 * This increases performance by allowing short-circuiting of the notification process
 * @see DispatcherImpl#hasStateChanged(Object, Object)
 * @see StateListener#hasStateChanged(Object, Object)
 * @param <State>
 *
 * Created by fgrott on 9/18/2016.
 */
@SuppressWarnings("unused")
public interface Reducer<State> {
  /**
   * Reduce the action to generate the next state of the app
   * If state is not changed, original state can be returned
   *
   * @param state The current State tree
   * @param action The action action
   * @return A {@link DispatchResult} to indicate if the action was handled and the state
   * @throws Exception
   */
  DispatchResult<State> reduce(State state, Action action) throws Exception;

  /**
   * Check if reducer has been called during current dispatch cycle. Used by {@link Dispatcher} internally.
   * @return True if resolved or False
   */
  boolean isResolved();

  /**
   * Set the resolved state of the reducer. Used by {@link Dispatcher} internally.
   * @param resolved Whether reducer is resolved or not
   * @return The current reducer instance
   */
  Reducer<State> setResolved(boolean resolved);

  /**
   * A convenience proxy method for {@link Dispatcher#waitFor(Class, Set, WaitCallback))
   * @param reducer
   * @param callback
   * @throws Exception
   */
  void waitFor(Class reducer, WaitCallback callback);

  /**
   * A convenience proxy method for {@link Dispatcher#waitFor(Class, Set, WaitCallback))
   * @param reducer
   * @param callback
   * @throws Exception
   */
  void waitFor(Class[] reducers, WaitCallback callback);

  /**
   * Get the callback passed to {@link #setWaitCallback(WaitCallback)}
   * This is used internally by {@link Dispatcher} to keep track of state of the reducer
   *
   * @return The callback
   */
  WaitCallback getWaitCallback();

  /**
   * Sets a callback to be retrieved later by {@link #getWaitCallback()}
   * This is used internally by {@link Dispatcher} to keep track of state of the reducer
   *
   * @return The current reducer instance
   */
  Reducer<State> setWaitCallback(WaitCallback callback);

  /**
   * Gets the list of reducers this reducer is waiting for
   * This is used internally by {@link Dispatcher} to keep track of state of the reducer
   *
   * @return The list of reducer class names
   */
  List<String> getWaitingOnList();

  /**
   * Adds list of store names to be retrieved later by {@link #getWaitingOnList()}
   * This is used internally by {@link Dispatcher} to keep track of state of the reducer
   *
   * @return The current reducer instance
   */
  Reducer<State> addToWaitingOnList(Collection<String> reducerNames);

  /**
   * A convenience method to inject the dispatcher into the reducer
   *
   * @param dispatcher The current dispatcher
   * @return The current reducer instance
   */
  Reducer<State> setDispatcher(Dispatcher dispatcher);

  /**
   * Resets all state flags used by the dispatcher internally
   *
   * @see #setWaitCallback(WaitCallback)
   * @see #addToWaitingOnList(Collection)
   * @see #setResolved(boolean)
   * @return The current reducer instance
   */
  Reducer<State> reset();
}