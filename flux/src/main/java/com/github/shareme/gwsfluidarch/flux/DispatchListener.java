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

/**
 * Defines an object that listens to changes to the state.
 *
 * @param <State>
 *
 * Created by fgrott on 9/18/2016.
 */
@SuppressWarnings("unused")
public interface DispatchListener<State> {
  /**
   * Called before an action is dispatched.
   *
   * @param action The action to be dispatched
   * @param currentState The current state after the dispatch.
   */
  void beforeDispatch(Action action, State currentState);

  /**
   * Called when an exception occurs during a dispatch.
   *
   * @param action The action that caused the Exception
   * @param ex the exception
   */
  void onDispatchException(Action action, Exception ex);

  /**
   * Called after the dispatch is complete.
   *
   * @param action The action that was dispatched
   * @param currentState The current state after the dispatch.
   * @param stateChanged Whether or not the state was changed
   * @param wasHandled Whether or not the action was handled
   */
  void afterDispatch(Action action, State currentState, Boolean stateChanged, Boolean
          wasHandled);

}
