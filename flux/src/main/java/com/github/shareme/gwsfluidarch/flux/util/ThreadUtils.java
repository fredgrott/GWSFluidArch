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
package com.github.shareme.gwsfluidarch.flux.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * Created by fgrott on 9/18/2016.
 */
@SuppressWarnings("unused")
public class ThreadUtils {

  public static boolean isOnMain() {
    return Looper.myLooper() == Looper.getMainLooper();
  }

  public static void ensureNotOnMain(){
    if(isOnMain())
      throw new DispatchOnMainThreadException();
  }

  public static void runOnMain(Runnable runnable) {
    if(isOnMain())
      runnable.run();
    else
      new Handler(Looper.getMainLooper()).post(runnable);
  }

  public static void runInBackground(@NonNull final Runnable runnable) {
    new Thread(runnable).start();
  }

  public static int getId() {
    return android.os.Process.myTid();
  }

  public static class DispatchOnMainThreadException extends RuntimeException {}

}
