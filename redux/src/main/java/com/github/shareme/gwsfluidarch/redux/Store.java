/*
  The MIT License (MIT)

Copyright (c) 2016 Serge Zaitsev
Modifications Copyright(c) 2016 Fred Grott(GrottWorkShop)

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
package com.github.shareme.gwsfluidarch.redux;

import java.util.ArrayList;
import java.util.List;

/**
 * store providing reducer and initial state
 *
 * Created by fgrott on 9/18/2016.
 */
@SuppressWarnings("unused")
public final class Store<A, S> {

  private S currentState;
  private final Middleware<A, S>[] middlewares;

  private final Reducer<A, S> reducer;
  private final List<Runnable> subscribers = new ArrayList<>();

  private final Middleware<A, S> dispatcher = new Middleware<A, S>() {
    @Override
    public void dispatch(Store<A, S> store, A action, NextDispatcher<A> next) {
      synchronized (this) {
        currentState = store.reducer.reduce(action, currentState);
      }
      for (int i = 0; i < subscribers.size(); i++) {
        store.subscribers.get(i).run();
      }
    }
  };

  private final List<NextDispatcher<A>> next = new ArrayList<>();




  @SafeVarargs
  public Store(Reducer<A, S> reducer, S state, Middleware<A, S> ...middlewares) {
    this.reducer = reducer;
    this.currentState = state;
    this.middlewares = middlewares;

    this.next.add(new NextDispatcher<A>() {
      public void dispatch(A action) {
        Store.this.dispatcher.dispatch(Store.this, action, null);
      }
    });
    for (int i = middlewares.length-1; i >= 0; i--) {
      final Middleware<A, S> mw = middlewares[i];
      final NextDispatcher<A> n = next.get(0);
      next.add(0, new NextDispatcher<A>() {
        public void dispatch(A action) {
          mw.dispatch(Store.this, action, n);
        }
      });
    }
  }

  public S dispatch(A action) {
    this.next.get(0).dispatch(action);
    return this.getState();
  }

  public S getState() {
    return this.currentState;
  }

  public Runnable subscribe(final Runnable r) {
    this.subscribers.add(r);
    return new Runnable() {
      public void run() {
        subscribers.remove(r);
      }
    };
  }

}
