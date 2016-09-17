/*
  The MIT License (MIT)

Copyright (c) 2015 Trikita
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
package com.github.shareme.gwsfluidarch.common.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fgrott on 9/11/2016.
 */
//TODO add uri validation and bundleEXTRAS parameter
@SuppressWarnings("unused")
public class ActivityRouter {

  public static final ActivityRouter sInstance = new ActivityRouter();

  private List<Pair<String, Class<? extends Activity>>> mRouting =
          new ArrayList<>();

  public ActivityRouter() {
    //
  }

  public static ActivityRouter getDefault() {
    return sInstance;
  }

  // TODO validate uri, throw RuntimeException if it's invalid
  @SuppressWarnings("unchecked")
  public ActivityRouter add(String uri, Class<? extends Activity> a) {
    mRouting.add(new Pair(uri, a));
    return this;
  }

  public boolean route(Context c, String uri) {
    Intent intent = findRoute(c, uri);
    if (intent == null) {
      return false;
    }
    // navigate to the matched activity
    c.startActivity(intent);
    return true;
  }

  Intent findRoute(Context c, String uri) {
    Map<String, String> properties = new HashMap<>();

    for (Pair<String, Class<? extends Activity>> entry : mRouting) {
      properties.clear();

      if (Utils.matchUri(uri, entry.first, properties)) {
        Intent intent = new Intent(c, entry.second);

        // copy parsed uri params into intent extras
        for (Map.Entry<String, String> p : properties.entrySet()) {
          intent.putExtra(p.getKey(), p.getValue());
        }
        return intent;
      }
    }
    return null;
  }

}
