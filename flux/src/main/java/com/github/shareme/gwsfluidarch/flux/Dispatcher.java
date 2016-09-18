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
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * The dispatcher.
 * This handles dispatching of all actions and notifying of registered listeners of changes
 *
 * @param <State> Your state object type. Ideally an immutable object
 *
 * Created by fgrott on 9/18/2016.
 */
@SuppressWarnings("unused")
public interface Dispatcher<State> {
  /**
   * Start the dispatcher.
   */
  void start();

  /**
   * Stop the dispatcher.
   */
  void stop();

  /**
   * Dispatch an action
   * @param action An action object containing the action type and the data action
   */
  void dispatch(@NonNull Action action);

  /**
   * Get the current state
   * @return The current state
   */
  State getState();

  /**
   * Get the reducer of the given class if registered
   *
   * @param reducerClass Class of reducer to return
   * @param <T> The class type of the reducer you wish to return. Should implement {@link Reducer}
   * @return The reducer or null
   */
  @Nullable
  <T extends Reducer<State>> T getReducer(Class<T> reducerClass);

  /**
   * Get all registered reducers
   *
   * @return A collection of all registered reducers
   */
  Collection<Reducer<State>> getReducers();

  /**
   * Register a reducer
   *
   * @param reducer Reducer to be registered
   * @return The reducer
   */
  Reducer<State> registerReducer(@NonNull Reducer<State> reducer);

  /**
   * Register a list of reducers
   * @param reducers The list of reducers to be registered
   * @return The list of reducers
   */
  Collection<Reducer<State>> registerReducers(@NonNull List<Reducer<State>> reducers);

  /**
   * Unregister a reducer
   *
   * @param reducerClass Class of reducer you wish to unregister
   * @param <T> The class type of the reducer you wish to unregister.
   * @return The unregistered reducer or null if not registered
   */
  @Nullable <T extends Reducer<State>> T unregisterReducer(Class<T> reducerClass);

  /**
   * Add a listener to listen to state changes
   * @param StateListener The listener
   * @return True on success or False on failure
   */
  boolean addListener(StateListener<State> StateListener);

  /**
   * Remove a listener
   * @param StateListener The listener
   * @return True on success or False on failure
   */
  boolean removeListener(StateListener<State> StateListener);

  /**
   * Wait for a given reducer before being applied to the state.
   * This should be called a reducer when it is being applied.
   *
   * @param waitingReducer The reducer that is waiting
   * @param reducers A set of the Classes of the reducers to wait for
   * @param callback Callback to be called after specified reducers have been applied
   */
  void waitFor(Class waitingReducer, Set<Class> reducers, WaitCallback callback);

  /**
   * Check if dispatcher is currently dispatching
   *
   * @return True if dispatching or False
   */
  boolean isDispatching();

  /**
   * Can be overridden to short-circuit the notification of listeners if the state has not changes
   * Especially useful when using an Immutable object to hold state. Check can simply be `newState != oldState`.
   *
   * @param newState The new state
   * @param oldState The old state
   * @return True if state has changed or False
   */
  boolean hasStateChanged(State newState, State oldState);
}
