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

import android.support.annotation.NonNull;

import com.github.shareme.gwsfluidarch.flux.impl.DispatcherImpl;

import java.util.Collection;
import java.util.List;

/**
 * This class serves as a coordinating object
 * It's only job is to instantiate the dispatcher.
 * It contains a bunch of proxy methods that call the corresponding methods on the dispatcher
 * It is not essential to use Fluxxan but helps quite a bit in keeping things cohesive.
 * By default, we use the {@link DispatcherImpl} provided. This can be changed by overriding {@link #initDispatcher(Object)}
 *
 * @param <State>
 *
 * Created by fgrott on 9/18/2016.
 */
@SuppressWarnings("unused")
public class Flux<State> {

  private final Dispatcher<State> mDispatcher;

  /**
   * Create a new instance
   * @param state The initial state tree
   */
  public Flux(@NonNull State state) {
    mDispatcher = initDispatcher(state);
  }

  /**
   * This can be overridden to provide a different {@link Dispatcher}
   * This is called once.
   *
   * @param state The intial state tree
   * @return An instance of the {@link Dispatcher}
   */
  protected Dispatcher<State> initDispatcher(State state) {
    return new DispatcherImpl<>(state);
  }

  /**
   * Get the dispatcher
   * @return The dispatcher
   */
  public Dispatcher getDispatcher() {
    return mDispatcher;
  }

  /**
   * Inject dispatcher into ActionCreator
   */
  public void inject(ActionCreator ac) {
    ac.setDispatcher(getDispatcher());
  }


  //dispatcher proxy methods

  /**
   * @see Dispatcher#getState()
   */
  public State getState() {
    return mDispatcher.getState();
  }

  /**
   * @see Dispatcher#getReducer(Class)
   */
  public <T extends Reducer<State>> T getReducer(Class<T> reducerClass) {
    return mDispatcher.getReducer(reducerClass);
  }

  /**
   * @see Dispatcher#registerReducer(Reducer)
   */
  public Reducer<State> registerReducer(@NonNull Reducer<State> reducer) {
    return mDispatcher.registerReducer(reducer);
  }

  /**
   * @see Dispatcher#registerReducers(List)
   */
  protected Collection<Reducer<State>> registerReducers(@NonNull List<Reducer<State>> reducers) {
    return mDispatcher.registerReducers(reducers);
  }

  /**
   * @see Dispatcher#unregisterReducer(Class)
   */
  public <T extends Reducer<State>> T unregisterReducer(Class<T> reducer) {
    return mDispatcher.unregisterReducer(reducer);
  }

  /**
   * @see Dispatcher#addListener(StateListener)
   */
  public boolean addListener(StateListener<State> stateListener) {
    return mDispatcher.addListener(stateListener);
  }

  /**
   * @see Dispatcher#removeListener(StateListener)
   */
  public boolean removeListener(StateListener<State> stateListener) {
    return mDispatcher.removeListener(stateListener);
  }

  /**
   * @see Dispatcher#start()
   */
  public void start() {
    mDispatcher.start();
  }

  /**
   * @see Dispatcher#stop()
   */
  public void stop() {
    mDispatcher.stop();
  }
}
